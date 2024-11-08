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
  ranking: number;
  achievements: Achievement[];
  tournaments: Tournament[];
}

const ProfileCard = ({ playerProfile, ranking, achievements, tournaments }: ProfileCardProps) => {
  const contentRef = useRef<HTMLDivElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const [isSheetOpen, setIsSheetOpen] = useState(false);
  const [activePanel, setActivePanel] = useState<string>(""); // Track which panel to show
  const router = useRouter()

  const handleButtonClick = (panel: string) => {
    setActivePanel(panel);  // Set the active panel based on the button clicked
    setIsSheetOpen(true);  // Open the sheet when any button is clicked
  };

  function handleEditProfile(): void {
    router.push("/profile/edit")
  }

  return (
    <div className="relative w-full h-full rounded-lg overflow-hidden flex items-center justify-center mt-16">
      <div
        className="absolute inset-0 bg-cover bg-center"
        style={{ backgroundImage: `url("/img/arm-wrestling-bg.jpg")` }}
      />
      <div className="absolute inset-0 bg-primary-foreground/80" />

      <div
        ref={containerRef}
        className="relative w-full min-h-[calc(100vh-8rem)] z-20 flex flex-col justify-center p-10 text-primary"
      >
        <Sheet onOpenChange={setIsSheetOpen}>
          <div className="fixed right-[-4rem] z-50 flex flex-col items-center gap-32">
            <TriggerButton isOpen={isSheetOpen} label="My Statistics" onClick={() => handleButtonClick("stats")}/>
            <TriggerButton isOpen={isSheetOpen} label="My Achievements" onClick={() => handleButtonClick("achievements")}/>
            <TriggerButton isOpen={isSheetOpen} label="My Tournaments" onClick={() => handleButtonClick("tournaments")}/>
          </div>

          <div
            ref={contentRef}
            className="relative flex flex-col items-center transition-all duration-300 w-full"
          >
            <PlayerInfo playerProfile={playerProfile} />
            <Button onClick={handleEditProfile}>Edit Profile</Button>
          </div>

          <SheetContent
            containerRef={contentRef}
            className="min-h-[calc(100vh-8rem)] p-8 overflow-y-auto bg-primary-foreground/60"
          >
            {activePanel === "stats" && (
              <StatisticsPanel playerProfile={playerProfile} ranking={ranking} />
            )}

            {activePanel === "achievements" && (
              <AchievementsPanel achievements={achievements} />
            )}

            {activePanel === "tournaments" && (
              <TournamentsPanel tournaments={tournaments} />
            )}
          </SheetContent>

        </Sheet>
      </div>
    </div>
  );
};

export default ProfileCard;
