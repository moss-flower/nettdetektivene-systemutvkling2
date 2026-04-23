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
	<section class="notificationsPage">
		<header class="pageHeader">
			<button class="backButton" type="button" @click="goBack">Tilbake</button>
			<h1>Varslinger</h1>
			<p>Denne siden er tom forelopig.</p>
		</header>
	</section>
</template>

<style scoped>
.notificationsPage {
	width: 100%;
	max-width: 1000px;
	margin: 0 auto;
	padding: var(--space-4);
}

.pageHeader {
	margin-bottom: 1rem;
}

.backButton {
	display: inline-flex;
	align-items: center;
	margin-bottom: 0.75rem;
	padding: 0;
	border: 0;
	background: transparent;
	color: var(--primary);
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
	margin-top: 0.5rem;
	color: var(--muted);
}
</style>
