package com.project.G1_T3.filestorage.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.G1_T3.common.exception.InvalidFileTypeException;

@Service
public class ImageValidationService {
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "image/svg+xml");

    public void validateImage(MultipartFile file) {
        
        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("No image file provided");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileTypeException("Invalid file type. Allowed types: JPEG, PNG, GIF, WebP, SVG");
        }

        // Validate actual file content
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null && !contentType.equals("image/svg+xml")) {
                throw new InvalidFileTypeException("Invalid image file content");
            }
        } catch (IOException e) {
            throw new InvalidFileTypeException("Error processing image file");
        }
    }
}
