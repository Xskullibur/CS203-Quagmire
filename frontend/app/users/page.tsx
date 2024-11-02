'use client';

import React, { useEffect, useState } from 'react';
import { Skeleton } from "@/components/ui/skeleton";
import axiosInstance from '@/lib/axios';

interface User {
  username: string;
  email: string;
  isVerified: boolean;
}

const UserPage: React.FC = () => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    // Fetch user data from the API
    const fetchUserData = async () => {
      try {
        const response = await axiosInstance.get(`${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/users`);
        setUser(response.data);
      } catch (error) {
        console.error("Failed to fetch user data", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserData();
  }, []);

  const handleResendVerification = async () => {
    try {
      const response = await axiosInstance.post(`${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/users/resend-email-verification`);
      if (response.status === 200) {
        setMessage("Verification email has been resent.");
      } else {
        setMessage("Failed to resend verification email. Please try again.");
      }
    } catch (error) {
      console.error("Error resending verification email:", error);
      setMessage("An error occurred. Please try again.");
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen p-8 flex flex-col items-center">
        <div className="w-96 p-4 space-y-4">
          <Skeleton className="h-[50px] w-full rounded-lg" />
          <Skeleton className="h-[50px] w-full rounded-lg" />
          <Skeleton className="h-[50px] w-full rounded-lg" />
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen p-8 flex flex-col items-center mt-24"> {/* Added mt-24 for top margin */}
      <h1 className="text-3xl font-bold mb-8 text-center">Account Information</h1>
      {user ? (
        <div className="bg-primary-foreground p-6 rounded-lg shadow-md w-96 space-y-4 text-center">
          <div className="p-4 bg-zinc-800 rounded-md shadow-sm">
            <h2 className="text-lg font-semibold">Username</h2>
            <p className="text-xl text-gray-200">{user.username}</p>
          </div>
          <div className="p-4 bg-zinc-800 rounded-md shadow-sm">
            <h2 className="text-lg font-semibold">Email</h2>
            <p className="text-xl text-gray-200">{user.email}</p>
          </div>
          <div className="p-4 bg-zinc-800 rounded-md shadow-sm">
            <h2 className="text-lg font-semibold">Verification Status</h2>
            <p className="text-xl text-gray-200">{user.isVerified ? "Verified" : "Not Verified"}</p>
          </div>
          {!user.isVerified && (
            <button
              onClick={handleResendVerification}
              className="w-full bg-accent text-white py-2 px-4 rounded-md hover:bg-accent-dark transition duration-300"
            >
              Resend Email Verification
            </button>
          )}
          {message && <p className="text-sm text-gray-400 mt-4">{message}</p>}
        </div>
      ) : (
        <p className="text-red-500 text-center">Failed to load user information.</p>
      )}
    </div>
  );
};

export default UserPage;
