'use client'

import React, { useState, useEffect } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useRouter, useParams } from "next/navigation";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;
const WEB_URL = `${process.env.NEXT_PUBLIC_API_URL}`

const UpdateTournament = () => {
  const router = useRouter();
  const { id } = useParams(); // Assuming the tournament ID is passed in the URL
  
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

  // Fetch existing tournament data based on the ID from the URL
  useEffect(() => {
    console.log(id)
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

  return (
    <div className="mt-20 mb-20 flex flex-col items-center justify-center mx-auto min-h-screen bg-primary-foreground">
      <div className="w-[95vw] md:w-1/2 max-w-xl p-6 bg-primary-foreground rounded-lg shadow-md relative
            overflow-hidden backdrop-blur-sm hover:backdrop-blur-md transition
            duration-300 z-10 border border-zinc-700 hover:border-zinc-400 hover:bg-zinc-800/50 shadow-zinc-800">
        <h1 className="text-2xl font-bold mb-6 text-white">Update Tournament</h1>
        <form className="flex flex-col gap-6" onSubmit={handleSubmit}>
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

          {/* Deadline */}
          <div>
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
          <div className="flex flex-col">
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

          {/* Submit Button */}
          <Button type="submit">Update Tournament</Button>
        </form>
      </div>
    </div>
  );
};

export default UpdateTournament;
