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
	padding: 1.5rem;
	background: rgba(0, 0, 0, 0.6);
	z-index: 1000;
}

.popupCard {
	width: min(100%, 28rem);
	display: grid;
	gap: 1rem;
	padding: 1.5rem;
	border: 1px solid var(--border);
	border-radius: 1.2rem;
	background: var(--surface);
	box-shadow: var(--shadow);
}

.popupCard h2,
.popupCard p {
	margin: 0;
}

.popupCard p {
	color: var(--muted);
	line-height: 1.5;
}

.primaryButton {
	justify-self: flex-start;
	border: 1px solid var(--primary);
	border-radius: 999px;
	padding: 0.75rem 1rem;
	font-weight: 700;
	background: var(--primary);
	color: var(--bg);
	transition: transform var(--transition), border-color var(--transition), background var(--transition);
}

.primaryButton:hover {
	transform: translateY(-1px);
}
</style>
