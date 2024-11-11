// utils/imageUtils.ts
export const VALID_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
export const MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

export interface ImageValidationResult {
    isValid: boolean;
    error?: string;
}

export const validateImageFile = (file: File): ImageValidationResult => {
    if (!file) {
        return { isValid: false, error: 'No file provided' };
    }

    if (!VALID_IMAGE_TYPES.includes(file.type)) {
        return {
            isValid: false,
            error: `Invalid file type. Allowed types: ${VALID_IMAGE_TYPES.join(', ')}`
        };
    }

    if (file.size > MAX_IMAGE_SIZE) {
        return {
            isValid: false,
            error: `File size must be less than ${MAX_IMAGE_SIZE / (1024 * 1024)}MB`
        };
    }

    return { isValid: true };
};

export const fetchDefaultImage = async (
    defaultImageUrl: string,
    filename: string
): Promise<File> => {
    try {
        const response = await fetch(defaultImageUrl);
        const blob = await response.blob();
        return new File([blob], filename, { type: blob.type });
    } catch (error) {
        throw new Error(`Failed to fetch default image: ${error}`);
    }
};

export const cleanupObjectURL = (url: string | null) => {
    if (url?.startsWith('blob:')) {
        URL.revokeObjectURL(url);
    }
};

export const addCacheBustingParameter = (url: string): string => {
    const separator = url.includes('?') ? '&' : '?';
    return `${url}${separator}v=${Date.now()}`;
};