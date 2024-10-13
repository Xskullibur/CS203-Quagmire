'use client'

import React, { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuth } from "@/hooks/useAuth";
import { Alert, AlertDescription } from '@/components/ui/alert';

/**
 * Login component for user authentication.
 * 
 * This component renders a login form that allows users to enter their username and password.
 * Upon submission, it attempts to authenticate the user and redirects them based on their role.
 * 
 * @component
 * @returns {JSX.Element} The rendered login form component.
 *
 * @remarks
 * This component uses the `useAuth` hook for authentication and `useRouter` for navigation.
 * It also manages form state and error handling using React's `useState` hook.
 * 
 * @function
 * @name Login
 * 
 * @typedef {Object} FormData
 * @property {string} username - The username entered by the user.
 * @property {string} password - The password entered by the user.
 * 
 * @typedef {Object} LoginResponse
 * @property {Object} user - The authenticated user object.
 * @property {Object} response - The response object from the login request.
 * @property {number} response.status - The HTTP status code of the login response.
 * 
 * @typedef {Object} ErrorResponse
 * @property {Object} response - The response object from the failed login request.
 * @property {Object} response.data - The data object containing error details.
 * @property {string} response.data.description - The error description.
 * 
 * @hook
 * @name useAuth
 * @description Custom hook to handle authentication logic.
 * 
 * @hook
 * @name useRouter
 * @description Custom hook to handle navigation.
 * 
 * @function
 * @name handleChange
 * @description Handles input changes and updates the form data state.
 * @param {React.ChangeEvent<HTMLInputElement>} e - The input change event.
 * 
 * @function
 * @name handleSubmit
 * @description Handles form submission, attempts to log in the user, and handles errors.
 * @param {React.FormEvent} e - The form submission event.
 * 
 * @state {FormData} formData - The state object containing the form data.
 * @state {string | null} error - The state object containing the error message, if any.
 * 
 * @style
 * @class .flex - Flexbox container for centering content.
 * @class .items-center - Aligns items vertically to the center.
 * @class .justify-center - Aligns items horizontally to the center.
 * @class .min-h-screen - Sets the minimum height to the full viewport height.
 * @class .bg-primary-foreground - Sets the background color to the primary foreground color.
 * @class .w-80 - Sets the width to 80 units.
 * @class .p-6 - Sets padding to 6 units.
 * @class .rounded-lg - Applies large border radius for rounded corners.
 * @class .shadow-md - Applies medium shadow for depth.
 * @class .relative - Sets position to relative.
 * @class .overflow-hidden - Hides overflow content.
 * @class .backdrop-blur-sm - Applies small backdrop blur effect.
 * @class .hover:backdrop-blur-md - Applies medium backdrop blur effect on hover.
 * @class .transition - Applies transition effect.
 * @class .duration-300 - Sets transition duration to 300ms.
 * @class .z-10 - Sets z-index to 10.
 * @class .border - Applies border.
 * @class .border-zinc-700 - Sets border color to zinc-700.
 * @class .hover:border-zinc-400 - Sets border color to zinc-400 on hover.
 * @class .hover:bg-zinc-800/50 - Sets background color to zinc-800 with 50% opacity on hover.
 * @class .shadow-zinc-800 - Sets shadow color to zinc-800.
 * @class .absolute - Sets position to absolute.
 * @class .inset-0 - Sets all inset properties to 0.
 * @class .bg-gradient-radial - Applies radial gradient background.
 * @class .from-zinc-700/30 - Sets gradient start color to zinc-700 with 30% opacity.
 * @class .to-transparent - Sets gradient end color to transparent.
 * @class .opacity-50 - Sets opacity to 50%.
 * @class .pointer-events-none - Disables pointer events.
 * @class .text-2xl - Sets text size to 2xl.
 * @class .font-bold - Applies bold font weight.
 * @class .mb-6 - Sets bottom margin to 6 units.
 * @class .text-white - Sets text color to white.
 * @class .mb-4 - Sets bottom margin to 4 units.
 * @class .alert - Applies alert styling.
 * @class .bg-transparent - Sets background to transparent.
 * @class .border-b - Applies bottom border.
 * @class .border-zinc-600 - Sets border color to zinc-600.
 * @class .placeholder-zinc-500 - Sets placeholder color to zinc-500.
 * @class .w-full - Sets width to 100%.
 * @class .bg-primary - Sets background color to primary.
 * @class .hover:bg-accent - Sets background color to accent on hover.
 * @class .text-black - Sets text color to black.
 * @class .hover:text-white - Sets text color to white on hover.
 * @class .mt-4 - Sets top margin to 4 units.
 * @class .text-center - Centers text.
 * @class .text-primary - Sets text color to primary.
 * @class .hover:text-zinc-400 - Sets text color to zinc-400 on hover.
 * @class .font-semibold - Applies semi-bold font weight.
 * @class .text-sm - Sets text size to small.
 */
const Login: React.FC = () => {
    const [formData, setFormData] = useState({ username: "", password: "" });
    const [error, setError] = useState<string | null>(null);
    const router = useRouter();
    const { login } = useAuth();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        try {
            const { user, response } = await login(formData.username, formData.password);
            if (response.status === 200) {
                router.push(user?.role === "ADMIN" ? "/admin/dashboard" : "/profile");
            }
        } catch (error: any) {
            if (error?.response?.data) {
                setError(error.response.data.description);
            } else {
                setError('Login failed. Please try again.');
            }
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-primary-foreground">
            <div className="w-80 p-6 bg-primary-foreground rounded-lg shadow-md relative
                overflow-hidden backdrop-blur-sm hover:backdrop-blur-md transition
                duration-300 z-10 border border-zinc-700 hover:border-zinc-400 hover:bg-zinc-800/50 shadow-zinc-800">
                <div className="absolute inset-0 bg-gradient-radial from-zinc-700/30 to-transparent opacity-50 pointer-events-none" />
                <h2 className="text-2xl font-bold mb-6 text-white">Login</h2>
                {error && (
                    <Alert variant="destructive" className="mb-4 alert">
                        <AlertDescription>{error}</AlertDescription>
                    </Alert>
                )}
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <Input
                            type="text"
                            name="username"
                            placeholder="Username"
                            value={formData.username}
                            onChange={handleChange}
                            className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                        />
                    </div>
                    <div className="mb-6">
                        <Input
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={formData.password}
                            onChange={handleChange}
                            className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                        />
                    </div>
                    <Button type="submit" className="w-full bg-primary hover:bg-accent text-black hover:text-white transition duration-300">
                        Login
                    </Button>
                    <div className="mt-4 text-center">
                        <Link href="/auth/register">
                            <span className="text-primary hover:text-zinc-400 transition duration-300 font-semibold text-sm">
                                Don&apos;t have an account? Register here.
                            </span>
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;