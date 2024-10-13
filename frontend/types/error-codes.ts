export const ErrorCodes = {
    INVALID_TOKEN: 'INVALID_TOKEN',
    TOKEN_EXPIRED: 'TOKEN_EXPIRED'
} as const;

export type ErrorCode = keyof typeof ErrorCodes;