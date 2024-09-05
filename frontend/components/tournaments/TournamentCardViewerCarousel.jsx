// components/tournaments/TournamentCardViewerCarousel.jsx
import { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight } from "lucide-react";
import TournamentCard from './TournamentCard';
import tournaments from './tournaments.json';
import { Button } from "@/components/ui/button";
import { motion } from 'framer-motion';

const TournamentCardViewerCarousel = () => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const [cardsToShow, setCardsToShow] = useState(3);

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

    const scroll = (direction) => {
        if (direction === 'left') {
            setCurrentIndex(prev => Math.max(prev - 1, 0));
        } else {
            setCurrentIndex(prev => Math.min(prev + 1, tournaments.length - cardsToShow));
        }
    };

    return (
        <div className="flex flex-col items-center justify-center text-white p-4 w-full">
            <h2 className="text-2xl md:text-3xl font-bold mb-4 text-center">Featured Tournaments</h2>
            <div className="relative w-full max-w-7xl overflow-x-hidden">
                <motion.div
                    className="flex space-x-8"
                    animate={{ x: `-${currentIndex * (100 / cardsToShow)}%` }}
                    transition={{ type: "spring", stiffness: 300, damping: 30 }}
                >
                    {tournaments.map((tournament) => (
                        <div key={tournament.tournament_id} className={`w-full flex-shrink-0 flex-grow-0`} style={{ flexBasis: `${100 / cardsToShow}%` }}>
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
                    disabled={currentIndex === tournaments.length - cardsToShow}
                    className="p-2"
                >
                    <ChevronRight className="h-6 w-6" />
                </Button>
            </div>
        </div>
    );
};

export default TournamentCardViewerCarousel;