export interface StudentProfileResponse {
  studentUid: string;
  studentName: string;
  currentLevel: number;
  currentXp: number;
  nextLevelXp: number;
  completedExercises: number;
  unlockedAchievements: number;
}
