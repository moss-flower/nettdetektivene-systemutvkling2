import type { StudentProfileResponse, UsernameUpdateResponse } from "~/composables/types/StudentProfileTypes";

export function useStudentProfile() {
  const { $api } = useNuxtApp();

  const getMyProfile = async (): Promise<StudentProfileResponse> => {
    return await $api("/students/me/profile", {
      method: "GET",
    });
  };

  const updateUsername = async (username: string): Promise<UsernameUpdateResponse> => {
    return await $api("/users/username", {
      method: "PATCH",
      body: { username },
    });
  };

  return {
    getMyProfile,
    updateUsername,
  };
}
