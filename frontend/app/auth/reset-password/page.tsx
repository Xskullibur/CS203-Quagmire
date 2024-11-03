"use client";

import React, { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Alert, AlertDescription } from "@/components/ui/alert";
import axios from "axios";
import axiosInstance from "@/lib/axios";

const ResetPasswordPage: React.FC = () => {
    const [formData, setFormData] = useState({
        username: "",
        currentPassword: "",
        newPassword: "",
        confirmPassword: "", // Added confirmPassword field
    });
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleConfirmPassword = (e: React.ChangeEvent<HTMLInputElement>) => {
        const confirmPassword = e.target.value;
        setFormData({ ...formData, confirmPassword });
        if (formData.newPassword !== confirmPassword) {
            setError("Passwords do not match");
        } else {
            setError(null);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        if (formData.newPassword !== formData.confirmPassword) {
            setError("Passwords do not match");
            return;
        }

        try {
            const response = await axios.put(
                `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/authentication/reset-password`,
                {
                    username: formData.username, 
                    currentPassword: formData.currentPassword,
                    newPassword: formData.newPassword,
                }
            );

            if (response.status === 200) {
                setSuccess("Password updated successfully. You can now log in with your new password.");
            }
        } catch (error: any) {
            setError("Failed to update password. Please check your credentials and try again.");
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-primary-foreground">
            <div className="w-80 p-6 bg-primary-foreground rounded-lg shadow-md">
                <h2 className="text-2xl font-bold mb-6 text-white">Reset Password</h2>
                <form onSubmit={handleSubmit}>
                    {error && (
                        <Alert variant="destructive" className="mb-4">
                            <AlertDescription>{error}</AlertDescription>
                        </Alert>
                    )}
                    {success && (
                        <Alert variant="successful" className="mb-4">
                            <AlertDescription>{success}</AlertDescription>
                        </Alert>
                    )}
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
                            type="password"
                            name="currentPassword"
                            placeholder="Current Password"
                            value={formData.currentPassword}
                            onChange={handleChange}
                            className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                        />
                    </div>
                    <div className="mb-4">
                        <Input
                            type="password"
                            name="newPassword"
                            placeholder="New Password"
                            value={formData.newPassword}
                            onChange={handleChange}
                            className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                        />
                    </div>
                    <div className="mb-6">
                        <Input
                            type="password"
                            name="confirmPassword"
                            placeholder="Confirm New Password"
                            value={formData.confirmPassword}
                            onChange={handleConfirmPassword}
                            className="bg-transparent border-b border-zinc-600 text-white placeholder-zinc-500 transition duration-300"
                        />
                    </div>
                    <Button type="submit" className="w-full bg-primary hover:bg-accent text-black hover:text-white transition duration-300">
                        Reset Password
                    </Button>
                </form>
            </div>
        </div>
    );
};

export default ResetPasswordPage;
