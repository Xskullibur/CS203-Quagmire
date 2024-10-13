// UpdateTournament.tsx
'use client'

import React, { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import TournamentForm from '@/components/tournaments/TournamentForm1';
import AdditionalDetailsForm from '@/components/tournaments/TournamentForm2';
import { Tournament } from "@/types/tournament";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;
const WEB_URL = `${process.env.NEXT_PUBLIC_API_URL}`;

const UpdateTournament = () => {
  const router = useRouter();
  const { id } = useParams(); // Assuming the tournament ID is passed in the URL

  // Step state (1 for Basic Info, 2 for Additional Details)
  const [step, setStep] = useState(1);

  const [tournament, setTournament] = useState<Tournament>({
    name: '',
    location: '',
    startDate: '',
    startTime: '',
    endDate: '',
    endTime: '',
    status: 'open',
    deadlineDate: '',
    deadlineTime: '',
    maxParticipants: 0,
    description: '',
  });

  // Fetch existing tournament data based on the ID from the URL
  useEffect(() => {
    const fetchTournament = async () => {
      try {
        const res = await fetch(`${API_URL}/tournament/${id}`);
        const data = await res.json();
        if (res.ok) {
          const { startDate, endDate, deadline, ...rest } = data;
          setTournament({
            ...rest,
            startDate: startDate.split('T')[0],
            startTime: startDate.split('T')[1].slice(0, 5),
            endDate: endDate.split('T')[0],
            endTime: endDate.split('T')[1].slice(0, 5),
            deadlineDate: deadline.split('T')[0],
            deadlineTime: deadline.split('T')[1].slice(0, 5),
          });
        } else {
          alert('Error fetching tournament details');
        }
      } catch (error) {
        console.error('Error fetching tournament:', error);
      }
    };

    if (id) fetchTournament();
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

    const startdatetime = `${tournament.startDate}T${tournament.startTime}:00`;
    const enddatetime = `${tournament.endDate}T${tournament.endTime}:00`;
    const deadline = `${tournament.deadlineDate}T${tournament.deadlineTime}:00`;

    const { startDate, startTime, endDate, endTime, deadlineDate, deadlineTime, ...tournamentDetails } = tournament;

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
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });

      if (res.ok) {
        router.push(WEB_URL + '/tournaments'); // Redirect after successful update
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
    <div className="mt-20 mb-20 flex flex-col items-center justify-center mx-auto min-h-screen bg-primary-foreground">
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

export default UpdateTournament;
