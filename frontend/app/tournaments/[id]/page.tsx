"use client";
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Button } from "@/components/ui/button";
import axiosInstance from '@/lib/axios';

interface Tournament {
    name: string;
    startDate: string;
    endDate: string;
    description: string;
    deadline: string;
    location: string;
    // photoFilename?: string;
    photoUrl?: string;
}

const TournamentDetails: React.FC<{ params: { id: string } }> = ({ params }) => {
    const { id } = params;
    const [tournament, setTournament] = useState<Tournament | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [registrationClosed, setRegistrationClosed] = useState<boolean>(false);

    // Base URL for accessing photos in Firebase
    const firebaseBaseURL = "https://firebasestorage.googleapis.com/v0/b/quagmire-smu.appspot.com/o/";

    useEffect(() => {
        const fetchTournamentDetails = async () => {
            if (!id) return;

            setLoading(true);
            setError(null);

            try {
                const response = await axiosInstance.get(`${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament/${id}`);
                const tournamentData = response.data;

                // Add full photo URL if only the filename/path is provided
                if (tournamentData.photoUrl) {
                    tournamentData.photoUrl = `${firebaseBaseURL}${encodeURIComponent(tournamentData.photoUrl)}?alt=media`;
                }

                setTournament(tournamentData);

                const currentDate = new Date();
                const deadlineDate = new Date(tournamentData.deadline);
                setRegistrationClosed(currentDate > deadlineDate);

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
            <header className="bg-background/10 w-full py-4 text-center text-white">
                <h1 className="text-3xl font-bold text-center">{tournament.name}</h1>
            </header>

            {/* Display the tournament image if available */}
            {tournament.photoUrl && (
                <img
                    src={tournament.photoUrl}
                    alt={`${tournament.name} photo`}
                    className="w-full max-w-md mt-6 mb-4 rounded-lg shadow-lg object-cover"
                />
            )}

            <p className="text-xl text-center">{`${new Date(tournament.startDate).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })} - ${new Date(tournament.endDate).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })}`}</p>

            <div className="mt-8 max-w-xl w-full">
                <h2 className="text-lg font-semibold">Location:</h2>
                <p className="text-base">{tournament.location}</p>
                <h2 className="mt-4 text-lg font-semibold">Description:</h2>
                <p className="text-base">{tournament.description}</p>
                <h2 className="mt-4 text-lg font-semibold">Registration Deadline:</h2>
                <p className="text-base">{new Date(tournament.deadline).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })}</p>
                <div className="flex justify-center w-full">
                    <Button 
                        variant={registrationClosed ? "outline" : "default"} 
                        disabled={registrationClosed} 
                        className="mt-4 flex justify-center w-auto"
                    >
                        {registrationClosed ? "Registration Closed" : "Register Now"}
                    </Button>
                </div>
            </div>

            {/* Register Button */}
            <div className="mt-4">
                {/* Horizontal line */}
                <hr className="w-full my-4 border-t border-gray-300" />

                {/* Tournament Draw Heading */}
                <h2 className="text-2xl font-semibold my-8 text-center">Tournament Draw</h2>

                <p className="text-center text-gray-500">Draw has yet to be released</p>
            </div>
        </div>
    );
};

export default TournamentDetails;
