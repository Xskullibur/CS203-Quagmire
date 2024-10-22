// player-profile.ts

export interface PlayerProfile {
    profileId: string; // UUID as string
    username: string;
    firstName: string;
    lastName: string;
    dateOfBirth: string; // LocalDate as ISO string
    country: string;
    bio: string;
    currentRating: number; // Float as number
}