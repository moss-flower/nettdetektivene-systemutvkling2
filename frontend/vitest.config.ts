import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    include: ["tests/**/*.test.ts"],
    environment: "node",
    coverage: {
      provider: "v8",
      reporter: ["text", "html"],
      include: [
        "app/composables/useClassrooms.ts",
        "app/composables/useStudentMystery.ts",
        "app/composables/utils/gameProgress.ts",
        "app/composables/utils/gameUtils.ts",
        "app/composables/utils/studentProgress.ts",
      ],
      thresholds: {
        lines: 35,
        functions: 35,
        branches: 30,
        statements: 35,
      },
    },
  },
});

