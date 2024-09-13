"use client";

import { useParams } from 'next/navigation';
import React, { useState, useEffect } from 'react';

const Profile = () => {
    // Handles dynamic route params
    const { id } = useParams();
    const [userData, setUserData] = useState<any>(null);

    useEffect(() => {

        const id = window.location.pathname.split("/").pop();

        if (id) {
            // Fetch user data from the backend using the dynamic route ID
            fetch(`http://localhost:8080/profile/${id}`)
                .then(response => response.json())
                .then(data => setUserData(data))
                .catch(error => console.error('Error fetching profile:', error));
        }
    }, [id]);

    if (!userData) return <div>Loading...</div>;

    return (
        <div className="bg-[#212121] text-white min-h-screen p-8 flex flex-col items-center justify-center">
            <h1 className="text-4xl font-bold mb-8">{userData.firstName} {userData.lastName}&apos;s Profile</h1>

            <div className="grid grid-cols-2 gap-8 w-full max-w-6xl">

                {/* Personal Info */}
                <div className="bg-[#171717] p-6 rounded-lg shadow-md">
                    <h2 className="text-2xl font-semibold mb-4">Personal Info</h2>
                    <p><strong>Full Name:</strong> {userData.firstName} {userData.lastName}</p>
                    <p><strong>Country:</strong> {userData.country}</p>
                    <p><strong>Date of Birth:</strong> {userData.dateOfBirth}</p>
                </div>

                {/* Bio  */}
                <div className="bg-[#171717] p-6 rounded-lg shadow-md">
                    <h2 className="text-2xl font-semibold mb-4">Bio</h2>
                    <p>{userData.bio}</p>
                </div>

                {/* Game Info */}
                <div className="bg-[#171717] p-6 rounded-lg shadow-md col-span-2">
                    <h2 className="text-2xl font-semibold mb-4">Game Info</h2>
                    <p><strong>Current Rating:</strong> {userData.currentRating}</p>
                </div>
            </div>
        </div>
    );
};

export default Profile;