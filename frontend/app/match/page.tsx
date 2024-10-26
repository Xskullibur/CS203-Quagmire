"use client";

import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import { Card, CardHeader, CardDescription, CardContent, CardTitle } from '@/components/ui/card';
import QueueManagement from '@/components/matches/QueueManagement';
import { useAuth } from '@/hooks/useAuth';
import withAuth from '@/hooks/withAuth';
import dynamic from 'next/dynamic';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import ProfilePlayerCard from '@/components/matches/ProfilePlayerCard';
import { PlayerProfile } from '@/types/player-profile';
import { AlertDialog, AlertDialogAction, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from '@/components/ui/alert-dialog';
import { Button } from '@/components/ui/button';
import { useGeolocation } from '@/hooks/useGeolocation'; // Import the useGeolocation hook

const MatchMap = dynamic(() => import('@/components/matches/MatchMap'), { ssr: false });

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

/**
 * Match component that handles the display and management of a player's active match.
 * It utilizes various hooks to fetch and display player and opponent profiles, 
 * as well as the meeting point for the match.
 * 
 * @component
 * @returns {JSX.Element} The rendered component.
 * 
 * 
 * @remarks
 * This component requires the `useAuth` and `useGeolocation` hooks to function properly.
 * It also makes API calls to fetch player and opponent profiles, as well as the current active match.
 * 
 * @hook
 * @function useAuth
 * @returns {object} The authenticated user object.
 * 
 * @hook
 * @function useGeolocation
 * @returns {object} The geolocation data including location, error, and loading state.
 * 
 * @state {string | null} playerId - The ID of the current player.
 * @state {boolean} matchFound - Indicates if a match has been found.
 * @state {string} opponentName - The name of the opponent.
 * @state {[number, number] | null} meetingPoint - The coordinates of the meeting point.
 * @state {PlayerProfile | null} opponentProfile - The profile of the opponent.
 * @state {any | null} activeMatch - The current active match data.
 * @state {PlayerProfile | null} playerProfile - The profile of the current player.
 * 
 * @function checkForActiveMatch
 * @async
 * @param {string} userId - The ID of the user to check for an active match.
 * @description Checks if there is an active match for the given user ID and updates the state accordingly.
 * 
 * @function fetchPlayerProfile
 * @async
 * @param {string} userId - The ID of the user to fetch the profile for.
 * @description Fetches the profile of the given user ID and updates the state.
 * 
 * @function fetchOpponentProfile
 * @async
 * @param {string} opponentId - The ID of the opponent to fetch the profile for.
 * @description Fetches the profile of the given opponent ID and updates the state.
 * 
 * @function handleMatchFound
 * @param {boolean} matchFound - Indicates if a match has been found.
 * @param {string} opponent - The name of the opponent.
 * @param {[number, number]} meeting - The coordinates of the meeting point.
 * @param {PlayerProfile} profile - The profile of the opponent.
 * @description Handles the event when a match is found and updates the state accordingly.
 * 
 * @returns {JSX.Element} The rendered component.
 */
const Match: React.FC = () => {
    const { user } = useAuth();
    const [playerId, setPlayerId] = useState<string | null>(null);
    const [matchFound, setMatchFound] = useState(false);
    const [opponentName, setOpponentName] = useState('');
    const [meetingPoint, setMeetingPoint] = useState<[number, number] | null>(null);
    const [opponentProfile, setOpponentProfile] = useState<PlayerProfile | null>(null);
    const [activeMatch, setActiveMatch] = useState<any | null>(null);
    const [playerProfile, setPlayerProfile] = useState<PlayerProfile | null>(null);

    // Use the useGeolocation hook
    const { location, error: locationError, loading: locationLoading } = useGeolocation();

    useEffect(() => {
        if (user?.userId) {
            setPlayerId(user.userId);
            checkForActiveMatch(user.userId);
            fetchPlayerProfile(user.userId);
        }
    }, [user]);

    const checkForActiveMatch = async (userId: string) => {
        try {
            console.log("Checking for active match for user:", userId);
            const response = await axios.get(`${API_URL}/matches/current/${userId}`);
            console.log("Active match API response:", response.data);

            if (response.data) {
                const match = response.data;
                setActiveMatch(match);
                setMatchFound(true);
                const isPlayer1 = match.player1Id === userId;
                const opponentId = isPlayer1 ? match.player2Id : match.player1Id;
                console.log("Match found. Opponent ID:", opponentId, "Opponent Name:", opponentName);
                setMeetingPoint([match.meetingLatitude, match.meetingLongitude]);
                await fetchOpponentProfile(opponentId);
            } else {
                console.log("No active match found");
                setActiveMatch(null);
                setMatchFound(false);
                setOpponentProfile(null);
            }
        } catch (error) {
            console.error("Error checking for active match:", error);
            setActiveMatch(null);
            setMatchFound(false);
            setOpponentProfile(null);
        }
    };

    const fetchPlayerProfile = async (userId: string) => {
        try {
            const response = await axios.get(`${API_URL}/profile/${userId}`);
            setPlayerProfile(response.data);
        } catch (error) {
            console.error("Error fetching player profile:", error);
        }
    };

    const fetchOpponentProfile = async (opponentId: string) => {
        try {
            console.log("Fetching profile for opponent ID:", opponentId);
            const response = await axios.get(`${API_URL}/profile/player/${opponentId}`);
            console.log("Opponent profile response:", response.data);
            setOpponentName(response.data.firstName);
            setOpponentProfile(response.data);
        } catch (error) {
            console.error("Error fetching opponent profile:", error);
            setOpponentProfile(null);
        }
    };

    const handleMatchFound = useCallback((matchFound: boolean, opponent: string, meeting: [number, number], profile: PlayerProfile) => {
        setMatchFound(matchFound);
        setOpponentName(opponent);
        setMeetingPoint(meeting);
        setOpponentProfile(profile);
    }, []);

    if (locationLoading || !playerId) {
        return <div className="flex justify-center items-center min-h-screen">Loading...</div>;
    }

    if (locationError || !location) {
        return (
            <div className="flex flex-col items-center justify-center min-h-screen p-4">
                <AlertDialog open={true}>
                    <AlertDialogContent>
                        <AlertDialogHeader>
                            <AlertDialogTitle>Precise Location Access Required</AlertDialogTitle>
                            <AlertDialogDescription>
                                To match you with nearby players and provide accurate directions, we need access to your precise location.
                                {locationError && (
                                    <div className="text-red-500 mt-2">
                                        Error: {locationError}
                                    </div>
                                )}
                                <div className="mt-2">
                                    Please ensure that:
                                    <ul className="list-disc list-inside mt-1">
                                        <li>You have allowed location access for this site</li>
                                        <li>Your device&apos;s location services are turned on</li>
                                        <li>You have a stable internet connection</li>
                                    </ul>
                                </div>
                            </AlertDialogDescription>
                        </AlertDialogHeader>
                        <AlertDialogFooter>
                            <AlertDialogAction asChild>
                                <Button onClick={() => window.location.reload()}>
                                    Retry Location Access
                                </Button>
                            </AlertDialogAction>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialog>
            </div>
        );
    }

    return (
        <div className='container mx-auto my-32'>
            <div className="flex flex-col items-center justify-center p-4">
                <h2 className="text-xl font-mono font-bold text-center mb-8 text-zinc-400">Run the 1s</h2>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 w-full max-w-4xl">
                    {/* Player Profile Card */}
                    {playerProfile && (
                        <Card className="col-span-1">
                            <CardHeader>
                                <CardTitle className="text-xl text-center">Your Profile</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <ProfilePlayerCard profile={playerProfile} name={user?.username || ''} />
                            </CardContent>
                        </Card>
                    )}

                    {(activeMatch || matchFound) && opponentProfile ? (
                        <Card className="col-span-1">
                            <CardHeader>
                                <CardTitle className="text-xl text-center">Opponent</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <ProfilePlayerCard profile={opponentProfile} name={opponentName} />
                            </CardContent>
                        </Card>
                    ) : (activeMatch || matchFound) ? (
                        <Card className="col-span-1">
                            <CardHeader>
                                <CardTitle className="text-xl text-center">Opponent</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <p>Loading opponent profile...</p>
                                <p>Active Match: {JSON.stringify(activeMatch)}</p>
                                <p>Match Found: {matchFound.toString()}</p>
                                <p>Opponent Name: {opponentName}</p>
                            </CardContent>
                        </Card>
                    ) : null}

                    {(activeMatch || matchFound) && location && meetingPoint && (
                        <Card className="col-span-1">
                            <CardHeader>
                                <CardTitle className="text-xl text-center">Meeting Point</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <MatchMap
                                    meetingPoint={meetingPoint}
                                    playerLocation={[location.latitude, location.longitude]}
                                />
                            </CardContent>
                        </Card>
                    )}
                    {/* Match Queue Card */}
                    <Card className="col-span-1">
                        <CardHeader>
                            <CardTitle className="text-2xl text-center">Match Queue</CardTitle>
                            <CardDescription className='text-center'>Time to Box!</CardDescription>
                        </CardHeader>
                        <CardContent>
                            {activeMatch ? (
                                <Alert className='text-center'>
                                    <AlertTitle>Active Match in Progress</AlertTitle>
                                    <AlertDescription>
                                        You have an active match against {opponentName}. Please complete it before joining a new queue.
                                    </AlertDescription>
                                </Alert>
                            ) : matchFound ? (
                                <Alert className='text-center'>
                                    <AlertTitle>Match Found!</AlertTitle>
                                    <AlertDescription>
                                        Prepare for your arm wrestling match against {opponentName}.
                                    </AlertDescription>
                                </Alert>
                            ) : (
                                <QueueManagement playerId={playerId} onMatchFound={handleMatchFound} />
                            )}
                            {(activeMatch || matchFound) && (
                                <Alert className='text-center mt-4'>
                                    <AlertTitle>Forfeit</AlertTitle>
                                    <AlertDescription>
                                        Your opponent is a no-show? Click the button below to forfeit the match.
                                        <br />
                                        <Button variant='destructive' className='mt-4' onClick={() => console.log('Forfeit match')}>
                                            Forfeit Match
                                        </Button>
                                    </AlertDescription>
                                </Alert>
                            )}
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>

    );
};

export default withAuth(Match);