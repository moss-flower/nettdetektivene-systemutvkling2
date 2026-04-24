<script setup lang="ts">
const emit = defineEmits<{
	(event: "close"): void;
}>();

const closePopup = () => {
	emit("close");
};
</script>

<template>
	<div class="popupOverlay" @click.self="closePopup">
		<section class="popupCard" role="dialog" aria-modal="true" aria-labelledby="popup-title">
			<h2 id="popup-title">Oppgaven er lagret</h2>
			<p>
				Dette blir senere sendt til backend. For nå er oppgaven lagret lokalt.
			</p>
			<button type="button" class="primaryButton" @click="closePopup">
				Lukk
			</button>
		</section>
	</div>
</template>

<style scoped>
.popupOverlay {
	position: fixed;
	inset: 0;
	display: grid;
	place-items: center;
	padding: var(--space-6);
	background: rgba(0, 0, 0, 0.6);
	z-index: 1000;
}

.popupCard {
	width: min(100%, 28rem);
	display: grid;
	gap: var(--space-4);
	padding: var(--space-6);
	border: var(--border-width-hairline) solid var(--color-border-default);
	border-radius: 1.2rem;
	background: var(--surface);
	box-shadow: var(--shadow);
}

.popupCard h2,
.popupCard p {
	margin: 0;
}

.popupCard p {
	color: var(--color-text-secondary);
	line-height: 1.5;
}

.primaryButton {
	justify-self: flex-start;
	border: var(--border-width-hairline) solid var(--color-brand-primary);
	border-radius: var(--radius-pill);
	padding: var(--space-3) var(--space-4);
	font-weight: 700;
	background: var(--color-brand-primary);
	color: var(--bg);
	transition: transform var(--transition), border-color var(--transition), background var(--transition);
}

.primaryButton:hover {
	transform: translateY(-1px);
}
</style>
