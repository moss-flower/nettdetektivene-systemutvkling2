<script setup lang="ts">
import { World } from '~/composables/types/WorldTypes';
import starEmpty from '~/assets/images/star/spStarEmpty.png';
import starFilled from '~/assets/images/star/spStarFilled.png';
import { getClassroomExercises } from '~/composables/useTeacherDashboard';
const route = useRoute();
import { routePaths, routePathsWithString, routePathsWithUid } from '~/composables/utils/routePaths';

type CompletedExerciseResponse = {
  exerciseUid: string;
};

type StudentProgress = {
  completed: number;
  remaining: number;
};

const { teacher } = useClassrooms();
const { $api } = useNuxtApp();

const classroomId = computed(() => String(route.query.classroomId ?? ''));

const { data: classroom, refresh: refreshClassroom } = await useAsyncData(
  'teacher-classroom-overview',
  () => (classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : null),
  { watch: [classroomId] },
);

const worldUid = computed(() => classroom.value?.worldUid ?? '');

const { data: world } = await useAsyncData(
  'teacher-classroom-world',
  () => (worldUid.value ? World.backendFetchWorldByUid(worldUid.value) : null),
  { watch: [worldUid] },
);

const { data: exercises } = await useAsyncData(
  'teacher-classroom-overview-exercises',
  () => getClassroomExercises(classroom.value ?? null),
  { watch: [classroom] },
);

const classroomExerciseUids = computed(() => new Set((exercises.value ?? []).map((exercise) => exercise.uid)));
const totalExercises = computed(() => classroomExerciseUids.value.size);

const { data: progressByStudent, refresh: refreshProgress } = await useAsyncData(
  'teacher-classroom-student-progress',
  async (): Promise<Record<string, StudentProgress>> => {
    if (!classroom.value || totalExercises.value === 0) {
      return {};
    }

    const entries = await Promise.all(classroom.value.members.map(async (student) => {
      const completedExercises = await $api<CompletedExerciseResponse[]>(`/completed-exercises/students/${student.publicUuid}`, {
        method: 'GET',
      }).catch(() => []);

      const completed = completedExercises.filter((exercise) => classroomExerciseUids.value.has(exercise.exerciseUid)).length;

      return [student.publicUuid, {
        completed,
        remaining: Math.max(0, totalExercises.value - completed),
      }] as const;
    }));

    return Object.fromEntries(entries);
  },
  { watch: [classroom, exercises] },
);

const removingStudentId = ref('');

const getStudentProgress = (studentId: string): StudentProgress => progressByStudent.value?.[studentId] ?? {
  completed: 0,
  remaining: totalExercises.value,
};

const removeStudent = async (studentId: string) => {
  if (!classroom.value || removingStudentId.value) {
    return;
  }

  removingStudentId.value = studentId;

  try {
    await teacher.removeMember(classroom.value.uid, studentId);
    await refreshClassroom();
    await refreshProgress();
  } finally {
    removingStudentId.value = '';
  }
};
</script>

<template>
  <section class="classroomPage">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePaths.teacherOverview">Tilbake til hjemskjerm</NuxtLink>
      <p class="eyebrow">Klasseromsdetaljer</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
      <p class="subtitle">Viser klasserommet slik det ligger i backend akkurat nå.</p>
    </header>

    <div v-if="classroom" class="actionGrid">
      <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomNotifications, `?classroomId=${classroom.uid}`)" class="actionCard">
        <span class="actionCardTitle">Varslingssenter</span>
        <img src="https://placehold.co/128x128?text=VS" alt="Varslingssenter" class="actionCardImage">
      </NuxtLink>

      <NuxtLink :to="routePathsWithUid(routePaths.teacherFactoryWorld, classroom.worldUid)" class="actionCard">
        <span class="actionCardTitle">Verden</span>
        <img src="https://placehold.co/128x128?text=VE" alt="Verden" class="actionCardImage">
      </NuxtLink>

      <NuxtLink :to="routePaths.studentHome" class="actionCard">
        <span class="actionCardTitle">Test spillet</span>
        <img src="https://placehold.co/128x128?text=TS" alt="Test spillet" class="actionCardImage">
      </NuxtLink>

      <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomMystery, `?classroomId=${classroom.uid}`)" class="actionCard">
        <span class="actionCardTitle">Ukens mysterium</span>
        <img src="https://placehold.co/128x128?text=UM" alt="Ukens mysterium" class="actionCardImage">
      </NuxtLink>
    </div>

    <div v-if="classroom" class="summaryRow">
      <article class="summaryCard">
        <span class="summaryCardLabel">Elever</span>
        <strong>{{ classroom.members.length }}</strong>
      </article>

      <article class="summaryCard">
        <span class="summaryCardLabel">Eiere</span>
        <strong>{{ classroom.owners.length }}</strong>
      </article>

      <article class="summaryCard">
        <span class="summaryCardLabel">Kode</span>
        <strong>{{ classroom.code }}</strong>
      </article>
    </div>

    <section v-if="classroom" class="pendingCard">
      <div class="pendingCardHeader">
        <h2>Verden</h2>
        <p>Klasserommet bruker verdenen som er koblet til i backend.</p>
      </div>

      <p class="pendingEmpty">{{ world?.title ?? classroom.worldUid }}</p>
    </section>

    <div v-if="classroom" class="tableCard">
      <table>
        <thead>
          <tr>
            <th>Elev</th>
            <th>Progresjon</th>
            <th class="actionColumn">Fjern</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="student in classroom.members" :key="student.publicUuid">
            <td>
              <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomStudentProgress, `?classroomId=${classroomId}&studentId=${student.publicUuid}`)" class="studentLink">
                {{ student.username }}
              </NuxtLink>
            </td>
            <td>
              <div class="progressCell" :aria-label="`${getStudentProgress(student.publicUuid).completed} fullført, ${getStudentProgress(student.publicUuid).remaining} gjenstår`">
                <span class="starCount">x{{ getStudentProgress(student.publicUuid).completed }}</span>
                <img :src="starFilled" alt="Fullført" class="starIcon">
                <span class="starCount">/x{{ getStudentProgress(student.publicUuid).remaining }}</span>
                <img :src="starEmpty" alt="Gjenstår" class="starIcon">
              </div>
            </td>
            <td class="actionColumn">
              <button
                class="removeButton"
                type="button"
                :aria-label="`Fjern ${student.username} fra klasserommet`"
                :disabled="removingStudentId === student.publicUuid"
                @click="removeStudent(student.publicUuid)"
              >
                X
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="emptyState">
      <h2>Fant ikke klasserommet</h2>
      <p>Velg et klasserom fra lærerhjemsiden.</p>
    </div>
  </section>
</template>

<style scoped>
.classroomPage {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  padding: var(--space-4);
  color: var(--color-text-primary);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(255, 255, 255, 0.88)),
    linear-gradient(180deg, var(--palette-gray-50) 0%, var(--palette-cream-100) 100%);
  border-radius: var(--radius-1);
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
  color: var(--color-text-primary);
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
}

.summaryRow {
  display: grid;
  gap: var(--space-4);
  grid-template-columns: repeat(3, minmax(0, 1fr));
  margin-bottom: var(--space-4);
}

.actionGrid {
  display: grid;
  gap: var(--space-4);
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  margin-bottom: var(--space-4);
}

.actionCard {
  display: grid;
  gap: var(--space-2);
  justify-items: center;
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  color: var(--color-text-primary);
  box-shadow: var(--shadow-1);
}

.actionCardTitle {
  font-weight: 700;
  color: var(--color-brand-primary);
  text-align: center;
}

.actionCardImage {
  display: block;
  width: 4.5rem;
  height: 4.5rem;
  border-radius: 0.9rem;
  object-fit: cover;
}

.summaryCard {
  display: grid;
  gap: 0.35rem;
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.summaryCardLabel {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
}

.summaryCard strong {
  font-size: 1.6rem;
  color: var(--color-brand-primary);
}

.pendingCard {
  display: grid;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
  padding: var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

.pendingCardHeader {
  display: grid;
  gap: 0.35rem;
}

.pendingCardHeader h2,
.pendingStudent strong {
  margin: 0;
}

.pendingCardHeader p,
.pendingEmpty,
.pendingStudent span {
  margin: 0;
  color: var(--color-text-secondary);
}

.pendingList {
  display: grid;
  gap: var(--space-3);
}

.pendingStudent {
  display: grid;
  gap: var(--space-1);
  padding: 0.85rem var(--space-4);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: calc(var(--radius-3) - 0.2rem);
  background: color-mix(in srgb, var(--color-bg-surface) 94%, var(--color-semantic-info) 6%);
}

.tableCard {
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  overflow: auto;
  box-shadow: var(--shadow-1);
}

table {
  width: 100%;
  border-collapse: collapse;
  min-width: 650px;
}

th,
td {
  text-align: left;
  padding: 0.85rem var(--space-4);
  border-bottom: var(--border-width-hairline) solid var(--color-border-default);
}

thead th {
  background: color-mix(in srgb, var(--color-bg-surface) 88%, var(--color-semantic-info) 12%);
  font-weight: 700;
}

.studentLink {
  color: var(--color-brand-primary);
  font-weight: 700;
  text-decoration: none;
}

.studentLink:hover {
  text-decoration: underline;
}

.progressCell {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  color: var(--color-text-primary);
  font-weight: 700;
}

.starCount {
  line-height: 1;
}

.starIcon {
  width: 1.35rem;
  height: 1.35rem;
  object-fit: contain;
}

.actionColumn {
  width: 1%;
  text-align: right;
  white-space: nowrap;
}

.removeButton {
  display: inline-grid;
  place-items: center;
  width: 2rem;
  height: 2rem;
  border: var(--border-width-hairline) solid color-mix(in srgb, var(--color-semantic-danger, #b42318) 65%, var(--color-border-default));
  border-radius: 999px;
  background: color-mix(in srgb, var(--color-bg-surface) 90%, var(--color-semantic-danger, #b42318) 10%);
  color: var(--color-semantic-danger, #b42318);
  cursor: pointer;
  font-weight: 800;
}

.removeButton:hover:not(:disabled) {
  background: color-mix(in srgb, var(--color-bg-surface) 80%, var(--color-semantic-danger, #b42318) 20%);
}

.removeButton:disabled {
  cursor: wait;
  opacity: 0.55;
}

.emptyState {
  padding: 1.25rem;
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-1);
}

@media (max-width: 800px) {
  .summaryRow {
    grid-template-columns: 1fr;
  }
}
</style>
