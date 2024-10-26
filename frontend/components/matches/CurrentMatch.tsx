// components/matches/CurrentMatch.tsx
import React from 'react';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/ui/card';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import ProfilePlayerCard from '@/components/matches/ProfilePlayerCard';
import { PlayerProfile } from '@/types/player-profile';
import MatchMap from '@/components/matches/MatchMap';

interface CurrentMatchProps {
  opponentName: string;
  opponentProfile: PlayerProfile;
  meetingPoint: [number, number];
  playerLocation: [number, number];
}

/**
 * `CurrentMatch` component displays the current match details including the opponent's profile and the meeting point on a map.
 *
 * @component
 * @param {Object} props - The properties object.
 * @param {string} props.opponentName - The name of the opponent.
 * @param {Object} props.opponentProfile - The profile information of the opponent.
 * @param {Object} props.meetingPoint - The coordinates of the meeting point.
 * @param {Object} props.playerLocation - The coordinates of the player's current location.
 *
 * @returns {JSX.Element} A React component that renders the current match details.
 *
 * @example
 * // Example usage of CurrentMatch component
 * <CurrentMatch
 *   opponentName="John Doe"
 *   opponentProfile={{ avatar: 'url', rank: 'Gold' }}
 *   meetingPoint={{ lat: 1.3521, lng: 103.8198 }}
 *   playerLocation={{ lat: 1.3000, lng: 103.8000 }}
 * />
 */
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