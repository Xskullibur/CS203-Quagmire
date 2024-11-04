import Image from "next/image";
import { PlayerProfile } from "@/types/player-profile";

const PROFILE_IMAGE_API = process.env.NEXT_PUBLIC_PROFILEPICTURE_API_URL;

interface PlayerInfoProps {
  playerProfile: PlayerProfile;
}

export const PlayerInfo = ({ playerProfile }: PlayerInfoProps) => (
  <div className="flex flex-col items-center">
    <Image
      src={playerProfile.profilePicturePath ?? `${PROFILE_IMAGE_API}${playerProfile.username}`}
      alt={`${playerProfile.firstName} ${playerProfile.lastName}`}
      width={100}
      height={100}
      className="rounded-full object-cover"
    />
    <h1 className="text-5xl font-bold mt-4">
      {`${playerProfile.firstName} ${playerProfile.lastName}`}
    </h1>
    <p className="text-lg mt-1 text-center mb-8">{playerProfile.bio}</p>
  </div>
);