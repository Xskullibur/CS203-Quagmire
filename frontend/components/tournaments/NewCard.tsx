// components/tournaments/NewCard.jsx
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { formatDate } from '@/utils/dateFormatter';
import { motion } from 'framer-motion';
import Link from 'next/link';
import PropTypes from 'prop-types';
import { Tournament } from '@/types/tournament';

interface NewCardProps {
    tournament: Tournament;
    className?: string;
}

const NewCard: React.FC<NewCardProps> = ({ tournament, className }) => {
    return (
        <Card className="flex flex-col border-border overflow-hidden bg-primary-foreground hover:shadow-[0_0_15px_rgba(200,200,200,0.1)] transition duration-200 ease-in-out transform hover:bg-zinc-900 min-h-[250px]">
            <CardHeader className="space-y-1 p-4">
                <CardTitle className="text-lg md:text-xl font-semibold">{tournament.name}</CardTitle>
            </CardHeader>
            <CardContent className="flex-grow p-4 flex flex-col justify-between">
                <div className="space-y-2 text-sm">
                    <div className="grid grid-cols-2 gap-2">
                        <p><span className="font-medium">Start:</span> {new Date(tournament.startDate).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })}</p>
                        <p><span className="font-medium">End:</span> {new Date(tournament.endDate).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })}</p>
                    </div>
                    <p><span className="font-medium">Registration:</span> {new Date(tournament.deadlineDate).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })}</p>
                    <p><span className="font-medium">Location:</span> {tournament.location}</p>
                </div>
            </CardContent>
            <CardFooter className="p-4">
                <Link href={`/tournaments/${tournament.id}`} className="w-full">
                    <Button size="sm" variant="outline" className="w-full rounded-full transform hover:-translate-y-0.5 hover:shadow-lg border">
                        View Tournament
                    </Button>
                </Link>
            </CardFooter>
        </Card>
    );
};

export default NewCard;
