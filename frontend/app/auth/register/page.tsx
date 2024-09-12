// app/auth/register/page.tsx
"use client";

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import axiosInstance from '@/lib/axios';
import axios from 'axios';

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

const Register: React.FC = () => {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const router = useRouter();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleConfirmPassword = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (formData.password !== e.target.value) {
            (document.querySelector('.passwordError') as HTMLElement)!.style.display = 'block';
            document.querySelector('.registerBtn')!.setAttribute('disabled', 'true');
        } else {
            document.querySelector('.registerBtn')!.removeAttribute('disabled');
            (document.querySelector('.passwordError') as HTMLElement)!.style.display = 'none';
        }
        setFormData({ ...formData, confirmPassword: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (formData.password !== formData.confirmPassword) {
            alert('Passwords do not match');
            return;
        }
        try {
            await axiosInstance.post(new URL('/authentication/register', API_URL).toString(), formData);
            router.push('/auth/login');
        } catch (error: any) {
            if (axios.isAxiosError(error)) {
                alert(`Registration failed: ${error.response?.data.description}`);
            } else {
                alert(`Registration failed: ${error.message}`);
            }
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-zinc-900">
            <div className="w-96 p-6 bg-zinc-900 rounded-lg shadow-md relative 
        overflow-hidden backdrop-blur-sm hover:backdrop-blur-md transition 
        duration-300 z-10 border border-zinc-700 hover:border-zinc-400 hover:bg-zinc-800/50 shadow-zinc-800">
                <div className="absolute inset-0 bg-gradient-radial from-zinc-700/30 to-transparent opacity-50 pointer-events-none" />
                <h2 className="text-2xl font-bold mb-6 text-white">Register</h2>
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
                    <Button type="submit" className="registerBtn w-full bg-primary hover:bg-accent text-black hover:text-white transition duration-300">
                        Register
                    </Button>
                    <p className='passwordError text-destructive' style={{ display: 'none' }}>Passwords do not match</p>
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
