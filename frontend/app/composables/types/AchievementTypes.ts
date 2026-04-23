export type MedalCategory = "stop" | "mystery" | "special";

export type AchievementResponse = {
  UUID: string;
  title: string;
  description: string;
  category: MedalCategory;
  source: string;
  unlocked: boolean;
  imageUrl: string;
  reward: string | null;
};

export type AchievementRequest = {
  title: string;
  description: string;
  category: MedalCategory;
  source: string;
  imageUrl: string;
  reward: string | null;
};