interface StatisticItemProps {
  label: string;
  value: string | number;
}

export const StatisticItem = ({ label, value }: StatisticItemProps) => (
  <div>
    <h3 className="text-sm font-medium text-gray-500 dark:text-gray-400">
      {label}
    </h3>
    <p className="text-2xl font-semibold mt-1">{value}</p>
  </div>
);

import { SheetHeader, SheetTitle } from "@/components/ui/sheet";
import { PlayerProfile } from "@/types/player-profile";
import { calculateAge } from "@/utils/dateUtils";

interface StatisticsPanelProps {
  playerProfile: PlayerProfile;
  ranking: number;
}

export const StatisticsPanel = ({
  playerProfile,
  ranking,
}: StatisticsPanelProps) => (
  <div className="h-full flex flex-col justify-center">
    <SheetHeader>
      <SheetTitle className="text-3xl font-bold mb-6">
        My Statistics
      </SheetTitle>
    </SheetHeader>

    <div className="space-y-8">
      <div className="rounded-lg p-6 shadow-sm">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <StatisticItem label="Username" value={playerProfile.username} />
          <StatisticItem
            label="Age"
            value={calculateAge(playerProfile.dateOfBirth)}
          />
        </div>
      </div>

      <div className="rounded-lg p-6 shadow-sm">
        <StatisticItem label="Country" value={playerProfile.country} />
      </div>

      <div className="rounded-lg p-6 shadow-sm">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <StatisticItem
            label="Current Rating"
            value={playerProfile.glickoRating}
          />
          <StatisticItem label="Ranking" value={`#${ranking}`} />
        </div>
      </div>
    </div>
  </div>
);
