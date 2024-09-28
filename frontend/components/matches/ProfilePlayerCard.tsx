// components/matches/Opponentprofile.tsx
import React from 'react';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/ui/card';
import { PlayerProfile } from '@/types/player';

interface OpponentprofileProps {
    profile: PlayerProfile | null;
    name: string;
}

const profilePlayerCard: React.FC<OpponentprofileProps> = ({ profile, name }) => {
    if (!profile) {
        return null;
    }

    return (
        <Card className="w-full max-w-md mt-4">
            <CardHeader>
                <CardTitle className="text-xl text-center">profile</CardTitle>
            </CardHeader>
            <CardContent>
                <p><strong>Username:</strong> {name}</p>
                {profile.firstName && <p><strong>First name:</strong> {profile.firstName}</p>}
                {profile.lastName && <p><strong>Last name:</strong> {profile.lastName}</p>}
                {profile.dateOfBirth && <p><strong>Date of Birth:</strong> {profile.dateOfBirth}</p>}
                {profile.country && <p><strong>Country:</strong> {profile.country}</p>}
                {profile.bio && <p><strong>Bio:</strong> {profile.bio}</p>}
                {profile.currentRating !== null && <p><strong>Current Rating:</strong> {profile.currentRating}</p>}
            </CardContent>
        </Card>
    );
};

export default profilePlayerCard;