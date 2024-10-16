"use client";

import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/hooks/useAuth';
import withAuth from '@/hooks/withAuth';

const ProfilePage = () => {
    const router = useRouter();
    const { user } = useAuth();

    useEffect(() => {
        router.push(`/profile/${user?.userId}`);
    }, [router, user]);

    return <div>Redirecting to your profile...</div>;
};

export default withAuth(ProfilePage);
