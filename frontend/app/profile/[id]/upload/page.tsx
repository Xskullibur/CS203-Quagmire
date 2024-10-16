// COMMENTED OUT FOR TESTING/DEMO, WILL CONTINUE LATER ON

// "use client";

// import React, { useState } from 'react';
// import { useRouter } from 'next/navigation';

// const UploadProfilePicture = () => {
//     const [file, setFile] = useState<File | null>(null);
//     const [message, setMessage] = useState('');
//     const router = useRouter();

//     // Extract ID from the URL manually
//     const id = window.location.pathname.split("/").pop();

//     const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
//         const selectedFile = e.target.files && e.target.files[0];
//         setFile(selectedFile || null);
//     };

//     const handleSubmit = async (e: React.FormEvent) => {
//         e.preventDefault();
//         if (!file) {
//             setMessage("Please select a file to upload.");
//             return;
//         }

//         if (!id) {
//             setMessage("User ID is not available.");
//             return;
//         }

//         const formData = new FormData();
//         formData.append("file", file);

//         try {
//             const response = await fetch(`http://localhost:8080/profile/${id}/upload`, {
//                 method: "POST",
//                 body: formData,
//             });

//             if (response.ok) {
//                 setMessage("Profile picture uploaded successfully!");
//             } else {
//                 setMessage("Failed to upload profile picture.");
//             }
//         } catch (error) {
//             console.error("Error:", error);
//             setMessage("An error occurred while uploading the profile picture.");
//         }
//     };

//     return (
//         <div className="min-h-screen bg-[#212121] text-white flex items-center justify-center p-4">
//             <div className="bg-[#171717] p-6 rounded-lg shadow-lg z-10 w-full max-w-lg">
//                 <h1 className="text-3xl font-bold mb-6">Upload Profile Picture</h1>
//                 <form onSubmit={handleSubmit}>
//                     <div className="mb-4">
//                         <input 
//                             type="file" 
//                             onChange={handleFileChange} 
//                             className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-[#4CAF50] file:text-white hover:file:bg-green-500"
//                         />
//                     </div>
//                     <button 
//                         type="submit" 
//                         className="w-full bg-[#4CAF50] text-white py-2 px-4 rounded-lg hover:bg-green-600">
//                         Upload
//                     </button>
//                 </form>
//                 {message && <p className="mt-4 text-center">{message}</p>}
//             </div>
//         </div>
//     );
// };

// export default UploadProfilePicture;