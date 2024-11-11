// AdditionalDetailsForm.tsx
import React from 'react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Tournament } from '@/types/tournament';

interface AdditionalDetailsFormProps {
    tournament: Tournament;
    handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
    handleBack: (e: React.FormEvent) => void;
    handleSubmit: (e: React.FormEvent) => void;
}


const AdditionalDetailsForm: React.FC<AdditionalDetailsFormProps> = ({
    tournament,
    handleChange,
    handleBack,
    handleSubmit,
}) => {
    return (
        <div className="w-[95vw] md:w-1/2 max-w-xl p-6 bg-primary-foreground rounded-lg shadow-md relative
      overflow-hidden backdrop-blur-sm hover:backdrop-blur-md transition
      duration-300 z-10 border border-zinc-700 hover:border-zinc-400 hover:bg-zinc-800/50 shadow-zinc-800">
            <h2 className="text-xl font-bold mb-4 text-white">Additional Details - Step 2</h2>

            <form className="flex flex-col gap-6" onSubmit={handleSubmit}>
                {/* Deadline */}
                <div className="mb-4">
                    <label className="text-sm font-medium text-white" htmlFor="deadline">Deadline to Join Tournament</label>
                    <div className="flex gap-4">
                        <Input
                            type="date"
                            id="deadline"
                            name="deadline"
                            value={tournament.deadline}
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

                {/* Max Participants */}
                <div className="mb-4">
                    <label className="text-sm font-medium text-white" htmlFor="maxParticipants">Max Participants</label>
                    <Input
                        type="number"
                        id="maxParticipants"
                        name="maxParticipants"
                        value={tournament.maxParticipants}
                        onChange={handleChange}
                        required
                        className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                    />
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
    );
};

export default AdditionalDetailsForm;
