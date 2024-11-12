import { useState } from "react";
import { PlayerProfile } from "@/types/player-profile";

const PROFILE_IMAGE_API = 'https://api.dicebear.com/9.x/initials/png?fontFamily=Georgia&backgroundType=gradientLinear&seed=';


interface ProfileDataState {
    isLoading: boolean;
    profileUpdates: PlayerProfile;
}

export const useProfileData = (initialUsername?: string) => {
    const [dataState, setDataState] = useState<ProfileDataState>({
        isLoading: true,
        profileUpdates: {
            username: initialUsername ?? "",
            profilePicturePath: initialUsername ? `${PROFILE_IMAGE_API} ${initialUsername}` : "",
        } as PlayerProfile,
    });

    const updateProfileData = (updates: Partial<PlayerProfile>) => {
        setDataState(prev => ({
            ...prev,
            profileUpdates: { ...prev.profileUpdates, ...updates },
        }));
    };

    const setLoading = (isLoading: boolean) => {
        setDataState(prev => ({ ...prev, isLoading }));
    };

    const handleInputChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ) => {
        const { name, value } = e.target;
        updateProfileData({ [name]: value || null });
    };

    return {
        dataState,
        updateProfileData,
        setLoading,
        handleInputChange,
    };
};