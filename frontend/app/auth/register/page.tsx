"use client";

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import axiosInstance from '@/lib/axios';
import { Alert, AlertDescription } from '@/components/ui/alert';

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

/**
 * Register component for user registration.
 * 
 * This component renders a registration form that allows users to create a new account.
 * It includes fields for username, email, password, and password confirmation.
 * The form performs client-side validation to ensure that the passwords match.
 * 
 * @returns {JSX.Element} The rendered registration form component.
 * 
 * @remarks
 * - Uses React hooks for state management.
 * - Uses Tailwind CSS for styling.
 * - Uses axios for making HTTP requests.
 * - Redirects to the login page upon successful registration.
 * 
 * @function
 * @name Register
 * 
 * @typedef {Object} FormData
 * @property {string} username - The username entered by the user.
 * @property {string} email - The email entered by the user.
 * @property {string} password - The password entered by the user.
 * @property {string} confirmPassword - The password confirmation entered by the user.
 * 
 * @typedef {Object} Error
 * @property {string | null} error - The error message to be displayed if any.
 * 
 * @typedef {Object} Router
 * @property {Function} push - Function to navigate to a different route.
 * 
 * @typedef {Object} Event
 * @property {Function} preventDefault - Function to prevent the default form submission behavior.
 * 
 * @typedef {Object} AxiosResponse
 * @property {number} status - The HTTP status code of the response.
 * 
 * @typedef {Object} AxiosError
 * @property {Object} response - The response object containing error details.
 * @property {Object} data - The data object containing error description.
 * 
 * @typedef {Object} InputProps
 * @property {string} type - The type of the input field.
 * @property {string} name - The name of the input field.
 * @property {string} placeholder - The placeholder text for the input field.
 * @property {string} value - The current value of the input field.
 * @property {Function} onChange - The function to handle input changes.
 * @property {string} className - The CSS class for styling the input field.
 * 
 * @typedef {Object} ButtonProps
 * @property {string} type - The type of the button.
 * @property {string} className - The CSS class for styling the button.
 * 
 * @typedef {Object} LinkProps
 * @property {string} href - The URL to navigate to when the link is clicked.
 * 
 * @typedef {Object} AlertProps
 * @property {string} variant - The variant of the alert (e.g., "destructive").
 * @property {string} className - The CSS class for styling the alert.
 * 
 * @typedef {Object} AlertDescriptionProps
 * @property {string} children - The content of the alert description.
 * 
 * @typedef {Object} DivProps
 * @property {string} className - The CSS class for styling the div.
 */
const Register: React.FC = () => {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [error, setError] = useState<string | null>(null);
    const router = useRouter();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleConfirmPassword = (e: React.ChangeEvent<HTMLInputElement>) => {
        const confirmPassword = e.target.value;
        setFormData({ ...formData, confirmPassword });
        if (formData.password !== confirmPassword) {
            setError('Passwords do not match');
        } else {
            setError(null);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }
        try {
            const response = await axiosInstance.post(new URL('/authentication/register', API_URL).toString(), formData);
            if (response.status === 201) {
                router.push('/auth/login');
            }
        } catch (error: any) {
            if (error?.response?.data) {
                setError(error.response.data.description);
            } else {
                setError('Registration failed. Please try again.');
            }
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-primary-foreground">
            <div className="w-96 p-6 bg-primary-foreground rounded-lg shadow-md relative 
        overflow-hidden backdrop-blur-sm hover:backdrop-blur-md transition 
        duration-300 z-10 border border-zinc-700 hover:border-zinc-400 hover:bg-zinc-800/50 shadow-zinc-800">
                <div className="absolute inset-0 bg-gradient-radial from-zinc-700/30 to-transparent opacity-50 pointer-events-none" />
                <h2 className="text-2xl font-bold mb-6 text-white">Register</h2>
                {error && (
                    <Alert variant="destructive" className="mb-4">
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
                    <div className="mb-4">
                        <Input
                            type="email"
                            name="email"
                            placeholder="Email"
                            value={formData.email}
                            onChange={handleChange}
                            className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                        />
                    </div>
                    <div className="mb-4">
                        <Input
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={formData.password}
                            onChange={handleChange}
                            className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                        />
                    </div>
                    <div className="mb-6">
                        <Input
                            type="password"
                            name="confirmPassword"
                            placeholder="Confirm Password"
                            value={formData.confirmPassword}
                            onChange={handleConfirmPassword}
                            className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                        />
                    </div>
                    <Button type="submit" className="w-full bg-primary hover:bg-accent text-black hover:text-white transition duration-300">
                        Register
                    </Button>
                    <div className="mt-4 text-center">
                        <Link href="/auth/login">
                            <span className="text-primary hover:text-zinc-400 transition duration-300 font-semibold text-sm">
                                Already have an account? Login here.
                            </span>
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Register;
