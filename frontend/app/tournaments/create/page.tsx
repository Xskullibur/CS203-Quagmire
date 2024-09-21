'use client'

import React, { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;


const CreateTournament = () => {
  const router = useRouter();

  const [tournament, setTournament] = useState({
    name: '',
    description: '',
    location: '',
    date: '',
    time: ''
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setTournament({
      ...tournament,
      [name]: value
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const datetime = `${tournament.date}T${tournament.time}:00`;
    const {time, ...tournamentDetails} = tournament;
    const data = {
      ...tournamentDetails,
      date: datetime
    }
    console.log(JSON.stringify(data))

    try {
      const res = await fetch( API_URL + '/tournament', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });

      if (res.ok) {
        router.push(API_URL + '/tournaments'); // Redirect after successful creation
      } else {
        alert('Error creating tournament');
      }
    } catch (error) {
      console.error('Error creating tournament:', error);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-primary-foreground">
      <div className="w-80 p-6 bg-primary-foreground rounded-lg shadow-md relative
                overflow-hidden backdrop-blur-sm hover:backdrop-blur-md transition
                duration-300 z-10 border border-zinc-700 hover:border-zinc-400 hover:bg-zinc-800/50 shadow-zinc-800">
        <h1 className="text-2xl font-bold mb-6 text-white">Create Tournament</h1>
        <form 
        className="flex flex-col gap-4"
        onSubmit={handleSubmit}>
          <div>
            <Input
              type="text"
              id="name"
              placeholder="Tournament name"
              name="name"
              value={tournament.name}
              onChange={handleChange}
              required
              className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
            />
          </div>

          <div>
            <Input
              id="description"
              placeholder="Tournament description"
              name="description"
              value={tournament.description}
              onChange={handleChange}
              required
              className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
            />
          </div>

          <div>
            <Input
              type="text"
              id="location"
              name="location"
              placeholder="Tournament location"
              value={tournament.location}
              onChange={handleChange}
              required
              className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
            />
          </div>

          <div>
            <Input
              type="date"
              id="date"
              name="date"
              placeholder="date"
              value={tournament.date}
              onChange={handleChange}
              required
              className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
            />
          </div>

          <div>
          <Input
            type="time"
            name="time"
            value={tournament.time}
            onChange={handleChange}
            required
            className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
          />
          </div>

          <Button type="submit">Create Tournament</Button>
        </form>
      </div>
    </div>
  );
};

export default CreateTournament;
