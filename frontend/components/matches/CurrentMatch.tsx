// components/matches/CurrentMatch.tsx
import React from 'react';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/ui/card';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import ProfilePlayerCard from '@/components/matches/ProfilePlayerCard';
import { PlayerProfile } from '@/types/player';
import MatchMap from '@/components/matches/MatchMap';

interface CurrentMatchProps {
  opponentName: string;
  opponentProfile: PlayerProfile;
  meetingPoint: [number, number];
  playerLocation: [number, number];
}

const CurrentMatch: React.FC<CurrentMatchProps> = ({
  opponentName,
  opponentProfile,
  meetingPoint,
  playerLocation,
}) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      <Card>
        <CardHeader>
          <CardTitle className="text-xl text-center">Current Match</CardTitle>
        </CardHeader>
        <CardContent>
          <Alert className="text-center mb-4">
            <AlertTitle>Match in Progress!</AlertTitle>
            <AlertDescription>
              You are currently in a match against {opponentName}.
            </AlertDescription>
          </Alert>
          <ProfilePlayerCard profile={opponentProfile} name={opponentName} />
        </CardContent>
      </Card>
      <Card>
        <CardHeader>
          <CardTitle className="text-xl text-center">Meeting Point</CardTitle>
        </CardHeader>
        <CardContent>
          <MatchMap meetingPoint={meetingPoint} playerLocation={playerLocation} />
        </CardContent>
      </Card>
    </div>
  );
};

export default CurrentMatch;