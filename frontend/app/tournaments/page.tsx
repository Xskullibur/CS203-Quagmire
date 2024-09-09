"use client";

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card';

interface Tournament {
    id: number;
    name: string;
    location: string;
    description: string;
    date: string; // Ensure this matches your backend format
}

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament`;

const TournamentPage: React.FC = () => {
    const [tournaments, setTournaments] = useState<Tournament[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        // Fetch tournaments from your API
        axios.get(`${API_URL}?page=0&size=10`)
            .then((response) => {
                setTournaments(response.data.content);
                setLoading(false);
            })
            .catch((error) => {
                console.error('Error fetching tournaments:', error);
                setError('Failed to load tournaments. Please try again.');
                setLoading(false);
            });
    }, []);

    return (
        <div className="flex flex-col items-center justify-center min-h-screen p-4">
            <h2 className="text-2xl font-bold mb-4">Tournaments</h2>

            {loading && <p className="text-lg text-gray-500">Loading...</p>}

            {error && <p className="text-lg text-red-500">Error: {error}</p>}

            {tournaments.length === 0 && !loading && !error && (
                <p className="text-lg text-gray-500">No tournaments available.</p>
            )}

            {tournaments.map(tournament => (
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
