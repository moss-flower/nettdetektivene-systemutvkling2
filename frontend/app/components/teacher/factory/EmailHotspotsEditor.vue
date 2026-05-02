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

			<label class="field fieldFull">
				<span>Prompt-tekst</span>
				<textarea v-model="props.task.prompt!.text" rows="2"></textarea>
			</label>

			<div class="hotspotCard">
				<div class="hotspotCardHeader">
					<span class="hotspotCardTitle">Avsendernavn-hotspot</span>
					<div class="hotspotToggleRow">
						<label class="hotspotToggle">
							<input
								:checked="isEmailHotspotSuspicious(emailHotspotTargetIds.senderName)"
								type="checkbox"
								@change="setEmailHotspotSuspicious(emailHotspotTargetIds.senderName, ($event.target as HTMLInputElement).checked)"
							>
							<span>Mistenkelig</span>
						</label>
					</div>
				</div>

				<label class="field">
					<span>Avsendernavn</span>
					<input v-model="props.task.email.senderName" type="text" >
				</label>
			</div>

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

			<div class="hotspotCard">
				<div class="hotspotCardHeader">
					<span class="hotspotCardTitle">Tidspunkt-hotspot</span>
					<div class="hotspotToggleRow">
						<label class="hotspotToggle">
							<input
								:checked="isEmailHotspotSuspicious(emailHotspotTargetIds.receivedLabel)"
								type="checkbox"
								@change="setEmailHotspotSuspicious(emailHotspotTargetIds.receivedLabel, ($event.target as HTMLInputElement).checked)"
							>
							<span>Mistenkelig</span>
						</label>
					</div>
				</div>

				<label class="field">
					<span>Tidspunkt</span>
					<input v-model="props.task.email.receivedLabel" type="text" >
				</label>
			</div>

			<div class="hotspotCard hotspotCardFull">
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

				<label class="field fieldFull">
					<span>Emne</span>
					<input v-model="props.task.email.subject" type="text" >
				</label>
			</div>

			<div class="hotspotCard hotspotCardFull">
				<div class="hotspotCardHeader">
					<span class="hotspotCardTitle">Hilsen-hotspot</span>
					<div class="hotspotToggleRow">
						<label class="hotspotToggle">
							<input
								:checked="isEmailHotspotSuspicious(emailHotspotTargetIds.greeting)"
								type="checkbox"
								@change="setEmailHotspotSuspicious(emailHotspotTargetIds.greeting, ($event.target as HTMLInputElement).checked)"
							>
							<span>Mistenkelig</span>
						</label>
					</div>
				</div>

				<label class="field fieldFull">
					<span>Hilsen</span>
					<input v-model="props.task.email.greeting" type="text" >
				</label>
			</div>

			<div class="hotspotCard hotspotCardFull">
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

				<label class="field fieldFull">
					<span>Brødtekst</span>
					<textarea v-model="emailTextBlock().text" rows="3"></textarea>
				</label>
			</div>

			<div class="hotspotCard hotspotCardFull">
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

			<div class="hotspotCard hotspotCardFull">
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

			<div class="hotspotCard hotspotCardFull">
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

			<div class="hotspotCard hotspotCardFull">
				<div class="hotspotCardHeader">
					<span class="hotspotCardTitle">Avslutning-hotspot</span>
					<div class="hotspotToggleRow">
						<label class="hotspotToggle">
							<input
								:checked="isEmailHotspotSuspicious(emailHotspotTargetIds.closing)"
								type="checkbox"
								@change="setEmailHotspotSuspicious(emailHotspotTargetIds.closing, ($event.target as HTMLInputElement).checked)"
							>
							<span>Mistenkelig</span>
						</label>
					</div>
				</div>

				<label class="field fieldFull">
					<span>Avslutning</span>
					<input v-model="props.task.email.closing" type="text" >
				</label>
			</div>
		</div>
	</div>
</template>

<style scoped>
.templateSection {
   margin-top: var(--space-4);
   padding-top: var(--space-4);
   border-top: var(--border-width-hairline) solid var(--color-border-default);
}

.templateSection h4 {
   margin: 0 0 var(--space-3);
}

.fieldGrid,
.hotspotCardFields {
   display: grid;
   grid-template-columns: repeat(2, minmax(0, 1fr));
   gap: var(--space-3);
}

.field {
   display: grid;
   gap: var(--space-1);
}

.fieldFull,
.hotspotCardFull {
   grid-column: 1 / -1;
}

.field span {
   color: var(--factory-label-color);
   font-family: var(--factory-label-font), monospace;
   font-size: var(--factory-label-size);
   font-weight: var(--font-weight-bold);
   letter-spacing: var(--factory-label-tracking);
   text-transform: uppercase;
}

.field input,
.field select,
.field textarea {
   width: 100%;
   padding: var(--space-3) var(--space-4);
   border: var(--border-width-hairline) solid var(--factory-field-border);
   border-radius: var(--radius-1);
   background: var(--factory-field-bg);
   color: var(--color-text-primary);
   font: inherit;
}

.field textarea {
   resize: vertical;
}

.field input:focus,
.field select:focus,
.field textarea:focus {
   outline: none;
   border-color: var(--factory-field-focus);
}

.hotspotCard {
   display: grid;
   gap: var(--space-3);
   padding: var(--space-3);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-2);
   background: var(--color-bg-canvas);
}

.hotspotCardHeader {
   display: flex;
   align-items: center;
   justify-content: space-between;
   gap: var(--space-3);
}

.hotspotToggleRow {
   display: flex;
   flex-wrap: wrap;
   justify-content: flex-end;
   gap: var(--space-2);
}

.hotspotCardTitle {
   color: var(--factory-label-color);
   font-family: var(--factory-label-font), monospace;
   font-size: var(--factory-label-size);
   font-weight: var(--font-weight-bold);
   letter-spacing: var(--factory-label-tracking);
   text-transform: uppercase;
}

.hotspotToggle {
   display: inline-flex;
   align-items: center;
   gap: var(--space-2);
   padding: var(--space-1) var(--space-3);
   border: var(--border-width-hairline) solid var(--color-border-default);
   border-radius: var(--radius-pill);
   background: var(--color-bg-surface-hover);
   color: var(--factory-label-color);
   font-family: var(--factory-label-font), monospace;
   font-size: var(--factory-label-size);
   font-weight: var(--font-weight-bold);
   letter-spacing: var(--factory-label-tracking);
   text-transform: uppercase;
}

.hotspotToggle input {
   margin: 0;
}

.hotspotToggle:has(input:checked) {
  border-color: var(--color-brand-primary);
  background: var(--factory-drop-accept);
}

@media (max-width: 800px) { /* deliberate: matches factory editor mobile breakpoint */
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
