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
    label: 'ElevRegister',
    to: routePathsWithString(routePaths.register, '?role=elev'),
    variant: 'teacher',
  },
  {
    label: 'LærerRegister',
    to: routePathsWithString(routePaths.register, '?role=lærer'),
    variant: 'teacher',
  },
];
</script>

<template>
  <LoginLandingScene :options="loginOptions" />
</template>
