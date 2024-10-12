// app/not-found.tsx
"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import Image from "next/image";
import { useAuth } from "@/hooks/useAuth";
import withAuth from "@/hooks/withAuth";
import axiosInstance from "@/lib/axios";
import { useErrorHandler } from "@/app/context/ErrorMessageProvider";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

const VerificationFailed: React.FC = () => {
  const { showErrorToast } = useErrorHandler();

  const router = useRouter();
  const { user } = useAuth();

  const handleResendVerification = async () => {
    try {
      const response = await axiosInstance.post(
        new URL("/authentication/send-verification", API_URL).toString(),
        user!.userId
      );

      if (response.status === 201) {
        router.push("/auth/login");
      }
    } catch (error: any) {
      showErrorToast(
        "Error",
        "Failed to resend verification email. Please try again at a later time."
      );

      if (error?.response?.data) {
        console.error(error.response.data.description);
      }
    }
  };

  return (
    <div className="flex flex-col items-center justify-center h-screen z-10">
      <div className="w-3/5 h-1/3 relative">
        <Image
          src="/img/No data-cuate.svg"
          alt="token expired"
          layout="fill"
          objectFit="contain"
        />
      </div>
      <h2 className="text-2xl font-bold mb-4 mt-8">Token Expired</h2>
      <p className="text-lg mb-4 text-muted-foreground">
        Your verification token has expired. Please click the button below to
        resend the verification email.
      </p>
      <Button
        variant="outline"
        className="hover:bg-muted transition duration-300"
        onClick={handleResendVerification}
      >
        Resend Verification Email
      </Button>
    </div>
  );
};

export default withAuth(VerificationFailed);
