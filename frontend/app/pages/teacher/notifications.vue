<script setup lang="ts">
import { routePaths } from "~/composables/utils/routePaths";

const router = useRouter();
definePageMeta({ layout: "teacher" });

const { connected, notifications, notificationCount } = useNotifications();

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
			<p class="eyebrow">Varslingssenter</p>
			<h1>Varslinger</h1>
			<p v-if="notificationCount > 0">
				Du har {{ notificationCount }} aktiv{{ notificationCount === 1 ? '' : 'e' }} varsling{{ notificationCount === 1 ? '' : 'er' }}.
			</p>
			<p v-else>Ingen varslinger akkurat nå.</p>
		</header>

		<article class="pageCard">
			<div class="pageCardHeader">
				<h2>Innkommende</h2>
				<span class="connectionStatus" :class="{ 'connectionStatus--connected': connected }">
					{{ connected ? 'Tilkoblet' : 'Kobler til...' }}
				</span>
			</div>

			<TransitionGroup v-if="notifications.length > 0" name="slide" tag="ul" class="notificationList">
				<NotificationItem
					v-for="(notification, index) in notifications"
					:key="notification.notificationUuid"
					:notification="notification"
					:index="index"
				/>
			</TransitionGroup>

			<p v-else class="emptyState">Nye varslinger fra elever dukker opp her.</p>
		</article>
	</section>
</template>

<style scoped>
.notificationsPage {
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

.pageCard {
	display: grid;
	gap: var(--space-4);
	padding: var(--space-5);
	border: var(--border-width-hairline) solid var(--color-border-default);
	border-radius: var(--radius-3);
	background: var(--color-bg-surface);
	box-shadow: var(--shadow-1);
}

.pageCardHeader {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: var(--space-3);
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

.eyebrow {
	margin: 0;
	color: var(--color-text-secondary);
	font-weight: var(--font-weight-bold);
	letter-spacing: var(--font-tracking-wider);
	text-transform: uppercase;
}

h1,
h2,
p {
	margin: 0;
}

p {
	margin-top: var(--space-2);
	color: var(--color-text-secondary);
}

.connectionStatus {
	font-size: var(--font-size-12);
	font-weight: var(--font-weight-bold);
	letter-spacing: var(--font-tracking-wider);
	text-transform: uppercase;
	color: var(--color-text-secondary);
}

.connectionStatus--connected {
	color: var(--palette-green-700);
}

.notificationList {
	list-style: none;
	margin: 0;
	padding: 0;
	display: grid;
	gap: var(--space-3);
}

.emptyState {
	margin: 0;
	color: var(--color-text-secondary);
	font-size: var(--font-size-14);
}

.slide-enter-active,
.slide-leave-active {
	transition: all 0.2s ease;
}

.slide-enter-from {
	opacity: 0;
	transform: translateY(-6px);
}

.slide-leave-to {
	opacity: 0;
	transform: translateY(6px);
}
</style>
