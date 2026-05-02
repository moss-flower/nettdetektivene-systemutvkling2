import type {
  Classroom,
  ClassroomRequest,
} from "~/composables/types/ClassroomTypes";

/**
 * Provides classroom-related API actions for both teachers and students.
 *
 * Uses the injected Nuxt $api client, which should point to the Nuxt proxy
 * routes under /api.
 */
export function useClassrooms() {
  /**
   * Custom API client provided by the Nuxt plugin.
   * This is typically configured with baseURL = "/api/v1"
   * and handles auth headers automatically.
   */
  const { $api } = useNuxtApp();

  const normalizeClassroom = (classroom: Classroom): Classroom => ({
    ...classroom,
    owners: Array.isArray(classroom.owners) ? classroom.owners : [],
    members: Array.isArray(classroom.members) ? classroom.members : [],
    pendingMembers: Array.isArray(classroom.pendingMembers) ? classroom.pendingMembers : [],
    pendingEntry: Boolean(classroom.pendingEntry),
  });

  const normalizeClassrooms = (classrooms: Classroom[]) =>
    classrooms.map(normalizeClassroom);

  /**
   * Teacher-only classroom management actions.
   *
   * These methods cover:
   * - reading owned classrooms
   * - creating/updating/deleting classrooms
   * - owner management
   * - member management
   * - join code regeneration
   */
  const teacher = {
    /**
     * Fetch all classrooms owned by the logged-in teacher.
     */
    getOwnedClassrooms: async (): Promise<Classroom[]> => {
      const classrooms = await $api<Classroom[]>("/classrooms", {
        method: "GET",
      });

      return normalizeClassrooms(classrooms);
    },

    /**
     * Fetch one specific classroom by its uid.
     */
    getClassroomById: async (classroomUid: string): Promise<Classroom> => {
      const classroom = await $api<Classroom>(`/classrooms/${classroomUid}`, {
        method: "GET",
      });

      return normalizeClassroom(classroom);
    },

    /**
     * Create a new classroom.
     *
     * The backend generates the join code automatically.
     */
    createClassroom: async (
      payload: ClassroomRequest,
    ): Promise<Classroom> => {
      const classroom = await $api<Classroom>("/classrooms", {
        method: "POST",
        body: payload,
      });

      return normalizeClassroom(classroom);
    },

    /**
     * Update classroom name and description.
     */
    updateClassroom: async (
      classroomUid: string,
      payload: ClassroomRequest,
    ): Promise<Classroom> => {
      const classroom = await $api<Classroom>(`/classrooms/${classroomUid}`, {
        method: "PUT",
        body: payload,
      });

      return normalizeClassroom(classroom);
    },

    /**
     * Permanently delete a classroom by uid.
     */
    deleteClassroom: async (classroomUid: string): Promise<void> => {
      await $api(`/classrooms/${classroomUid}`, {
        method: "DELETE",
      });
    },

    /**
     * Add a teacher as an owner of the classroom.
     *
     * @param classroomUid - Classroom uid
     * @param userUuid - Public UUID of the teacher to add
     */
    addOwner: async (
      classroomUid: string,
      userUuid: string,
    ): Promise<Classroom> => {
      const classroom = await $api<Classroom>(`/classrooms/${classroomUid}/owners/${userUuid}`, {
        method: "POST",
      });

      return normalizeClassroom(classroom);
    },

    /**
     * Remove a teacher as an owner of the classroom.
     *
     * Note: backend should prevent removing the last owner.
     */
    removeOwner: async (
      classroomUid: string,
      userUuid: string,
    ): Promise<Classroom> => {
      const classroom = await $api<Classroom>(`/classrooms/${classroomUid}/owners/${userUuid}`, {
        method: "DELETE",
      });

      return normalizeClassroom(classroom);
    },

    /**
     * Generate a new join code for the classroom.
     *
     * The previous code becomes invalid after regeneration.
     */
    regenerateJoinCode: async (classroomUid: string): Promise<Classroom> => {
      const classroom = await $api<Classroom>(`/classrooms/${classroomUid}/code`, {
        method: "POST",
      });

      return normalizeClassroom(classroom);
    },

    /**
     * Remove a student from a classroom.
     *
     * @param classroomUid - Classroom uid
     * @param userUuid - Public UUID of the student to remove
     */
    removeMember: async (
      classroomUid: string,
      userUuid: string,
    ): Promise<Classroom> => {
      const classroom = await $api<Classroom>(`/classrooms/${classroomUid}/members/${userUuid}`, {
        method: "DELETE",
      });

      return normalizeClassroom(classroom);
    },

    approveMember: async (
      classroomUid: string,
      userUuid: string,
    ): Promise<Classroom> => {
      const classroom = await $api<Classroom>(`/classrooms/${classroomUid}/members/${userUuid}/approve`, {
        method: "POST",
      });

      return normalizeClassroom(classroom);
    },

    /**
     * Update a student's username (teacher-forced rename).
     *
     * @param studentUuid - Public UUID of the student
     * @param newUsername - The new username
     */
    updateStudentUsername: async (
      studentUuid: string,
      newUsername: string,
    ): Promise<{ username: string; message: string }> => {
      return await $api(`/users/${studentUuid}/username`, {
        method: "PATCH",
        body: { username: newUsername },
      });
    },
  };

  /**
   * Student-only classroom actions.
   *
   * These methods cover:
   * - joining a classroom by code
   * - fetching the student's own classroom
   * - leaving the classroom
   */
  const student = {
    /**
     * Join a classroom using a join code.
     *
     * A student is only allowed to belong to one classroom at a time.
     */
    joinClassroom: async (code: string): Promise<Classroom> => {
      const classroom = await $api<Classroom>(`/classrooms/join/${code}`, {
        method: "POST",
      });

      return normalizeClassroom(classroom);
    },

    /**
     * Fetch the classroom the currently logged-in student belongs to.
     */
    getMyClassroom: async (): Promise<Classroom> => {
      const classroom = await $api<Classroom>("/classrooms/me", {
        method: "GET",
      });

      return normalizeClassroom(classroom);
    },

    /**
     * Leave the classroom the student is currently a member of.
     *
     * The backend route requires the classroom uid in the path.
     */
    leaveClassroom: async (classroomUid: string): Promise<void> => {
      await $api(`/classrooms/${classroomUid}/members/me`, {
        method: "DELETE",
      });
    },
  };

  /**
   * Return both grouped API domains.
   *
   * Example:
   * const { teacher, student } = useClassrooms()
   */
  return {
    teacher,
    student,
  };
}
