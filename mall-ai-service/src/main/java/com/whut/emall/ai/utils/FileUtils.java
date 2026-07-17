package com.whut.emall.ai.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.whut.emall.common.entity.ApiException;

@Component
public class FileUtils {
    
    Path docPath;

    public FileUtils(@Value("${emall.static_path.doc:./static/doc}") String docPath) {
        this.docPath = Path.of(docPath).normalize().toAbsolutePath();
    }

    public File uploadDoc(MultipartFile file, String ext) {
        try {
            Files.createDirectories(docPath);
            String fileName;
            do {
                fileName = UUID.randomUUID().toString() + "." + ext;
            } while (Files.exists(docPath.resolve(fileName)));
            Files.copy(file.getInputStream(), docPath.resolve(fileName));
            return docPath.resolve(fileName).toFile();
        } catch (Exception e) {
            throw ApiException.err(500, "知识文档上传失败: "+e.getLocalizedMessage());
        }
    }

    public void deleteDoc(String fileName) {
        try {
            Path target = docPath.resolve(fileName);
            if (Files.exists(target)) {
                Files.delete(target);
            }
        } catch (Exception e) {
            throw ApiException.err(500, "知识文档删除失败: "+e.getLocalizedMessage());
        }
    }
}
