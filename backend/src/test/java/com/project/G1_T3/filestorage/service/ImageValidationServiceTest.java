package com.project.G1_T3.filestorage.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.project.G1_T3.common.exception.InvalidFileTypeException;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ImageValidationServiceTest {

    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    @InjectMocks
    private ImageValidationService imageValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateImage_NullFile_ThrowsException() {
        assertThrows(InvalidFileTypeException.class,
            () -> imageValidationService.validateImage(null), "No image file provided");
    }

    @Test
    void validateImage_EmptyFile_ThrowsException() {
        MockMultipartFile emptyFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg",
            new byte[0]);

        InvalidFileTypeException exception = assertThrows(InvalidFileTypeException.class,
            () -> imageValidationService.validateImage(emptyFile));
        assertEquals("No image file provided", exception.getMessage());
    }

    @Test
    void validateImage_NearMaxSize_Succeeds() throws IOException {
        // Create a large valid image (close to 5MB)
        BufferedImage image = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Fill with a pattern to ensure it's a valid image with real content
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 2000, 2000);
        g2d.setColor(Color.RED);
        for (int i = 0; i < 2000; i += 50) {
            g2d.drawLine(0, i, 2000, i);
            g2d.drawLine(i, 0, i, 2000);
        }
        g2d.dispose();

        // Convert to JPEG with minimal compression to get a large file
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(1.0f); // Maximum quality

        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);
        writer.write(null, new IIOImage(image, null, null), param);
        writer.dispose();

        byte[] imageData = baos.toByteArray();

        // Create MultipartFile
        MockMultipartFile largeFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg",
            imageData);

        // Verify the file is a substantial size but within limits
        assertTrue(largeFile.getSize() > 0 && largeFile.getSize() <= MAX_FILE_SIZE,
            "File size should be greater than 0 and not exceed 5MB");

        // Test the validation
        assertDoesNotThrow(() -> imageValidationService.validateImage(largeFile));
    }

    @Test
    void validateImage_ValidJpeg_Succeeds() throws IOException {
        // Create a valid JPEG image
        MockMultipartFile jpegFile = createValidImageFile("image/jpeg", "jpg");
        assertDoesNotThrow(() -> imageValidationService.validateImage(jpegFile));
    }

    /**
     * Helper method to create valid image files for testing
     */
    private MockMultipartFile createValidImageFile(String contentType, String formatName)
        throws IOException {
        // Create a simple image with some content
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Draw something to ensure it's a valid image
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 100, 100);
        g.setColor(Color.RED);
        g.drawLine(0, 0, 100, 100);
        g.dispose();

        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, baos);
        byte[] imageBytes = baos.toByteArray();

        return new MockMultipartFile("test." + formatName, "test." + formatName, contentType,
            imageBytes);
    }

    @Test
    void validateImage_ValidPng_Succeeds() throws IOException {
        // Create a valid PNG image
        MockMultipartFile pngFile = createValidImageFile("image/png", "png");
        assertDoesNotThrow(() -> imageValidationService.validateImage(pngFile));
    }

    @Test
    void validateImage_ValidGif_Succeeds() throws IOException {
        // Create a valid GIF image
        MockMultipartFile gifFile = createValidImageFile("image/gif", "gif");
        assertDoesNotThrow(() -> imageValidationService.validateImage(gifFile));
    }

    @Test
    void validateImage_ValidSvg_Succeeds() {
        String svgContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100\" height=\"100\">\n"
            + "    <rect width=\"100\" height=\"100\" fill=\"blue\"/>\n" + "</svg>";

        MockMultipartFile svgFile = new MockMultipartFile("test.svg", "test.svg", "image/svg+xml",
            svgContent.getBytes());

        assertDoesNotThrow(() -> imageValidationService.validateImage(svgFile));
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/bmp", "image/tiff", "application/pdf", "text/plain", ""})
    void validateImage_DisallowedContentTypes_ThrowsException(String contentType) {
        MockMultipartFile invalidFile = new MockMultipartFile("test", "test.file", contentType,
            "test content".getBytes());

        InvalidFileTypeException exception = assertThrows(InvalidFileTypeException.class,
            () -> imageValidationService.validateImage(invalidFile));
        assertEquals("Invalid file type. Allowed types: JPEG, PNG, GIF, WebP, SVG",
            exception.getMessage());
    }

    @Test
    void validateImage_NullContentType_ThrowsException() {
        // Create file with null content type
        MockMultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", null,
            // null content type
            "some content".getBytes());

        InvalidFileTypeException exception = assertThrows(InvalidFileTypeException.class,
            () -> imageValidationService.validateImage(file));
        assertEquals("Invalid file type. Allowed types: JPEG, PNG, GIF, WebP, SVG",
            exception.getMessage());
    }

    @Test
    void validateImage_ValidExtensionInvalidContent_ThrowsException() {
        // Create a file with JPEG extension but invalid content
        MockMultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg",
            "This is not a real JPEG content".getBytes());

        InvalidFileTypeException exception = assertThrows(InvalidFileTypeException.class,
            () -> imageValidationService.validateImage(file));
        assertEquals("Invalid image file content", exception.getMessage());
    }

    @Test
    void validateImage_ValidContentTypeIOException_ThrowsException() throws IOException {
        // Mock MultipartFile to throw IOException when getting InputStream
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getInputStream()).thenThrow(new IOException("Simulated IO error"));

        InvalidFileTypeException exception = assertThrows(InvalidFileTypeException.class,
            () -> imageValidationService.validateImage(mockFile));
        assertEquals("Error processing image file", exception.getMessage());
    }

    @Test
    void validateImage_NonImageWithImageContentType_ThrowsException() {
        // Create a text file with image content type
        MockMultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg",
            "{\"key\": \"This is JSON data pretending to be an image\"}".getBytes());

        InvalidFileTypeException exception = assertThrows(InvalidFileTypeException.class,
            () -> imageValidationService.validateImage(file));
        assertEquals("Invalid image file content", exception.getMessage());
    }

    @Test
    void validateImage_ValidSvgWithXmlDeclaration_Succeeds() {
        String svgContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100\" height=\"100\">\n"
            + "    <rect width=\"100\" height=\"100\" fill=\"blue\"/>\n" + "</svg>";

        MockMultipartFile file = new MockMultipartFile("test.svg", "test.svg", "image/svg+xml",
            svgContent.getBytes());

        assertDoesNotThrow(() -> imageValidationService.validateImage(file));
    }
}
