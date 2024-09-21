// components/tournaments/NewCard.jsx
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { formatDate } from '@/utils/dateFormatter';
import { motion } from 'framer-motion';
import Link from 'next/link'; 
import PropTypes from 'prop-types';

const NewCard = ({ tournament, className }) => {
    return (
            <Card className="flex flex-col border-border overflow-hidden bg-primary-foreground hover:shadow-[0_0_15px_rgba(200,200,200,0.1)] transition duration-200 ease-in-out transform hover:bg-zinc-900 min-h-[250px]">
                <CardHeader className="space-y-1 p-4">
                    <CardTitle className="text-lg md:text-xl font-semibold">{tournament.name}</CardTitle>
                </CardHeader>
                <CardContent className="flex-grow p-4 flex flex-col justify-between">
                    <div className="space-y-2 text-sm">
                        <div className="grid grid-cols-2 gap-2">
                            <p><span className="font-medium">Start:</span> {formatDate(tournament.startDate)}</p>
                            <p><span className="font-medium">End:</span> {formatDate(tournament.endDate)}</p>
                        </div>
                        <p><span className="font-medium">Registration:</span> {formatDate(tournament.deadline)}</p>
                        <p><span className="font-medium">Location:</span> {tournament.location}</p>
                    </div>
                </CardContent>
                <CardFooter className="p-4">
                    <Link href={`/tournaments/${tournament.id}`} className="w-full">
                        <Button size="sm" variant="primary" className="w-full rounded-full transform hover:-translate-y-0.5 hover:shadow-lg border">
                            View Tournament
                        </Button>
                    </Link>
                </CardFooter>
            </Card>
    );
};

NewCard.propTypes = {
    tournament: PropTypes.shape({
        name: PropTypes.string.isRequired,
        location: PropTypes.string.isRequired,
        startDate: PropTypes.string.isRequired,
        endDate: PropTypes.string.isRequired,
        deadline: PropTypes.string.isRequired,
    }).isRequired,
};

export default NewCard;
