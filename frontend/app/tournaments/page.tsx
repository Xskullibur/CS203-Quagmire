'use client'

import React, { useEffect, useState } from 'react';
import { Card, CardHeader, CardDescription, CardContent, CardTitle } from '@/components/ui/card';

// Define the Tournament interface
interface Tournament {
    id: number;
    name: string;
    location: string;
    description: string;
    date: string;
}

export default function Tournament() {
    // State to hold the list of tournaments
    const [tournaments, setTournaments] = useState<Tournament[]>([]);

    useEffect(() => {
        // Fetch tournaments from your API
        fetch('/api/tournaments?page=0&size=10')
            .then((response) => response.json())
            .then((data) => setTournaments(data.content))
            .catch((error) => console.error('Error fetching tournaments:', error));
    }, []);

    return (
        <div className="flex flex-col items-center justify-center min-h-screen">
            <h2 className="text-2xl font-bold mb-4">Tournaments</h2>
            
            {tournaments.length > 0 ? (
                tournaments.map((tournament) => (
                    <Card key={tournament.id} className="mb-4 w-full max-w-md">
                        <CardHeader>
                            <CardTitle className="text-2xl">{tournament.name}</CardTitle>
                            <CardDescription>{tournament.location}</CardDescription>
                        </CardHeader>
                        <CardContent>
                            <p>{tournament.description}</p>
                            <p className="mt-2 text-sm">Date: {new Date(tournament.date).toLocaleDateString()}</p>
                        </CardContent>
                    </Card>
                ))
            ) : (
                <p>No tournaments available</p>
            )}
        </div>
    );
}