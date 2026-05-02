<script setup lang="ts">
import { World } from '~/composables/types/WorldTypes';
import starEmpty from '~/assets/images/star/spStarEmpty.png';
import starFilled from '~/assets/images/star/spStarFilled.png';
import { getClassroomExercises } from '~/composables/useTeacherDashboard';
import { Bell, Map, Play, Search } from 'lucide-vue-next';
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
const pendingMembers = computed(() => classroom.value?.pendingMembers ?? []);

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
const approvingStudentId = ref('');
const deletingClassroom = ref(false);

const getStudentProgress = (studentId: string): StudentProgress => progressByStudent.value?.[studentId] ?? {
  completed: 0,
  remaining: totalExercises.value,
};

const removeStudent = async (studentId: string, studentName: string) => {
  if (!classroom.value || removingStudentId.value) {
    return;
  }

  if (!window.confirm(`Fjerne ${studentName} fra klasserommet?`)) return;

  removingStudentId.value = studentId;

  try {
    await teacher.removeMember(classroom.value.uid, studentId);
    await refreshClassroom();
    await refreshProgress();
  } finally {
    removingStudentId.value = '';
  }
};

const approveStudent = async (studentId: string) => {
  if (!classroom.value || approvingStudentId.value) {
    return;
  }

  approvingStudentId.value = studentId;

  try {
    await teacher.approveMember(classroom.value.uid, studentId);
    await refreshClassroom();
    await refreshProgress();
  } finally {
    approvingStudentId.value = '';
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

const deleteClassroom = async () => {
  if (!classroom.value || deletingClassroom.value) return;

  if (!window.confirm(`Slette klasserommet "${classroom.value.name}"? Dette kan ikke angres.`)) return;

  deletingClassroom.value = true;
  try {
    await teacher.deleteClassroom(classroom.value.uid);
    await navigateTo(routePaths.teacherOverview);
  } catch (error) {
    console.error('Failed to delete classroom', error);
    window.alert('Kunne ikke slette klasserommet. Prøv igjen.');
  } finally {
    deletingClassroom.value = false;
  }
};
</script>

<template>
  <section class="classroomPage">
    <header class="pageHeader">
      <div class="headerActions">
        <NuxtLink class="backLink" :to="routePaths.teacherOverview">Tilbake til hjemskjerm</NuxtLink>
        <button
          v-if="classroom"
          class="deleteClassroomButton"
          type="button"
          :disabled="deletingClassroom"
          @click="deleteClassroom"
        >
          Slett klasserom
        </button>
      </div>
      <p class="eyebrow">Klasseromsdetaljer</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
      <p class="subtitle">Viser klasserommet slik det ligger i backend akkurat nå.</p>
    </header>

    <div v-if="classroom" class="actionGrid">
      <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomNotifications, `?classroomId=${classroom.uid}`)" class="actionCard">
        <span class="actionCardTitle">Varslingssenter</span>
        <Bell :size="48" class="actionCardIcon" />
      </NuxtLink>

      <NuxtLink :to="routePathsWithUid(routePaths.teacherFactoryWorld, classroom.worldUid)" class="actionCard">
        <span class="actionCardTitle">Stasjonskart</span>
        <Map :size="48" class="actionCardIcon" />
      </NuxtLink>

      <NuxtLink :to="{ path: routePaths.studentLevels, query: { preview: '1', classroomId: classroom.uid } }" class="actionCard">
        <span class="actionCardTitle">Test spillet</span>
        <Play :size="48" class="actionCardIcon" />
      </NuxtLink>

      <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomMystery, `?classroomId=${classroom.uid}`)" class="actionCard">
        <span class="actionCardTitle">Ukens mysterium</span>
        <Search :size="48" class="actionCardIcon" />
      </NuxtLink>
    </div>

    <div v-if="classroom" class="summaryRow">
      <article class="summaryCard">
        <span class="summaryCardLabel">Elever</span>
        <strong>{{ classroom.members.length }}</strong>
      </article>

      <article class="summaryCard">
        <span class="summaryCardLabel">Venter</span>
        <strong>{{ pendingMembers.length }}</strong>
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

    <section v-if="classroom" class="pendingCard">
      <div class="pendingCardHeader">
        <h2>Pending</h2>
        <p>Elever som har bedt om å bli med i klasserommet.</p>
      </div>

      <div v-if="pendingMembers.length" class="pendingList">
        <article v-for="student in pendingMembers" :key="student.publicUuid" class="pendingStudent">
          <div>
            <strong>{{ student.username }}</strong>
            <span>{{ student.publicUuid }}</span>
          </div>
          <button
            class="approveButton"
            type="button"
            :disabled="approvingStudentId === student.publicUuid"
            @click="approveStudent(student.publicUuid)"
          >
            Godkjenn
          </button>
        </article>
      </div>

      <p v-else class="pendingEmpty">Ingen elever venter på godkjenning.</p>
    </section>

    <div v-if="classroom" class="tableCard">
      <h2 class="tableTitle">Students</h2>
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
                @click="removeStudent(student.publicUuid, student.username)"
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

.headerActions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-3);
  margin-bottom: var(--space-3);
}

.backLink {
  display: inline-block;
  color: var(--color-brand-primary);
  font-weight: var(--font-weight-semibold);
  transition: var(--transition-fast);
}

.backLink:hover {
  text-decoration: underline;
}

.deleteClassroomButton {
  padding: 0.55rem 1rem;
  border: var(--border-width-hairline) solid color-mix(in srgb, var(--color-semantic-danger, #b42318) 70%, var(--color-border-default));
  border-radius: var(--radius-2);
  background: color-mix(in srgb, var(--color-bg-surface) 88%, var(--color-semantic-danger, #b42318) 12%);
  color: var(--color-semantic-danger, #b42318);
  cursor: pointer;
  font-weight: 700;
}

.deleteClassroomButton:hover:not(:disabled) {
  background: color-mix(in srgb, var(--color-bg-surface) 76%, var(--color-semantic-danger, #b42318) 24%);
}

.deleteClassroomButton:disabled {
  cursor: wait;
  opacity: 0.55;
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
  grid-template-columns: repeat(4, minmax(0, 1fr));
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
  text-decoration: none;
  cursor: pointer;
  transition: transform var(--transition-fast), box-shadow var(--transition-fast), border-color var(--transition-fast);
}

.actionCard:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-2);
  border-color: var(--color-brand-primary);
}

.actionCardTitle {
  font-weight: var(--font-weight-bold);
  color: var(--color-brand-primary);
  text-align: center;
}

.actionCardIcon {
  color: var(--color-brand-primary);
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
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-1);
  padding: var(--space-3) var(--space-4);
  background: var(--color-bg-canvas);
  border-radius: var(--radius-2);
}

.pendingStudent div {
  display: grid;
  gap: var(--space-1);
}

.approveButton {
  padding: 0.55rem 1rem;
  border: 0;
  border-radius: var(--radius-2);
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
  cursor: pointer;
  font-weight: 700;
}

.approveButton:disabled {
  cursor: wait;
  opacity: 0.55;
}

.tableCard {
  background: var(--color-bg-surface);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  box-shadow: var(--shadow-1);
  overflow: auto;
}

.tableTitle {
  margin: 0;
  padding: var(--space-4) var(--space-4) 0;
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
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
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
  .headerActions {
    align-items: flex-start;
    flex-direction: column;
  }

  .summaryRow {
    grid-template-columns: 1fr;
  }
}
</style>
