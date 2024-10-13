// CreateTournament.tsx
import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import TournamentForm from '@/components/tournaments/TournamentForm1';
import AdditionalDetailsForm from '@/components/tournaments/TournamentForm2';
import { Tournament } from '@/types/tournament';

const CreateTournament = () => {
  const router = useRouter();

  // State to control the form step (1 for Basic Info, 2 for Additional Details)
  const [step, setStep] = useState(1);

  // Tournament state
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

  // Handle input change
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setTournament({
      ...tournament,
      [name]: value
    });
  };

  // Handle next step
  const handleNext = (e: React.FormEvent) => {
    e.preventDefault();
    setStep(2);
  };

  // Handle going back to the previous step
  const handleBack = (e: React.FormEvent) => {
    e.preventDefault();
    setStep(1);
  };

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Example logic for submission
    try {
      const res = await fetch('/api/tournaments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(tournament),
      });

      if (res.ok) {
        router.push('/tournaments'); // Redirect to tournaments page after successful creation
      } else {
        alert('Error creating tournament');
      }
    } catch (error) {
      console.error('Error creating tournament:', error);
    }
  };

  return (
    <div className="mt-20 flex flex-col items-center justify-center mx-auto min-h-screen bg-primary-foreground">
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

export default CreateTournament;
