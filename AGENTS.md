# AGENTS.md — Frontend Styling (Teacher Side)

## Scope
This file governs **CSS-only refactors** of Vue 3 (Nuxt 4, `ssr: false`) teacher-side components.
Do NOT touch `<script>` or `<template>` blocks unless explicitly asked.
Do NOT refactor pupil-side components — that is a separate track.

## Repo Layout (styling-relevant paths only)
frontend/
├── app/
│   ├── assets/css/main.css          ← DESIGN SYSTEM SOURCE OF TRUTH
│   ├── components/teacher/factory/  ← factory editor components
│   ├── pages/teacher/               ← teacher route pages
│   └── layouts/default.vue          ← data-theme wiring (useHead)
├── middleware/[theme.global](http://theme.global).ts       ← sets data-theme="teacher" for /teacher/
└── nuxt.config.ts                   ← Google Fonts CDN link


## Design System
- **Token source:** `frontend/app/assets/css/main.css` — read this file before every refactor.
- Teacher tokens live under `:root[data-theme="teacher"]`.
- Shared tokens (spacing, typography, radius, shadow, layout) live in bare `:root`.
- Theme wiring: `data-theme="teacher"` is set on `<html>` via route middleware for all `/teacher/**` paths. Semantic `--color-*` tokens resolve automatically — do NOT flatten to `--palette-*`.

## CSS Rules
- **Token-only values.** Every color, spacing, radius, shadow, font-size, font-weight, border-width, and transition must use `var(--token-name)` from `main.css`. No raw hex, rgba, rem, or px.
- **Commented exceptions allowed for:** fluid sizing (`clamp()`, `vw`), fixed layout atoms (thumbnail sizes, min-widths, breakpoints), `aspect-ratio`, `font-feature-settings: 'tnum'`. Always add an inline `/* deliberate: reason */` comment.
- **Scoped component tokens:** Each component defines `--<component>-*` tokens at its top-level selector, referencing semantic or palette tokens. Inner rules reference only the scoped aliases.
- **No `--palette-*` in component rules** except:
    - Overlay tokens (`--palette-overlay-*`)
    - Scoped component token *definitions* (e.g. `--factory-tag-blue: var(--palette-ice-200)`)
    - Shell gold strip (`--palette-gold-500`) — one specific exception
- **No `--shadow-card`** on teacher surfaces (black-tinted, pupil-only). Use `--shadow-1` or `--shadow-folder`.
- **No `--radius-pill`** on action buttons. Teacher buttons use `--radius-1`.
- **No `--layout-page-padding-comfortable`** on teacher pages. Use `--layout-page-padding`.
- **No gold on white for body text** (AA fails). Gold is for accents only.
- **No `color-mix()`.**
- **No raw font-weight integers.** Use `--font-weight-regular/medium/semibold/bold`.
- **Font stack:** Nunito (display + body) + JetBrains Mono (codes, timestamps). No other fonts.

## Workflow
1. Read `main.css` from disk to confirm current token state.
2. Read the target `.vue` file.
3. Rewrite ONLY the `<style scoped>` block.
4. Output the complete replacement block in chat.
5. **Do NOT apply changes.** Wait for explicit user confirmation before writing to any file.

## Commands
- `cd frontend && bun run dev` — dev server
- `cd frontend && bun run build` — production build
- `docker compose up --build` from repo root — full stack with backend + MySQL
- `docker compose down` — stop; add `-v` to reset DB state
