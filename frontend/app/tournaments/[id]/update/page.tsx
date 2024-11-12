// UpdateTournament.tsx
// UpdateTournament.tsx
"use client";

import React, { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import TournamentForm from "@/components/tournaments/TournamentForm1";
import AdditionalDetailsForm from "@/components/tournaments/TournamentForm2";
import { Tournament } from "@/types/tournament";
import axiosInstance from "@/lib/axios";
import axios from "axios";
import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import { useToast } from "@/hooks/use-toast";
import withAuth from "@/HOC/withAuth";
import { UserRole } from "@/types/user-role";
import { useAlertDialog } from "@/app/context/AlertDialogContext";

const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;

const UpdateTournament = () => {
  const router = useRouter();
  const { id } = useParams(); // Ensure the correct usage of useParams depending on Next.js version

  // Step state (1 for Basic Info, 2 for Additional Details)
  const [step, setStep] = useState(1);

  const [tournament, setTournament] = useState<Tournament>({
    id: null,
    name: "",
    location: "",
    startDate: "",
    startTime: "",
    endDate: "",
    endTime: "",
    status: "SCHEDULED",
    deadline: "",
    deadlineTime: "",
    maxParticipants: 0,
    description: "",
    photoUrl: "",
  });

  const { handleError, showErrorToast } = useGlobalErrorHandler();
  const { showAlert } = useAlertDialog();
  const { toast } = useToast();

  // Fetch existing tournament data based on the ID from the URL
  useEffect(() => {
    if (!id) return;

    const fetchTournament = async () => {
      try {
        const res = await axios.get(
          new URL(`/tournament/DTO/${id}`, API_URL).toString()
        );
      
        if (res.status !== 200) {
          showErrorToast('Creation Error', 'Failed to create tournament. Please try again');
          return;
        }
      
        const data = res.data;
        const { startDate, endDate, deadline, ...rest } = data;
      
        setTournament({
          ...rest,
          startDate: startDate.split("T")[0],
          startTime: startDate.split("T")[1]?.slice(0, 5),
          endDate: endDate.split("T")[0],
          endTime: endDate.split("T")[1]?.slice(0, 5),
          deadline: deadline.split("T")[0],
          deadlineTime: deadline.split("T")[1]?.slice(0, 5),
        });
      } catch (error) {
        if (axios.isAxiosError(error)) {
          handleError(error);
        }
      
        console.error("Error fetching tournament details:", error);
      }
    };

    fetchTournament();
  }, [id]);

  // Handle input changes
  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setTournament({
      ...tournament,
      [name]: value,
    });
  };

  // Handle form submission to update tournament
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validate maxParticipants before submitting
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

    const {
      startDate,
      startTime,
      endDate,
      endTime,
      deadlineTime,
      ...tournamentDetails
    } = tournament;

    const data = {
      ...tournamentDetails,
      startDate: startdatetime,
      endDate: enddatetime,
      deadline: deadline,
    };

    try {
      const res = await axiosInstance.put(
        new URL(`/tournament/${id}`, API_URL).toString(),
        data,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (res.status === 200) {
        router.push("/admin/dashboard");

        toast({
          title: "Success",
          description: "Tournament updated successfully",
          variant: "success",
        });
      } else {
        showErrorToast('Update Error', 'Failed to update tournament. Please try again');
      }
    } catch (error) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }
    }
  };

  // Handle navigating between steps
  const handleNext = (e: React.FormEvent) => {
    e.preventDefault();
    setStep(2);
  };

  const handleBack = (e: React.FormEvent) => {
    e.preventDefault();
    setStep(1);
  };

  return (
    <div className="flex flex-col items-center justify-center mx-auto min-h-screen bg-primary-foreground">
      {step === 1 && (
        <TournamentForm
          tournament={tournament}
          handleChange={handleChange}
          handleSubmit={handleNext}
          buttonLabel="Next"
          isCreate={false}
        />
      )}

      {step === 2 && (
        <AdditionalDetailsForm
          tournament={tournament}
          handleChange={handleChange}
          handleBack={handleBack}
          handleSubmit={handleSubmit}
          isCreate={false}
        />
      )}
    </div>
  );
};

export default withAuth(UpdateTournament, UserRole.ADMIN);
