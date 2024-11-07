'use client';

import React, { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Alert, AlertDescription } from "@/components/ui/alert";
import axiosInstance from "@/lib/axios";
import { useRouter } from 'next/navigation';

const UpdateEmailPage: React.FC = () => {
  const router = useRouter();
  const [formData, setFormData] = useState({
    newEmail: "",
    password: ""
  });
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    try {
      const response = await axiosInstance.put(`${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/users/update-email`, {
        newEmail: formData.newEmail,
        password: formData.password
      });

      if (response.status === 200) {
        setSuccess("Email updated successfully.");
        setTimeout(() => router.push("/users"), 2000); // Redirect to /users after 2 seconds
      }
    } catch (error: any) {
      console.error("Failed to update email:", error);
      setError("Failed to update email. Please check your password and try again.");
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-primary-foreground">
      <div className="w-full max-w-md p-6 bg-primary-foreground rounded-lg shadow-md text-center">
        <h2 className="text-2xl font-bold mb-6 text-white">Update Email</h2>
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
              type="email"
              name="newEmail"
              placeholder="Enter new email"
              value={formData.newEmail}
              onChange={handleChange}
              required
            />
          </div>
          <div className="mb-4">
            <Input
              type="password"
              name="password"
              placeholder="Enter your password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>
          <Button type="submit" className="w-full bg-primary hover:bg-accent text-black hover:text-white transition duration-300">
            Update Email
          </Button>
        </form>
      </div>
    </div>
  );
};

export default UpdateEmailPage;
