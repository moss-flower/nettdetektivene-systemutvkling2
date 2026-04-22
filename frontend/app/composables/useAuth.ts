export const useAuth = () => {
  const { $api } = useNuxtApp();

  const login = async (email: string, password: string) => {
    console.log("REACHED LOGIN")

    return await $api("/auth/login", {
      method: "POST",
      body: { email, password },
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
    registerStudent,
    registerTeacher,
  };
};