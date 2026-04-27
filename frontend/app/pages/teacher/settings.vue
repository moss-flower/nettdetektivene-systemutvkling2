<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";

const router = useRouter();

const goBack = () => {
	if (import.meta.client && window.history.length > 1) {
		router.back();
		return;
	}

	void navigateTo(routePaths.teacherOverview);
};
</script>

<template>
	<section class="settingsPage">
		<header class="pageHeader">
			<button class="backButton" type="button" @click="goBack">Tilbake</button>
			<h1>Innstillinger</h1>
			<p>Denne siden er tom forelopig.</p>
		</header>
	</section>
</template>

<style scoped>
.settingsPage {
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
	display: grid;
	gap: var(--space-2);
	margin-bottom: var(--space-4);
	padding: var(--space-5);
	border: var(--border-width-hairline) solid var(--color-border-default);
	border-radius: var(--radius-3);
	background: var(--color-bg-surface);
	box-shadow: var(--shadow-1);
}

.backButton {
	display: inline-flex;
	align-items: center;
	margin-bottom: var(--space-3);
	padding: 0;
	border: 0;
	background: transparent;
	color: var(--color-brand-primary);
	font: inherit;
	font-weight: 600;
	cursor: pointer;
}

.backButton:hover {
	text-decoration: underline;
}

h1,
p {
	margin: 0;
}

p {
	margin-top: var(--space-2);
	color: var(--color-text-secondary);
}
</style>
