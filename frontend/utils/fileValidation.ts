export const validateImageFile = (file: File): { isValid: boolean; error?: string } => {
    // List of allowed MIME types for images
    const allowedTypes = [
        'image/jpeg',
        'image/png',
        'image/gif',
        'image/webp',
        'image/svg+xml'
    ];

    // Maximum file size (5MB)
    const maxSize = 5 * 1024 * 1024; // 5MB in bytes

    if (!allowedTypes.includes(file.type)) {
        return {
            isValid: false,
            error: 'File type not supported. Please upload a JPEG, PNG, GIF, WebP, or SVG image.'
        };
    }

    if (file.size > maxSize) {
        return {
            isValid: false,
            error: 'File size too large. Maximum size is 5MB.'
        };
    }

    return { isValid: true };
};