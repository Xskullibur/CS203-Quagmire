"use client";

import React from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuth } from "@/hooks/useAuth";
import withAuth from "@/hooks/withAuth";
import ProfilePicture from "@/components/profile/ProfilePicture";
import FormFields from "@/components/profile/FormFields";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import EditProfileSkeleton from "@/components/profile/edit/EditProfileSkeletion";
import { useProfileManagement } from "@/hooks/profile/useProfileManagement";

const EditProfile = () => {
  const router = useRouter();
  const { isLoading: authLoading, user } = useAuth();
  const searchParams = useSearchParams();
  const isNewProfile = searchParams.get("new") === "true";

  const {
    state,
    isLoading,
    handleInputChange,
    handleImageSelect,
    handleResetToDefault,
    handleSubmit,
  } = useProfileManagement({
    userId: user?.userId,
    username: user?.username,
    isNewProfile,
    onSuccess: () => router.push(`/profile/${user?.username}`),
  });

  if (isLoading || authLoading) return <EditProfileSkeleton />;

  return (
    <div className="flex flex-col items-center min-h-screen mt-24">
      <Card className="w-full flex flex-col max-w-2xl">
        <form onSubmit={handleSubmit}>
          <CardHeader>
            <CardTitle className="text-center">
              {isNewProfile ? "Create Profile" : "Edit Profile"}
            </CardTitle>
          </CardHeader>

          <CardContent>
            <ProfilePicture
              currentImageUrl={state.currentImageUrl}
              isDefaultImage={state.isDefaultImage}
              onImageSelect={handleImageSelect}
              onReset={handleResetToDefault}
            />

            <FormFields
              profileData={state.profileUpdates}
              onChange={handleInputChange}
            />
          </CardContent>

          <CardFooter>
            <Button type="submit">
              {isNewProfile ? "Create Profile" : "Save Changes"}
            </Button>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
};

export default withAuth(EditProfile);
