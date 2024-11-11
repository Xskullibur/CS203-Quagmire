// UpdateTournament.tsx
// UpdateTournament.tsx
'use client'

import React, { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import TournamentForm from '@/components/tournaments/TournamentForm1';
import AdditionalDetailsForm from '@/components/tournaments/TournamentForm2';
import { Tournament } from "@/types/tournament";
import axiosInstance from "@/lib/axios";
import axios from "axios";
import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import withAuth from "@/HOC/withAuth";
import { UserRole } from "@/types/user-role";

const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;
const WEB_URL = process.env.NEXT_PUBLIC_API_URL;

const UpdateTournament = () => {
  const router = useRouter();
  const { id } = useParams(); // Ensure the correct usage of useParams depending on Next.js version

  // Step state (1 for Basic Info, 2 for Additional Details)
  const [step, setStep] = useState(1);
  

  const [tournament, setTournament] = useState<Tournament>({
    id: null,
    name: '',
    location: '',
    startDate: '',
    startTime: '',
    endDate: '',
    endTime: '',
    status: 'SCHEDULED',
    deadline: '',
    deadlineTime: '',
    maxParticipants: 0,
    description: '',
    photoUrl: ''
  });

  const [refereeSearchQuery, setRefereeSearchQuery] = useState<string>('');
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const [selectedReferees, setSelectedReferees] = useState<string[]>([]);
  const { handleError } = useGlobalErrorHandler();

  const handleRefereeSearch = async (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setRefereeSearchQuery(e.target.value);

    if (e.target.value.length >= 3) {
      // Start searching after 3 characters
      try {
        const res = await axiosInstance.get(
          new URL(
            `/users/search?username=${e.target.value}`,
            API_URL
          ).toString()
        );

        // const res = await fetch(`${API_URL}/users/search?username=${e.target.value}`);
        // const data = await res.json();
        setSearchResults(res.data);
      } catch (error) {
        if (axios.isAxiosError(error)) {
          handleError(error);
        }

        console.error("Error searching for referees:", error);
      }
    } else {
      setSearchResults([]);
    }
  };

  // Fetch existing tournament data based on the ID from the URL
  useEffect(() => {
    if (!id) return; // Ensure 'id' is available before fetching

    const fetchTournament = async () => {
      try {
        const res = await fetch(`${API_URL}/tournament/DTO/${id}`);
        if (!res.ok) {
          alert('Error fetching tournament details');
          return;
        }

        const data = await res.json();
        const { startDate, endDate, deadline, ...rest } = data;

        setTournament({
          ...rest,
          startDate: startDate.split('T')[0],
          startTime: startDate.split('T')[1]?.slice(0, 5),
          endDate: endDate.split('T')[0],
          endTime: endDate.split('T')[1]?.slice(0, 5),
          deadline: deadline.split('T')[0],
          deadlineTime: deadline.split('T')[1]?.slice(0, 5),
        });
      } catch (error) {
        console.error('Error fetching tournament:', error);
      }
    };

    fetchTournament();
  }, [id]);

  // Handle input changes
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setTournament({
      ...tournament,
      [name]: value
    });
  };

  // Handle form submission to update tournament
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validate maxParticipants before submitting
    if (tournament.maxParticipants <= 0) {
      alert("Max participants must be a positive number");
      return;
    }

    const startdatetime = `${tournament.startDate}T${tournament.startTime}:00`;
    const enddatetime = `${tournament.endDate}T${tournament.endTime}:00`;
    const deadline = `${tournament.deadline}T${tournament.deadlineTime}:00`;

    const { startDate, startTime, endDate, endTime, deadlineTime, ...tournamentDetails } = tournament;

    const data = {
      ...tournamentDetails,
      startDate: startdatetime,
      endDate: enddatetime,
      deadline: deadline
    };

    try {
      const res = await fetch(`${API_URL}/tournament/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      if (res.ok) {
        router.push(`${WEB_URL}/tournaments`); // Redirect after successful update
      } else {
        alert('Error updating tournament');
      }
    } catch (error) {
      console.error('Error updating tournament:', error);
    }
  };

  // Handle navigating between steps
  const handleNext = (e: React.FormEvent) => {
    e.preventDefault();
    setStep(2);
  };

  const handleBack = (e: React.FormEvent) => {
    e.preventDefault();
    setStep(1);
  };

  return (
    <div className="flex flex-col items-center justify-center mx-auto min-h-screen bg-primary-foreground">
      {step === 1 && (
        <TournamentForm
          tournament={tournament}
          handleChange={handleChange}
          handleSubmit={handleNext}
          buttonLabel="Next"
        />
      )}

      {step === 2 && (
        <AdditionalDetailsForm
        tournament={tournament}
        handleChange={handleChange}
        handleBack={handleBack}
        handleSubmit={handleSubmit}
      />
        
      )}
    </div>
  );
};

export default withAuth(UpdateTournament, UserRole.ADMIN);
