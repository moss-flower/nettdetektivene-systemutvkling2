import type { Exercise } from '~/composables/types/ExerciseType'

export const newsStandExerciseMock: Exercise = {
  id: 'newsstand-exercise-1',
  title: 'Troverdige nyheter',
  difficultyText: 'Lett',
  description: 'Undersøk hvilke nyhetssaker som virker ekte, og hvilke faresignaler som bør sjekkes ekstra godt.',
  tasks: [
    {
      id: 'newsstand-task-1',
      templateKind: 'multiple-choice',
      prompt: {
        title: 'Hvilken forklaring passer best?',
        text: 'Se på skjermbildet og velg det beste tegnet på at saken virker troverdig.',
        imageUrl: 'https://placehold.co/1200x720?text=Troverdig+nyhet',
        imageAlt: 'Skjermbilde av en rolig nyhetssak med tydelig avsender',
      },
      introText: 'Velg den forklaringen som best viser hvorfor saken virker trygg å stole på.',
      feedbackCorrect: 'Bra observert. En tydelig avsender gjør det lettere å sjekke hvem som står bak informasjonen.',
      feedbackIncorrect: 'Se etter tegn som hjelper deg å sjekke saken, som tydelig avsender og konkret informasjon.',
      options: [
        { key: 'a', title: 'Saken har en tydelig avsender du kan sjekke' },
        { key: 'b', title: 'Saken bruker mange utropstegn og sterke ord' },
      ],
      correctChoice: 'a',
    },
    {
      id: 'newsstand-task-2',
      templateKind: 'multiple-choice',
      introText: 'Se på skjermbildet og velg den beste grunnen til at saken virker falsk.',
      feedbackCorrect: 'Riktig. Når en overskrift prøver å sjokkere og du ikke kjenner avsenderen, bør du stoppe opp og sjekke mer.',
      feedbackIncorrect: 'Tenk på hvilke tegn som faktisk svekker troverdigheten, ikke bare hvordan saken ser ut visuelt.',
      prompt: {
        title: 'Hvorfor virker denne saken falsk?',
        text: 'Velg forklaringen som passer best til det du ser i skjermbildet.',
        imageUrl: 'https://placehold.co/1200x720?text=Nyhetsskjermbilde',
        imageAlt: 'Skjermbilde av en tvilsom nyhetsartikkel',
      },
      options: [
        { key: 'a', title: 'Den har en tydelig avsender og gode kilder' },
        { key: 'b', title: 'Overskriften prøver å sjokkere og avsenderen virker ukjent' },
        { key: 'c', title: 'Bildet er stort og fargerikt' },
      ],
      correctChoice: 'b',
    },
    {
      id: 'newsstand-task-3',
      templateKind: 'multiple-choice',
      prompt: {
        title: 'Hva er det største varselsignalet?',
        text: 'Se på saken og velg det alternativet som best forklarer hvorfor du bør være skeptisk.',
        imageUrl: 'https://placehold.co/1200x720?text=Varselsignaler+i+nyheter',
        imageAlt: 'Skjermbilde av en tvilsom nyhetssak med dramatisk språk',
      },
      introText: 'Velg det tydeligste varselsignalet i saken.',
      feedbackCorrect: 'Ja. Når en sak lover noe helt utrolig uten gode forklaringer, er det et tydelig varselsignal.',
      feedbackIncorrect: 'Tenk på hva som faktisk gjør saken mindre troverdig: sterke løfter, uklar avsender eller manglende forklaringer.',
      options: [
        { key: 'a', title: 'Den bruker et rolig språk og forklarer saken steg for steg' },
        { key: 'b', title: 'Den lover noe utrolig uten å vise gode kilder' },
        { key: 'c', title: 'Den har et bilde som passer til saken' },
        { key: 'd', title: 'Den nevner at noen har blitt intervjuet' },
      ],
      correctChoice: 'b',
    },
  ],
}

export const newsStandMediumExerciseMock: Exercise = {
  id: 'newsstand-exercise-2',
  title: 'Troverdige nyheter',
  difficultyText: 'Medium',
  description: 'Sammenlign to nyhetssaker og finn den du har best grunn til å stole på.',
  tasks: [
    {
      id: 'newsstand-medium-task-1',
      templateKind: 'x-or-y',
      prompt: {
        title: 'Hvilken sak virker mest troverdig?',
        text: 'Velg saken som har tydeligst avsender og mest realistisk språk.',
      },
      introText: 'Sammenlign de to sakene og velg den du ville stolt mest på.',
      feedbackCorrect: 'Riktig. Den troverdige saken har en kjent avsender, et roligere språk og mer konkret informasjon.',
      feedbackIncorrect: 'Se etter hvem som står bak saken, og om overskriften prøver å sjokkere deg før den forklarer noe.',
      options: {
        x: {
          imageUrl: 'https://placehold.co/600x400?text=Skoleavisen',
          imageAlt: 'Sak fra skoleavisen om ny leseuke på biblioteket',
          eyebrow: 'Skoleavisen',
          title: 'Biblioteket inviterer til leseuke for hele mellomtrinnet',
          text: 'Saken forklarer hva som skjer, når det skjer, og hvem som arrangerer opplegget.',
        },
        y: {
          imageUrl: 'https://placehold.co/600x400?text=Sensasjon24',
          imageAlt: 'Mistenkelig nyhetssak med dramatisk overskrift om skoleforbud',
          eyebrow: 'Sensasjon24',
          title: 'Alle bøker blir forbudt i norske skoler neste uke',
          text: 'Overskriften er dramatisk og forklarer nesten ingenting om hvem som vet dette.',
        },
      },
      correctChoice: 'x',
    },
    {
      id: 'newsstand-medium-task-2',
      templateKind: 'x-or-y',
      prompt: {
        title: 'Hvilken overskrift bør du stole mest på?',
        text: 'Se etter en overskrift som forklarer saken rolig, i stedet for å prøve å sjokkere deg.',
      },
      introText: 'Velg overskriften som virker mest troverdig.',
      feedbackCorrect: 'Bra. Den beste overskriften lover ikke noe utrolig, og den gir en konkret forklaring på hva saken handler om.',
      feedbackIncorrect: 'Tenk på om overskriften prøver å gjøre deg redd eller stresset med en gang, i stedet for å forklare saken.',
      options: {
        x: {
          imageUrl: 'https://placehold.co/600x400?text=Kommunenytt',
          imageAlt: 'Nyhetssak fra Kommunenytt om nye refleksvester til elever',
          eyebrow: 'Kommunenytt',
          title: 'Elever får nye refleksvester før mørketiden starter',
          text: 'Saken beskriver tiltaket, hvem som deler ut vestene og hvorfor det gjøres nå.',
        },
        y: {
          imageUrl: 'https://placehold.co/600x400?text=AlarmNorge',
          imageAlt: 'Mistenkelig nyhetssak med dramatisk overskrift om farlig skolevei',
          eyebrow: 'AlarmNorge',
          title: 'Barn i hele landet livsfare på vei til skolen i morgen',
          text: 'Overskriften er dramatisk, men sier lite om hvem som vet dette eller hva som faktisk har skjedd.',
        },
      },
      correctChoice: 'x',
    },
  ],
}
