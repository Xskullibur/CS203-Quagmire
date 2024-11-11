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
import withAuth from "@/HOC/withAuth";
import { UserRole } from "@/types/user-role";

const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;

const CreateTournament = () => {
  const router = useRouter();

  // State to control the form step (1 for Basic Info, 2 for Additional Details)
  const [step, setStep] = useState(1);

  // Tournament state
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

  const [photo, setPhoto] = useState<File | null>(null);
  const [refereeSearchQuery, setRefereeSearchQuery] = useState<string>("");
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const [selectedReferees, setSelectedReferees] = useState<string[]>([]);
  const { handleError } = useGlobalErrorHandler();

  const handleRefereeSearch = async (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setRefereeSearchQuery(e.target.value);

    if (e.target.value.length >= 3) {
      // Start searching after 3 characters
      try {
        const res = await axiosInstance.get(
          new URL(
            `/users/search?username=${e.target.value}`,
            API_URL
          ).toString()
        );

        // const res = await fetch(`${API_URL}/users/search?username=${e.target.value}`);
        // const data = await res.json();
        setSearchResults(res.data);
      } catch (error) {
        if (axios.isAxiosError(error)) {
          handleError(error);
        }

        console.error("Error searching for referees:", error);
      }
    } else {
      setSearchResults([]);
    }
  };

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
      alert("Max participants must be a positive number");
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
        alert("Error creating tournament");
      }
    } catch (error) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }

      console.error("Error creating tournament:", error);
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
        />
      )}

      {step === 2 && (
        <AdditionalDetailsForm
          tournament={tournament}
          handleChange={handleChange}
          handleBack={handleBack}
          handleSubmit={handleSubmit}
        />
      )}
    </div>
  );
};

export default withAuth(CreateTournament, UserRole.ADMIN);
