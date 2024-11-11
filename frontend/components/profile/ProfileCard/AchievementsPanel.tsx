import { Achievement } from "@/types/achievement"; // Define Achievement type
import { SheetHeader, SheetTitle } from "@/components/ui/sheet";
import { Card, CardContent } from "@/components/ui/card";

interface AchievementsPanelProps {
    achievements: Achievement[];
}

export const AchievementsPanel = ({
    achievements
}: AchievementsPanelProps) => {
    return (
        <div className="h-full flex flex-col justify-center">
            <SheetHeader>
                <SheetTitle className="text-3xl font-bold mb-6">
                    My Achievements
                </SheetTitle>
            </SheetHeader>

            {/* Step 1: Check if achievements exist */}
            {achievements.length === 0 ? (
                <p>You are on the way to getting your first achievement!</p>
            ) : (
                // Step 2: Display achievements if available
                <div className="space-y-2">
                    {achievements.map((achievement) => (
                        <div className="mb-4" key={achievement.id}>
                            <Card>
                                <CardContent className='flex justify-between items-end py-4 items-center'>
                                    <div className=''>{achievement.name}</div>
                                    <div className='w-40 md:w-48 lg:w-60 xl:w-80 2xl:w-96'>{achievement.description}</div>
                                </CardContent>
                            </Card>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};
