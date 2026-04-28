import type { AvatarConfig } from "~/composables/types/AvatarTypes";

export function useAvatar() {
  const { $api } = useNuxtApp();

  const getMyAvatar = async (): Promise<AvatarConfig> => {
    return await $api<AvatarConfig>("/avatar", {
      method: "GET",
    });
  };

  const updateMyAvatar = async (payload: AvatarConfig): Promise<void> => {
    await $api("/avatar", {
      method: "POST",
      body: payload,
    });
  };

  return {
    getMyAvatar,
    updateMyAvatar,
  };
}
