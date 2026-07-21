package com.whut.emall.business.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.whut.emall.common.utils.OSSFileManager;

import jakarta.annotation.Resource;

@Service
public class UploadService {
    @Resource OSSFileManager ossFileManager;
    public List<String> uploadImages(MultipartFile[] images) {
        return ossFileManager.imagesUpload(images);
    }
}
