export const useAuth = () => {
  const login = async (email: string, password: string) => {
    return await $fetch("/api/auth/login", {
      method: "POST",
      body: { email, password },
    });
  };

  const registerStudent = async (username: string, email: string, password: string) => {
    return await $fetch("/api/auth/register/student", {
      method: "POST",
      body: { username, email, password },
    });
  };

  const registerTeacher = async (username: string, email: string, password: string) => {
    return await $fetch("/api/auth/register/teacher", {
      method: "POST",
      body: { username, email, password },
    });
  };

  return { login, registerStudent, registerTeacher };
};