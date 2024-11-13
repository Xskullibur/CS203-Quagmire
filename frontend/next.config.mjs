/** @type {import('next').NextConfig} */

const nextConfig = {
    reactStrictMode: false,
    images: {
        remotePatterns: [
            {
                hostname: 'firebasestorage.googleapis.com'
            },
            {
                hostname: 'api.dicebear.com'
            }
        ]
    },
    env: {
        NEXT_PUBLIC_PROFILEPICTURE_API_URL: process.env.NEXT_PUBLIC_PROFILEPICTURE_API_URL,
        NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL,
        NEXT_PUBLIC_FIREBASE_BASE_URL: process.env.NEXT_PUBLIC_FIREBASE_BASE_URL,
        NEXT_PUBLIC_SPRINGBOOT: process.env.NEXT_PUBLIC_SPRINGBOOT
    }
};

export default nextConfig;
