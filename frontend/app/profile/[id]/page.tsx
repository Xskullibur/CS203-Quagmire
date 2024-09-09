"use client";

import { useRouter } from 'next/navigation';
import React, { useState, useEffect } from 'react';

const fakeUserData = {
    1: {id: 1, name: "John", email: "john@google.com" },
    2: {id: 2, name: "Jane", email: "jane@google.com"},
    3: {id: 3, name: "Joseph", email: "Joseph@google.com"},
};

const Profile = () => {
    const router = useRouter();
    const [userData, setUserData] = useState<any>(null);

    useEffect(() => {

        const id = window.location.pathname.split("/").pop();

        if (id) {
            // Fetch corresponding user data 
            const numericId = Number(id);
            const user = fakeUserData[numericId as keyof typeof fakeUserData];
            setUserData(user);
        }
    }, []);

    if (!userData) return <div>Loading...</div>;

    return (
        <div className="flex flex-col items-center justify-center min-h-screen">
            <h2 className="text-2xl font-bold mb-4">Profile</h2>
            <p>Welcome, {userData.name}</p>
            <p>Email: {userData.email}</p>
        </div>
    );
};

export default Profile;