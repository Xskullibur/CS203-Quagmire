"use client";

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { addDays, format } from "date-fns"
import { Calendar as CalendarIcon } from "lucide-react"
import { DateRange } from "react-day-picker"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"

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

    <div>
        <Tabs defaultValue={currentTab} className="w-full max-w-md">
            <TabsList className="flex justify-center w-full">
                <TabsTrigger value="upcoming" onClick={() => setCurrentTab('upcoming')}>
                    Upcoming
                </TabsTrigger>
                <TabsTrigger value="past" onClick={() => setCurrentTab('past')}>
                    Past
                </TabsTrigger>
            </TabsList>
            </Tabs>
    </div>
);

export function DatePickerWithRange({
    className,
  }: React.HTMLAttributes<HTMLDivElement>) {
    const [date, setDate] = React.useState<DateRange | undefined>({
      from: new Date(2022, 0, 20),
      to: addDays(new Date(2022, 0, 20), 20),
    })
    }

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
