<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";
import { useClassrooms } from "~/composables/useClassrooms";
import { useAuth } from "~/composables/useAuth";

const { student } = useClassrooms();
const { logout } = useAuth();

const { data: classroom, refresh } = await useAsyncData(
  "student-pending-classroom",
  () => student.getMyClassroom().catch(() => null),
);

watchEffect(async () => {
  if (classroom.value && !classroom.value.pendingEntry) {
    await navigateTo(routePaths.studentHome);
  }
});

async function handleLogout() {
  await logout();
  await navigateTo(routePaths.login);
}
</script>

<template>
  <section class="pendingPage">
    <article class="pendingCard">
      <p class="eyebrow">Forespørsel sendt</p>
      <h1>Venter på godkjenning</h1>
      <p class="message">
        Du har bedt om å bli med i {{ classroom?.name ?? "klasserommet" }}. Læreren din må godkjenne forespørselen før du kan fortsette.
      </p>

      <div class="actions">
        <button class="secondaryButton" type="button" @click="handleLogout">
          Logg ut
        </button>
        <button class="primaryButton" type="button" @click="refresh">
          Oppdater
        </button>
      </div>
    </article>
  </section>
</template>

<style scoped>
.pendingPage {
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-4);
  background: color-mix(in srgb, var(--color-bg-canvas) 12%, var(--palette-white));
}

.pendingCard {
  width: min(100%, 32rem);
  display: grid;
  gap: var(--space-4);
  padding: var(--space-8);
  border: var(--border-width-hairline) solid var(--color-border-default);
  border-radius: var(--radius-3);
  background: var(--color-bg-surface);
  box-shadow: var(--shadow-2);
  text-align: center;
}

.eyebrow {
  margin: 0;
  color: var(--color-brand-primary);
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

h1 {
  margin: 0;
  color: var(--color-text-primary);
}

.message {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.actions {
  display: flex;
  justify-content: center;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.primaryButton,
.secondaryButton {
  padding: 0.85rem var(--space-5);
  border-radius: var(--radius-2);
  font-weight: 700;
  cursor: pointer;
}

.primaryButton {
  border: 0;
  background: var(--color-brand-primary);
  color: var(--color-text-onDark);
}

.primaryButton:hover {
  background: var(--palette-navy-700);
}

.secondaryButton {
  border: var(--border-width-hairline) solid var(--color-border-default);
  background: var(--color-bg-surface);
  color: var(--color-text-primary);
}

.secondaryButton:hover {
  border-color: var(--color-brand-primary);
  color: var(--color-brand-primary);
}
</style>
