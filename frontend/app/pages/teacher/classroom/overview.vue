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
const renamingStudentId = ref('');

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

const renameStudent = async (studentId: string, currentName: string) => {
  if (!classroom.value || renamingStudentId.value) return;

  const nextName = window.prompt(`Endre navn fra "${currentName}"`, currentName)?.trim();
  if (!nextName || nextName === currentName) return;

  if (!window.confirm(`Endre navn fra "${currentName}" til "${nextName}"?`)) return;

  renamingStudentId.value = studentId;
  try {
    await teacher.updateStudentUsername(studentId, nextName);
    await refreshClassroom();
  } catch (error) {
    console.error('Failed to rename student', error);
    window.alert('Kunne ikke endre navn. Prøv igjen.');
  } finally {
    renamingStudentId.value = '';
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
        <span class="actionCardTitle">Stasjonskart</span>
        <img src="https://placehold.co/128x128?text=SK" alt="Stasjonskart" class="actionCardImage">
      </NuxtLink>

      <NuxtLink :to="{ path: routePaths.studentLevels, query: { preview: '1', classroomId: classroom.uid } }" class="actionCard">
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
        <h2>Stasjonskart</h2>
        <p>Klasserommet bruker stasjonskartet som er koblet til i backend.</p>
      </div>

      <p class="pendingEmpty">{{ world?.title ?? classroom.worldUid }}</p>
    </section>

    <div v-if="classroom" class="tableCard">
      <table>
        <thead>
          <tr>
            <th>Elev</th>
            <th>Progresjon</th>
            <th class="actionColumn">Endre navn</th>
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
                class="renameButton"
                type="button"
                :aria-label="`Endre navn på ${student.username}`"
                :disabled="renamingStudentId === student.publicUuid"
                @click="renameStudent(student.publicUuid, student.username)"
              >
                Endre navn
              </button>
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
  max-width: var(--layout-max-width);
  margin: 0 auto;
  padding: var(--layout-page-padding);
  color: var(--color-text-primary);
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
  transition: var(--transition-fast);
}
.backLink:hover {
  text-decoration: underline;
}
.eyebrow {
  color: var(--color-brand-primary);
  font-weight: var(--font-weight-bold);
  font-size: var(--font-size-12);
  letter-spacing: var(--font-tracking-wider);
  text-transform: uppercase;
}
h1 {
  font-family: var(--font-family-serif), sans-serif;
  font-size: var(--font-size-28);
  line-height: var(--font-leading-28);
  margin: 0;
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
  background: var(--color-bg-surface);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  box-shadow: var(--shadow-1);
}
.actionCardTitle {
  font-weight: var(--font-weight-bold);
  color: var(--color-brand-primary);
  text-align: center;
}
.actionCardImage {
  display: block;
  width: 72px; /* deliberate: fixed placeholder icon */
  height: 72px; /* deliberate: fixed placeholder icon */
  border-radius: var(--radius-2);
  object-fit: cover;
}
.summaryCard {
  display: grid;
  gap: var(--space-1);
  padding: var(--space-4);
  background: var(--color-bg-surface);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  box-shadow: var(--shadow-1);
}
.summaryCardLabel {
  font-size: var(--font-size-14);
  color: var(--color-text-secondary);
}
.summaryCard strong {
  font-size: var(--font-size-22);
  color: var(--color-brand-primary);
}
.pendingCard {
  display: grid;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
  padding: var(--space-4);
  background: var(--color-bg-surface);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  box-shadow: var(--shadow-1);
}
.pendingCardHeader {
  display: grid;
  gap: var(--space-1);
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
  padding: var(--space-3) var(--space-4);
  background: var(--color-bg-canvas);
  border-radius: var(--radius-2);
}
.tableCard {
  background: var(--color-bg-surface);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  box-shadow: var(--shadow-1);
  overflow: auto;
}
table {
  width: 100%;
  border-collapse: collapse;
  min-width: 650px; /* deliberate: minimum readable table width */
}
th,
td {
  text-align: left;
  padding: var(--space-3) var(--space-4);
  border-bottom: var(--border-width-hairline) solid var(--color-border-default);
}
thead th {
  background: var(--color-bg-canvas);
  font-weight: var(--font-weight-bold);
}
.studentLink {
  color: var(--color-brand-primary);
  font-weight: var(--font-weight-bold);
  text-decoration: none;
}
.studentLink:hover {
  text-decoration: underline;
}
.progressCell {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  font-weight: var(--font-weight-bold);
}
.starCount {
  line-height: 1;
}
.starIcon {
  width: var(--size-icon-sm);
  height: var(--size-icon-sm);
  object-fit: contain;
}
.actionColumn {
  width: 1%; /* deliberate: shrink-to-fit column */
  text-align: right;
  white-space: nowrap;
}
.removeButton {
  display: inline-grid;
  place-items: center;
  width: var(--size-control-sm);
  height: var(--size-control-sm);
  border-radius: var(--radius-pill);
  border: var(--border-width-hairline) solid var(--color-semantic-error);
  background: var(--color-bg-surface);
  color: var(--color-semantic-error);
  cursor: pointer;
  font-weight: var(--font-weight-bold);
}
.removeButton:hover:not(:disabled) {
  background: var(--palette-overlay-red-soft);
}
.removeButton:disabled {
  cursor: wait;
  opacity: 0.55;
}
.emptyState {
  padding: var(--space-4);
  background: var(--color-bg-surface);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  box-shadow: var(--shadow-1);
}
@media (max-width: 800px) {
  .summaryRow {
    grid-template-columns: 1fr;
  }
}
</style>
