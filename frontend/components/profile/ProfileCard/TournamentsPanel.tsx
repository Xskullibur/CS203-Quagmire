import { useState, useEffect } from "react";
import { PlayerProfile } from "@/types/player-profile";
import { Tournament } from "@/types/tournament";
import { SheetHeader, SheetTitle } from "@/components/ui/sheet";
import { Card, CardContent } from "@/components/ui/card";
import NewCard from "@/components/tournaments/NewCard";

interface TournamentsPanelProps {
    tournaments: Tournament[];
}

export const TournamentsPanel = ({
    tournaments
}: TournamentsPanelProps) => {
    return (
        <div className="h-full flex flex-col justify-center">
            <SheetHeader>
                <SheetTitle className="text-3xl font-bold mb-6">
                    Tournaments
                </SheetTitle>
            </SheetHeader>

            {/* Step 1: Check if tournaments exist */}
            {tournaments.length === 0 ? (
                <p>Register for your first tournament!</p>
            ) : (
                // Step 2: Display tournaments if available
                <div>
                    {tournaments.map((tournament) => (
                        <div>
                            <NewCard key={tournament.id} tournament={tournament} className="w-full" />
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};
