import { Sheet, SheetContent } from "@/components/ui/sheet";
import { useRef, useState } from "react";
import { PlayerProfile } from "@/types/player-profile";
import { TriggerButton } from "./TriggerButton";
import { PlayerInfo } from "./PlayerInfo";
import { StatisticsPanel } from "./StatisticsPanel";

interface ProfileCardProps {
  playerProfile: PlayerProfile;
  ranking: number;
}

const ProfileCard = ({ playerProfile, ranking }: ProfileCardProps) => {
  const contentRef = useRef<HTMLDivElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const [isSheetOpen, setIsSheetOpen] = useState(false);

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
          <TriggerButton isOpen={isSheetOpen} />

          <div
            ref={contentRef}
            className="relative flex flex-col items-center transition-all duration-300 w-full"
          >
            <PlayerInfo playerProfile={playerProfile} />
            {/* Achievements section can be added here */}
            <div></div>
          </div>

          <SheetContent
            containerRef={contentRef}
            className="min-h-[calc(100vh-8rem)] p-8 overflow-y-auto bg-primary-foreground/60"
          >
            <StatisticsPanel playerProfile={playerProfile} ranking={ranking} />
          </SheetContent>
        </Sheet>
      </div>
    </div>
  );
};

export default ProfileCard;
