// app/auth/login/page.tsx
"use client";

import React, { useState } from 'react';
import axios from 'axios';
import { useRouter } from 'next/navigation';

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

const Login: React.FC = () => {
    const [formData, setFormData] = useState({ username: '', password: '' });
    const router = useRouter();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await axios.post(new URL('/authentication/login', API_URL).toString(), formData);
            if (response.status === 200) {
                localStorage.setItem('userId', response.data.userId);
                localStorage.setItem('username', response.data.username);
                localStorage.setItem('token', response.data.token);
                router.push('/profile');
            } else {
                alert('Invalid credentials');
            }
        } catch (error: any) {
            alert('Login failed');
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-background">
            <h2 className="text-2xl font-bold mb-4 text-foreground">Login</h2>
            <form className="w-80" onSubmit={handleSubmit}>
                <div className="mb-4">
                    <label className="block text-foreground">Username</label>
                    <input
                        type="text"
                        name="username"
                        value={formData.username}
                        onChange={handleChange}
                        className="w-full p-2 border border-border rounded-md"
                        style={{ backgroundColor: "hsl(var(--input))", borderRadius: "calc(var(--radius) - 4px)" }}
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-foreground">Password</label>
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        className="w-full p-2 border border-border rounded-md"
                        style={{ backgroundColor: "hsl(var(--input))", borderRadius: "calc(var(--radius) - 4px)" }}
                    />
                </div>
                <div>
                    <button
                        type="submit"
                        className="w-full p-2 transform transition duration-300 hover:scale-105 bg-primary text-primary-foreground rounded-md hover:bg-primary-foreground hover:text-primary"
                        style={{ borderRadius: "calc(var(--radius) - 4px)" }}
                    >
                        Login
                    </button>
                </div>
            </form>
        </div>
    );
};

export default Login;
