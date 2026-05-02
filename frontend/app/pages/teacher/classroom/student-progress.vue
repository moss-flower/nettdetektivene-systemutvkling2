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
  () => classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : Promise.resolve(null),
  { watch: [classroomId] },
);

const student = computed(() => getClassroomMemberById(classroom.value, studentId.value));

const { data: studentProfile } = await useAsyncData(
  'teacher-classroom-selected-student-profile',
  () => studentId.value ? $api<StudentProfileResponse>(`/students/${studentId.value}/profile`, { method: 'GET' }).catch(() => null) : Promise.resolve(null),
  { watch: [studentId] },
);

const { data: completedExercises } = await useAsyncData(
  'teacher-classroom-selected-student-completed-exercises',
  () => studentId.value ? $api<CompletedExerciseResponse[]>(`/completed-exercises/students/${studentId.value}`, { method: 'GET' }).catch(() => []) : Promise.resolve([]),
  { watch: [studentId] },
);

const { data: classroomExercises } = await useAsyncData(
  'teacher-classroom-selected-student-exercises',
  () => classroom.value ? getClassroomExercises(classroom.value) : Promise.resolve([]),
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
      <NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomOverview, { classroomId: classroomId })">Tilbake til klasserommet</NuxtLink>
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
      <NuxtLink 
        :to="routePathsWithString(routePaths.teacherClassroomStudentNotes, { classroomId: classroomId, studentId: studentId })" 
        class="summaryCard summaryCard--link"
      >
        <span class="summaryLabel">Se notater</span>
        <strong>→</strong>
      </NuxtLink>
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
  max-width: var(--layout-max-width);
  margin: 0 auto;
  padding: var(--layout-page-padding);
  color: var(--color-text-primary);
  background: var(--color-bg-canvas);
}

.pageHeader {
  margin-bottom: var(--space-4);
  background: var(--color-bg-surface);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-top: var(--border-width-thick) solid var(--color-accent-reward);
  box-shadow: var(--shadow-1);
  border-radius: var(--radius-3);
  padding: var(--space-5);
  display: grid;
  gap: var(--space-2);
}

.backLink {
  display: inline-block;
  margin-bottom: var(--space-3);
  color: var(--color-brand-primary);
  font-weight: var(--font-weight-semibold);
  transition: color var(--transition-fast);
}

.backLink:hover {
  text-decoration: underline;
}

.eyebrow {
  margin: 0;
  color: var(--color-brand-primary);
  font-weight: var(--font-weight-bold);
  font-size: var(--font-size-12);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}

h1 {
  margin: 0;
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-28);
  line-height: var(--font-leading-28);
  color: var(--color-text-primary);
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
}

.summaryGrid {
  display: grid;
  gap: var(--space-4);
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); /* deliberate: minimum card width */
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

.summaryCard {
  display: grid;
  gap: var(--space-1);
}

.summaryLabel {
  display: block;
  color: var(--color-text-secondary);
  margin-bottom: var(--space-1);
}

.summaryCard strong {
  color: var(--color-brand-primary);
  font-size: var(--font-size-22);
}

.summaryCard--link {
  text-decoration: none;
  transition: transform var(--transition-fast), border-color var(--transition-fast);
}

.summaryCard--link:hover {
  transform: translateY(-1px);
  border-color: var(--color-brand-primary);
}

.summaryCard--link strong {
  color: var(--color-brand-primary);
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
  background: var(--color-bg-canvas);
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
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-pill);
  /* deliberate exception, palette direct */
  background: var(--palette-ice-200);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-bold);
}

.stationStatusComplete {
  /* status chip, direct exception */
  background: var(--palette-overlay-green-soft);
  color: var(--palette-green-700);
}
</style>
