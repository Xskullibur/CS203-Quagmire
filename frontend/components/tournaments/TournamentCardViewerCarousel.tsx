import { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight } from "lucide-react";
import TournamentCard from './TournamentCard';
import tournaments from './tournaments.json';
import { Button } from "@/components/ui/button";
import { motion } from 'framer-motion';
import { Tournament } from '@/types/tournament';

const transformTournamentData = (tournament: any): Tournament => {
    return {
        id: String(tournament.id),
        name: tournament.name,
        location: tournament.location,
        startDate: tournament.start_date.split('T')[0],
        startTime: tournament.start_date.split('T')[1]?.slice(0, 5) || '00:00',
        endDate: tournament.end_date.split('T')[0],
        endTime: tournament.end_date.split('T')[0]?.slice(0, 5) || '00:00',
        status: tournament.status.toUpperCase() as Tournament['status'],
        deadlineDate: tournament.registration_deadline.split('T')[0],
        deadlineTime: tournament.registration_deadline.split('T')[1]?.slice(0, 5) || '00:00',
        maxParticipants: tournament.max_participants,
        description: tournament.description,
        refereeIds: tournament.refereeIds || []
    };
};

const TournamentCardViewerCarousel = () => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const [cardsToShow, setCardsToShow] = useState(3);
    const [transformedTournaments, setTransformedTournaments] = useState<Tournament[]>([]);

    useEffect(() => {
        // Transform the imported tournaments data to match the Tournament interface
        const transformed = tournaments.map(transformTournamentData);
        setTransformedTournaments(transformed);
    }, []);

    useEffect(() => {
        const handleResize = () => {
            if (window.innerWidth < 640) setCardsToShow(1);
            else if (window.innerWidth < 1024) setCardsToShow(2);
            else setCardsToShow(3);
        };

        handleResize();
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    const scroll = (direction: 'left' | 'right') => {
        if (direction === 'left') {
            setCurrentIndex(prev => Math.max(prev - 1, 0));
        } else {
            setCurrentIndex(prev => Math.min(prev + 1, transformedTournaments.length - cardsToShow));
        }
    };

    return (
        <div className="flex flex-col items-center justify-center text-white p-4 w-full my-16">
            <p className="text-sm text-center mb-8 font-mono text-zinc-400">Featured Tournaments</p>
            <div className="relative w-full max-w-7xl overflow-x-hidden">
                <motion.div
                    className="flex space-x-8"
                    animate={{ x: `-${currentIndex * (100 / cardsToShow)}%` }}
                    transition={{ type: "spring", stiffness: 300, damping: 30 }}
                >
                    {transformedTournaments.map((tournament) => (
                        <div
                            key={tournament.id}
                            className={`w-full flex-shrink-0 flex-grow-0`}
                            style={{ flexBasis: `${100 / cardsToShow}%` }}
                        >
                            <TournamentCard tournament={tournament} />
                        </div>
                    ))}
                </motion.div>
            </div>
            <div className="flex justify-center mt-4 space-x-4">
                <Button
                    variant="outline"
                    size="icon"
                    onClick={() => scroll('left')}
                    disabled={currentIndex === 0}
                    className="p-2"
                >
                    <ChevronLeft className="h-6 w-6" />
                </Button>
                <Button
                    variant="outline"
                    size="icon"
                    onClick={() => scroll('right')}
                    disabled={currentIndex === transformedTournaments.length - cardsToShow}
                    className="p-2"
                >
                    <ChevronRight className="h-6 w-6" />
                </Button>
            </div>
        </div>
    );
};

export default TournamentCardViewerCarousel;
