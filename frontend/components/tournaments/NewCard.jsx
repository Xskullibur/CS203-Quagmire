// components/tournaments/NewCard.jsx
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { formatDate } from '@/utils/dateFormatter';
import { motion } from 'framer-motion';
import PropTypes from 'prop-types';

const NewCard = ({ tournament, className }) => {
    return (
        <motion.div
            transition={{ duration: 0.2 }}
            className={`h-full ${className}`}
        >
            <Card className="h-full flex flex-col border-border overflow-x-hidden
                 bg-primary-foregorund hover:shadow-[0_0_15px_rgba(200,200,200,0.1)] transition 
                 duration-200 ease-in-out transform hover:bg-zinc-900">
                <CardHeader className="space-y-1 p-4">
                    <CardTitle className="text-lg md:text-xl font-semibold">{tournament.name}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-2 text-sm flex-grow p-4">
                    <div className="grid grid-cols-2 gap-2">
                        <p><span className="font-medium">Start:</span> {formatDate(tournament.start_date)}</p>
                        <p><span className="font-medium">End:</span> {formatDate(tournament.end_date)}</p>
                    </div>
                    <p><span className="font-medium">Registration:</span> {formatDate(tournament.registration_deadline)}</p>
                    <p><span className="font-medium">Location:</span> {tournament.location}</p>
                </CardContent>
                <CardFooter className="p-4">
                    <Button size="sm" variant="primary" className="w-full rounded-full
                        transition duration-200 ease-in-out transform hover:-translate-y-0.5 hover:shadow-lg border">
                        View Tournament
                    </Button>
                </CardFooter>
            </Card>
        </motion.div>
    );
};

NewCard.propTypes = {
    tournament: PropTypes.shape({
        name: PropTypes.string.isRequired,
        location: PropTypes.string.isRequired,
        start_date: PropTypes.string.isRequired,
        end_date: PropTypes.string.isRequired,
        registration_deadline: PropTypes.string.isRequired,
    }).isRequired,
};

export default NewCard;
