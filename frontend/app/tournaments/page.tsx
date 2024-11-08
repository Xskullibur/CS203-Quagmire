"use client";

import React, { useState, useEffect } from "react";
import axiosInstance from "@/lib/axios";
import { useGlobalErrorHandler } from "../context/ErrorMessageProvider";
import NewCard from "@/components/tournaments/NewCard";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { DatePickerWithRange } from "@/components/ui/DatePickerWithRange";
import { Tournament } from "@/types/tournament";
import { DateRange } from "react-day-picker";
import { addDays } from "date-fns";
import { useSearchParams, useRouter, usePathname } from "next/navigation";
import axios from "axios";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament`;

const TournamentHeader: React.FC = () => (
  <header className="bg-background/10 w-full py-4 text-center text-white">
    <h1 className="text-3xl font-bold">Tournaments</h1>
  </header>
);

const TournamentTabs: React.FC<{
  currentTab: "upcoming" | "past";
  setCurrentTab: (tab: "upcoming" | "past") => void;
}> = ({ currentTab, setCurrentTab }) => (
  <div>
    <Tabs defaultValue={currentTab} className="w-full max-w-md">
      <TabsList className="flex justify-center w-full">
        <TabsTrigger value="upcoming" onClick={() => setCurrentTab("upcoming")}>
          Upcoming
        </TabsTrigger>
        <TabsTrigger value="past" onClick={() => setCurrentTab("past")}>
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
  const { handleError } = useGlobalErrorHandler();
  const [currentTab, setCurrentTab] = useState<"upcoming" | "past">("upcoming");
  const [date, setDate] = useState<DateRange | undefined>({
    from: new Date(),
    to: addDays(new Date(), 30),
  });

  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  // Update URL when date range changes
  useEffect(() => {
    if (date?.from || date?.to) {
      const params = new URLSearchParams(searchParams.toString());
      if (date.from) {
        params.set("from", date.from.toISOString().split("T")[0]);
      }
      if (date.to) {
        params.set("to", date.to.toISOString().split("T")[0]);
      }
      router.push(`${pathname}?${params.toString()}`);
    }
  }, [date, pathname, router]);

  useEffect(() => {
    const fetchTournaments = async () => {
      setLoading(true);
      setError(null);

      try {
        const endpoint =
          currentTab === "upcoming"
            ? `${API_URL}/upcoming?page=0&size=10`
            : `${API_URL}/past?page=0&size=10`;

        const response = await axiosInstance.get(endpoint);
        setTournaments(response.data.content);
      } catch (error) {
        if (axios.isAxiosError(error)) {
          handleError(error);
        }

        console.error("Error fetching tournaments:", error);
        setError("Failed to load tournaments. Please try again.");
        setTournaments([]);
      } finally {
        setLoading(false);
      }
    };

    fetchTournaments();
  }, [currentTab, date]);

  return (
    <div className="flex flex-col items-center min-h-screen pt-20">
      <TournamentHeader />
      <div className="relative flex items-center w-full max-w-6xl px-4">
        <div className="flex justify-center w-full">
          <TournamentTabs
            currentTab={currentTab}
            setCurrentTab={setCurrentTab}
          />
        </div>
        <div className="absolute right-4">
          <DatePickerWithRange date={date} setDate={setDate} />
        </div>
      </div>

      <div className="flex flex-col items-center w-full p-4">
        {loading && <p className="text-lg text-gray-500">Loading...</p>}
        {error && <p className="text-lg text-red-500">Error: {error}</p>}
        {!loading && !error && tournaments.length === 0 && (
          <p className="text-lg text-gray-500">
            {currentTab === "upcoming"
              ? "No upcoming tournaments available."
              : "No past tournaments available."}
          </p>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 w-full">
          {!loading &&
            !error &&
            tournaments.map((tournament) => (
              <NewCard
                key={tournament.id}
                tournament={tournament}
                className="w-full"
              />
            ))}
        </div>
      </div>
    </div>
  );
};

export default TournamentPage;
