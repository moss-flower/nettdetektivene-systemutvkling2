<script setup lang="ts">
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';
import { getLevel } from '~/composables/utils/studentProgress';
import { getClassroomExercises, getClassroomMemberById } from '~/composables/useTeacherDashboard';
import type { StudentProfileResponse } from '~/composables/types/StudentProfileTypes';

type CompletedExerciseResponse = {
  exerciseUid: string;
};

const route = useRoute();
const { teacher } = useClassrooms();
const { $api } = useNuxtApp();

const classroomId = computed(() => String(route.query.classroomId ?? ''));
const studentId = computed(() => String(route.query.studentId ?? ''));

const { data: classroom } = await useAsyncData(
  'teacher-classroom-selected-student-classroom',
  () => (classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : null),
  { watch: [classroomId] },
);

const student = computed(() => getClassroomMemberById(classroom.value, studentId.value));

const { data: studentProfile } = await useAsyncData(
  'teacher-classroom-selected-student-profile',
  () => (studentId.value ? $api<StudentProfileResponse>(`/students/${studentId.value}/profile`, { method: 'GET' }).catch(() => null) : null),
  { watch: [studentId] },
);

const { data: completedExercises } = await useAsyncData(
  'teacher-classroom-selected-student-completed-exercises',
  () => (studentId.value ? $api<CompletedExerciseResponse[]>(`/completed-exercises/students/${studentId.value}`, { method: 'GET' }).catch(() => []) : []),
  { watch: [studentId] },
);

const { data: classroomExercises } = await useAsyncData(
  'teacher-classroom-selected-student-exercises',
  () => getClassroomExercises(classroom.value ?? null),
  { watch: [classroom] },
);

const level = computed(() => getLevel(studentProfile.value?.totalXp ?? 0).level);
const completedExerciseUids = computed(() => new Set((completedExercises.value ?? []).map((exercise) => exercise.exerciseUid)));
const completedClassroomExercises = computed(() => (classroomExercises.value ?? [])
  .filter((exercise) => completedExerciseUids.value.has(exercise.uid))
  .length);

const stationProgress = computed(() => {
  const groupedStations = new Map<string, {
    title: string;
    totalExercises: number;
    completedExercises: number;
  }>();

  for (const exercise of classroomExercises.value ?? []) {
    const station = groupedStations.get(exercise.stationUid) ?? {
      title: exercise.stationTitle,
      totalExercises: 0,
      completedExercises: 0,
    };

    station.totalExercises += 1;
    station.completedExercises += completedExerciseUids.value.has(exercise.uid) ? 1 : 0;
    groupedStations.set(exercise.stationUid, station);
  }

  return [...groupedStations.values()].map((station) => ({
    ...station,
    isCompleted: station.totalExercises > 0 && station.completedExercises === station.totalExercises,
  }));
});

const completedStations = computed(() => stationProgress.value.filter((station) => station.isCompleted).length);
</script>

<template>
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomOverview, `?classroomId=${classroomId}`)">Tilbake til klasserommet</NuxtLink>
      <p class="eyebrow">Enkeltelev</p>
      <h1>{{ studentProfile?.studentName ?? student?.username ?? 'Ukjent elev' }}</h1>
      <p class="subtitle">{{ classroom?.name ?? 'Ukjent klasserom' }}</p>
    </header>

    <div v-if="student" class="summaryGrid">
      <article class="summaryCard">
        <span class="summaryLabel">Elev</span>
        <strong>{{ studentProfile?.studentName ?? student.username }}</strong>
      </article>
      <article class="summaryCard">
        <span class="summaryLabel">Klasse</span>
        <strong>{{ classroom?.name }}</strong>
      </article>
      <article class="summaryCard">
        <span class="summaryLabel">Level</span>
        <strong>{{ level }}</strong>
      </article>
      <article class="summaryCard">
        <span class="summaryLabel">Stjerner</span>
        <strong>{{ studentProfile?.completedExercises ?? completedExercises?.length ?? 0 }}</strong>
      </article>
      <article class="summaryCard">
        <span class="summaryLabel">Klasseoppgaver gjort</span>
        <strong>{{ completedClassroomExercises }} / {{ classroomExercises?.length ?? 0 }}</strong>
      </article>
      <article class="summaryCard">
        <span class="summaryLabel">Stasjoner gjort</span>
        <strong>{{ completedStations }} / {{ stationProgress.length }}</strong>
      </article>
    </div>

    <div v-if="student" class="detailCard">
      <h2>Stasjonsprogresjon</h2>
      <p v-if="stationProgress.length === 0" class="emptyText">Dette klasserommet har ingen stasjoner med oppgaver ennå.</p>
      <ul v-else class="stationList">
        <li v-for="station in stationProgress" :key="station.title" class="stationRow">
          <span>
            <strong>{{ station.title }}</strong>
            <small>{{ station.completedExercises }} / {{ station.totalExercises }} oppgaver</small>
          </span>
          <span class="stationStatus" :class="{ stationStatusComplete: station.isCompleted }">
            {{ station.isCompleted ? 'Gjort' : 'Pågår' }}
          </span>
        </li>
      </ul>
    </div>
  </section>
</template>

<style scoped>
.pageShell {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  padding: var(--space-4);
  color: var(--color-text-primary);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(255, 255, 255, 0.88)),
    linear-gradient(180deg, var(--palette-gray-50) 0%, var(--palette-cream-100) 100%);
  border-radius: var(--radius-4);
}

.pageHeader {
  margin-bottom: var(--space-4);
}

.backLink {
  display: inline-block;
  margin-bottom: var(--space-3);
  color: var(--color-brand-primary);
  font-weight: 600;
}

.eyebrow {
  color: var(--color-brand-primary);
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: var(--space-2);
}

h1 {
  margin: 0 0 var(--space-2) 0;
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
}

.summaryGrid {
  display: grid;
  gap: var(--space-4);
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  margin-bottom: var(--space-4);
}

.summaryCard,
.detailCard {
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.summaryLabel {
  display: block;
  color: var(--color-text-secondary);
  margin-bottom: 0.35rem;
}

.summaryCard strong {
  color: var(--color-brand-primary);
  font-size: 1.4rem;
}

.stationList {
  display: grid;
  gap: var(--space-3);
  padding: 0;
  margin: var(--space-3) 0 0;
  list-style: none;
}

.stationRow {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
  padding: var(--space-3);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-2);
  background: color-mix(in srgb, var(--color-bg-surface) 92%, var(--color-brand-primary) 8%);
}

.stationRow strong,
.stationRow small {
  display: block;
}

.stationRow small,
.emptyText {
  color: var(--color-text-secondary);
}

.stationStatus {
  padding: 0.35rem 0.75rem;
  border-radius: 999px;
  background: var(--palette-gray-100);
  color: var(--color-text-secondary);
  font-weight: 700;
}

.stationStatusComplete {
  background: color-mix(in srgb, var(--color-semantic-success) 18%, var(--color-bg-surface));
  color: var(--color-semantic-success);
}
</style>
