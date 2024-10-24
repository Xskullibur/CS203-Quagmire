package com.project.G1_T3.filestorage.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private static final String FILE_OPENER_URL = "/o/";
    private static final String MEDIA_URL_PARAMETER = "?alt=media";

    @Value("${firebase.storage.url}")
    private String storagePath;

    @Value("${firebase.storage.bucket}")
    private String storageBucket;

    public String uploadFile(String folder, String fileName, MultipartFile file) throws IOException {

        if (file == null) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String filePath = Paths.get(folder, fileName).toString();
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create(filePath, file.getBytes(), file.getContentType());
        return storagePath + storageBucket + FILE_OPENER_URL + blob.getName() + MEDIA_URL_PARAMETER;
    }
}
