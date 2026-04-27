<script setup lang="ts">
import { routePaths } from '~/composables/utils/routePaths';

type LoginOption = {
  label: string;
  to: string;
  variant: 'student' | 'teacher';
};

definePageMeta({
  layout: false,
});

const loginOptions: LoginOption[] = [
  {
    label: 'Logg Inn',
    to: routePaths.login,
    variant: 'student',
  },
];
</script>

<template>
  <LoginLandingScene :options="loginOptions" />
</template>
