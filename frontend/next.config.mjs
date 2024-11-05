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
    }
};

export default nextConfig;
