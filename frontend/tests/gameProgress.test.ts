import { beforeEach, describe, expect, it, vi } from "vitest";
import {
  markExerciseCompleted,
  useCompletedExerciseUids,
} from "../app/composables/utils/gameProgress";

describe("gameProgress", () => {
  const apiMock = vi.fn();
  const refreshNuxtDataMock = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();

    (globalThis as { useNuxtApp?: unknown }).useNuxtApp = () => ({
      $api: apiMock,
    });
    (globalThis as { refreshNuxtData?: unknown }).refreshNuxtData = refreshNuxtDataMock;
  });

  it("maps completed exercise response to uid list", async () => {
    apiMock.mockResolvedValueOnce([{ exerciseUid: "e-1" }, { exerciseUid: "e-2" }]);

    const result = await useCompletedExerciseUids();

    expect(apiMock).toHaveBeenCalledWith("/completed-exercises/me", { method: "GET" });
    expect(result).toEqual(["e-1", "e-2"]);
  });

  it("returns empty array when completed exercise fetch fails", async () => {
    apiMock.mockRejectedValueOnce(new Error("network"));

    const result = await useCompletedExerciseUids();
    expect(result).toEqual([]);
  });

  it("posts completion and refreshes all expected keys", async () => {
    apiMock.mockResolvedValueOnce(undefined);

    await markExerciseCompleted("exercise-1");

    expect(apiMock).toHaveBeenCalledWith("/completed-exercises", {
      method: "POST",
      body: { exerciseUid: "exercise-1" },
    });
    expect(refreshNuxtDataMock).toHaveBeenCalledTimes(3);
    expect(refreshNuxtDataMock).toHaveBeenCalledWith("completed-exercises");
    expect(refreshNuxtDataMock).toHaveBeenCalledWith("student-completed-exercises");
    expect(refreshNuxtDataMock).toHaveBeenCalledWith("student-profile");
  });

  it("swallows 409 errors and still refreshes data", async () => {
    apiMock.mockRejectedValueOnce({ statusCode: 409 });

    await expect(markExerciseCompleted("exercise-2")).resolves.toBeUndefined();
    expect(refreshNuxtDataMock).toHaveBeenCalledTimes(3);
  });

  it("rethrows non-409 errors", async () => {
    apiMock.mockRejectedValueOnce({ statusCode: 500 });

    await expect(markExerciseCompleted("exercise-3")).rejects.toEqual({ statusCode: 500 });
  });
});

