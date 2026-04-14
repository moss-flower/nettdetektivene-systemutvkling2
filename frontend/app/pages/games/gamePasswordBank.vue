<template>
  <div>
    <h1>Passordbanken</h1>
    <!-- TODO: implement game logic for each subtask type -->
  </div>
</template>

<script setup lang="ts">
import {
  TaskPasswordBankDTO,
  TaskPasswordBankGuessSingleton,
  TaskPasswordBankImproveSingleton,
  TaskPasswordBankMakePasswordSingleton,
} from '~/composables/types/TaskTypes'

const mockData: TaskPasswordBankDTO = {
  taskId: "1",
  description: "Passordutfordringer",
  passwordTasks: [
    Object.assign(new TaskPasswordBankGuessSingleton(), {
      taskId: "1a",
      description: "Hvilke passord er svake og hvilke er sterke?",
      passwordWeak: ["123456", "passord"],
      passwordStrong: ["K7$mP!xQ9z", "Tr0ub4dor&3"],
    }),
    Object.assign(new TaskPasswordBankImproveSingleton(), {
      taskId: "1b",
      description: "Forbedre dette passordet",
      passwordStart: "hund123",
    }),
    Object.assign(new TaskPasswordBankMakePasswordSingleton(), {
      taskId: "1c",
      description: "Lag et sterkt passord",
      password: "",
    }),
  ]
}
</script>

<style scoped>
/* Styles here */
</style>