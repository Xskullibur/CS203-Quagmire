export type tournamentDTO = {
    id: string | null,
    name: string,
    location: string,
    startDate: string,
    endDate: string,
    status: "SCHEDULED" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED",
    deadline: string,
    maxParticipants: number,
    description: string,
    stageDTOs: any[] | null,
}
