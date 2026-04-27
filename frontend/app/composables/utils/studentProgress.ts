const XP_PER_LEVEL = 500;

export interface StudentProgressView {
  level: number;
  currentXp: number;
  nextLevelXp: number;
  progressPercent: number;
}

export function getStudentProgress(totalXp: number): StudentProgressView {
  const safeTotalXp = Math.max(0, totalXp);
  const level = Math.max(1, Math.floor(safeTotalXp / XP_PER_LEVEL) + 1);
  const xpAtLevelStart = (level - 1) * XP_PER_LEVEL;
  const currentXp = safeTotalXp - xpAtLevelStart;
  const progressPercent = Math.min(100, Math.round((currentXp / XP_PER_LEVEL) * 100));

  return {
    level,
    currentXp,
    nextLevelXp: XP_PER_LEVEL,
    progressPercent,
  };
}
