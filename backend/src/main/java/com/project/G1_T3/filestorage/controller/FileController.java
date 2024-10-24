package com.project.G1_T3.filestorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.G1_T3.filestorage.service.FileStorageService;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping()
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file) {
        try {
            String fileUrl = fileStorageService.uploadFile("profile-picture", file.getOriginalFilename(), file);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteFile(@RequestParam String filePath) {
        try {
            fileStorageService.deleteFile(filePath);
            return ResponseEntity.ok("File deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("File deletion failed: " + e.getMessage());
        }
    }
}
