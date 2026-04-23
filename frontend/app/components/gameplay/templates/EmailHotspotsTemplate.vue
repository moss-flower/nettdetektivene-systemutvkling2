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
  <section class="email-hotspots-template">
    <header
      v-if="props.prompt?.title || props.prompt?.text"
      class="email-hotspots-template__header"
    >
      <div class="email-hotspots-template__heading">
        <h2 v-if="props.prompt?.title" class="email-hotspots-template__title">
          {{ props.prompt.title }}
        </h2>
        <p v-if="props.prompt?.text" class="email-hotspots-template__text">
          {{ props.prompt.text }}
        </p>
      </div>
      <p v-if="needsMultipleSelections" class="email-hotspots-template__progress-pill">
        {{ progressText }}
      </p>
    </header>

    <article class="email-hotspots-template__mail">
      <header class="email-hotspots-template__mail-header">
        <div class="email-hotspots-template__sender">
          <div class="email-hotspots-template__avatar" aria-hidden="true">
            {{ props.email.senderAvatarText ?? props.email.senderName.slice(0, 2).toUpperCase() }}
          </div>
          <div class="email-hotspots-template__sender-copy">
            <button
              type="button"
              class="email-hotspots-template__target email-hotspots-template__target--plain email-hotspots-template__target--sender-name"
              :class="{
                'email-hotspots-template__target--found': isFound(props.email.senderNameTargetId),
                'email-hotspots-template__target--incorrect': isIncorrect(props.email.senderNameTargetId),
              }"
              :disabled="props.disabled || !props.email.senderNameTargetId"
              @click="activateTarget(props.email.senderNameTargetId)"
            >
              {{ props.email.senderName }}
            </button>
            <button
              type="button"
              class="email-hotspots-template__target email-hotspots-template__target--plain email-hotspots-template__target--inline"
              :class="{
                'email-hotspots-template__target--found': isFound(props.email.senderAddressTargetId),
                'email-hotspots-template__target--incorrect': isIncorrect(props.email.senderAddressTargetId),
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
          class="email-hotspots-template__target email-hotspots-template__target--plain email-hotspots-template__target--received-label"
          :class="{
            'email-hotspots-template__target--found': isFound(props.email.receivedLabelTargetId),
            'email-hotspots-template__target--incorrect': isIncorrect(props.email.receivedLabelTargetId),
          }"
          :disabled="props.disabled || !props.email.receivedLabelTargetId"
          @click="activateTarget(props.email.receivedLabelTargetId)"
        >
          {{ props.email.receivedLabel }}
        </button>
      </header>

      <div class="email-hotspots-template__subject-row">
        <button
          type="button"
          class="email-hotspots-template__target email-hotspots-template__target--plain email-hotspots-template__target--subject"
          :class="{
            'email-hotspots-template__target--found': isFound(props.email.subjectTargetId),
            'email-hotspots-template__target--incorrect': isIncorrect(props.email.subjectTargetId),
          }"
          :disabled="props.disabled || !props.email.subjectTargetId"
          @click="activateTarget(props.email.subjectTargetId)"
        >
          {{ props.email.subject }}
        </button>
      </div>

      <div class="email-hotspots-template__body">
        <button
          v-if="props.email.greeting"
          type="button"
          class="email-hotspots-template__target email-hotspots-template__target--plain email-hotspots-template__target--paragraph"
          :class="{
            'email-hotspots-template__target--found': isFound(props.email.greetingTargetId),
            'email-hotspots-template__target--incorrect': isIncorrect(props.email.greetingTargetId),
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
            class="email-hotspots-template__target email-hotspots-template__target--plain email-hotspots-template__target--paragraph"
            :class="{
              'email-hotspots-template__target--found': isFound(block.targetId),
              'email-hotspots-template__target--incorrect': isIncorrect(block.targetId),
            }"
            :disabled="props.disabled || !block.targetId"
            @click="activateTarget(block.targetId)"
          >
            {{ block.text }}
          </button>

          <button
            v-else-if="block.type === 'link'"
            type="button"
            class="email-hotspots-template__target email-hotspots-template__target--link"
            :class="{
              'email-hotspots-template__target--found': isFound(block.targetId),
              'email-hotspots-template__target--incorrect': isIncorrect(block.targetId),
            }"
            :disabled="props.disabled || !block.targetId"
            @click="activateTarget(block.targetId)"
          >
            <span class="email-hotspots-template__link-label">{{ block.label }}</span>
            <span class="email-hotspots-template__link-url">{{ block.urlText }}</span>
            <span v-if="block.helperText" class="email-hotspots-template__link-helper">{{ block.helperText }}</span>
          </button>
        </template>

        <button
          v-if="props.email.actionButton"
          type="button"
          class="email-hotspots-template__target email-hotspots-template__target--cta"
          :class="{
            'email-hotspots-template__target--found': isFound(props.email.actionButton.targetId),
            'email-hotspots-template__target--incorrect': isIncorrect(props.email.actionButton.targetId),
          }"
          :disabled="props.disabled || !props.email.actionButton.targetId"
          @click="activateTarget(props.email.actionButton.targetId)"
        >
          <span>{{ props.email.actionButton.label }}</span>
          <span v-if="props.email.actionButton.helperText" class="email-hotspots-template__cta-helper">
            {{ props.email.actionButton.helperText }}
          </span>
        </button>

        <div
          v-if="props.email.attachments?.length"
          class="email-hotspots-template__attachments"
        >
          <button
            v-for="attachment in props.email.attachments"
            :key="attachment.id"
            type="button"
            class="email-hotspots-template__attachment"
            :class="{
              'email-hotspots-template__attachment--interactive': Boolean(attachment.targetId),
              'email-hotspots-template__target--found': isFound(attachment.targetId),
              'email-hotspots-template__target--incorrect': isIncorrect(attachment.targetId),
            }"
            :disabled="props.disabled || !attachment.targetId"
            @click="activateTarget(attachment.targetId)"
          >
            <span class="email-hotspots-template__attachment-name">{{ attachment.name }}</span>
            <span v-if="attachment.details" class="email-hotspots-template__attachment-details">
              {{ attachment.details }}
            </span>
          </button>
        </div>

        <button
          v-if="props.email.closing"
          type="button"
          class="email-hotspots-template__target email-hotspots-template__target--plain email-hotspots-template__target--paragraph"
          :class="{
            'email-hotspots-template__target--found': isFound(props.email.closingTargetId),
            'email-hotspots-template__target--incorrect': isIncorrect(props.email.closingTargetId),
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
.email-hotspots-template {
  display: grid;
  gap: 1rem;
  color: #223044;
}

.email-hotspots-template__header {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: 1rem;
}

.email-hotspots-template__heading {
  display: grid;
  gap: 0.35rem;
}

.email-hotspots-template__title {
  font-size: clamp(1.15rem, 1.8vw, 1.45rem);
  line-height: 1.15;
  color: #172033;
}

.email-hotspots-template__text {
  max-width: 46rem;
  font-size: 0.95rem;
  line-height: 1.45;
  color: #4b5563;
}

.email-hotspots-template__progress-pill {
  flex-shrink: 0;
  padding: 0.45rem 0.8rem;
  border-radius: 999px;
  background: #e8f1fb;
  color: #24456e;
  font-size: 0.88rem;
  font-weight: 700;
}

.email-hotspots-template__mail {
  border-radius: 1.6rem;
  background: #fffdfa;
  border: 2px solid #d6dce6;
  box-shadow: 0 16px 34px rgba(50, 72, 103, 0.1);
  overflow: hidden;
}

.email-hotspots-template__mail-header {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: 1rem;
  padding: 1rem 1.15rem;
  background: #f5f9ff;
  border-bottom: 1px solid #d9e2ef;
}

.email-hotspots-template__sender {
  display: flex;
  gap: 0.85rem;
  min-width: 0;
}

.email-hotspots-template__avatar {
  display: grid;
  place-items: center;
  width: 2.8rem;
  height: 2.8rem;
  border-radius: 999px;
  background: #dce9f8;
  color: #234369;
  font-weight: 800;
}

.email-hotspots-template__sender-copy {
  display: grid;
  gap: 0.2rem;
  min-width: 0;
}

.email-hotspots-template__sender-name {
  font-weight: 700;
  color: #172033;
}

.email-hotspots-template__sender-address,
.email-hotspots-template__received-label {
  font-size: 0.9rem;
  color: #617083;
}

.email-hotspots-template__subject-row {
  padding: 1rem 1.15rem 0;
}

.email-hotspots-template__subject {
  font-size: clamp(1.2rem, 1.9vw, 1.6rem);
  line-height: 1.15;
  color: #172033;
}

.email-hotspots-template__body {
  display: grid;
  gap: 0.8rem;
  padding: 1rem 1.15rem 1.2rem;
}

.email-hotspots-template__greeting,
.email-hotspots-template__paragraph,
.email-hotspots-template__closing {
  font-size: 1rem;
  line-height: 1.55;
  color: #334155;
}

.email-hotspots-template__link-card,
.email-hotspots-template__target {
  display: grid;
  gap: 0.2rem;
  width: 100%;
  text-align: left;
  border-radius: 1rem;
}

.email-hotspots-template__target {
  padding: 0.8rem 0.9rem;
  border: 2px dashed #b9cae1;
  background: #f8fbff;
  transition: transform 0.2s ease, border-color 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;
}

.email-hotspots-template__target--plain {
  padding: 0;
  border: 0;
  background: transparent;
  box-shadow: none;
}

.email-hotspots-template__target:hover:not(:disabled),
.email-hotspots-template__target:focus-visible,
.email-hotspots-template__attachment--interactive:hover:not(:disabled),
.email-hotspots-template__attachment--interactive:focus-visible {
  transform: translateY(-1px);
  border-color: #7ea1ca;
  box-shadow: 0 12px 24px rgba(71, 107, 153, 0.12);
  outline: none;
}

.email-hotspots-template__target--inline {
  justify-self: start;
  width: auto;
  color: #4f6683;
  font-size: 0.9rem;
  text-decoration: underline;
  text-decoration-style: dashed;
  text-underline-offset: 0.16em;
  box-shadow: none;
}

.email-hotspots-template__target--subject {
  font-size: clamp(1.2rem, 1.9vw, 1.6rem);
  line-height: 1.15;
  color: #172033;
  box-shadow: none;
}

.email-hotspots-template__target--sender-name {
  justify-self: start;
  width: auto;
  font-weight: 700;
  color: #172033;
}

.email-hotspots-template__target--received-label {
  justify-self: start;
  width: auto;
  font-size: 0.9rem;
  color: #617083;
}

.email-hotspots-template__target--paragraph {
  font-size: 1rem;
  line-height: 1.55;
  color: #334155;
}

.email-hotspots-template__target--plain:hover:not(:disabled),
.email-hotspots-template__target--plain:focus-visible {
  transform: none;
  box-shadow: none;
  outline: none;
}

.email-hotspots-template__link-card {
  padding: 0.85rem 0.95rem;
  background: #eef5ff;
  border: 1px solid #d3e1f4;
}

.email-hotspots-template__target--link {
  border-style: solid;
}

.email-hotspots-template__link-label,
.email-hotspots-template__attachment-name {
  font-weight: 700;
  color: #173556;
}

.email-hotspots-template__link-url,
.email-hotspots-template__link-helper,
.email-hotspots-template__attachment-details,
.email-hotspots-template__cta-helper {
  font-size: 0.88rem;
  color: #5f7289;
}

.email-hotspots-template__target--cta,
.email-hotspots-template__cta-static {
  justify-self: start;
  padding: 0.9rem 1.1rem;
  border-radius: 999px;
  border: 0;
  background: #2d5a8f;
  color: #f8fbff;
  font-weight: 700;
}

.email-hotspots-template__target--cta {
  transition: transform 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
}

.email-hotspots-template__target--cta:hover:not(:disabled),
.email-hotspots-template__target--cta:focus-visible {
  background: #3468a7;
  box-shadow: 0 12px 24px rgba(52, 104, 167, 0.22);
  outline: none;
}

.email-hotspots-template__attachments {
  display: grid;
  gap: 0.7rem;
}

.email-hotspots-template__attachment {
  display: grid;
  gap: 0.15rem;
  justify-items: start;
  padding: 0.8rem 0.9rem;
  border-radius: 0.95rem;
  border: 1px solid #d9e1ec;
  background: #fbfcfe;
  text-align: left;
}

.email-hotspots-template__attachment--interactive {
  border: 2px dashed #b9cae1;
  transition: transform 0.2s ease, border-color 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;
}

.email-hotspots-template__target--found {
  border-color: #4a9b5a;
  background: #eefaf0;
}

.email-hotspots-template__target--incorrect {
  border-color: #d66363;
  background: #fff0f0;
}

.email-hotspots-template__target--plain.email-hotspots-template__target--found,
.email-hotspots-template__target--plain.email-hotspots-template__attachment--interactive.email-hotspots-template__target--found {
  border-radius: 0.45rem;
  background: #eef5ff;
}

.email-hotspots-template__target--plain.email-hotspots-template__target--incorrect,
.email-hotspots-template__target--plain.email-hotspots-template__attachment--interactive.email-hotspots-template__target--incorrect {
  border-radius: 0.45rem;
  background: #fff0f0;
}

@media (max-width: 760px) {
  .email-hotspots-template__header,
  .email-hotspots-template__mail-header {
    flex-direction: column;
    align-items: stretch;
  }

  .email-hotspots-template__target--cta,
  .email-hotspots-template__cta-static {
    width: 100%;
    justify-self: stretch;
  }
}
</style>
