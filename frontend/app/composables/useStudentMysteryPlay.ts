import type {
  ExerciseTask,
  MultipleChoiceTaskData,
  XOrYTaskData,
} from '~/composables/types/TaskTypes'

export type WeeklyMysteryPlaySession = {
  id: string
  weekLabel: string
  title: string
  summary: string
  outroSuccess: string
  outroRetry: string
  tasks: ExerciseTask[]
}

const mysteryPlaySession: WeeklyMysteryPlaySession = {
  id: 'm-2026-17-play',
  weekLabel: 'Uke 17',
  title: 'Mysteriet om den ville overskriften',
  summary: 'Bruk detektivblikket ditt og finn tegn på at en overskrift prøver å styre følelsene dine.',
  outroSuccess: 'Saken er løst. Du oppdaget flere tegn på at sterke overskrifter kan lure deg.',
  outroRetry: 'Du er på sporet, men noen ledetråder glapp. Prøv en gang til og se ekstra etter sterke ord og uklare kilder.',
  tasks: [
    {
      id: 'weekly-mystery-task-1',
      templateKind: 'multiple-choice',
      introText: 'Start med å finne det tydeligste varselsignalet i overskriften.',
      feedbackCorrect: 'Riktig. Når en sak prøver å gjøre deg redd med en gang, bør du stoppe opp og sjekke mer.',
      feedbackIncorrect: 'Se etter om overskriften prøver å trigge følelser i stedet for å forklare rolig hva som har skjedd.',
      prompt: {
        title: 'Hva er det første varselsignalet?',
        text: 'Se på overskriften og velg det som gjør den mest mistenkelig.',
        imageUrl: 'https://placehold.co/1200x720?text=SJOKK%3A+Dette+ma+foreldre+vite+na',
        imageAlt: 'Skjermbilde av en dramatisk overskrift som prøver å skape frykt',
      },
      options: [
        { key: 'a', title: 'Den bruker ord som prøver å gjøre deg redd med en gang' },
        { key: 'b', title: 'Den har et stort bilde ved siden av teksten' },
        { key: 'c', title: 'Den handler om noe mange bryr seg om' },
      ],
      correctChoice: 'a',
    } satisfies MultipleChoiceTaskData,
    {
      id: 'weekly-mystery-task-2',
      templateKind: 'x-or-y',
      introText: 'Sammenlign de to sakene og velg den som virker mest troverdig.',
      feedbackCorrect: 'Bra. Den troverdige saken forklarer konkret hva som har skjedd og hvem som sier det.',
      feedbackIncorrect: 'Se etter rolig språk, tydelig avsender og konkrete opplysninger om saken.',
      prompt: {
        title: 'Hvilken overskrift virker mest troverdig?',
        text: 'Velg saken du ville stolt mest på etter første blikk.',
      },
      options: {
        x: {
          imageUrl: 'https://placehold.co/1200x720?text=DU+VIL+IKKE+TRO+DETTE',
          imageAlt: 'Skjermbilde av en dramatisk og vag overskrift',
          eyebrow: 'Sak X',
          title: 'Du vil ikke tro hva som skjedde i skolegaarden',
          text: 'Overskriften er dramatisk, men sier lite om hvem som står bak eller hva som faktisk skjedde.',
        },
        y: {
          imageUrl: 'https://placehold.co/1200x720?text=Skolen+forklarer+ny+regel',
          imageAlt: 'Skjermbilde av en rolig overskrift med tydelig avsender',
          eyebrow: 'Sak Y',
          title: 'Skolen forklarer ny regel for mobilbruk',
          text: 'Overskriften sier hva saken handler om og virker lettere å sjekke videre.',
        },
      },
      correctChoice: 'y',
    } satisfies XOrYTaskData,
    {
      id: 'weekly-mystery-task-3',
      templateKind: 'multiple-choice',
      introText: 'Velg det smarteste neste steget når en sak virker mistenkelig.',
      feedbackCorrect: 'Ja. Det smarteste er å sjekke avsender og kilde før du tror på eller deler saken.',
      feedbackIncorrect: 'Tenk på hva som hjelper deg å undersøke saken, ikke hva som sprer den raskest videre.',
      prompt: {
        title: 'Hva bør du gjøre nå?',
        text: 'Du blir usikker på om saken stemmer. Velg det beste neste steget.',
        imageUrl: 'https://placehold.co/1200x720?text=Sjekk+kilden+forst',
        imageAlt: 'Skjermbilde av en nyhetssak som bør sjekkes nærmere',
      },
      options: [
        { key: 'a', title: 'Sjekke hvem som har laget saken og om kilden virker ekte' },
        { key: 'b', title: 'Dele saken videre og høre hva andre tror' },
        { key: 'c', title: 'Tro på saken hvis bildet ser ekte ut' },
      ],
      correctChoice: 'a',
    } satisfies MultipleChoiceTaskData,
  ],
}

export function useStudentMysteryPlay() {
  return {
    mysteryPlaySession,
  }
}
