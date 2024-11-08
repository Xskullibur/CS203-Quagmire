export type tournamentDTO = {
    id: string | null,
    name: string,
    location: string,
    startDate: string,
    endDate: string,
    status: "SCHEDULED" | "INPROGRESS" | "COMPLETED" | "CANCELLED",
    deadline: string,
    maxParticipants: number,
    description: string,
    stageDTOs: any[] | null,
}
