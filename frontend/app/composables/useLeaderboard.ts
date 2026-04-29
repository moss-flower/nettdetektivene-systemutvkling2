import type {
  ClassroomLeaderboardEntry,
  PageResponse,
} from "~/composables/types/LeaderboardTypes";

export function useLeaderboard() {
  const { $api } = useNuxtApp();

  const getClassroomRankings = async (
    page = 0,
    size = 25,
  ): Promise<PageResponse<ClassroomLeaderboardEntry>> => {
    return await $api("/leaderboard/classroom", {
      method: "GET",
      query: {
        page,
        size,
      },
    });
  };

  return {
    getClassroomRankings,
  };
}
