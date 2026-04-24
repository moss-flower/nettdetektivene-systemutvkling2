import type { StudentProfileResponse } from "~/composables/types/StudentProfileTypes";

export function useStudentProfile() {
  const { $api } = useNuxtApp();

  const getMyProfile = async (): Promise<StudentProfileResponse> => {
    return await $api("/students/me/profile", {
      method: "GET",
    });
  };

  return {
    getMyProfile,
  };
}
