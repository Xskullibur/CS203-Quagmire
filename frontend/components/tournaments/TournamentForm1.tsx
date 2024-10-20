// TournamentForm.tsx
import React from 'react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

interface TournamentFormProps {
  tournament: {
    name: string;
    location: string;
    startDate: string;
    startTime: string;
    endDate: string;
    endTime: string;
  };
  handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
  handleSubmit: (e: React.FormEvent) => void;
  buttonLabel: string;
}

const TournamentForm: React.FC<TournamentFormProps> = ({
  tournament,
  handleChange,
  handleSubmit,
  buttonLabel,
}) => {
  return (
    <div className="w-[95vw] md:w-1/2 max-w-xl p-6 bg-primary-foreground rounded-lg shadow-md relative
        overflow-hidden backdrop-blur-sm hover:backdrop-blur-md transition
        duration-300 z-10 border border-zinc-700 hover:border-zinc-400 hover:bg-zinc-800/50 shadow-zinc-800">
      <h1 className="text-2xl font-bold mb-6 text-white">Create Tournament - Step 1</h1>
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

        <Button type="submit">{buttonLabel}</Button>
      </form>
    </div>
  );
};

export default TournamentForm;
