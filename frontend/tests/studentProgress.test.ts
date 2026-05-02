import { describe, expect, it } from "vitest";
import { getLevel, getStudentProgress } from "../app/composables/utils/studentProgress";

describe("studentProgress", () => {
  it("clamps negative xp to level 1 progress", () => {
    const progress = getLevel(-100);

    expect(progress).toEqual({
      level: 1,
      currentXp: 0,
      nextLevelXp: 300,
      xpToNextLevel: 300,
      progressPercent: 0,
    });
  });

  it("calculates exact level up at 300 xp", () => {
    const progress = getLevel(300);

    expect(progress.level).toBe(2);
    expect(progress.currentXp).toBe(0);
    expect(progress.xpToNextLevel).toBe(300);
  });

  it("calculates mid-level progress percentages", () => {
    const progress = getLevel(450);

    expect(progress.level).toBe(2);
    expect(progress.currentXp).toBe(150);
    expect(progress.progressPercent).toBe(50);
  });

  it("supports alias helper getStudentProgress", () => {
    expect(getStudentProgress(600).level).toBe(3);
  });
});

