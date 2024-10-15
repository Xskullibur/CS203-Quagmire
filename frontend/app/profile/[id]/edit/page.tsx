"use client";

// For editing profile

import { useParams } from 'next/navigation';
import React, { useState, useEffect } from 'react';

const EditProfile = () => {
    const { id } = useParams();
    const [userData, setUserData] = useState<any>({
        firstName: '',
        lastName: '',
        bio: '',
        country: '',
        dateOfBirth: ''
    });
    const [isLoading, setIsLoading] = useState(true);
    const [message, setMessage] = useState('');

    useEffect(() => {
        const fetchProfile = async () => {
            const response = await fetch(`http://localhost:8080/profile/${id}`);
            const data = await response.json();
            setUserData({
                firstName: data.firstName,
                lastName: data.lastName,
                bio: data.bio,
                country: data.country,
                dateOfBirth: data.dateOfBirth
            });
            setIsLoading(false);
        };

        fetchProfile();
    }, [id]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const response = await fetch(`http://localhost:8080/profile/${id}/edit`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });
        
        if (response.ok) {
            setMessage('Profile updated successfully!');
        } else {
            setMessage('Failed to update profile.');
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setUserData({
            ...userData,
            [e.target.name]: e.target.value
        });
    };

    if (isLoading) return <div>Loading...</div>;

    return (
        <div className="min-h-screen bg-[#212121] text-white flex flex-col items-center justify-center">
            <h1 className="text-3xl font-bold mb-6">Edit Profile</h1>
            <form onSubmit={handleSubmit} className="w-full max-w-2xl bg-[#171717] p-6 rounded-lg shadow-lg">
                <div className="mb-4">
                    <label htmlFor="firstName" className="block text-xl mb-2">First Name:</label>
                    <input
                        type="text"
                        id="firstName"
                        name="firstName"
                        value={userData.firstName}
                        onChange={handleChange}
                        className="w-full p-2 rounded-lg bg-[#333333] text-white"
                    />
                </div>
                <div className="mb-4">
                    <label htmlFor="lastName" className="block text-xl mb-2">Last Name:</label>
                    <input
                        type="text"
                        id="lastName"
                        name="lastName"
                        value={userData.lastName}
                        onChange={handleChange}
                        className="w-full p-2 rounded-lg bg-[#333333] text-white"
                    />
                </div>
                <div className="mb-4">
                    <label htmlFor="bio" className="block text-xl mb-2">Bio:</label>
                    <textarea
                        id="bio"
                        name="bio"
                        value={userData.bio}
                        onChange={handleChange}
                        className="w-full p-2 rounded-lg bg-[#333333] text-white"
                    />
                </div>
                <div className="mb-4">
                    <label htmlFor="country" className="block text-xl mb-2">Country:</label>
                    <input
                        type="text"
                        id="country"
                        name="country"
                        value={userData.country}
                        onChange={handleChange}
                        className="w-full p-2 rounded-lg bg-[#333333] text-white"
                    />
                </div>
                <div className="mb-4">
                    <label htmlFor="dateOfBirth" className="block text-xl mb-2">Date of Birth:</label>
                    <input
                        type="date"
                        id="dateOfBirth"
                        name="dateOfBirth"
                        value={userData.dateOfBirth}
                        onChange={handleChange}
                        className="w-full p-2 rounded-lg bg-[#333333] text-white"
                    />
                </div>
                <button type="submit" className="w-full py-2 px-4 rounded-lg bg-[#4CAF50] text-white">
                    Save Changes
                </button>
            </form>
            {message && <p className="mt-4">{message}</p>}
        </div>
    );
};

export default EditProfile;


/* Code below for use with Jwt Authentication
   Commented out for now to see edit profile page */

// "use client";

// // For editing profile with JWT authentication (no redirect)

// import { useParams } from 'next/navigation';
// import React, { useState, useEffect } from 'react';
// import jwtDecode from 'jwt-decode'; // Use this to decode JWT token

// const EditProfile = () => {
//     const { id } = useParams(); // Get profile ID from URL
//     const [userData, setUserData] = useState<any>({
//         firstName: '',
//         lastName: '',
//         bio: '',
//         country: '',
//         dateOfBirth: ''
//     });
//     const [isLoading, setIsLoading] = useState(true);
//     const [message, setMessage] = useState('');
//     const [isAuthorized, setIsAuthorized] = useState(false); // Track authorization status

//     useEffect(() => {
//         const token = localStorage.getItem('authToken'); // Fetch JWT token from local storage

//         if (!token) {
//             setMessage("You are not logged in or authorized to edit this profile.");
//             setIsLoading(false);
//         } else {
//             try {
//                 const decodedToken: any = jwtDecode(token); // Decode the JWT to get user info
//                 const userId = decodedToken.sub; // Extract the userId from the token

//                 if (userId !== id) {
//                     setMessage("You are not authorized to edit this profile.");
//                     setIsAuthorized(false);
//                     setIsLoading(false);
//                 } else {
//                     // If authorized, fetch the profile data
//                     const fetchProfile = async () => {
//                         const response = await fetch(`http://localhost:8080/profile/${id}`, {
//                             headers: {
//                                 Authorization: `Bearer ${token}`, // Send the token in the request
//                             },
//                         });
//                         const data = await response.json();
//                         setUserData({
//                             firstName: data.firstName,
//                             lastName: data.lastName,
//                             bio: data.bio,
//                             country: data.country,
//                             dateOfBirth: data.dateOfBirth
//                         });
//                         setIsAuthorized(true);
//                         setIsLoading(false);
//                     };

//                     fetchProfile();
//                 }
//             } catch (error) {
//                 setMessage("Invalid token or authentication failed.");
//                 setIsAuthorized(false);
//                 setIsLoading(false);
//             }
//         }
//     }, [id]);

//     // Handle form submission for updating profile
//     const handleSubmit = async (e: React.FormEvent) => {
//         e.preventDefault();

//         const token = localStorage.getItem('authToken'); // Fetch JWT token for update request
//         if (!token || !isAuthorized) {
//             setMessage("You are not authorized to edit this profile.");
//             return;
//         }

//         const response = await fetch(`http://localhost:8080/profile/${id}/edit`, {
//             method: 'PUT',
//             headers: {
//                 'Content-Type': 'application/json',
//                 Authorization: `Bearer ${token}`, // Attach token for authorization
//             },
//             body: JSON.stringify(userData),
//         });
        
//         if (response.ok) {
//             setMessage('Profile updated successfully!');
//         } else {
//             setMessage('Failed to update profile.');
//         }
//     };

//     // Handle form input changes
//     const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
//         setUserData({
//             ...userData,
//             [e.target.name]: e.target.value
//         });
//     };

//     if (isLoading) return <div>Loading...</div>;

//     if (!isAuthorized) {
//         // If not authorized, show message
//         return <div className="text-red-500">{message}</div>;
//     }

//     return (
//         <div className="min-h-screen bg-[#212121] text-white flex flex-col items-center justify-center">
//             <h1 className="text-3xl font-bold mb-6">Edit Profile</h1>
//             <form onSubmit={handleSubmit} className="w-full max-w-2xl bg-[#171717] p-6 rounded-lg shadow-lg">
//                 <div className="mb-4">
//                     <label className="block text-xl mb-2">First Name:</label>
//                     <input
//                         type="text"
//                         name="firstName"
//                         value={userData.firstName}
//                         onChange={handleChange}
//                         className="w-full p-2 rounded-lg bg-[#333333] text-white"
//                     />
//                 </div>
//                 <div className="mb-4">
//                     <label className="block text-xl mb-2">Last Name:</label>
//                     <input
//                         type="text"
//                         name="lastName"
//                         value={userData.lastName}
//                         onChange={handleChange}
//                         className="w-full p-2 rounded-lg bg-[#333333] text-white"
//                     />
//                 </div>
//                 <div className="mb-4">
//                     <label className="block text-xl mb-2">Bio:</label>
//                     <textarea
//                         name="bio"
//                         value={userData.bio}
//                         onChange={handleChange}
//                         className="w-full p-2 rounded-lg bg-[#333333] text-white"
//                     />
//                 </div>
//                 <div className="mb-4">
//                     <label className="block text-xl mb-2">Country:</label>
//                     <input
//                         type="text"
//                         name="country"
//                         value={userData.country}
//                         onChange={handleChange}
//                         className="w-full p-2 rounded-lg bg-[#333333] text-white"
//                     />
//                 </div>
//                 <div className="mb-4">
//                     <label className="block text-xl mb-2">Date of Birth:</label>
//                     <input
//                         type="date"
//                         name="dateOfBirth"
//                         value={userData.dateOfBirth}
//                         onChange={handleChange}
//                         className="w-full p-2 rounded-lg bg-[#333333] text-white"
//                     />
//                 </div>
//                 <button type="submit" className="w-full py-2 px-4 rounded-lg bg-[#4CAF50] text-white">
//                     Save Changes
//                 </button>
//             </form>
//             {message && <p className="mt-4">{message}</p>}
//         </div>
//     );
// };

// export default EditProfile;