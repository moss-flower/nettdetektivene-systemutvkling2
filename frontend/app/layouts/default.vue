<template>
  <div class="appShell">
    <aside class="sidebar">
      <div class="profile">
        <!-- avatar +(?) detektiv navn -->
      </div>

      <button class="navHome" type="button" aria-label="Tilbake til hovedmeny">
        <!-- map-pin icon -->
      </button>
    </aside>

    <main class="content">
      <slot />
    </main>
  </div>
</template>

<style scoped>
.appShell {
  min-height: 100dvh;
  width: 100%;
  display: grid;
  grid-template-columns: var(--layout-sidebar-width) 1fr;
}

.sidebar {
  background: var(--color-bg-surface);
  border-right: var(--border-width-hairline) solid var(--color-border-default);
  padding: var(--space-5) var(--space-3);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.profile {
  margin: auto 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
  text-align: center;
}

.navHome {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-2);
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  transition: transform var(--transition-fast);
}

.navHome:hover {
  transform: translateY(-2px);
}

.content {
  min-width: 0;
  min-height: 0;
}
</style>
