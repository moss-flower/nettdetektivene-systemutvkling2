import { beforeEach, describe, expect, it, vi } from "vitest";
import { useStudentMystery } from "../app/composables/useStudentMystery";

type StateRef<T> = { value: T };

describe("useStudentMystery", () => {
  const stateStore = new Map<string, StateRef<unknown>>();
  const apiMock = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    stateStore.clear();

    (globalThis as { useNuxtApp?: unknown }).useNuxtApp = () => ({
      $api: apiMock,
    });
    (globalThis as { useClassrooms?: unknown }).useClassrooms = () => ({
      student: {
        getMyClassroom: () => apiMock("/classrooms/me", { method: "GET" }),
      },
    });
    (globalThis as { useStudentProfile?: unknown }).useStudentProfile = () => ({
      getMyProfile: () => apiMock("/students/me/profile", { method: "GET" }),
    });
    (globalThis as { useState?: unknown }).useState = <T>(key: string, init: () => T) => {
      if (!stateStore.has(key)) {
        stateStore.set(key, { value: init() });
      }

      return stateStore.get(key) as StateRef<T>;
    };
  });

  it("loads current, previous, and student-specific submission history", async () => {
    apiMock.mockImplementation(async (path: string) => {
      if (path === "/classrooms/me") {
        return { uid: "class-1", name: "5A", description: "", worldUid: "world-1", status: "ACTIVE" };
      }

      if (path === "/students/me/profile") {
        return { studentUid: "student-1" };
      }

      if (path === "/classrooms/class-1/weekly-mystery") {
        return {
          publicId: "m-current",
          weekLabel: "  Uke 17  ",
          title: "Ukens mysterium",
          prompt: "Hva skjedde?",
          clue: "Se etter faresignaler",
          exerciseUid: null,
        };
      }

      if (path === "/weekly-mystery/classroom-history") {
        return [
          {
            publicId: "m-current",
            weekLabel: "Uke 17",
            title: "Ukens mysterium",
            prompt: "Hva skjedde?",
            clue: "Se etter faresignaler",
            exerciseUid: null,
          },
          {
            publicId: "m-old",
            weekLabel: "  ",
            title: "Forrige mysterium",
            prompt: "Hvem kan du stole pa?",
            clue: "Snakk med en voksen",
            exerciseUid: "exercise-2",
          },
        ];
      }

      if (path === "/weekly-mystery/m-current/submissions") {
        return [
          {
            publicUid: "submission-1",
            message: "Mitt svar",
            studentPublicUid: "student-1",
            fileName: "bevis.png",
            fileUrl: "/files/bevis.png",
          },
          {
            publicUid: "submission-2",
            message: "Et annet svar",
            studentPublicUid: "student-2",
            fileName: "annet.png",
            fileUrl: "/files/annet.png",
          },
        ];
      }

      throw new Error(`Unexpected path: ${path}`);
    });

    const { currentMystery, previousMysteries, submissionHistory, loadMysteryData } = useStudentMystery();
    await loadMysteryData();

    expect(currentMystery.value).toMatchObject({
      publicId: "m-current",
      weekLabel: "Uke 17",
    });
    expect(previousMysteries.value).toEqual([
      expect.objectContaining({
        publicId: "m-old",
        weekLabel: "Ukens mysterium",
      }),
    ]);
    expect(submissionHistory.value).toEqual([
      expect.objectContaining({
        id: "submission-1",
        message: "Mitt svar",
        screenshotName: "bevis.png",
        status: "Til vurdering",
        studentPublicUid: "student-1",
      }),
    ]);
  });

  it("submits trimmed text and refreshes the student submission history", async () => {
    apiMock.mockImplementation(async (path: string, options?: { body?: FormData }) => {
      if (path === "/classrooms/me") {
        return { uid: "class-1", name: "5A", description: "", worldUid: "world-1", status: "ACTIVE" };
      }

      if (path === "/students/me/profile") {
        return { studentUid: "student-1" };
      }

      if (path === "/classrooms/class-1/weekly-mystery") {
        return {
          publicId: "m-current",
          weekLabel: "Uke 17",
          title: "Ukens mysterium",
          prompt: "Hva skjedde?",
          clue: "Se etter faresignaler",
          exerciseUid: null,
        };
      }

      if (path === "/weekly-mystery/classroom-history") {
        return [];
      }

      if (path === "/weekly-mystery" && options?.body) {
        expect(options.body.get("message")).toBe("Dette er et tips");
        expect(options.body.get("weeklyMysterPromptUuid")).toBe("m-current");
        expect(options.body.get("file")).toBeInstanceOf(File);
        return undefined;
      }

      if (path === "/weekly-mystery/m-current/submissions") {
        return [
          {
            publicUid: "submission-12345",
            message: "Dette er et tips",
            studentPublicUid: "student-1",
            fileName: "bilde.png",
            fileUrl: "/files/bilde.png",
          },
          {
            publicUid: "submission-99999",
            message: "Skal filtreres bort",
            studentPublicUid: "student-2",
            fileName: "annet.png",
            fileUrl: "/files/annet.png",
          },
        ];
      }

      throw new Error(`Unexpected path: ${path}`);
    });

    const { loadMysteryData, submissionHistory, submitMysteryExample } = useStudentMystery();
    await loadMysteryData();
    await submitMysteryExample("  Dette er et tips  ", new File(["image"], "bilde.png", { type: "image/png" }));

    expect(submissionHistory.value).toEqual([
      expect.objectContaining({
        id: "submission-12345",
        message: "Dette er et tips",
        screenshotName: "bilde.png",
        status: "Til vurdering",
      }),
    ]);
  });

  it("rejects empty messages before posting", async () => {
    apiMock.mockImplementation(async (path: string) => {
      if (path === "/classrooms/me") {
        return { uid: "class-1", name: "5A", description: "", worldUid: "world-1", status: "ACTIVE" };
      }

      if (path === "/students/me/profile") {
        return { studentUid: "student-1" };
      }

      if (path === "/classrooms/class-1/weekly-mystery") {
        return {
          publicId: "m-current",
          weekLabel: "Uke 17",
          title: "Ukens mysterium",
          prompt: "Hva skjedde?",
          clue: "Se etter faresignaler",
          exerciseUid: null,
        };
      }

      if (path === "/weekly-mystery/classroom-history") {
        return [];
      }

      if (path === "/weekly-mystery/m-current/submissions") {
        return [];
      }

      throw new Error(`Unexpected path: ${path}`);
    });

    const { loadMysteryData, submitMysteryExample } = useStudentMystery();
    await loadMysteryData();

    await expect(
      submitMysteryExample("   ", new File(["image"], "bilde.png", { type: "image/png" })),
    ).rejects.toThrow("Skriv en kort forklaring før du sender inn.");
    expect(apiMock).not.toHaveBeenCalledWith(
      "/weekly-mystery",
      expect.objectContaining({ method: "POST" }),
    );
  });
});
