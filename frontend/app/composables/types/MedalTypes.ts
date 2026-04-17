export type MedalCategory = "stop" | "mystery" | "special";

export type Medal = {
  id: string;
  title: string;
  description: string;
  category: MedalCategory;
  source: string | null;
  unlocked: boolean;
  imageUrl: string;
  reward?: string | null;
};