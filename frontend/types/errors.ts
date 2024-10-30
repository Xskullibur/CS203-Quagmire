export interface ErrorResponse {
    status: number;
    message: string;
    code?: ErrorCode;
}

export interface ErrorMapping {
    title: string;
    message: string;
}

export enum HttpStatusCode {
    BAD_REQUEST = 400,
    UNAUTHORIZED = 401,
    FORBIDDEN = 403,
    NOT_FOUND = 404,
    INTERNAL_SERVER_ERROR = 500
}

export enum ErrorCode {
    // Authentication & Authorization
    INVALID_CREDENTIALS = 'INVALID_CREDENTIALS',
    SESSION_EXPIRED = 'SESSION_EXPIRED',
    INSUFFICIENT_PERMISSIONS = 'INSUFFICIENT_PERMISSIONS',
    ACCOUNT_LOCKED = 'ACCOUNT_LOCKED',

    // User Operations
    USER_FETCH_ERROR = 'USER_FETCH_ERROR',
    USER_CREATE_ERROR = 'USER_CREATE_ERROR',
    USER_UPDATE_ERROR = 'USER_UPDATE_ERROR',
    USER_DELETE_ERROR = 'USER_DELETE_ERROR',

    // Form & Validation
    VALIDATION_ERROR = 'VALIDATION_ERROR',
    INVALID_INPUT = 'INVALID_INPUT',
    REQUIRED_FIELD_MISSING = 'REQUIRED_FIELD_MISSING',

    // Network & System
    NETWORK_ERROR = 'NETWORK_ERROR',
    API_ERROR = 'API_ERROR',
    TIMEOUT_ERROR = 'TIMEOUT_ERROR',

    // Data Operations
    DATA_NOT_FOUND = 'DATA_NOT_FOUND',
    DUPLICATE_ENTRY = 'DUPLICATE_ENTRY',

    // File Operations
    FILE_UPLOAD_ERROR = 'FILE_UPLOAD_ERROR',
    FILE_TOO_LARGE = 'FILE_TOO_LARGE',
    INVALID_FILE_TYPE = 'INVALID_FILE_TYPE',

    // Default
    UNKNOWN_ERROR = 'UNKNOWN_ERROR'
}