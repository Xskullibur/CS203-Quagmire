"use client";

import React, { useState, useEffect } from 'react';
import axiosInstance from '@/lib/axios';
import NewCard from "@/components/tournaments/NewCard"; 
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { DatePickerWithRange } from "@/components/ui/DatePickerWithRange";
import { Tournament } from "@/types/tournament";
import { DateRange } from 'react-day-picker';
import { addDays } from "date-fns";

// Constants
const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament`;

// Tournament Header Component
const TournamentHeader: React.FC = () => (
  <header className="bg-background/10 w-full py-4 text-center text-white">
    <h1 className="text-3xl font-bold">Tournaments</h1>
  </header>
);

// Tabs for Switching Between Upcoming and Past Tournaments
const TournamentTabs: React.FC<{ 
  currentTab: 'upcoming' | 'past'; 
  setCurrentTab: (tab: 'upcoming' | 'past') => void; 
}> = ({ currentTab, setCurrentTab }) => (
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

// Main Tournament Page Component
const TournamentPage: React.FC = () => {
  const [tournaments, setTournaments] = useState<Tournament[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [currentTab, setCurrentTab] = useState<'upcoming' | 'past'>('upcoming');
  const [dateRange, setDateRange] = useState<DateRange | undefined>({
    from: new Date(2022, 0, 20),
    to: addDays(new Date(2022, 0, 20), 20),
  });

  // Fetch tournaments based on selected tab and date range
  const fetchTournaments = async () => {
    setLoading(true);
    setError(null);

    try {
      const fromDate = dateRange?.from?.toISOString().split('T')[0];
      const toDate = dateRange?.to?.toISOString().split('T')[0];

      const endpoint = currentTab === 'upcoming'
        ? `${API_URL}/upcoming?page=0&size=10&from=${fromDate}&to=${toDate}`
        : `${API_URL}/past?page=0&size=10&from=${fromDate}&to=${toDate}`;

      const response = await axiosInstance.get(endpoint);
      setTournaments(response.data.content);
    } catch (error) {
      console.error('Error fetching tournaments:', error);
      setError('Failed to load tournaments. Please try again.');
      setTournaments([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTournaments();
  }, [currentTab, dateRange]);

  return (
    <div className="flex flex-col items-center min-h-screen pt-20">
      <TournamentHeader />
      
      {/* Flex Container for Tabs and DatePicker */}
      <div className="relative flex items-center w-full max-w-6xl px-4">
        {/* Tournament Tabs - Centered */}
        <div className="flex justify-center w-full">
          <TournamentTabs currentTab={currentTab} setCurrentTab={setCurrentTab} />
        </div>
        
        {/* Date Picker for Range */}
        <div className="absolute right-4">
          <DatePickerWithRange 
            className="w-full max-w-xs" 
            dateRange={dateRange} 
            setDateRange={setDateRange} 
          />
        </div>
      </div>

      {/* Tournament List */}
      <div className="flex flex-col items-center w-full p-4">
        {loading && <p className="text-lg text-gray-500">Loading...</p>}
        {error && <p className="text-lg text-red-500">Error: {error}</p>}
        {!loading && !error && tournaments.length === 0 && (
          <p className="text-lg text-gray-500">
            {currentTab === 'upcoming' ? 'No upcoming tournaments available.' : 'No past tournaments available.'}
          </p>
        )}
        
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 w-full">
          {!loading && !error && tournaments.map(tournament => (
            <NewCard key={tournament.id} tournament={tournament} className="w-full" />
          ))}
        </div>
      </div>
    </div>
  );
};

export default TournamentPage;
