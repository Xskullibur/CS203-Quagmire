import axios from "axios";
import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import { toast } from "@/hooks/use-toast";
import axiosInstance from "@/lib/axios";

const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;

interface UseProfileAPIProps {
    onSuccess?: () => void;
}

export const useProfileAPI = ({ onSuccess }: UseProfileAPIProps = {}) => {
    const { handleError } = useGlobalErrorHandler();

    const checkProfile = async (username?: string) => {
        try {
            const response = await axios.get(
                new URL(`/profile`, API_URL).toString(),
                { params: { username } }
            );
            return { exists: response.status === 200, data: response.data };
        } catch (error) {
            if (axios.isAxiosError(error) && error.response?.status === 404) {
                return { exists: false, data: null };
            }
            throw error;
        }
    };

    const fetchProfile = async (userId?: string) => {
        const response = await axios.get(
            new URL(`/profile/${userId}`, API_URL).toString()
        );
        return response.data;
    };

    const saveProfile = async (
        formData: FormData,
        isNewProfile: boolean
    ) => {
        try {
            await axiosInstance({
                method: isNewProfile ? "post" : "put",
                url: `${API_URL}/profile`,
                data: formData,
                headers: { "Content-Type": "multipart/form-data" },
            });

            toast({
                variant: "success",
                title: "Success",
                description: isNewProfile
                    ? "Profile created successfully."
                    : "Profile updated successfully.",
            });

            onSuccess?.();
        } catch (error) {
            if (axios.isAxiosError(error)) {
                handleError(error);
            }
            throw error;
        }
    };

    return {
        checkProfile,
        fetchProfile,
        saveProfile,
    };
};