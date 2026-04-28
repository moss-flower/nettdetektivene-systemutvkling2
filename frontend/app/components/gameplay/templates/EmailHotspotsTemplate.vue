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
  display: grid;
  gap: var(--space-4);
  color: var(--color-text-primary);
}

.hotspotsTemplateHeader {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: var(--space-4);
}

.hotspotsTemplateHeading {
  display: grid;
  gap: 0.35rem;
}

.hotspotsTemplateTitle {
  font-size: clamp(1.15rem, 1.8vw, 1.45rem);
  line-height: 1.15;
  color: var(--color-text-primary);
}

.hotspotsTemplateText {
  max-width: 46rem;
  font-size: 0.95rem;
  line-height: 1.45;
  color: var(--color-text-secondary);
}

.templateProgressPill {
  flex-shrink: 0;
  padding: 0.45rem 0.8rem;
  border-radius: var(--radius-pill);
  background: var(--color-semantic-info);
  color: #24456e;
  font-size: 0.88rem;
  font-weight: 700;
}

.hotspotsTemplateMail {
  border-radius: 1.6rem;
  background: var(--color-bg-surface);
  border: var(--border-width-thin) solid var(--color-border-default);
  box-shadow: 0 16px 34px rgba(50, 72, 103, 0.1);
  overflow: hidden;
}

.templateMailHeader {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: var(--space-4);
  padding: var(--space-4) 1.15rem;
  background: var(--color-semantic-info);
  border-bottom: var(--border-width-hairline) solid var(--color-border-default);
}

.hotspotsTemplateSender {
  display: flex;
  gap: 0.85rem;
  min-width: 0;
}

.hotspotsTemplateAvatar {
  display: grid;
  place-items: center;
  width: 2.8rem;
  height: 2.8rem;
  border-radius: var(--radius-pill);
  background: #dce9f8;
  color: #234369;
  font-weight: 800;
}

.templateSenderCopy {
  display: grid;
  gap: 0.2rem;
  min-width: 0;
}

.templateSenderName {
  font-weight: 700;
  color: var(--color-text-primary);
}

.templateSenderAddress,
.templateReceivedLabel {
  font-size: 0.9rem;
  color: #617083;
}

.templateSubjectRow {
  padding: var(--space-4) 1.15rem 0;
}

.hotspotsTemplateSubject {
  font-size: clamp(1.2rem, 1.9vw, 1.6rem);
  line-height: 1.15;
  color: var(--color-text-primary);
}

.hotspotsTemplateBody {
  display: grid;
  gap: 0.8rem;
  padding: var(--space-4) 1.15rem 1.2rem;
}

.hotspotsTemplateGreeting,
.hotspotsTemplateParagraph,
.hotspotsTemplateClosing {
  font-size: var(--font-size-16);
  line-height: 1.55;
  color: #334155;
}

.templateLinkCard,
.hotspotsTemplateTarget {
  display: grid;
  gap: 0.2rem;
  width: 100%;
  text-align: left;
  border-radius: var(--radius-2);
}

.hotspotsTemplateTarget {
  padding: 0.8rem 0.9rem;
  border: var(--border-width-thin) dashed #b9cae1;
  background: var(--palette-white);
  transition: transform var(--transition-base) ease, border-color var(--transition-base) ease, background-color var(--transition-base) ease, box-shadow var(--transition-base) ease;
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
  transform: translateY(-1px);
  border-color: #7ea1ca;
  box-shadow: 0 12px 24px rgba(71, 107, 153, 0.12);
  outline: none;
}

.templateTargetInline {
  justify-self: start;
  width: auto;
  color: #4f6683;
  font-size: 0.9rem;
  text-decoration: underline;
  text-decoration-style: dashed;
  text-underline-offset: 0.16em;
  box-shadow: none;
}

.templateTargetSubject {
  font-size: clamp(1.2rem, 1.9vw, 1.6rem);
  line-height: 1.15;
  color: var(--color-text-primary);
  box-shadow: none;
}

.targetSenderName {
  justify-self: start;
  width: auto;
  font-weight: 700;
  color: var(--color-text-primary);
}

.targetReceivedLabel {
  justify-self: start;
  width: auto;
  font-size: 0.9rem;
  color: #617083;
}

.templateTargetParagraph {
  font-size: var(--font-size-16);
  line-height: 1.55;
  color: #334155;
}

.templateTargetPlain:hover:not(:disabled),
.templateTargetPlain:focus-visible {
  transform: none;
  box-shadow: none;
  outline: none;
}

.templateLinkCard {
  padding: 0.85rem 0.95rem;
  background: var(--color-semantic-info);
  border: var(--border-width-hairline) solid #d3e1f4;
}

.templateTargetLink {
  border-style: solid;
}

.templateLinkLabel,
.templateAttachmentName {
  font-weight: 700;
  color: #173556;
}

.templateLinkUrl,
.templateLinkHelper,
.templateAttachmentDetails,
.templateCtaHelper {
  font-size: 0.88rem;
  color: #5f7289;
}

.templateTargetCta,
.templateCtaStatic {
  justify-self: start;
  padding: 0.9rem 1.1rem;
  border-radius: var(--radius-pill);
  border: 0;
  background: #2d5a8f;
  color: var(--palette-white);
  font-weight: 700;
}

.templateTargetCta {
  transition: transform var(--transition-base) ease, box-shadow var(--transition-base) ease, background-color var(--transition-base) ease;
}

.templateTargetCta:hover:not(:disabled),
.templateTargetCta:focus-visible {
  background: #3468a7;
  box-shadow: 0 12px 24px rgba(52, 104, 167, 0.22);
  outline: none;
}

.templateAttachments {
  display: grid;
  gap: 0.7rem;
}

.templateAttachment {
  display: grid;
  gap: 0.15rem;
  justify-items: start;
  padding: 0.8rem 0.9rem;
  border-radius: 0.95rem;
  border: var(--border-width-hairline) solid #d9e1ec;
  background: #fbfcfe;
  text-align: left;
}

.templateAttachmentInteractive {
  border: var(--border-width-thin) dashed #b9cae1;
  transition: transform var(--transition-base) ease, border-color var(--transition-base) ease, background-color var(--transition-base) ease, box-shadow var(--transition-base) ease;
}

.templateTargetFound {
  border-color: #4a9b5a;
  background: #eefaf0;
}

.templateTargetIncorrect {
  border-color: #d66363;
  background: #fff0f0;
}

.templateTargetPlain.templateTargetFound,
.templateTargetPlain.templateAttachmentInteractive.templateTargetFound {
  border-radius: 0.45rem;
  background: var(--color-semantic-info);
  box-shadow: inset 0 0 0 2px #4a9b5a;
}

.templateTargetPlain.templateTargetIncorrect,
.templateTargetPlain.templateAttachmentInteractive.templateTargetIncorrect {
  border-radius: 0.45rem;
  background: #fff0f0;
}

</style>
