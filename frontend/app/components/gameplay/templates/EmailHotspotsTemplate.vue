<script setup lang="ts">
import type {
  EmailHotspotContent,
  EmailHotspotResult,
  EmailHotspotTarget,
} from '~/composables/types/TaskTypes'

const props = defineProps<{
  prompt?: {
    title?: string
    text?: string
  }
  email: EmailHotspotContent
  targets: EmailHotspotTarget[]
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'answered', payload: EmailHotspotResult): void
}>()

const foundTargetIds = ref<string[]>([])
const incorrectTargetId = ref<string | null>(null)

const targetMap = computed(() => new Map(props.targets.map((target) => [target.id, target])))
const suspiciousTargetCount = computed(() => props.targets.filter((target) => target.isSuspicious).length)
const requiredCorrectSelections = computed(() => Math.max(1, suspiciousTargetCount.value))

const progressText = computed(() => `Du har funnet ${foundTargetIds.value.length} av ${requiredCorrectSelections.value}`)
const needsMultipleSelections = computed(() => requiredCorrectSelections.value > 1)

const isFound = (targetId?: string) => {
  if (!targetId) {
    return false
  }

  return foundTargetIds.value.includes(targetId)
}

const isIncorrect = (targetId?: string) => incorrectTargetId.value === targetId

const activateTarget = (targetId?: string) => {
  if (props.disabled || !targetId || isFound(targetId)) {
    return
  }

  const target = targetMap.value.get(targetId)

  if (!target) {
    return
  }

  if (!target.isSuspicious) {
    incorrectTargetId.value = targetId
    emit('answered', {
      correct: false,
      selectedTargetIds: [...foundTargetIds.value, targetId],
      incorrectTargetId: targetId,
    })
    return
  }

  const nextFoundTargetIds = [...foundTargetIds.value, targetId]
  foundTargetIds.value = nextFoundTargetIds

  if (nextFoundTargetIds.length >= requiredCorrectSelections.value) {
    emit('answered', {
      correct: true,
      selectedTargetIds: nextFoundTargetIds,
    })
  }
}
</script>

<template>
  <section class="emailHotspotsTemplate">
    <header
      v-if="props.prompt?.title || props.prompt?.text"
      class="hotspotsTemplateHeader"
    >
      <div class="hotspotsTemplateHeading">
        <h2 v-if="props.prompt?.title" class="hotspotsTemplateTitle">
          {{ props.prompt.title }}
        </h2>
        <p v-if="props.prompt?.text" class="hotspotsTemplateText">
          {{ props.prompt.text }}
        </p>
      </div>
      <p v-if="needsMultipleSelections" class="templateProgressPill">
        {{ progressText }}
      </p>
    </header>

    <article class="hotspotsTemplateMail">
      <div v-if="props.email.imageUrl" class="templateMailImageFrame">
        <img
          :src="props.email.imageUrl"
          :alt="props.email.imageAlt ?? ''"
          class="templateMailImage"
        >
      </div>

      <header class="templateMailHeader">
        <div class="hotspotsTemplateSender">
          <div class="hotspotsTemplateAvatar" aria-hidden="true">
            {{ props.email.senderAvatarText ?? props.email.senderName.slice(0, 2).toUpperCase() }}
          </div>
          <div class="templateSenderCopy">
            <button
              type="button"
              class="hotspotsTemplateTarget templateTargetPlain targetSenderName"
              :class="{
                'templateTargetFound': isFound(props.email.senderNameTargetId),
                'templateTargetIncorrect': isIncorrect(props.email.senderNameTargetId),
              }"
              :disabled="props.disabled || !props.email.senderNameTargetId"
              @click="activateTarget(props.email.senderNameTargetId)"
            >
              {{ props.email.senderName }}
            </button>
            <button
              type="button"
              class="hotspotsTemplateTarget templateTargetPlain templateTargetInline"
              :class="{
                'templateTargetFound': isFound(props.email.senderAddressTargetId),
                'templateTargetIncorrect': isIncorrect(props.email.senderAddressTargetId),
              }"
              :disabled="props.disabled || !props.email.senderAddressTargetId"
              @click="activateTarget(props.email.senderAddressTargetId)"
            >
              {{ props.email.senderAddress }}
            </button>
          </div>
        </div>
        <button
          v-if="props.email.receivedLabel"
          type="button"
          class="hotspotsTemplateTarget templateTargetPlain targetReceivedLabel"
          :class="{
              'templateTargetFound': isFound(props.email.receivedLabelTargetId),
              'templateTargetIncorrect': isIncorrect(props.email.receivedLabelTargetId),
            }"
          :disabled="props.disabled || !props.email.receivedLabelTargetId"
          @click="activateTarget(props.email.receivedLabelTargetId)"
        >
          {{ props.email.receivedLabel }}
        </button>
      </header>

      <div class="templateSubjectRow">
        <button
          type="button"
          class="hotspotsTemplateTarget templateTargetPlain templateTargetSubject"
          :class="{
            'templateTargetFound': isFound(props.email.subjectTargetId),
            'templateTargetIncorrect': isIncorrect(props.email.subjectTargetId),
          }"
          :disabled="props.disabled || !props.email.subjectTargetId"
          @click="activateTarget(props.email.subjectTargetId)"
        >
          {{ props.email.subject }}
        </button>
      </div>

      <div class="hotspotsTemplateBody">
        <button
          v-if="props.email.greeting"
          type="button"
          class="hotspotsTemplateTarget templateTargetPlain templateTargetParagraph"
          :class="{
            'templateTargetFound': isFound(props.email.greetingTargetId),
            'templateTargetIncorrect': isIncorrect(props.email.greetingTargetId),
          }"
          :disabled="props.disabled || !props.email.greetingTargetId"
          @click="activateTarget(props.email.greetingTargetId)"
        >
          {{ props.email.greeting }}
        </button>

        <template v-for="block in props.email.bodyBlocks" :key="block.id">
          <button
            v-if="block.type === 'text'"
            type="button"
            class="hotspotsTemplateTarget templateTargetPlain templateTargetParagraph"
            :class="{
              'templateTargetFound': isFound(block.targetId),
              'templateTargetIncorrect': isIncorrect(block.targetId),
            }"
            :disabled="props.disabled || !block.targetId"
            @click="activateTarget(block.targetId)"
          >
            {{ block.text }}
          </button>

          <button
            v-else-if="block.type === 'link'"
            type="button"
            class="hotspotsTemplateTarget templateTargetLink"
            :class="{
              'templateTargetFound': isFound(block.targetId),
              'templateTargetIncorrect': isIncorrect(block.targetId),
            }"
            :disabled="props.disabled || !block.targetId"
            @click="activateTarget(block.targetId)"
          >
            <span class="templateLinkLabel">{{ block.label }}</span>
            <span class="templateLinkUrl">{{ block.urlText }}</span>
            <span v-if="block.helperText" class="templateLinkHelper">{{ block.helperText }}</span>
          </button>
        </template>

        <button
          v-if="props.email.actionButton"
          type="button"
          class="hotspotsTemplateTarget templateTargetCta"
          :class="{
            templateTargetFound: isFound(props.email.actionButton.targetId),
            templateTargetIncorrect: isIncorrect(props.email.actionButton.targetId),
          }"
          :disabled="props.disabled || !props.email.actionButton.targetId"
          @click="activateTarget(props.email.actionButton.targetId)"
        >
          <span>{{ props.email.actionButton.label }}</span>
          <span v-if="props.email.actionButton.helperText" class="templateCtaHelper">
            {{ props.email.actionButton.helperText }}
          </span>
        </button>

        <div
          v-if="props.email.attachments?.length"
          class="templateAttachments"
        >
          <button
            v-for="attachment in props.email.attachments"
            :key="attachment.id"
            type="button"
            class="templateAttachment"
            :class="{
              templateAttachmentInteractive: Boolean(attachment.targetId),
              templateTargetFound: isFound(attachment.targetId),
              templateTargetIncorrect: isIncorrect(attachment.targetId),
            }"
            :disabled="props.disabled || !attachment.targetId"
            @click="activateTarget(attachment.targetId)"
          >
            <span class="templateAttachmentName">{{ attachment.name }}</span>
            <span v-if="attachment.details" class="templateAttachmentDetails">
              {{ attachment.details }}
            </span>
          </button>
        </div>

        <button
          v-if="props.email.closing"
          type="button"
          class="hotspotsTemplateTarget templateTargetPlain templateTargetParagraph"
          :class="{
            templateTargetFound: isFound(props.email.closingTargetId),
            templateTargetIncorrect: isIncorrect(props.email.closingTargetId),
          }"
          :disabled="props.disabled || !props.email.closingTargetId"
          @click="activateTarget(props.email.closingTargetId)"
        >
          {{ props.email.closing }}
        </button>
      </div>
    </article>
  </section>
</template>

<style scoped>
.emailHotspotsTemplate {
  /* header */
  --hot-title: var(--palette-cork-900);
  --hot-muted: var(--palette-cork-580);

  /* progress pill */
  --hot-pill-bg: var(--palette-cream-200);
  --hot-pill-text: var(--palette-navy-700);

  /* email card */
  --hot-mail-bg: var(--palette-cream-100);
  --hot-mail-border: var(--palette-overlay-cork-medium);
  --hot-mail-shadow: var(--palette-overlay-dark-soft);

  /* email header bar */
  --hot-header-bg: var(--palette-cream-200);
  --hot-header-border: var(--palette-overlay-cork-medium);

  /* avatar */
  --hot-avatar-bg: var(--palette-navy-200);
  --hot-avatar-text: var(--palette-navy-900);

  /* sender / meta text */
  --hot-sender-name: var(--palette-cork-900);
  --hot-sender-address: var(--palette-cork-700);
  --hot-received-label: var(--palette-cork-700);

  /* subject */
  --hot-subject: var(--palette-cork-900);

  /* body text */
  --hot-body-text: var(--palette-cork-700);

  /* target buttons - idle */
  --hot-target-bg: var(--palette-white);
  --hot-target-border: var(--palette-navy-200);
  --hot-target-shadow: var(--palette-overlay-dark-soft);

  /* target buttons - hover */
  --hot-target-border-hover: var(--palette-navy-700);
  --hot-target-shadow-hover: var(--palette-overlay-dark-medium);

  /* link card */
  --hot-link-bg: var(--palette-cream-200);
  --hot-link-border: var(--palette-cream-300);
  --hot-link-label: var(--palette-navy-900);
  --hot-link-url: var(--palette-cork-580);

  /* CTA button */
  --hot-cta-bg: var(--palette-navy-700);
  --hot-cta-bg-hover: var(--palette-navy-600);
  --hot-cta-text: var(--palette-white);
  --hot-cta-shadow-hover: var(--palette-overlay-dark-medium);

  /* attachment */
  --hot-attach-bg: var(--palette-gray-50);
  --hot-attach-border: var(--palette-gray-200);
  --hot-attach-name: var(--palette-navy-900);
  --hot-attach-details: var(--palette-cork-580);
  --hot-attach-interactive-border: var(--palette-navy-200);

  /* found / correct state */
  --hot-found-bg: var(--palette-green-100);
  --hot-found-border: var(--palette-green-700);

  /* incorrect state */
  --hot-incorrect-bg: var(--palette-red-100);
  --hot-incorrect-border: var(--palette-red-700);

  /* plain target found/incorrect (inset ring style) */
  --hot-plain-found-bg: var(--palette-cream-200);
  --hot-plain-found-ring: var(--palette-green-700);
  --hot-plain-incorrect-bg: var(--palette-red-100);

  display: grid;
  gap: var(--space-4);
  color: var(--hot-title);
}

.hotspotsTemplateHeader {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: var(--space-4);
}

.hotspotsTemplateHeading {
  display: grid;
  gap: var(--space-1);
}

.hotspotsTemplateTitle {
  /* fluid title scale, deliberate exception */
  font-size: clamp(1.15rem, 1.8vw, 1.45rem);
  line-height: 1.15;
  color: var(--hot-title);
}

.hotspotsTemplateText {
  max-width: 46ch;
  font-size: var(--font-size-14);
  line-height: var(--font-leading-14);
  color: var(--hot-muted);
}

.templateProgressPill {
  flex-shrink: 0;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-pill);
  background: var(--hot-pill-bg);
  color: var(--hot-pill-text);
  font-size: var(--font-size-14);
  font-weight: var(--font-weight-bold);
}

.hotspotsTemplateMail {
  border-radius: var(--radius-3);
  background: var(--hot-mail-bg);
  border: var(--border-width-thin) solid var(--hot-mail-border);
  box-shadow: 0 var(--space-4) var(--space-6) var(--hot-mail-shadow);
  overflow: hidden;
}

.templateMailHeader {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: var(--space-4);
  padding: var(--space-4) var(--space-3);
  background: var(--hot-header-bg);
  border-bottom: var(--border-width-hairline) solid var(--hot-header-border);
}

.templateMailImageFrame {
  padding: 0.85rem 0.85rem 0;
  background: var(--color-bg-surface);
}

.templateMailImage {
  width: 100%;
  max-height: min(42dvh, 22rem);
  object-fit: contain;
  background: #fff;
  display: block;
  border-radius: 1rem;
  border: var(--border-width-thin) solid var(--color-border-default);
}

.hotspotsTemplateSender {
  display: flex;
  gap: var(--space-2);
  min-width: 0;
}

.hotspotsTemplateAvatar {
  display: grid;
  place-items: center;
  width: var(--size-control-md);
  height: var(--size-control-md);
  border-radius: var(--radius-pill);
  background: var(--hot-avatar-bg);
  color: var(--hot-avatar-text);
  font-weight: var(--font-weight-bold);
}

.templateSenderCopy {
  display: grid;
  gap: var(--space-1);
  min-width: 0;
}

.templateSenderName {
  font-weight: var(--font-weight-bold);
  color: var(--hot-sender-name);
}

.templateSenderAddress,
.templateReceivedLabel {
  font-size: var(--font-size-14);
  color: var(--hot-sender-address);
}

.templateSubjectRow {
  padding: var(--space-4) var(--space-3) 0;
}

.hotspotsTemplateSubject {
  /* fluid subject scale, deliberate exception */
  font-size: clamp(1.2rem, 1.9vw, 1.6rem);
  line-height: 1.15;
  color: var(--hot-subject);
}

.hotspotsTemplateBody {
  display: grid;
  gap: var(--space-2);
  padding: var(--space-4) var(--space-3) var(--space-3);
}

.hotspotsTemplateGreeting,
.hotspotsTemplateParagraph,
.hotspotsTemplateClosing {
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
  color: var(--hot-body-text);
}

.templateLinkCard,
.hotspotsTemplateTarget {
  display: grid;
  gap: var(--space-1);
  width: 100%;
  text-align: left;
  border-radius: var(--radius-2);
}

.hotspotsTemplateTarget {
  padding: var(--space-2) var(--space-3);
  border: var(--border-width-thin) dashed var(--hot-target-border);
  background: var(--hot-target-bg);
  transition: transform var(--transition-base), border-color var(--transition-base), background-color var(--transition-base), box-shadow var(--transition-base);
}

.templateTargetPlain {
  padding: 0;
  border: 0;
  background: transparent;
  box-shadow: none;
}

.hotspotsTemplateTarget:hover:not(:disabled),
.hotspotsTemplateTarget:focus-visible,
.templateAttachmentInteractive:hover:not(:disabled),
.templateAttachmentInteractive:focus-visible {
  transform: translateY(calc(var(--border-width-hairline) * -1));
  border-color: var(--hot-target-border-hover);
  box-shadow: 0 var(--space-3) var(--space-5) var(--hot-target-shadow-hover);
  outline: none;
}

.templateTargetInline {
  justify-self: start;
  width: auto;
  color: var(--hot-sender-address);
  font-size: var(--font-size-14);
  text-decoration: underline;
  text-decoration-style: dashed;
  text-underline-offset: var(--space-1);
  box-shadow: none;
}

.templateTargetSubject {
  /* fluid subject scale, deliberate exception */
  font-size: clamp(1.2rem, 1.9vw, 1.6rem);
  line-height: 1.15;
  color: var(--hot-subject);
  box-shadow: none;
}

.targetSenderName {
  justify-self: start;
  width: auto;
  font-weight: var(--font-weight-bold);
  color: var(--hot-sender-name);
}

.targetReceivedLabel {
  justify-self: start;
  width: auto;
  font-size: var(--font-size-14);
  color: var(--hot-received-label);
}

.templateTargetParagraph {
  font-size: var(--font-size-16);
  line-height: var(--font-leading-16);
  color: var(--hot-body-text);
}

.templateTargetPlain:hover:not(:disabled),
.templateTargetPlain:focus-visible {
  transform: none;
  box-shadow: none;
  outline: none;
}

.templateLinkCard {
  padding: var(--space-2) var(--space-3);
  background: var(--hot-link-bg);
  border: var(--border-width-hairline) solid var(--hot-link-border);
}

.templateTargetLink {
  border-style: solid;
}

.templateLinkLabel {
  font-weight: var(--font-weight-bold);
  color: var(--hot-link-label);
}

.templateAttachmentName {
  font-weight: var(--font-weight-bold);
  color: var(--hot-attach-name);
}

.templateLinkUrl,
.templateLinkHelper {
  font-size: var(--font-size-14);
  color: var(--hot-link-url);
}

.templateCtaHelper {
  font-size: var(--font-size-14);
  color: var(--hot-cta-text);
}

.templateAttachmentDetails {
  font-size: var(--font-size-14);
  color: var(--hot-attach-details);
}

.templateTargetCta,
.templateCtaStatic {
  justify-self: start;
  padding: var(--space-3) var(--space-3);
  border-radius: var(--radius-pill);
  border: 0;
  background: var(--hot-cta-bg);
  color: var(--hot-cta-text);
  font-weight: var(--font-weight-bold);
}

.templateTargetCta {
  transition: transform var(--transition-base), box-shadow var(--transition-base), background-color var(--transition-base);
}

.templateTargetCta:hover:not(:disabled),
.templateTargetCta:focus-visible {
  background: var(--hot-cta-bg-hover);
  box-shadow: 0 var(--space-3) var(--space-5) var(--hot-cta-shadow-hover);
  outline: none;
}

.templateAttachments {
  display: grid;
  gap: var(--space-2);
}

.templateAttachment {
  display: grid;
  gap: var(--space-1);
  justify-items: start;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-2);
  border: var(--border-width-hairline) solid var(--hot-attach-border);
  background: var(--hot-attach-bg);
  text-align: left;
}

.templateAttachmentInteractive {
  border: var(--border-width-thin) dashed var(--hot-attach-interactive-border);
  transition: transform var(--transition-base), border-color var(--transition-base), background-color var(--transition-base), box-shadow var(--transition-base);
}

.templateTargetFound {
  border-color: var(--hot-found-border);
  background: var(--hot-found-bg);
  transition: background var(--transition-fast), border-color var(--transition-fast);
}

.templateTargetIncorrect {
  border-color: var(--hot-incorrect-border);
  background: var(--hot-incorrect-bg);
  transition: background var(--transition-fast), border-color var(--transition-fast);
}

.templateTargetPlain.templateTargetFound,
.templateTargetPlain.templateAttachmentInteractive.templateTargetFound {
  border-radius: var(--radius-1);
  background: var(--hot-plain-found-bg);
  box-shadow: inset 0 0 0 var(--border-width-thin) var(--hot-plain-found-ring);
}

.templateTargetPlain.templateTargetIncorrect,
.templateTargetPlain.templateAttachmentInteractive.templateTargetIncorrect {
  border-radius: var(--radius-1);
  background: var(--hot-plain-incorrect-bg);
}

</style>
