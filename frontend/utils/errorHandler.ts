import { AxiosError } from 'axios';
import { httpStatusMappings, errorCodeMappings } from './errorMappings';
import { ErrorResponse, ErrorMapping, ErrorCode, HttpStatusCode } from '../types/errors';

export class ErrorHandler {
    static getErrorMapping(error: AxiosError): ErrorMapping {

        if (error instanceof AxiosError) {
            const status = error.response?.status as HttpStatusCode;
            const errorCode = (error.response?.data as ErrorResponse)?.code;

            // First try to match custom error code
            if (errorCode && errorCode in ErrorCode) {
                return errorCodeMappings[errorCode];
            }

            // Then try to match HTTP status
            if (status && status in HttpStatusCode) {
                return httpStatusMappings[status];
            }

            // Check for network errors
            if (error.message === 'Network Error') {
                return errorCodeMappings[ErrorCode.NETWORK_ERROR];
            }
        }

        // Default error
        return errorCodeMappings[ErrorCode.UNKNOWN_ERROR];
    }

    static handleError(error: AxiosError) {
        console.error('Error details:', error);
        return this.getErrorMapping(error);
    }
}