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
      return await $api("/classrooms", {
        method: "GET",
      });
    },

    /**
     * Fetch one specific classroom by its numeric ID.
     */
    getClassroomById: async (classroomId: number): Promise<Classroom> => {
      return await $api(`/classrooms/${classroomId}`, {
        method: "GET",
      });
    },

    /**
     * Create a new classroom.
     *
     * The backend generates the join code automatically.
     */
    createClassroom: async (
      payload: ClassroomRequest,
    ): Promise<Classroom> => {
      return await $api("/classrooms", {
        method: "POST",
        body: payload,
      });
    },

    /**
     * Update classroom name and description.
     */
    updateClassroom: async (
      classroomId: number,
      payload: ClassroomRequest,
    ): Promise<Classroom> => {
      return await $api(`/classrooms/${classroomId}`, {
        method: "PUT",
        body: payload,
      });
    },

    /**
     * Permanently delete a classroom by ID.
     */
    deleteClassroom: async (classroomId: number): Promise<void> => {
      await $api(`/classrooms/${classroomId}`, {
        method: "DELETE",
      });
    },

    /**
     * Add a teacher as an owner of the classroom.
     *
     * @param classroomId - Numeric classroom ID
     * @param userUuid - Public UUID of the teacher to add
     */
    addOwner: async (
      classroomId: number,
      userUuid: string,
    ): Promise<Classroom> => {
      return await $api(`/classrooms/${classroomId}/owners/${userUuid}`, {
        method: "POST",
      });
    },

    /**
     * Remove a teacher as an owner of the classroom.
     *
     * Note: backend should prevent removing the last owner.
     */
    removeOwner: async (
      classroomId: number,
      userUuid: string,
    ): Promise<Classroom> => {
      return await $api(`/classrooms/${classroomId}/owners/${userUuid}`, {
        method: "DELETE",
      });
    },

    /**
     * Generate a new join code for the classroom.
     *
     * The previous code becomes invalid after regeneration.
     */
    regenerateJoinCode: async (classroomId: number): Promise<Classroom> => {
      return await $api(`/classrooms/${classroomId}/code`, {
        method: "POST",
      });
    },

    /**
     * Remove a student from a classroom.
     *
     * @param classroomId - Numeric classroom ID
     * @param userUuid - Public UUID of the student to remove
     */
    removeMember: async (
      classroomId: number,
      userUuid: string,
    ): Promise<Classroom> => {
      return await $api(`/classrooms/${classroomId}/members/${userUuid}`, {
        method: "DELETE",
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
      return await $api(`/classrooms/join/${code}`, {
        method: "POST",
      });
    },

    /**
     * Fetch the classroom the currently logged-in student belongs to.
     */
    getMyClassroom: async (): Promise<Classroom> => {
      return await $api("/classrooms/me", {
        method: "GET",
      });
    },

    /**
     * Leave the classroom the student is currently a member of.
     *
     * The backend route requires the classroom ID in the path.
     */
    leaveClassroom: async (classroomId: number): Promise<void> => {
      await $api(`/classrooms/${classroomId}/members/me`, {
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