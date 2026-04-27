import { describe, expect, it, vi } from "vitest";
import { sampleAndShuffle } from "../app/composables/utils/gameUtils";

describe("sampleAndShuffle", () => {
  it("returns the requested number of items", () => {
    const result = sampleAndShuffle([1, 2, 3, 4, 5], 3);
    expect(result).toHaveLength(3);
  });

  it("does not mutate the original pool", () => {
    const pool = [1, 2, 3, 4];
    sampleAndShuffle(pool, 2);
    expect(pool).toEqual([1, 2, 3, 4]);
  });

  it("shuffles deterministically with mocked randomness", () => {
    const randomSpy = vi
      .spyOn(Math, "random")
      .mockReturnValueOnce(0.4)
      .mockReturnValueOnce(0.1)
      .mockReturnValueOnce(0.8);

    const result = sampleAndShuffle(["a", "b", "c", "d"], 4);
    expect(result).toEqual(["c", "d", "a", "b"]);

    randomSpy.mockRestore();
  });
});

