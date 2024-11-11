// components/matches/Opponentprofile.tsx
import React from 'react';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/ui/card';
import { PlayerProfile } from '@/types/player-profile';

// Define the props for the Opponentprofile component
interface OpponentprofileProps {
    profile: PlayerProfile | null; // The player's profile data, or null if not available
    name: string; // The player's username
}

/**
 * ProfilePlayerCard component displays the profile information of a player.
 *
 * @param {OpponentprofileProps} props - The props for the component.
 * @param {PlayerProfile | null} props.profile - The player's profile data.
 * @param {string} props.name - The player's username.
 * @returns {JSX.Element | null} The rendered component or null if no profile is provided.
 */
const ProfilePlayerCard: React.FC<OpponentprofileProps> = ({ profile, name }) => {
    // If no profile is provided, return null to render nothing
    if (!profile) {
        return null;
    }

    // Render the player's profile information inside a card
    return (
        <Card className="w-full max-w-md mt-4">
            <CardHeader>
                <CardTitle className="text-xl text-center">Profile</CardTitle>
            </CardHeader>
            <CardContent>
                <p><strong>Username:</strong> {name}</p>
                {profile.firstName && <p><strong>First name:</strong> {profile.firstName}</p>}
                {profile.lastName && <p><strong>Last name:</strong> {profile.lastName}</p>}
                {profile.dateOfBirth && <p><strong>Date of Birth:</strong> {profile.dateOfBirth}</p>}
                {profile.country && <p><strong>Country:</strong> {profile.country}</p>}
                {profile.bio && <p><strong>Bio:</strong> {profile.bio}</p>}
                {/* {profile.currentRating !== null && <p><strong>Current Rating:</strong> {profile.currentRating}</p>} */}
                {profile.glickoRating !== null && <p><strong>Glicko Rating:</strong> {profile.glickoRating}</p>}
            </CardContent>
        </Card>
    );
};

export default ProfilePlayerCard;
