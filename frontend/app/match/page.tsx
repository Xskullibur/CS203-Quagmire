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
import { PlayerProfile } from '@/types/player';
import { AlertDialog, AlertDialogAction, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from '@/components/ui/alert-dialog';

const MatchMap = dynamic(() => import('@/components/matches/MatchMap'), { ssr: false });

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

const Match: React.FC = () => {
    const { user } = useAuth();
    const [playerId, setPlayerId] = useState<string | null>(null);
    const [matchFound, setMatchFound] = useState(false);
    const [opponentName, setOpponentName] = useState('');
    const [meetingPoint, setMeetingPoint] = useState<[number, number] | null>(null);
    const [playerLocation, setPlayerLocation] = useState<[number, number] | null>(null);
    const [opponentProfile, setOpponentProfile] = useState<PlayerProfile | null>(null);
    const [activeMatch, setActiveMatch] = useState<any | null>(null);
    const [loading, setLoading] = useState(true);
    const [showLocationAlert, setShowLocationAlert] = useState(false);
    const [playerProfile, setPlayerProfile] = useState<PlayerProfile | null>(null);

    useEffect(() => {
        if (user?.userId) {
            setPlayerId(user.userId);
            checkForActiveMatch(user.userId);
            fetchPlayerProfile(user.userId);
        }
    }, [user]);

    const fetchPlayerProfile = async (userId: string) => {
        try {
            const response = await axios.get(`${API_URL}/profile/${userId}`);
            setPlayerProfile(response.data);
        } catch (error) {
            console.error("Error fetching player profile:", error);
        }
    };

    const checkForActiveMatch = async (userId: string) => {
        try {
            setLoading(true);
            const response = await axios.get(`${API_URL}/matches/current/${userId}`);
            if (response.data) {
                setActiveMatch(response.data);
                setMatchFound(true);
                setOpponentName(response.data.player1Id === userId ? response.data.player2Name : response.data.player1Name);
                setMeetingPoint([response.data.meetingLatitude, response.data.meetingLongitude]);
                // Fetch opponent profile
                const opponentId = response.data.player1Id === userId ? response.data.player2Id : response.data.player1Id;
                const opponentProfileResponse = await axios.get(`${API_URL}/profile/${opponentId}`);
                setOpponentProfile(opponentProfileResponse.data);
            }
        } catch (error) {
            console.error("Error checking for active match:", error);
        } finally {
            setLoading(false);
        }
    };

    const getLocation = useCallback(() => {
        if (typeof window !== 'undefined' && navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    setPlayerLocation([position.coords.latitude, position.coords.longitude]);
                    setShowLocationAlert(false);
                },
                (error) => {
                    console.error("Error getting location:", error);
                    setShowLocationAlert(true);
                },
                { enableHighAccuracy: true, timeout: 5000, maximumAge: 0 }
            );
        } else {
            setShowLocationAlert(true);
        }
    }, []);

    useEffect(() => {
        getLocation();
        const intervalId = setInterval(getLocation, 10000); // Check location every 10 seconds
        return () => clearInterval(intervalId);
    }, [getLocation]);

    const handleMatchFound = useCallback((opponent: string, meeting: [number, number], profile: PlayerProfile) => {
        setMatchFound(true);
        setOpponentName(opponent);
        setMeetingPoint(meeting);
        setOpponentProfile(profile);
    }, []);

    const handleEnableLocation = useCallback(() => {
        getLocation();
    }, [getLocation]);

    if (loading) {
        return <div className="flex justify-center items-center min-h-screen">Loading...</div>;
    }

    if (!playerId) {
        return <div className="flex justify-center items-center min-h-screen">Loading...</div>;
    }

    return (
        <div className="flex flex-col items-center justify-center min-h-screen p-4">
            <h2 className="text-3xl font-bold text-center mb-8 text-zinc-400">Run the 1s</h2>

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
                    </CardContent>
                </Card>

                {/* Opponent Profile Card */}
                {(matchFound || activeMatch) && opponentProfile && (
                    <Card className="col-span-1">
                        <CardHeader>
                            <CardTitle className="text-xl text-center">Opponent</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <ProfilePlayerCard profile={opponentProfile} name={opponentName} />
                        </CardContent>
                    </Card>
                )}

                {/* Meeting Point Map Card */}
                {(matchFound || activeMatch) && playerLocation && meetingPoint && (
                    <Card className="col-span-1">
                        <CardHeader>
                            <CardTitle className="text-xl text-center">Meeting Point</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <MatchMap
                                meetingPoint={meetingPoint}
                                playerLocation={playerLocation}
                            />
                        </CardContent>
                    </Card>
                )}
            </div>

            <AlertDialog open={showLocationAlert} onOpenChange={setShowLocationAlert}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Precise Location Access Required</AlertDialogTitle>
                        <AlertDialogDescription>
                            To match you with nearby players and provide accurate directions, we need access to your precise location. Please enable precise location access in your browser settings.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogAction onClick={handleEnableLocation}>Try Again</AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    );
};

export default withAuth(Match);