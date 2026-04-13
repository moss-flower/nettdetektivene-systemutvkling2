<script setup lang="ts">
type Student = {
  firstName: string
  lastName: string
  classLabel: string
  tasksCompleted: number
  xp: number
}

const students = [
  { firstName: 'Ada', lastName: 'Berg', classLabel: 'Ronvik skole 6A', tasksCompleted: 14, xp: 980 },
  { firstName: 'Liam', lastName: 'Dahl', classLabel: 'Ronvik skole 6A', tasksCompleted: 11, xp: 820 },
  { firstName: 'Nora', lastName: 'Eide', classLabel: 'Ronvik skole 6B', tasksCompleted: 19, xp: 1240 },
  { firstName: 'Isak', lastName: 'Foss', classLabel: 'Ronvik skole 6B', tasksCompleted: 7, xp: 590 },
  { firstName: 'Mina', lastName: 'Hagen', classLabel: 'Ronvik skole 6A', tasksCompleted: 16, xp: 1070 },
  { firstName: 'Sander', lastName: 'Lunde', classLabel: 'Ronvik skole 6C', tasksCompleted: 9, xp: 670 },
  { firstName: 'Emilie', lastName: 'Nilsen', classLabel: 'Ronvik skole 6C', tasksCompleted: 20, xp: 1330 },
  { firstName: 'Tobias', lastName: 'Solheim', classLabel: 'Ronvik skole 6A', tasksCompleted: 6, xp: 530 },
  { firstName: 'Oskar', lastName: 'Vik', classLabel: 'Ronvik skole 6B', tasksCompleted: 12, xp: 870 },
  { firstName: 'Leah', lastName: 'Aas', classLabel: 'Ronvik skole 6C', tasksCompleted: 18, xp: 1160 }
] satisfies Student[]

const sortedStudents = computed(() => {
  return [...students].sort((a, b) => a.lastName.localeCompare(b.lastName))
})
</script>

<template>
  <section class="overviewPage">
    <header class="overviewHeader">
      <p class="eyebrow">Læreroversikt</p>
      <h1>Elevstatistikk</h1>
      <p class="subtitle">Midlertidig visning med mock-data sortert på etternavn.</p>
    </header>

    <div class="tableCard">
      <table>
        <thead>
          <tr>
            <th>Navn</th>
            <th>Klasse</th>
            <th>Fullførte oppgaver</th>
            <th>XP</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="student in sortedStudents" :key="`${student.lastName}-${student.firstName}`">
            <td>{{ student.firstName }} {{ student.lastName }}</td>
            <td>{{ student.classLabel }}</td>
            <td>{{ student.tasksCompleted }}</td>
            <td>{{ student.xp }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.overviewPage {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: var(--space-4);
}

.overviewHeader {
  margin-bottom: var(--space-4);
}

.eyebrow {
  color: var(--primary);
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

h1 {
  margin: 0 0 0.5rem 0;
  color: var(--text);
}

.subtitle {
  margin: 0;
  color: var(--muted);
}

.tableCard {
  border: 1px solid var(--border);
  border-radius: 1rem;
  background: #fff;
  overflow: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  min-width: 650px;
}

th,
td {
  text-align: left;
  padding: 0.85rem 1rem;
  border-bottom: 1px solid var(--border);
}

thead th {
  background: #f8fafc;
  font-weight: 700;
}
</style>
