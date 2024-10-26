import { PlayerProfile } from "./player-profile";

export interface PlayerProfileRequest {
  id: string;
  profileUpdates: PlayerProfile;
  profileImage: File;
}