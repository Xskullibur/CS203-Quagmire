"use client";

import React, { useEffect, useState } from 'react';

const Profile: React.FC = () => {
    const [username, setUsername] = useState('Guest');

    useEffect(() => {
        if (typeof localStorage !== 'undefined') {
            const storedUsername = localStorage.getItem('username') || 'Guest';
            setUsername(storedUsername);
        }
    }, []);

    return (
        <div className="flex flex-col items-center justify-center min-h-screen">
            <h2 className="text-2xl font-bold mb-4">Profile</h2>
            <p>Welcome, {username}!</p>
        </div>
    );
};

export default Profile;
