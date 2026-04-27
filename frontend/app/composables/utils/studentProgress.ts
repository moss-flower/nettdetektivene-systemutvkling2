const XP_PER_LEVEL = 300;

export interface StudentProgressView {
  level: number;
  currentXp: number;
  nextLevelXp: number;
  xpToNextLevel: number;
  progressPercent: number;
}

export function getLevel(totalXp: number): StudentProgressView {
  const safeTotalXp = Math.max(0, totalXp);
  const level = Math.max(1, Math.floor(safeTotalXp / XP_PER_LEVEL) + 1);
  const currentXp = safeTotalXp % XP_PER_LEVEL;
  const progressPercent = Math.min(100, (currentXp / XP_PER_LEVEL) * 100);

  return {
    level,
    currentXp,
    nextLevelXp: XP_PER_LEVEL,
    xpToNextLevel: XP_PER_LEVEL - currentXp,
    progressPercent,
  };
}

export const getStudentProgress = getLevel;
