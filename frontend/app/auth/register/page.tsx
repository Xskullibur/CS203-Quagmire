// app/auth/register/page.tsx
"use client";

import React, { useState } from 'react';
import axios from 'axios';
import { useRouter } from 'next/navigation';

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
            const response = await axios.post(new URL('/authentication/register', API_URL).toString(), formData);
            if (response.status === 201) {
                router.push('/auth/login');
            } else {
                alert('Registration failed');
            }
        } catch (error: any) {
            alert('Registration failed');
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-background">
            <h2 className="text-2xl font-bold mb-4 text-foreground">Register</h2>
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
                    <label className="block text-foreground">Email</label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
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
                <div className="mb-4">
                    <label className="block text-foreground">Confirm Password</label>
                    <input
                        type="password"
                        name="confirmPassword"
                        value={formData.confirmPassword}
                        onChange={handleConfirmPassword}
                        className="w-full p-2 border border-border rounded-md"
                        style={{ backgroundColor: "hsl(var(--input))", borderRadius: "calc(var(--radius) - 4px)" }}
                    />
                </div>
                <button className='registerBtn transform transition duration-300 hover:scale-105 hover:cursor-pointer w-full p-2 bg-primary text-primary-foreground rounded-md hover:bg-primary-foreground hover:text-primary' type="submit" disabled>
                    Register
                </button>
                <p className='passwordError text-destructive' style={{ display: 'none' }}>Passwords do not match</p>
            </form>
        </div>
    );
};

export default Register;
