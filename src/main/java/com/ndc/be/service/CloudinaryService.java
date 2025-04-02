package com.ndc.be.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ndc.be.util.error.InternalServerException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                return uploadResult.get("url").toString();
            }

            return null;
        } catch (IOException e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
