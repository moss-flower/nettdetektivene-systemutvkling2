import type { AvatarConfig, AvatarOptions } from "~/composables/types/AvatarTypes";

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

  const getAvatarOptions = async (): Promise<AvatarOptions> => {
    return await $api<AvatarOptions>("/avatar/options", {
      method: "GET",
    });
  };

  return {
    getMyAvatar,
    updateMyAvatar,
    getAvatarOptions,
  };
}
