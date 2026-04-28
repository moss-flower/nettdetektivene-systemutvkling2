import { beforeEach, describe, expect, it, vi } from "vitest";
import { useStudentMystery } from "../app/composables/useStudentMystery";

type StateRef<T> = { value: T };

describe("useStudentMystery", () => {
  const stateStore = new Map<string, StateRef<unknown>>();

  beforeEach(() => {
    vi.restoreAllMocks();
    stateStore.clear();

    (globalThis as { useState?: unknown }).useState = <T>(key: string, init: () => T) => {
      if (!stateStore.has(key)) {
        stateStore.set(key, { value: init() });
      }
      return stateStore.get(key) as StateRef<T>;
    };
  });

  it("adds a trimmed submission to history", () => {
    vi.spyOn(Date, "now").mockReturnValue(12345);

    const { submissionHistory, submitMysteryExample } = useStudentMystery();
    submitMysteryExample("  Dette er et tips  ", "  bilde.png  ");

    expect(submissionHistory.value).toHaveLength(1);
    expect(submissionHistory.value[0]).toMatchObject({
      id: "submission-12345",
      message: "Dette er et tips",
      screenshotName: "bilde.png",
      status: "Til vurdering",
    });
  });

  it("does not add submission when message or screenshot is empty", () => {
    const { submissionHistory, submitMysteryExample } = useStudentMystery();

    submitMysteryExample("   ", "bilde.png");
    submitMysteryExample("Gyldig melding", "   ");

    expect(submissionHistory.value).toHaveLength(0);
  });

  it("exposes current and previous mystery data", () => {
    const { currentMystery, previousMysteries } = useStudentMystery();

    expect(currentMystery.id).toBe("m-2026-17");
    expect(previousMysteries.length).toBeGreaterThan(0);
  });
});
