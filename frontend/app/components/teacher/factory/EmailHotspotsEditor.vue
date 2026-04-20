<script setup lang="ts">
import { emailHotspotTargetIds } from "~/composables/useExerciseFactory";

import type {
	EmailHotspotBodyLinkBlock,
	EmailHotspotBodyTextBlock,
	EmailHotspotTaskData,
} from "~/composables/types/TaskTypes";

const props = defineProps<{
	task: EmailHotspotTaskData;
}>();

const emailTextBlock = () => props.task.email.bodyBlocks[0] as EmailHotspotBodyTextBlock;
const emailLinkBlock = () => props.task.email.bodyBlocks[1] as EmailHotspotBodyLinkBlock;
const emailActionButton = () => props.task.email.actionButton;
const emailAttachment = () => props.task.email.attachments?.[0];

const getEmailTarget = (targetId: string) => {
	return props.task.targets.find((target) => target.id === targetId);
};

const ensureEmailTarget = (targetId: string) => {
	const existingTarget = getEmailTarget(targetId);

	if (existingTarget) {
		return existingTarget;
	}

	const nextTarget = {
		id: targetId,
		isSuspicious: false,
	};

	props.task.targets.push(nextTarget);
	return nextTarget;
};

const isEmailHotspotSuspicious = (targetId: string) => {
	return getEmailTarget(targetId)?.isSuspicious ?? false;
};

const setEmailHotspotSuspicious = (targetId: string, isSuspicious: boolean) => {
	ensureEmailTarget(targetId).isSuspicious = isSuspicious;
};
</script>

<template>
	<div class="templateSection">
		<h4>E-post hotspots</h4>
		<div class="fieldGrid">
			<label class="field">
				<span>Prompt-tittel</span>
				<input v-model="props.task.prompt!.title" type="text" >
			</label>

			<label class="field field--full">
				<span>Prompt-tekst</span>
				<textarea v-model="props.task.prompt!.text" rows="2"></textarea>
			</label>

			<label class="field">
				<span>Avsendernavn</span>
				<input v-model="props.task.email.senderName" type="text" >
			</label>

			<div class="hotspotCard">
				<div class="hotspotCardHeader">
					<span class="hotspotCardTitle">Avsender-hotspot</span>
					<div class="hotspotToggleRow">
						<label class="hotspotToggle">
							<input
								:checked="isEmailHotspotSuspicious(emailHotspotTargetIds.senderAddress)"
								type="checkbox"
								@change="setEmailHotspotSuspicious(emailHotspotTargetIds.senderAddress, ($event.target as HTMLInputElement).checked)"
							>
							<span>Mistenkelig</span>
						</label>
					</div>
				</div>

				<label class="field">
					<span>Avsenderadresse</span>
					<input v-model="props.task.email.senderAddress" type="email" >
				</label>
			</div>

			<div class="hotspotCard hotspotCard--full">
				<div class="hotspotCardHeader">
					<span class="hotspotCardTitle">Emne-hotspot</span>
					<div class="hotspotToggleRow">
						<label class="hotspotToggle">
							<input
								:checked="isEmailHotspotSuspicious(emailHotspotTargetIds.subject)"
								type="checkbox"
								@change="setEmailHotspotSuspicious(emailHotspotTargetIds.subject, ($event.target as HTMLInputElement).checked)"
							>
							<span>Mistenkelig</span>
						</label>
					</div>
				</div>

				<label class="field field--full">
					<span>Emne</span>
					<input v-model="props.task.email.subject" type="text" >
				</label>
			</div>

			<div class="hotspotCard hotspotCard--full">
				<div class="hotspotCardHeader">
					<span class="hotspotCardTitle">Tekst-hotspot</span>
					<div class="hotspotToggleRow">
						<label class="hotspotToggle">
							<input
								:checked="isEmailHotspotSuspicious(emailHotspotTargetIds.bodyText)"
								type="checkbox"
								@change="setEmailHotspotSuspicious(emailHotspotTargetIds.bodyText, ($event.target as HTMLInputElement).checked)"
							>
							<span>Mistenkelig</span>
						</label>
					</div>
				</div>

				<label class="field field--full">
					<span>Brødtekst</span>
					<textarea v-model="emailTextBlock().text" rows="3"></textarea>
				</label>
			</div>

			<div class="hotspotCard hotspotCard--full">
				<div class="hotspotCardHeader">
					<span class="hotspotCardTitle">Lenke-hotspot</span>
					<div class="hotspotToggleRow">
						<label class="hotspotToggle">
							<input
								:checked="isEmailHotspotSuspicious(emailHotspotTargetIds.bodyLink)"
								type="checkbox"
								@change="setEmailHotspotSuspicious(emailHotspotTargetIds.bodyLink, ($event.target as HTMLInputElement).checked)"
							>
							<span>Mistenkelig</span>
						</label>
					</div>
				</div>

				<div class="hotspotCardFields">
					<label class="field">
						<span>Lenketekst</span>
						<input v-model="emailLinkBlock().label" type="text" >
					</label>

					<label class="field">
						<span>Lenke-URL</span>
						<input v-model="emailLinkBlock().urlText" type="text" >
					</label>
				</div>
			</div>

			<div class="hotspotCard hotspotCard--full">
				<div class="hotspotCardHeader">
					<span class="hotspotCardTitle">Knapp-hotspot</span>
					<div class="hotspotToggleRow">
						<label class="hotspotToggle">
							<input
								:checked="isEmailHotspotSuspicious(emailHotspotTargetIds.actionButton)"
								type="checkbox"
								@change="setEmailHotspotSuspicious(emailHotspotTargetIds.actionButton, ($event.target as HTMLInputElement).checked)"
							>
							<span>Mistenkelig</span>
						</label>
					</div>
				</div>

				<div class="hotspotCardFields">
					<label class="field">
						<span>Knappetekst</span>
						<input v-model="emailActionButton()!.label" type="text" >
					</label>

					<label class="field">
						<span>Knapp-hjelpetekst</span>
						<input v-model="emailActionButton()!.helperText" type="text" >
					</label>
				</div>
			</div>

			<div class="hotspotCard hotspotCard--full">
				<div class="hotspotCardHeader">
					<span class="hotspotCardTitle">Vedlegg-hotspot</span>
					<div class="hotspotToggleRow">
						<label class="hotspotToggle">
							<input
								:checked="isEmailHotspotSuspicious(emailHotspotTargetIds.attachment)"
								type="checkbox"
								@change="setEmailHotspotSuspicious(emailHotspotTargetIds.attachment, ($event.target as HTMLInputElement).checked)"
							>
							<span>Mistenkelig</span>
						</label>
					</div>
				</div>

				<div class="hotspotCardFields">
					<label class="field">
						<span>Vedleggsnavn</span>
						<input v-model="emailAttachment()!.name" type="text" >
					</label>

					<label class="field">
						<span>Vedleggsdetaljer</span>
						<input v-model="emailAttachment()!.details" type="text" >
					</label>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
.templateSection {
	margin-top: 1rem;
	padding-top: 1rem;
	border-top: 1px solid var(--border);
}

.templateSection h4 {
	margin: 0 0 0.8rem;
	font-weight: 700;
}

.fieldGrid,
.hotspotCardFields {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 0.9rem;
}

.field {
	display: grid;
	gap: 0.45rem;
}

.field--full,
.hotspotCard--full {
	grid-column: 1 / -1;
}

.field span {
	font-weight: 700;
}

.field input,
.field textarea {
	width: 100%;
	padding: 0.85rem 1rem;
	border: 1px solid var(--border);
	border-radius: 0.9rem;
	background: rgba(0, 0, 0, 0.16);
	color: var(--text);
	font: inherit;
}

.field textarea {
	resize: vertical;
}

.field input:focus,
.field textarea:focus {
	outline: none;
	border-color: var(--primary);
}

.hotspotCard {
	display: grid;
	gap: 0.85rem;
	padding: 0.85rem;
	border: 1px solid var(--border);
	border-radius: 0.9rem;
	background: rgba(255, 255, 255, 0.03);
}

.hotspotCardHeader {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 0.75rem;
}

.hotspotToggleRow {
	display: flex;
	flex-wrap: wrap;
	justify-content: flex-end;
	gap: 0.5rem;
}

.hotspotCardTitle {
	font-weight: 700;
	color: var(--text);
}

.hotspotToggle {
	display: inline-flex;
	align-items: center;
	gap: 0.5rem;
	padding: 0.45rem 0.75rem;
	border: 1px solid var(--border);
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.03);
	font-weight: 700;
	color: var(--text);
}

.hotspotToggle input {
	margin: 0;
}

@media (max-width: 800px) {
	.fieldGrid,
	.hotspotCardFields {
		grid-template-columns: 1fr;
	}

	.hotspotCardHeader {
		flex-direction: column;
		align-items: flex-start;
	}

	.hotspotToggleRow {
		justify-content: flex-start;
	}
}
</style>