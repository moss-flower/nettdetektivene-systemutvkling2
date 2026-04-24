import type {
  AchievementRequest,
  AchievementResponse,
} from "~/composables/types/AchievementTypes";

/**
 * Provides achievement-related API actions.
 *
 * Assumes the injected $api client already has the correct baseURL
 * (for example /api/v1 through your proxy setup).
 */
export function useAchievements() {
  const { $api } = useNuxtApp();

  /**
   * Student-facing actions.
   */
  const student = {
    /**
     * Fetch all achievements, marked with unlocked=true/false
     * for the currently logged-in student.
     */
    getUnlockedAchievements: async (): Promise<AchievementResponse[]> => {
      return await $api("/achievements/unlocked", {
        method: "GET",
      });
    },
  };

  /**
   * Teacher/admin management actions.
   */
  const teacher = {
    /**
     * Fetch all achievement definitions.
     */
    getAllAchievements: async (): Promise<AchievementResponse[]> => {
      return await $api("/achievements/all", {
        method: "GET",
      });
    },

    /**
     * Fetch one achievement definition by UUID.
     */
    getAchievementByUUID: async (
      achievementUUID: string,
    ): Promise<AchievementResponse> => {
      return await $api(`/achievements/${achievementUUID}`, {
        method: "GET",
      });
    },

    /**
     * Create a new achievement definition.
     */
    createAchievement: async (
      payload: AchievementRequest,
    ): Promise<AchievementResponse> => {
      return await $api("/achievements", {
        method: "POST",
        body: payload,
      });
    },

    /**
     * Update an existing achievement definition by UUID.
     */
    updateAchievement: async (
      achievementUUID: string,
      payload: AchievementRequest,
    ): Promise<AchievementResponse> => {
      return await $api(`/achievements/${achievementUUID}`, {
        method: "PUT",
        body: payload,
      });
    },

    /**
     * Delete an achievement definition by UUID.
     */
    deleteAchievement: async (achievementUUID: string): Promise<void> => {
      await $api(`/achievements/${achievementUUID}`, {
        method: "DELETE",
      });
    },
  };

  return {
    student,
    teacher,
  };
}