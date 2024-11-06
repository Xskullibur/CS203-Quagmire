"use client";

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import NewCard from "@/components/tournaments/NewCard"; // Update the import to your new card component
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Tournament } from "@/types/tournament";
import axiosInstance from '@/lib/axios';
import { useGlobalErrorHandler } from '../context/ErrorMessageProvider';

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament`;

const TournamentHeader: React.FC = () => (
    <header className="bg-background/10 w-full py-4 text-center text-white">
        <h1 className="text-3xl font-bold">Tournaments</h1>
    </header>
);

const TournamentTabs: React.FC<{ currentTab: 'upcoming' | 'past' | 'current'; setCurrentTab: (tab: 'upcoming' | 'past' | 'current') => void }> = ({ currentTab, setCurrentTab }) => (
    <div>
        <Tabs defaultValue={currentTab} className="w-full max-w-md">
            <TabsList className="flex justify-center w-full">
                <TabsTrigger value="upcoming" onClick={() => setCurrentTab('upcoming')}>
                    Upcoming
                </TabsTrigger>
                <TabsTrigger value="past" onClick={() => setCurrentTab('past')}>
                    Past
                </TabsTrigger>
                <TabsTrigger value="current" onClick={() => setCurrentTab('current')}>
                    Current
                </TabsTrigger>
            </TabsList>
        </Tabs>
    </div>
);

// const TournamentPage: React.FC = () => {
//     const [tournaments, setTournaments] = useState<Tournament[]>([]);
//     const [loading, setLoading] = useState<boolean>(true);
//     const [error, setError] = useState<string | null>(null);
//     const [currentTab, setCurrentTab] = useState<'upcoming' | 'past'>('upcoming');

//     useEffect(() => {
//         const fetchTournaments = async () => {
//             setLoading(true);
//             setError(null);

//             try {
//                 const endpoint = currentTab === 'upcoming'
//                     ? `${API_URL}/upcoming?page=0&size=10`
//                     : `${API_URL}/past?page=0&size=10`;

//                 const response = await axiosInstance.get(
//                     endpoint
//                 )
//                 setTournaments(response.data.content);
//             } catch (error) {
//                 console.error('Error fetching tournaments:', error);
//                 setError('Failed to load tournaments. Please try again.');
//                 setTournaments([]);
//             } finally {
//                 setLoading(false);
//             }
//         };

//         fetchTournaments();
//     }, [currentTab]);

const TournamentPage: React.FC = () => {
    const [tournaments, setTournaments] = useState<Tournament[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const { handleError } = useGlobalErrorHandler();
    const [currentTab, setCurrentTab] = useState<'upcoming' | 'past' | 'current'>('upcoming');

    useEffect(() => {
        const fetchTournaments = async () => {
            setLoading(true);
            setError(null);

            try {
                let endpoint = '';

                if (currentTab === 'upcoming') {
                    endpoint = `${API_URL}/upcoming?page=0&size=10`;
                } else if (currentTab === 'past') {
                    endpoint = `${API_URL}/past?page=0&size=10`;
                } else if (currentTab === 'current') {
                    endpoint = `${API_URL}/current?page=0&size=10`;
                }

                const response = await axiosInstance.get(endpoint);
                setTournaments(response.data.content);
            } catch (error) {

                if (axios.isAxiosError(error)) {
                    handleError(error)
                }

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
        <div className="flex flex-col items-center min-h-screen pt-20">
            <TournamentHeader />
            {/* Tabs for Upcoming, In Progress, and Past tournaments */}
            <TournamentTabs currentTab={currentTab} setCurrentTab={setCurrentTab} />

            <div className="flex flex-col items-center w-full p-4">
                {loading && <p className="text-lg text-gray-500">Loading...</p>}
                {error && <p className="text-lg text-red-500">Error: {error}</p>}
                {!loading && !error && tournaments && tournaments.length === 0 && (
                    <p className="text-lg text-gray-500">
                        {currentTab === 'upcoming' ? 'No upcoming tournaments available.' : currentTab === 'past' ? 'No past tournaments available.' : 'No current tournaments available.'}
                    </p>
                )}
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 w-full">
                    {!loading && !error && tournaments && tournaments.map(tournament => (
                        <NewCard key={tournament.id} tournament={tournament} className="w-full" />
                    ))}
                </div>
            </div>
        </div>
    );
};

//     return (
//         <div className="flex flex-col items-center min-h-screen pt-20">
//             <TournamentHeader />
//             <TournamentTabs currentTab={currentTab} setCurrentTab={setCurrentTab} />
//             <div className="flex flex-col items-center w-full p-4">
//                 {loading && <p className="text-lg text-gray-500">Loading...</p>}
//                 {error && <p className="text-lg text-red-500">Error: {error}</p>}
//                 {!loading && !error && tournaments && tournaments.length === 0 && (
//                     <p className="text-lg text-gray-500">
//                         {currentTab === 'upcoming' ? 'No upcoming tournaments available.' : 'No past tournaments available.'}
//                     </p>
//                 )}
//                 <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 w-full">
//                     {!loading && !error && tournaments && tournaments.map(tournament => (
//                         <NewCard key={tournament.id} tournament={tournament} className="w-full" /> // Use NewCard here
//                     ))}
//                 </div>
//             </div>
//         </div>
//     );
// };

export default TournamentPage;
