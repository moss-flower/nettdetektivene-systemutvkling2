import type { AvatarConfig } from "~/composables/types/AvatarTypes";

export function useAvatar() {
  const { $api } = useNuxtApp();

  const getMyAvatar = async (): Promise<AvatarConfig> => {
    return await $api("/avatar", {
      method: "GET",
    });
  };

  return {
    getMyAvatar,
  };
}
