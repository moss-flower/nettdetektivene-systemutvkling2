<template>
  <div class="app-shell">
    <header class="topbar">
      <h1>Nettdetektivene</h1>
    </header>

    <main class="content">
      <slot />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  width: 100%;
  font-family: Arial, sans-serif;
}

.topbar {
  padding: 1rem;
  border-bottom: 1px solid #ddd;
}

.content {
  padding: 1rem;
  width: 90%;
  margin: auto;
  height: 90vh;
}
</style>
