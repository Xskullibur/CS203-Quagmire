import { useEffect, useState } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import { useAuth } from '@/hooks/useAuth';
import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;

export const useProfileCheck = () => {
  const { user, isLoading: authLoading } = useAuth();
  const [isChecking, setIsChecking] = useState(true);
  const [hasProfile, setHasProfile] = useState(false);
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    const checkProfile = async () => {
      if (!user?.userId) return;

      try {
        const response = await axios.get(new URL(`/profile/${user.userId}`, API_URL).toString());
        setHasProfile(true);
      } catch (error: any) {
        if (error.response?.status === 404) {
          // Store the current path in sessionStorage before redirecting
          sessionStorage.setItem('returnPath', pathname);
          router.push('/profile/edit?new=true');
        }
        setHasProfile(false);
      } finally {
        setIsChecking(false);
      }
    };

    if (!authLoading && user) {
      checkProfile();
    } else if (!authLoading) {
      setIsChecking(false);
    }
  }, [user, authLoading, router, pathname]);

  return { isChecking, hasProfile };
};