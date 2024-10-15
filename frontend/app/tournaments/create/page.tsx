'use client'

import React, { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;
const WEB_URL = `${process.env.NEXT_PUBLIC_API_URL}`

const CreateTournament = () => {
  const router = useRouter();

  // State to control the form step (1 for Basic Info, 2 for Additional Details)
  const [step, setStep] = useState(1);

  // Tournament state
  const [tournament, setTournament] = useState({
    name: '',
    location: '',
    startDate: '',
    startTime: '',
    endDate: '',
    endTime: '',
    deadlineDate: '',
    deadlineTime: '',
    description: ''
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
      const res = await fetch(API_URL + '/tournament', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });

      if (res.ok) {
        router.push(WEB_URL + '/tournaments'); // Redirect after successful creation
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
        // Step 1: Basic Info Section
        <div className="w-[95vw] md:w-1/2 max-w-xl p-6 bg-primary-foreground rounded-lg shadow-md relative
            overflow-hidden backdrop-blur-sm hover:backdrop-blur-md transition
            duration-300 z-10 border border-zinc-700 hover:border-zinc-400 hover:bg-zinc-800/50 shadow-zinc-800">
          <h1 className="text-2xl font-bold mb-6 text-white">Create Tournament - Step 1</h1>
          <form className="flex flex-col gap-6" onSubmit={handleNext}>
            {/* Tournament Name */}
            <div>
              <label className="text-sm font-medium text-white" htmlFor="name">Tournament Name</label>
              <Input
                type="text"
                id="name"
                placeholder="Enter tournament name"
                name="name"
                value={tournament.name}
                onChange={handleChange}
                required
                className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
              />
            </div>

            {/* Tournament Location */}
            <div>
              <label className="text-sm font-medium text-white" htmlFor="location">Tournament Location</label>
              <Input
                type="text"
                id="location"
                name="location"
                placeholder="Enter tournament location"
                value={tournament.location}
                onChange={handleChange}
                required
                className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
              />
            </div>

            {/* Start Date & Time */}
            <div>
              <label className="text-sm font-medium text-white" htmlFor="startDate">Start Date & Time</label>
              <div className="flex gap-4">
                <Input
                  type="date"
                  id="startDate"
                  name="startDate"
                  value={tournament.startDate}
                  onChange={handleChange}
                  required
                  className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                />

                <Input
                  type="time"
                  name="startTime"
                  id="startTime"
                  value={tournament.startTime}
                  onChange={handleChange}
                  required
                  className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                />
              </div>
            </div>

            {/* End Date & Time */}
            <div>
              <label className="text-sm font-medium text-white" htmlFor="endDate">End Date & Time</label>
              <div className="flex gap-4">
                <Input
                  type="date"
                  id="endDate"
                  name="endDate"
                  value={tournament.endDate}
                  onChange={handleChange}
                  required
                  className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                />

                <Input
                  type="time"
                  name="endTime"
                  id="endTime"
                  value={tournament.endTime}
                  onChange={handleChange}
                  required
                  className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                />
              </div>
            </div>

            <Button type="submit">Next</Button>
          </form>
        </div>
      )}

      {step === 2 && (
        // Step 2: Additional Details Section
        <div className="w-[95vw] md:w-1/2 max-w-xl p-6 bg-primary-foreground rounded-lg shadow-md relative
            overflow-hidden backdrop-blur-sm hover:backdrop-blur-md transition
            duration-300 z-10 border border-zinc-700 hover:border-zinc-400 hover:bg-zinc-800/50 shadow-zinc-800">
          <h2 className="text-xl font-bold mb-4 text-white">Additional Details - Step 2</h2>

          <form className="flex flex-col gap-6" onSubmit={handleSubmit}>
            {/* Deadline */}
            <div className="mb-4">
              <label className="text-sm font-medium text-white" htmlFor="deadlineDate">Deadline to Join Tournament</label>
              <div className="flex gap-4">
                <Input
                  type="date"
                  id="deadlineDate"
                  name="deadlineDate"
                  value={tournament.deadlineDate}
                  onChange={handleChange}
                  required
                  className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                />

                <Input
                  type="time"
                  id="deadlineTime"
                  name="deadlineTime"
                  value={tournament.deadlineTime}
                  onChange={handleChange}
                  required
                  className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                />
              </div>
            </div>

            {/* Description */}
            <div className="flex flex-col gap-2">
              <label className="text-sm font-medium text-white" htmlFor="description">Tournament Description</label>
              <textarea
                id="description"
                placeholder="Describe the tournament"
                name="description"
                value={tournament.description}
                onChange={handleChange}
                required
                className="h-20 bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                rows={4}
                style={{ resize: 'none' }}
              />
            </div>

            <div className="flex justify-between gap-8">
              <Button onClick={handleBack} type="button" className="w-1/2">Back</Button>
              <Button type="submit" className="w-1/2">Create Tournament</Button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};

export default CreateTournament;
