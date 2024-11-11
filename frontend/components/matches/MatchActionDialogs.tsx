import React from 'react';
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Match } from '@/types/match';
import { PlayerProfile } from '@/types/player-profile';
import axios from 'axios';
import { useGlobalErrorHandler } from '../../app/context/ErrorMessageProvider';

const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;

interface MatchActionDialogsProps {
    activeMatch: Match | null;
    currentPlayerId?: string;
    opponentProfile?: PlayerProfile | null;
    onMatchComplete: () => void;
}

export default function MatchActionDialogs({
    activeMatch,
    currentPlayerId,
    opponentProfile,
    onMatchComplete,
}: MatchActionDialogsProps) {
    const [forfeitDialogOpen, setForfeitDialogOpen] = React.useState(false);
    const [completeDialogOpen, setCompleteDialogOpen] = React.useState(false);
    const [playerScore, setPlayerScore] = React.useState('0');
    const [opponentScore, setOpponentScore] = React.useState('0');

    const { showErrorToast, handleError } = useGlobalErrorHandler(); // Destructure the functions

    const handleForfeit = async () => {
        if (!currentPlayerId || !activeMatch) {
            showErrorToast('Match Error', 'Current player ID or match is not available');
            return;
        }

        try {
            await axios.put(
                `${API_URL}/matches/${activeMatch.matchId}/forfeit?forfeitedById=${currentPlayerId}`
            );
            onMatchComplete();
        } catch (error) {
            if (axios.isAxiosError(error)) {
                handleError(error);
            } else {
                showErrorToast('Error', 'An unexpected error occurred.');
            }
            // refresh page
            window.location.reload();
        }
    };

    const handleCompleteMatch = async (winnerId: string) => {
        if (!activeMatch) {
            showErrorToast('Match Error', 'Match is not available');
            return;
        }

        const playerScoreNum = parseInt(playerScore);
        const opponentScoreNum = parseInt(opponentScore);

        if (isNaN(playerScoreNum) || isNaN(opponentScoreNum)) {
            showErrorToast('Invalid Input', 'Please enter valid numerical scores.');
            return;
        }

        if (playerScoreNum === 0 && opponentScoreNum === 0) {
            showErrorToast('Invalid Scores', 'Scores cannot both be zero.');
            return;
        }

        // Ensure the winner has a higher score
        if (
            (winnerId === currentPlayerId && playerScoreNum <= opponentScoreNum) ||
            (winnerId !== currentPlayerId && playerScoreNum >= opponentScoreNum)
        ) {
            showErrorToast(
                'Invalid Winner',
                'Winner must have a higher score than the opponent.'
            );
            return;
        }

        try {
            const score = `${playerScore}-${opponentScore}`;
            await axios.put(`${API_URL}/matches/${activeMatch.matchId}/complete`, null, {
                params: {
                    winnerId,
                    score,
                },
            });
            onMatchComplete();
        } catch (error) {
            if (axios.isAxiosError(error)) {
                handleError(error);
            } else {
                showErrorToast('Error', 'An unexpected error occurred.');
            }
        }
    };

    const validateScore = (value: string) => {
        const score = parseInt(value);
        if (isNaN(score) || score < 0) return '0';
        return score.toString();
    };

    // Don't render if we don't have the necessary data
    if (!currentPlayerId || !activeMatch) {
        return null;
    }

    const isMatchActionable =
        activeMatch.status === 'IN_PROGRESS' || activeMatch.status === 'SCHEDULED';

    return (
        <div>
            {/* Match Actions Buttons */}
            {isMatchActionable && (
                <div className="flex space-x-4 justify-center mt-4">
                    <Button variant="secondary" onClick={() => setCompleteDialogOpen(true)}>
                        Complete Match
                    </Button>
                    <Button variant="destructive" onClick={() => setForfeitDialogOpen(true)}>
                        Forfeit Match
                    </Button>
                </div>
            )}

            {/* Forfeit Dialog */}
            <AlertDialog
                open={forfeitDialogOpen}
                onOpenChange={setForfeitDialogOpen}
            >
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Confirm Forfeit</AlertDialogTitle>
                        <AlertDialogDescription>
                            Are you sure you want to forfeit this match? This action cannot be
                            undone and will count as a loss.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                        <AlertDialogAction
                            onClick={handleForfeit}
                            className="bg-destructive text-destructive-foreground"
                        >
                            Forfeit Match
                        </AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>

            {/* Complete Match Dialog */}
            <AlertDialog
                open={completeDialogOpen}
                onOpenChange={setCompleteDialogOpen}
            >
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Match Results</AlertDialogTitle>
                        <AlertDialogDescription>
                            Enter the final scores and select the winner:
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <div className="flex flex-col space-y-6 my-4">
                        {/* Score inputs */}
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="playerScore">Your Score</Label>
                                <Input
                                    id="playerScore"
                                    type="number"
                                    min="0"
                                    value={playerScore}
                                    onChange={(e) => setPlayerScore(validateScore(e.target.value))}
                                    className="w-full"
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="opponentScore">
                                    {opponentProfile?.firstName || 'Opponent'}&apos;s Score
                                </Label>
                                <Input
                                    id="opponentScore"
                                    type="number"
                                    min="0"
                                    value={opponentScore}
                                    onChange={(e) =>
                                        setOpponentScore(validateScore(e.target.value))
                                    }
                                    className="w-full"
                                />
                            </div>
                        </div>

                        {/* Winner selection buttons */}
                        <div className="space-y-2">
                            <Button
                                variant="outline"
                                onClick={() => handleCompleteMatch(currentPlayerId)}
                                className="w-full mb-2"
                            >
                                I Won
                            </Button>
                            {opponentProfile && opponentProfile.profileId && (
                                <Button
                                    variant="outline"
                                    onClick={() => handleCompleteMatch(opponentProfile.profileId)}
                                    className="w-full"
                                >
                                    {opponentProfile.firstName || 'Opponent'} Won
                                </Button>
                            )}
                        </div>
                    </div>
                    <AlertDialogFooter>
                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    );
}
