"use client";

import React, { useEffect, useState } from 'react';
import { useAuth } from '@/hooks/useAuth';
import withAuth from '@/hooks/withAuth';

const Profile: React.FC = () => {
    const { user } = useAuth();
    const [username, setUsername] = useState('Guest');

    useEffect(() => {
        const storedUsername = user?.username ?? 'Guest';
        setUsername(storedUsername);
    }, [user]);

    return (
        <div className = "flex flex-col items-center justify-center min-h-screen">
            <h2 className = "text-2xl font-bold mb-4">Profile</h2>
            <p>Welcome, {username}!</p>
            <p>Email: {user!.email}</p>
            <p>UserId: {user!.userId}</p>
        </div>
    );
};

export default withAuth(Profile);