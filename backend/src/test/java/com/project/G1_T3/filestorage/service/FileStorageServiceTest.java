package com.project.G1_T3.filestorage.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.project.G1_T3.common.exception.FileNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private StorageClient storageClient;

    @Mock
    private Bucket bucket;

    @Mock
    private Blob blob;

    @InjectMocks
    private FileStorageService fileStorageService;

    private static final String TEST_FOLDER = "test-folder";
    private static final String TEST_FILENAME = "test-file.txt";
    private static final String TEST_CONTENT = "test content";
    private static final String TEST_CONTENT_TYPE = "text/plain";
    private static final String STORAGE_PATH = "https://storage.googleapis.com/";
    private static final String STORAGE_BUCKET = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileStorageService, "storagePath", STORAGE_PATH);
        ReflectionTestUtils.setField(fileStorageService, "storageBucket", STORAGE_BUCKET);
    }

    @Test
    void uploadFile_Success() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                TEST_FILENAME,
                TEST_FILENAME,
                TEST_CONTENT_TYPE,
                TEST_CONTENT.getBytes());

        try (MockedStatic<StorageClient> storageClientMockedStatic = mockStatic(StorageClient.class)) {
            storageClientMockedStatic.when(StorageClient::getInstance).thenReturn(storageClient);
            when(storageClient.bucket()).thenReturn(bucket);
            when(bucket.create(anyString(), any(byte[].class), anyString())).thenReturn(blob);

            // Act
            String result = fileStorageService.uploadFile(TEST_FOLDER, TEST_FILENAME, file);

            // Assert
            assertNotNull(result);
            assertTrue(result.contains(STORAGE_PATH));
            assertTrue(result.contains(STORAGE_BUCKET));
            assertTrue(result.contains(TEST_FILENAME));
            verify(bucket).create(eq(TEST_FOLDER + "/" + TEST_FILENAME), any(byte[].class), eq(TEST_CONTENT_TYPE));
        }
    }

    @Test
    void uploadFile_NullFile_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> fileStorageService.uploadFile(TEST_FOLDER, TEST_FILENAME, null));
    }

    @Test
    void deleteFile_Success() {
        // Arrange
        String filePath = TEST_FOLDER + "/" + TEST_FILENAME;

        try (MockedStatic<StorageClient> storageClientMockedStatic = mockStatic(StorageClient.class)) {
            storageClientMockedStatic.when(StorageClient::getInstance).thenReturn(storageClient);
            when(storageClient.bucket()).thenReturn(bucket);
            when(bucket.get(filePath)).thenReturn(blob);
            when(blob.exists()).thenReturn(true);

            // Act
            fileStorageService.deleteFile(filePath);

            // Assert
            verify(blob).delete();
        }
    }

    @Test
    void deleteFile_FileNotFound_ThrowsException() {
        // Arrange
        String filePath = TEST_FOLDER + "/" + TEST_FILENAME;

        try (MockedStatic<StorageClient> storageClientMockedStatic = mockStatic(StorageClient.class)) {
            storageClientMockedStatic.when(StorageClient::getInstance).thenReturn(storageClient);
            when(storageClient.bucket()).thenReturn(bucket);
            when(bucket.get(filePath)).thenReturn(null);

            // Act & Assert
            assertThrows(FileNotFoundException.class,
                    () -> fileStorageService.deleteFile(filePath));
        }
    }

    @Test
    void deleteFile_BlobNotExists_ThrowsException() {
        // Arrange
        String filePath = TEST_FOLDER + "/" + TEST_FILENAME;

        try (MockedStatic<StorageClient> storageClientMockedStatic = mockStatic(StorageClient.class)) {
            storageClientMockedStatic.when(StorageClient::getInstance).thenReturn(storageClient);
            when(storageClient.bucket()).thenReturn(bucket);
            when(bucket.get(filePath)).thenReturn(blob);
            when(blob.exists()).thenReturn(false);

            // Act & Assert
            assertThrows(FileNotFoundException.class,
                    () -> fileStorageService.deleteFile(filePath));
        }
    }
}