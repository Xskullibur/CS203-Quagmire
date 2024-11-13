import { useEffect } from "react";
import { useProfileImage } from "./useProfileImage";
import { useProfileData } from "./useProfileData";
import { useProfileAPI } from "./useProfileAPI";
import { toast } from "../use-toast";
import { useRouter } from "next/navigation";
import axios from "axios";
import { addCacheBustingParameter, cleanupObjectURL } from "@/utils/imageUtils";
import { PlayerProfile } from "@/types/player-profile";

const PROFILE_IMAGE_API = 'https://api.dicebear.com/9.x/initials/png?fontFamily=Georgia&backgroundType=gradientLinear&seed=';


interface UseProfileManagementProps {
    userId?: string;
    username?: string;
    isNewProfile: boolean;
    onSuccess: () => void;
}

export const useProfileManagement = ({
    userId,
    username,
    isNewProfile,
    onSuccess,
}: UseProfileManagementProps) => {

    const router = useRouter();

    const handleRedirect = (title: string, description: string, path: string) => {
        toast({ variant: "default", title, description });
        router.replace(path);
    };

    const {
        imageState,
        handleImageSelect,
        setDefaultImage,
        updateImageState,
    } = useProfileImage(username);

    const {
        dataState,
        updateProfileData,
        setLoading,
        handleInputChange,
    } = useProfileData(username);

    const { checkProfile, fetchProfile, saveProfile } = useProfileAPI({ onSuccess });

    useEffect(() => {
        const initProfile = async () => {
            if (!userId || !username) return;

            try {
                const { exists } = await checkProfile(username);

                if (isNewProfile && exists) {
                    handleRedirect("Profile Exists", "You already have a profile.", "/profile/edit");
                    return;
                }

                if (!isNewProfile && !exists) {
                    handleRedirect(
                        "No Profile Found",
                        "Please create your profile.",
                        "/profile/edit?new=true"
                    );
                    return;
                }

                if (isNewProfile) {
                    const defaultImageUrl = await setDefaultImage(username);
                    if (defaultImageUrl) {
                        updateProfileData({ profilePicturePath: defaultImageUrl });
                    }
                } else {
                    const profile = await fetchProfile(userId);
                    initializeExistingProfile(profile);
                }
            } catch (error) {
                if (axios.isAxiosError(error) && error.response?.status === 404 && isNewProfile) {
                    const defaultImageUrl = await setDefaultImage(username);
                    if (defaultImageUrl) {
                        updateProfileData({ profilePicturePath: defaultImageUrl });
                    }
                }
            } finally {
                setLoading(false);
            }
        };

        initProfile();
        return () => cleanupObjectURL(imageState.currentImageUrl);
    }, [userId, username, isNewProfile]);

    const initializeExistingProfile = (profileData: PlayerProfile) => {
        
        const isDefault = !profileData.profilePicturePath || profileData.profilePicturePath.includes(PROFILE_IMAGE_API!);
        const profilePicturePath = profileData.profilePicturePath || `${PROFILE_IMAGE_API}${profileData.username}`;

        const imageUrl = addCacheBustingParameter(profilePicturePath);

        updateImageState({
            currentImageUrl: imageUrl,
            isDefaultImage: isDefault,
            profileImage: null,
        });

        updateProfileData({
            ...profileData,
            profilePicturePath: imageUrl,
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append("id", userId ?? "");
        formData.append(
            "profileUpdates",
            new Blob([JSON.stringify(dataState.profileUpdates)], {
                type: "application/json",
            })
        );

        if (imageState.profileImage) {
            formData.append("profileImage", imageState.profileImage);
        }

        await saveProfile(formData, isNewProfile);
    };

    return {
        state: {
            ...imageState,
            ...dataState
        },
        isLoading: dataState.isLoading,
        handleInputChange,
        handleImageSelect,
        handleResetToDefault: () => setDefaultImage(dataState.profileUpdates.username),
        handleSubmit,
    };
};