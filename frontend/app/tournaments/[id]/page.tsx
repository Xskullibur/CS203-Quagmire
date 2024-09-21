// app/tournaments/[id]/page.tsx

"use client"; // Add this line to make this a client component

import React, { useEffect, useState } from 'react';
import axios from 'axios';

interface Tournament {
    name: string;
    startDate: string;
    endDate: string;
    description: string;
    deadline: string;
}

const TournamentDetails: React.FC<{ params: { id: string } }> = ({ params }) => {
    const { id } = params; // Get the tournament ID from the URL parameters
    const [tournament, setTournament] = useState<Tournament | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchTournamentDetails = async () => {
            if (!id) return; // Ensure ID is available

            setLoading(true);
            setError(null);

            try {
                const response = await axios.get(`${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament/${id}`);
                setTournament(response.data);
            } catch (error) {
                console.error('Error fetching tournament details:', error);
                setError('Failed to load tournament details. Please try again.');
            } finally {
                setLoading(false);
            }
        };

        fetchTournamentDetails();
    }, [id]);

    if (loading) return <p className="text-lg text-gray-500">Loading...</p>;
    if (error) return <p className="text-lg text-red-500">Error: {error}</p>;
    if (!tournament) return <p className="text-lg text-gray-500">Tournament not found.</p>;

    return (
        <div className="flex flex-col items-center min-h-screen pt-20">
            <h1 className="text-3xl font-bold text-center">{tournament.name}</h1>
            <p className="text-xl text-center">{`${new Date(tournament.startDate).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })} - ${new Date(tournament.endDate).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })}`}</p>
            <div className="mt-8 max-w-xl w-full">
                <h2 className="text-lg font-semibold">Description:</h2>
                <p className="text-base">{tournament.description}</p>
                <h2 className="mt-4 text-lg font-semibold">Registration Deadline:</h2>
                <p className="text-base">{new Date(tournament.deadline).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })}</p>
            </div>
        </div>
    );
};

export default TournamentDetails;
