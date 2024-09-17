"use client";

import React, { useEffect, useState } from 'react';
import { Card, CardHeader, CardDescription, CardContent, CardTitle } from '@/components/ui/card';
import QueueManagement from '@/components/matches/QueueManagement';
import { useAuth } from '@/hooks/useAuth';
import withAuth from '@/hooks/withAuth';

const Match: React.FC = () => {
    const { user } = useAuth();
    const [playerId, setPlayerId] = useState<string | null>(null);

    useEffect(() => {
        if (user?.userId) {
            setPlayerId(user.userId);
        }
    }, [user]);

    if (!playerId) {
        return <div>Loading...</div>;
    }

    return (
        <div className="flex flex-col items-center justify-center min-h-screen">
            <h2 className="text-2xl font-bold mb-4">Run the 1s</h2>
            <Card className="w-full max-w-md mb-4">
                <CardHeader>
                    <CardTitle className="text-2xl">Match Queue</CardTitle>
                    <CardDescription>Time to Box!</CardDescription>
                </CardHeader>
                <CardContent>
                    <QueueManagement playerId={playerId} />
                </CardContent>
            </Card>

            <Card className="w-full max-w-md">
                <CardHeader>
                    <CardTitle className="text-2xl">Event Highlights</CardTitle>
                    <CardDescription>What makes our tournament special</CardDescription>
                </CardHeader>
                <CardContent>
                    <ul className="list-disc list-inside space-y-2">
                        <li>International competitors from over 30 countries</li>
                        <li>Live streaming of all matches</li>
                        <li>Interactive fan experience with real-time voting</li>
                        <li>Professional referees and state-of-the-art equipment</li>
                    </ul>
                </CardContent>
            </Card>
        </div>
    );
};

export default withAuth(Match);