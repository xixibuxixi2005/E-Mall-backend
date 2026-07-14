package com.whut.emall.ai.utils;

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

    public String uploadDoc(MultipartFile file) {
        try {
            Files.createDirectories(docPath);
            String uuid = UUID.randomUUID().toString();
            while (Files.exists(docPath.resolve(uuid))) {
                uuid = UUID.randomUUID().toString();
            }
            Files.copy(file.getInputStream(), docPath.resolve(uuid));
            return uuid;
        } catch (Exception e) {
            throw ApiException.err(500, "知识文档上传失败: "+e.getLocalizedMessage());
        }
    }

    public void deleteDoc(String uuid) {
        try {
            Files.delete(docPath.resolve(uuid));
        } catch (Exception e) {
            throw ApiException.err(500, "知识文档删除失败: "+e.getLocalizedMessage());
        }
    }
}
