import { ErrorCode, ErrorMapping, HttpStatusCode } from '../types/errors';

export const httpStatusMappings: Record<HttpStatusCode, ErrorMapping> = {
    [HttpStatusCode.BAD_REQUEST]: {
        title: 'Bad Request',
        message: 'The request was invalid. Please check your input and try again.'
    },
    [HttpStatusCode.UNAUTHORIZED]: {
        title: 'Unauthorized',
        message: 'Your session has expired. Please log in again.'
    },
    [HttpStatusCode.FORBIDDEN]: {
        title: 'Forbidden',
        message: 'You don\'t have permission to perform this action.'
    },
    [HttpStatusCode.NOT_FOUND]: {
        title: 'Not Found',
        message: 'The requested resource was not found.'
    },
    [HttpStatusCode.INTERNAL_SERVER_ERROR]: {
        title: 'Internal Server Error',
        message: 'An unexpected error occurred. Please try again later.'
    }
};

export const errorCodeMappings: Record<ErrorCode, ErrorMapping> = {
    // Authentication & Authorization
    [ErrorCode.AUTHENTICATION_FAILED]: {
        title: 'Authentication Failed',
        message: 'Invalid username or password.'
    },
    [ErrorCode.INVALID_CREDENTIALS]: {
        title: 'Authentication Failed',
        message: 'Invalid username or password.'
    },
    [ErrorCode.SESSION_EXPIRED]: {
        title: 'Session Expired',
        message: 'Your session has expired. Please log in again.'
    },
    [ErrorCode.INSUFFICIENT_PERMISSIONS]: {
        title: 'Access Denied',
        message: 'You don\'t have permission to perform this action.'
    },
    [ErrorCode.ACCOUNT_LOCKED]: {
        title: 'Account Locked',
        message: 'Your account has been locked. Please contact support.'
    },

    // Registration
    [ErrorCode.USERNAME_TAKEN]: {
        title: 'Username Not Available',
        message: 'This username is already taken. Please choose another username.'
    },
    [ErrorCode.EMAIL_IN_USE]: {
        title: 'Email Already Registered',
        message: 'An account with this email already exists.'
    },
    [ErrorCode.REGISTRATION_FAILED]: {
        title: 'Registration Failed',
        message: 'Unable to complete registration. Please try again.'
    },
    [ErrorCode.EMAIL_VERIFICATION_FAILED]: {
        title: 'Verification Failed',
        message: 'Failed to send verification email. Please try again.'
    },

    // User Operations
    [ErrorCode.USER_FETCH_ERROR]: {
        title: 'User Data Error',
        message: 'Failed to retrieve user information.'
    },
    [ErrorCode.USER_CREATE_ERROR]: {
        title: 'User Creation Failed',
        message: 'Failed to create new user.'
    },
    [ErrorCode.USER_UPDATE_ERROR]: {
        title: 'User Update Failed',
        message: 'Failed to update user information.'
    },
    [ErrorCode.USER_DELETE_ERROR]: {
        title: 'User Deletion Failed',
        message: 'Failed to delete user.'
    },

    // Form & Validation
    [ErrorCode.VALIDATION_ERROR]: {
        title: 'Validation Error',
        message: 'Please check your input and try again.'
    },
    [ErrorCode.INVALID_INPUT]: {
        title: 'Invalid Input',
        message: 'One or more fields contain invalid data.'
    },
    [ErrorCode.REQUIRED_FIELD_MISSING]: {
        title: 'Missing Information',
        message: 'Please fill in all required fields.'
    },
    // Validation mappings
    [ErrorCode.INVALID_USERNAME]: {
        title: 'Invalid Username',
        message: 'Username must be between 3 and 50 characters.'
    },
    [ErrorCode.INVALID_EMAIL]: {
        title: 'Invalid Email',
        message: 'Please enter a valid email address.'
    },
    [ErrorCode.INVALID_PASSWORD]: {
        title: 'Invalid Password',
        message: 'Password must be at least 7 characters long.'
    },
    
    // Network & System
    [ErrorCode.NETWORK_ERROR]: {
        title: 'Network Error',
        message: 'Please check your internet connection and try again.'
    },
    [ErrorCode.API_ERROR]: {
        title: 'Service Error',
        message: 'The service is currently unavailable. Please try again later.'
    },
    [ErrorCode.TIMEOUT_ERROR]: {
        title: 'Request Timeout',
        message: 'The request took too long to complete. Please try again.'
    },
    [ErrorCode.EMAIL_SERVICE_ERROR]: {
        title: 'Email Service Error',
        message: 'Failed to send email. Please try again later.'
    },

    // Data Operations
    [ErrorCode.DATA_NOT_FOUND]: {
        title: 'Data Not Found',
        message: 'The requested information could not be found.'
    },
    [ErrorCode.DUPLICATE_ENTRY]: {
        title: 'Duplicate Entry',
        message: 'This entry already exists in the system.'
    },

    // File Operations
    [ErrorCode.FILE_UPLOAD_ERROR]: {
        title: 'Upload Failed',
        message: 'Failed to upload file. Please try again.'
    },
    [ErrorCode.FILE_TOO_LARGE]: {
        title: 'File Too Large',
        message: 'The selected file exceeds the maximum size limit.'
    },
    [ErrorCode.INVALID_FILE_TYPE]: {
        title: 'Invalid File Type',
        message: 'The selected file type is not supported.'
    },

    // Default
    [ErrorCode.UNKNOWN_ERROR]: {
        title: 'Error',
        message: 'An unexpected error occurred. Please try again later.'
    }
};