"use client";

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"

interface Tournament {
    id: number;
    name: string;
    location: string;
    description: string;
    date: string;
}

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament`;

const TournamentHeader: React.FC = () => (
    <header className="bg-background/10 w-full py-4 text-center text-white">
        <h1 className="text-3xl font-bold">Tournaments</h1>
    </header>
);

const TournamentTabs: React.FC<{ currentTab: 'upcoming' | 'past'; setCurrentTab: (tab: 'upcoming' | 'past') => void }> = ({ currentTab, setCurrentTab }) => (
    // <div className="flex justify-center bg-background/10 w-full py-2">
    //     <button
    //         onClick={() => setCurrentTab('upcoming')}
    //         className={`px-4 py-2 mx-2 ${currentTab === 'upcoming' ? 'bg-white text-black' : 'bg-gray-500 text-black'}`}
    //     >
    //         Upcoming Tournaments
    //     </button>
    //     <button
    //         onClick={() => setCurrentTab('past')}
    //         className={`px-4 py-2 mx-2 ${currentTab === 'past' ? 'bg-white text-black' : 'bg-gray-500 text-black'}`}
    //     >
    //         Past Tournaments
    //     </button>
    // </div>

    <div>
        <Tabs defaultValue={currentTab} className="w-full max-w-md">
            <TabsList className="flex justify-center w-full">
                <TabsTrigger value="account" onClick={() => setCurrentTab('upcoming')}>
                    Upcoming
                </TabsTrigger>
                <TabsTrigger value="password" onClick={() => setCurrentTab('past')}>
                    Past
                </TabsTrigger>
            </TabsList>
            </Tabs>
    </div>
);

const TournamentPage: React.FC = () => {
    const [tournaments, setTournaments] = useState<Tournament[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [currentTab, setCurrentTab] = useState<'upcoming' | 'past'>('upcoming');

    useEffect(() => {
        const fetchTournaments = async () => {
            setLoading(true);
            setError(null);

            try {
                const endpoint = currentTab === 'upcoming'
                    ? `${API_URL}/upcoming?page=0&size=10`
                    : `${API_URL}/past?page=0&size=10`;

                const response = await axios.get(endpoint);
                setTournaments(response.data.content);
            } catch (error) {
                console.error('Error fetching tournaments:', error);
                setError('Failed to load tournaments. Please try again.');
                setTournaments([]);
            } finally {
                setLoading(false);
            }
        };

        fetchTournaments();
    }, [currentTab]);

    return (
        <div className="flex flex-col items-center min-h-screen pt-20"> {/* Add padding to account for MenuBar */}
            {/* Persistent Header */}
            <TournamentHeader />

            {/* Persistent Tabs */}
            <TournamentTabs currentTab={currentTab} setCurrentTab={setCurrentTab} />

            {/* Display loading, error, or tournaments based on state */}
            <div className="flex flex-col items-center w-full p-4">
                {loading && <p className="text-lg text-gray-500">Loading...</p>}

                {error && <p className="text-lg text-red-500">Error: {error}</p>}

                {!loading && !error && tournaments.length === 0 && (
                    <p className="text-lg text-gray-500">
                        {currentTab === 'upcoming' ? 'No upcoming tournaments available.' : 'No past tournaments available.'}
                    </p>
                )}

                {/* Conditionally render tournaments */}
                {!loading && !error && tournaments.map(tournament => (
                    <Card key={tournament.id} className="mb-4 w-full max-w-md">
                        <CardHeader>
                            <CardTitle className="text-xl">{tournament.name}</CardTitle>
                            <CardDescription>{tournament.description}</CardDescription>
                        </CardHeader>
                        <CardContent>
                            <p className="text-lg">Location: {tournament.location}</p>
                            <p className="text-lg">Date: {new Date(tournament.date).toLocaleDateString()}</p>
                        </CardContent>
                    </Card>
                ))}
            </div>
        </div>
    );
};

export default TournamentPage;
