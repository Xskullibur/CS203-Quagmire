// app/not-found.tsx
"use client";

import React from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import Image from "next/image";
import { useAuth } from "@/hooks/useAuth";
import axiosInstance from "@/lib/axios";
import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import { toast } from "@/hooks/use-toast";
import { AxiosError } from "axios";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

const VerificationFailed: React.FC = () => {
  const { handleError } = useGlobalErrorHandler();
  const router = useRouter();
  const { user, isAuthenticated } = useAuth();

  const handleResendVerification = async () => {
    axiosInstance
      .post(
        new URL("/authentication/send-verification-email", API_URL).toString(),
        user!.userId,
        {
          headers: {
            "Content-Type": "text/plain",
          },
        }
      )
      .then((response) => {
        if (response.status === 201) {
          
          toast({
            variant: "success",
            title: "Success",
            description: "Successfully resend verification email.",
          });

          router.push("/auth/login");
        }
      })
      .catch((error: AxiosError) => {
        handleError(error);
      });
  };

  const handleSignIn = () => {
    router.push(`/auth/login?redirect=/auth/verification-failed`);
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
      {isAuthenticated ? (
        <Button
          variant="outline"
          className="hover:bg-muted transition duration-300"
          onClick={handleResendVerification}
        >
          Resend Verification Email
        </Button>
      ) : (
        <Button
          variant="outline"
          className="hover:bg-muted transition duration-300"
          onClick={handleSignIn}
        >
          Sign-in to resend verification
        </Button>
      )}
    </div>
  );
};

export default VerificationFailed;
