<script setup lang="ts">
import { routePaths, routePathsWithString } from '~/composables/utils/routePaths';
const route = useRoute();
const { teacher } = useClassrooms();
  
const classroomId = computed(() => String(route.query.classroomId ?? ''));
const { data: classroom, refresh: refreshClassroom } = await useAsyncData(
  'teacher-classroom-students',
  () => classroomId.value ? teacher.getClassroomById(classroomId.value).catch(() => null) : Promise.resolve(null),
  { watch: [classroomId] },
);

const students = computed(() => classroom.value?.members ?? []);
const renamingStudentId = ref('');

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
  <section class="pageShell">
    <header class="pageHeader">
      <NuxtLink class="backLink" :to="routePathsWithString(routePaths.teacherClassroomOverview, { classroomId: classroomId })">Tilbake til klasserommet</NuxtLink>
      <p class="eyebrow">Elevoversikt</p>
      <h1>{{ classroom?.name ?? 'Ukjent klasserom' }}</h1>
      <p class="subtitle">Klikk på en elev for å se progresjonen.</p>
    </header>

    <div class="tableCard">
      <table>
        <thead>
          <tr>
            <th>Elev</th>
            <th>Bruker-ID</th>
            <th class="actionColumn">Endre navn</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="student in students" :key="student.publicUuid">
            <td>
              <NuxtLink :to="routePathsWithString(routePaths.teacherClassroomStudentProgress, { classroomId: classroomId, studentId: student.publicUuid })">{{ student.username }}</NuxtLink>
            </td>
            <td>{{ student.publicUuid }}</td>
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
          </tr>
        </tbody>
      </table>
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
  color: var(--color-text-primary);
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
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
  min-width: 680px;
}

th,
td {
  text-align: left;
  padding: 0.85rem var(--space-4);
  border-bottom: var(--border-width-hairline) solid var(--color-border-default);
}

thead th {
  background: color-mix(in srgb, var(--color-bg-surface) 88%, var(--color-semantic-info) 12%);
}

tbody a {
  color: var(--color-brand-primary);
  font-weight: 700;
}

.actionColumn {
  width: 1%;
  text-align: right;
  white-space: nowrap;
}

.renameButton {
  padding: 0.5rem 1rem;
  border: var(--border-width-hairline) solid var(--color-brand-primary);
  border-radius: var(--radius-2);
  background: color-mix(in srgb, var(--color-bg-surface) 90%, var(--color-brand-primary) 10%);
  color: var(--color-brand-primary);
  cursor: pointer;
  font-weight: 600;
  font-size: 0.9rem;
}

.renameButton:hover:not(:disabled) {
  background: color-mix(in srgb, var(--color-bg-surface) 80%, var(--color-brand-primary) 20%);
}

.renameButton:disabled {
  cursor: wait;
  opacity: 0.55;
}
</style>
