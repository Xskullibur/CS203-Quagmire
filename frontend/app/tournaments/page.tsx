"use client";

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card';

interface Tournament {
    id: number;
    name: string;
    location: string;
    description: string;
    date: string;
}

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament`;

const TournamentPage: React.FC = () => {
    const [tournaments, setTournaments] = useState<Tournament[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [currentTab, setCurrentTab] = useState<'upcoming' | 'past'>('upcoming');

    useEffect(() => {
        // Function to fetch tournaments based on the selected tab
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
        <div className="flex flex-col items-center justify-center min-h-screen p-4">
            <h2 className="text-2xl font-bold mb-4">Tournaments</h2>

            {/* Tabs for selecting "Upcoming" or "Past" tournaments */}
            <div className="mb-4">
                <button
                    onClick={() => setCurrentTab('upcoming')}
                    className={`px-4 py-2 ${currentTab === 'upcoming' ? 'bg-blue-500 text-white' : 'bg-gray-300 text-black'}`}
                >
                    Upcoming Tournaments
                </button>
                <button
                    onClick={() => setCurrentTab('past')}
                    className={`px-4 py-2 ml-2 ${currentTab === 'past' ? 'bg-blue-500 text-white' : 'bg-gray-300 text-black'}`}
                >
                    Past Tournaments
                </button>
            </div>

            {/* Display loading, error, or tournaments based on state */}
            {loading && <p className="text-lg text-gray-500">Loading...</p>}

            {error && <p className="text-lg text-red-500">Error: {error}</p>}

            {!loading && !error && currentTab === 'upcoming' && tournaments.length === 0 && (
                <p className="text-lg text-gray-500">No upcoming tournaments available.</p>
            )}

            {!loading && !error && currentTab === 'past' && tournaments.length === 0 && (
                <p className="text-lg text-gray-500">No past tournaments available.</p>
            )}

            {/* Conditionally render either upcoming or past tournaments */}
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
    );
};

export default TournamentPage;
