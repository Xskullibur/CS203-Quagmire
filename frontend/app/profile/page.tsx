"use client";

import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';

const ProfilePage = () => {
    const router = useRouter();

    useEffect(() => {
        // Retrieve the userId from localStorage 
        const userId = localStorage.getItem('userId');

        if (userId) {
            // Redirect to the user's specific profile page
            router.push(`/profile/${userId}`);
        } else {
            // Redirect to login if no userId is found 
            router.push('/auth/login');
        }
    }, [router]);

    return <div>Redirecting to your profile...</div>;
};

export default ProfilePage;