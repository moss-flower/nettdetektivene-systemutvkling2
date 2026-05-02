import { beforeEach, describe, expect, it, vi } from "vitest";
import { useClassrooms } from "../app/composables/useClassrooms";

describe("useClassrooms", () => {
  const apiMock = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (globalThis as { useNuxtApp?: unknown }).useNuxtApp = () => ({
      $api: apiMock,
    });
  });

  it("teacher.createClassroom posts payload", async () => {
    apiMock.mockResolvedValueOnce({ uid: "class-1" });
    const { teacher } = useClassrooms();

    await teacher.createClassroom({
      name: "Class A",
      description: "Desc",
      worldUid: "world-1",
    });

    expect(apiMock).toHaveBeenCalledWith("/classrooms", {
      method: "POST",
      body: {
        name: "Class A",
        description: "Desc",
        worldUid: "world-1",
      },
    });
  });

  it("teacher.getClassroomById uses classroom path", async () => {
    apiMock.mockResolvedValueOnce({ uid: "class-1" });
    const { teacher } = useClassrooms();

    await teacher.getClassroomById("class-1");

    expect(apiMock).toHaveBeenCalledWith("/classrooms/class-1", {
      method: "GET",
    });
  });

  it("teacher.removeMember calls delete member endpoint", async () => {
    apiMock.mockResolvedValueOnce({});
    const { teacher } = useClassrooms();

    await teacher.removeMember("class-1", "student-1");

    expect(apiMock).toHaveBeenCalledWith("/classrooms/class-1/members/student-1", {
      method: "DELETE",
    });
  });

  it("teacher.approveMember calls approve member endpoint", async () => {
    apiMock.mockResolvedValueOnce({});
    const { teacher } = useClassrooms();

    await teacher.approveMember("class-1", "student-1");

    expect(apiMock).toHaveBeenCalledWith("/classrooms/class-1/members/student-1/approve", {
      method: "POST",
    });
  });

  it("student.getMyClassroom calls me endpoint", async () => {
    apiMock.mockResolvedValueOnce({ uid: "class-1" });
    const { student } = useClassrooms();

    await student.getMyClassroom();

    expect(apiMock).toHaveBeenCalledWith("/classrooms/me", {
      method: "GET",
    });
  });

  it("student.leaveClassroom calls delete me member endpoint", async () => {
    apiMock.mockResolvedValueOnce(undefined);
    const { student } = useClassrooms();

    await student.leaveClassroom("class-1");

    expect(apiMock).toHaveBeenCalledWith("/classrooms/class-1/members/me", {
      method: "DELETE",
    });
  });
});
