<script setup lang="ts">
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';

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
    label: 'Elev',
    to: routePathsWithString(routePaths.login, '?role=elev'),
    variant: 'student',
  },
  {
    label: 'Lærer',
    to: routePathsWithString(routePaths.login, '?role=lærer'),
    variant: 'teacher',
  },
  {
    label: 'Elevregistrering',
    to: routePathsWithString(routePaths.register, '?role=elev'),
    variant: 'student',
  },
  {
    label: 'Lærerregistrering',
    to: routePathsWithString(routePaths.register, '?role=lærer'),
    variant: 'teacher',
  },
];
</script>

<template>
  <LoginLandingScene :options="loginOptions" />
</template>
