import { Sheet, SheetContent } from "@/components/ui/sheet";
import { useRef, useState } from "react";
import { PlayerProfile } from "@/types/player-profile";
import { TriggerButton } from "./TriggerButton";
import { PlayerInfo } from "./PlayerInfo";
import { StatisticsPanel } from "./StatisticsPanel";
import { AchievementsPanel } from "./AchievementsPanel";
import { Achievement } from "@/types/achievement";
import { Tournament } from "@/types/tournament";
import { TournamentsPanel } from "./TournamentsPanel";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";

interface ProfileCardProps {
  playerProfile: PlayerProfile;
  ranking: number | null;
  rankPercentage : number | null;
  achievements: Achievement[];
  tournaments: Tournament[];
  isOwnProfile?: boolean;
}

const ProfileCard = ({ 
  playerProfile, 
  ranking, 
  rankPercentage,
  achievements, 
  tournaments, 
  isOwnProfile = false 
}: ProfileCardProps) => {
  const contentRef = useRef<HTMLDivElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const [isSheetOpen, setIsSheetOpen] = useState(false);
  const [activePanel, setActivePanel] = useState<string>("");
  const router = useRouter();

  const handleButtonClick = (panel: string) => {
    setActivePanel(panel);
    setIsSheetOpen(true);
  };

  function handleEditProfile(): void {
    router.push("/profile/edit");
  }

  const getButtonLabel = (base: string) => {
    return isOwnProfile ? base : `${playerProfile.username}'s ${base.slice(3)}`;
  };

  return (
    <div className="relative w-full h-full rounded-lg overflow-hidden flex items-center justify-center mt-16">
      <div
        ref={containerRef}
        className="relative w-full min-h-[calc(100vh-8rem)] z-20 flex flex-col justify-center p-10 text-primary"
      >
        <Sheet onOpenChange={setIsSheetOpen}>
          <div className="fixed right-[-4rem] z-50 flex flex-col items-center gap-32">
            <TriggerButton 
              isOpen={isSheetOpen} 
              label={getButtonLabel("My Statistics")} 
              onClick={() => handleButtonClick("stats")}
            />
            <TriggerButton 
              isOpen={isSheetOpen} 
              label={getButtonLabel("My Achievements")} 
              onClick={() => handleButtonClick("achievements")}
            />
            <TriggerButton 
              isOpen={isSheetOpen} 
              label={getButtonLabel("My Tournaments")} 
              onClick={() => handleButtonClick("tournaments")}
            />
          </div>

          <div
            ref={contentRef}
            className="relative flex flex-col items-center transition-all duration-300 w-full"
          >
            <PlayerInfo playerProfile={playerProfile} />
            {isOwnProfile && (
              <Button 
                onClick={handleEditProfile}
                className="mt-4"
              >
                Edit Profile
              </Button>
            )}
          </div>

          <SheetContent
            containerRef={contentRef}
            className="min-h-[calc(100vh-8rem)] p-8 overflow-y-auto bg-primary-foreground/60"
          >
            {activePanel === "stats" && (
              <StatisticsPanel 
                playerProfile={playerProfile} 
                ranking={ranking}
                rankPercentage = {rankPercentage}
              />
            )}

            {activePanel === "achievements" && (
              <AchievementsPanel 
                achievements={achievements}
              />
            )}

            {activePanel === "tournaments" && (
              <TournamentsPanel 
                tournaments={tournaments}
              />
            )}
          </SheetContent>
        </Sheet>
      </div>
    </div>
  );
};

export default ProfileCard;