export interface StudentProfileResponse {
  studentUid: string;
  studentName: string;
  score: number;
  totalXp: number;
  completedExercises: number;
  unlockedAchievements: number;
}

export interface UsernameUpdateResponse {
  userUid: string;
  username: string;
  message: string;
}
