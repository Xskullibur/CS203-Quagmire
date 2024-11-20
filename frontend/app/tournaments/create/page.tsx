// CreateTournament.tsx
"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import TournamentForm from "@/components/tournaments/TournamentForm1";
import AdditionalDetailsForm from "@/components/tournaments/TournamentForm2";
import { Tournament } from "@/types/tournament";
import axiosInstance from "@/lib/axios";
import axios from "axios";
import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import { tournamentDTO } from "@/types/tournamentDTO";
import { useToast } from "@/hooks/use-toast";
import withAuth from "@/HOC/withAuth";
import { UserRole } from "@/types/user-role";
import { useAlertDialog } from "@/app/context/AlertDialogContext";

const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;

const CreateTournament = () => {
  const router = useRouter();
  useToast();

  // State to control the form step (1 for Basic Info, 2 for Additional Details)
  const [step, setStep] = useState(1);

  // Tournament state
  const [tournament, setTournament] = useState<Tournament>({
    id: null,
    name: "New Tournament",
    location: "Singapore",
    startDate: new Date((new Date().getDate()) + 1).toISOString().split("T")[0], 
    startTime: "09:00", // Default to 9:00 AM
    endDate: new Date((new Date().getDate()) + 1).toISOString().split("T")[0], 
    endTime: "18:00", // Default to 6:00 PM
    status: "SCHEDULED", // Default status
    deadline: new Date().toISOString().split("T")[0], 
    deadlineTime: "23:59",
    maxParticipants: 16, 
    description: "Please arrive on time so that the weighing process can be swiftly concluded.", 
    photoUrl: "", 
  });
  

  const [photo, setPhoto] = useState<File | null>(null);
  const { handleError, showErrorToast } = useGlobalErrorHandler();
  const { showAlert } = useAlertDialog();
  const { toast } = useToast();

  // Handle input change
  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setTournament({
      ...tournament,
      [name]: value,
    });
  };

  const handlePhotoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setPhoto(e.target.files[0]);
    }
  };

  // Handle next step
  const handleNext = (e: React.FormEvent) => {
    e.preventDefault();
    setStep(2);
  };

  // Handle going back to the previous step
  const handleBack = (e: React.FormEvent) => {
    e.preventDefault();
    setStep(1);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validate maxParticipants before sending the request
    if (tournament.maxParticipants <= 0) {
      showAlert({
        variant: "alert",
        title: "Warning",
        description:
          "Max participants must be a positive number",
        confirmText: "OK",
      });
      return;
    }

    const startdatetime = `${tournament.startDate}T${tournament.startTime}:00`;
    const enddatetime = `${tournament.endDate}T${tournament.endTime}:00`;
    const deadline = `${tournament.deadline}T${tournament.deadlineTime}:00`;

    const tournamentData: tournamentDTO = {
      id: tournament.id,
      name: tournament.name,
      location: tournament.location,
      status: tournament.status,
      maxParticipants: tournament.maxParticipants,
      description: tournament.description,
      startDate: startdatetime,
      endDate: enddatetime,
      deadline: deadline,
      stageDTOs: null,
    };

    const formData = new FormData();
    formData.append("tournament", JSON.stringify(tournamentData));
    if (photo) {
      formData.append("photo", photo);
    }

    try {
      const res = await axiosInstance.post(
        new URL("/tournament/create", API_URL).toString(),
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      if (res.status === 200) {
        router.push(res.data.id);
      } else {
        showErrorToast('Creation Error', 'Failed to create tournament. Please try again');
      }

      toast({
        title: "Success",
        description: "Tournament created successfully",
        variant: "success",
      })
    } catch (error) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }
    }
  };

  return (
    <div className="flex flex-col items-center justify-center mx-auto min-h-screen bg-primary-foreground">
      {step === 1 && (
        <TournamentForm
          tournament={tournament}
          handleChange={handleChange}
          handleSubmit={handleNext}
          buttonLabel="Next"
          isCreate={true}
        />
      )}

      {step === 2 && (
        <AdditionalDetailsForm
          tournament={tournament}
          handleChange={handleChange}
          handleBack={handleBack}
          handleSubmit={handleSubmit}
          isCreate={true}
        />
      )}
    </div>
  );
};

export default withAuth(CreateTournament, UserRole.ADMIN);
