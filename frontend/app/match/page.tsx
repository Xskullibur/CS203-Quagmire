'use client'

import React from 'react';
import { Card, CardHeader, CardDescription, CardContent, CardTitle } from '@/components/ui/card';

export default function Match() {
    return (
        <div className="flex flex-col items-center justify-center min-h-screen">
            <h2 className="text-2xl font-bold mb-4">Leaderboard</h2>
            <Card>
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
}