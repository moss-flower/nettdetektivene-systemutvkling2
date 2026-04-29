export type ClassroomLeaderboardEntry = {
  classroomPublicUuid: string;
  className: string;
  score: number;
  rank: number;
};

export type PageResponse<T> = {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
};
