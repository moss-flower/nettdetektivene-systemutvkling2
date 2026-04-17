<script setup lang="ts">
const backPath = routePaths.teacherOverview;
</script>

<template>
	<section class="factoryPage">
		<p class="factoryEyebrow">Lærerverktøy</p>
		<h1>Oppgavefabrikk</h1>
		<p class="factoryDescription">
			Denne siden er ikke implementert ennå.
		</p>
		<NuxtLink class="factoryBackLink" :to="backPath">
			Tilbake til læreroversikt
		</NuxtLink>
	</section>
</template>

<style scoped>
.factoryPage {
	width: 100%;
	max-width: 900px;
	margin: 0 auto;
	padding: var(--space-4);
}

.factoryEyebrow {
	margin: 0 0 0.5rem;
	color: var(--primary);
	font-weight: 700;
	letter-spacing: 0.08em;
	text-transform: uppercase;
}

h1 {
	margin: 0 0 0.75rem;
}

.factoryDescription {
	margin: 0 0 1rem;
	color: var(--muted);
}

.factoryBackLink {
	color: var(--primary);
	font-weight: 700;
}
</style>
