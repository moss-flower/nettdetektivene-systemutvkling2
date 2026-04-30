export const useAuth = () => {
  const { $api } = useNuxtApp();

  const login = async (email: string, password: string) => {
    return await $api("/auth/login", {
      method: "POST",
      body: { email, password },
    });
  };

  const logout = async () => {
    await $api("/auth/logout", {
      method: "POST",
    });
  };

  const refresh = async () => {
    await $api("/auth/refresh", {
      method: "POST",
    });
  };

  const registerStudent = async (
    username: string,
    email: string,
    password: string,
  ) => {
    return await $api("/register/student", {
      method: "POST",
      body: { username, email, password },
    });
  };

  const registerTeacher = async (
    username: string,
    email: string,
    password: string,
  ) => {
    return await $api("/register/teacher", {
      method: "POST",
      body: { username, email, password },
    });
  };

  return {
    login,
    logout,
    refresh,
    registerStudent,
    registerTeacher,
  };
};
