package com.khangmoihocit.VocabFlow.core.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadAvatar(MultipartFile file) throws IOException {
        // Cấu hình upload: Gom ảnh vào folder "avatars" trên Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "avatars", // Tên thư mục trên Cloudinary
                "resource_type", "image"
        ));

        // Trả về đường link ảnh bảo mật (https)
        return uploadResult.get("secure_url").toString();
    }
}