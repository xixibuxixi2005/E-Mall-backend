package com.whut.emall.common.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.messages.DeleteObject;
import jakarta.annotation.Resource;

@Component
public class OSSFileManager {
    private String makePublicPolicy(String bucket) {
        return String.format("""
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "*"
        ]
      },
      "Action": [
        "s3:GetBucketLocation",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::%s"
      ]
    },
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "*"
        ]
      },
      "Action": [
        "s3:GetObject"
      ],
      "Resource": [
        "arn:aws:s3:::%s/*"
      ]
    }
  ]
}
""", bucket, bucket);
    }

    private void ensureBucket(String bucket) throws Exception{
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            client.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket(bucket).config(makePublicPolicy(bucket)).build()
            );
        }
    }

    @Resource MinioClient client;
    private List<String> uploadFiles(MultipartFile[] files, String bucket) throws Exception{
        ensureBucket(bucket);List<String> filenames = new ArrayList<>();
        for (var file: files) {
            String fileFullName = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString();
            String ext = "";
            int dotIndex = fileFullName.lastIndexOf(".");
            if (dotIndex != -1) {
                ext = fileFullName.substring(dotIndex);
                fileName = fileName + ext;
            }
            try (InputStream in = file.getInputStream()) {
                client.putObject(PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(fileName)
                        .stream(in, in.available(), -1)
                        .contentType(file.getContentType())
                        .build()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            filenames.add(fileName);
        }
        return filenames;
    }

    private int deleteFiles(List<String> filenames, String bucket) {
        int deleted = filenames.size();
        var errors = client.removeObjects(RemoveObjectsArgs.builder().bucket(bucket).objects(
            filenames.stream().map(DeleteObject::new).toList()
        ).build());
        for (var error: errors) {
            try {
                System.err.println("文件删除失败："+error.get().message());
                deleted -= 1;
            } catch (Exception e) {
              System.err.println("文件删除失败："+e.getLocalizedMessage());
            }
        }
        return deleted;
    }
    
    
    @Value("${emall.img-bucket:img}")
    String imgBucket;
    @Value("${emall.img-url-prefix:/static/img/}")
    String imgUrlPrefix;
    public List<String> imagesUpload(MultipartFile[] images) {
        try {
            return uploadFiles(images, imgBucket).stream().map(name -> imgUrlPrefix+name).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public int imagesDelete(List<String> urls) {
        return deleteFiles(urls.stream().map(
          url -> url.substring(url.lastIndexOf("/")+1)
        ).toList(), imgBucket);
    }

    @Value("${emall.doc-bucket:doc}")
    String docBucket;
    @Value("${emall.doc-url-prefix:/static/doc/}")
    String docUrlPrefix;
    public List<String> docsUpload(MultipartFile[] docs) {
        try {
            return uploadFiles(docs, docBucket).stream().map(name -> docUrlPrefix+name).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public int docsDelete(List<String> urls) {
        return deleteFiles(urls.stream().map(
          url -> url.substring(url.lastIndexOf("/")+1)
        ).toList(), docBucket);
    }
}
