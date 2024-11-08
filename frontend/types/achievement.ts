export interface Achievement {
    id: number;  // id field from the backend
    name: string;
    description: string;
    criteriaType: string;  // new field
    criteriaCount: number;  // new field
  }
  