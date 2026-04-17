import type { StationMock } from '~/composables/types/TaskTypes'

export const newsStandStationMock: StationMock = {
  id: "station-newsstand",
  slug: "newsstand",
  title: "Nyhetskvartalet",
  description: "I oppgaven under ser du nyhetssaker og skjermbilder. Bruk detektivblikket ditt for å finne ut hva som virker troverdig og hva som virker mistenkelig.",
  exercises: [
    {
      id: "newsstand-exercise-1",
      title: "Troverdige nyheter",
      description: "Undersøk hvilke nyhetssaker som virker ekte, og hvilke faresignaler som bør sjekkes ekstra godt.",
      tasks: [
        {
          id: "newsstand-task-1",
          templateKind: "x-or-y",
          prompt: {
            title: "Velg nyhetssaken som virker mest troverdig",
            text: "Se etter tydelig avsender, en rolig overskrift og tekst som forklarer hva saken faktisk handler om.",
          },
          introText: "Trykk på nyhetsartikkelen som virker mest troverdig.",
          feedbackCorrect: "Bra observert. Den troverdige artikkelen har en tydelig avsender og en mer realistisk overskrift.",
          feedbackIncorrect: "Se etter hvem som står bak saken og om overskriften prøver å skape sterke følelser med en gang.",
          options: {
            x: {
              imageUrl: "https://placehold.co/600x400?text=NRK+Nyhet",
              imageAlt: "Artikkel fra NRK om sykkelparkering ved skolen",
              eyebrow: "NRK",
              title: "Skolen lager ny sykkelparkering ved hovedinngangen",
              text: "Kommunen og elevene samarbeider om tryggere sykkelparkering før sommeren.",
            },
            y: {
              imageUrl: "https://placehold.co/600x400?text=Klikkagn",
              imageAlt: "Mistenkelig artikkel med dramatisk overskrift om sykler",
              eyebrow: "SuperNytt24",
              title: "Alle skoler i Norge forbyr sykler fra mandag",
              text: "Overskriften prøver å skape sjokk, men sier lite om hvem som faktisk vet dette.",
            },
          },
          correctChoice: "x",
        },
        {
          id: "newsstand-task-2",
          templateKind: "three-option-image",
          introText: "Se på skjermbildet og velg den beste grunnen til at saken virker falsk.",
          feedbackCorrect: "Riktig. Når en overskrift prøver å sjokkere og du ikke kjenner avsenderen, bør du stoppe opp og sjekke mer.",
          feedbackIncorrect: "Tenk på hvilke tegn som faktisk svekker troverdigheten, ikke bare hvordan saken ser ut visuelt.",
          prompt: {
            title: "Hvorfor virker denne saken falsk?",
            text: "Velg forklaringen som passer best til det du ser i skjermbildet.",
            imageUrl: "https://placehold.co/1200x720?text=Nyhetsskjermbilde",
            imageAlt: "Skjermbilde av en tvilsom nyhetsartikkel",
          },
          options: [
            { key: "a", title: "Den har en tydelig avsender og gode kilder", text: "Dette gjør vanligvis en sak mer troverdig, ikke mindre." },
            { key: "b", title: "Overskriften prøver å sjokkere og avsenderen virker ukjent", text: "Sterk følelsesbruk og uklar avsender er vanlige varselsignaler." },
            { key: "c", title: "Bildet er stort og fargerikt", text: "Et stort bilde alene sier ikke noe sikkert om troverdighet." },
          ],
          correctChoice: "b",
        },
        {
          id: "newsstand-task-3",
          templateKind: "x-or-y",
          prompt: {
            title: "Hvilken sak høres mest ekte ut?",
            text: "Når en sak lover veldig mye eller prøver å sjokkere deg med en gang, er det lurt å være ekstra skeptisk.",
          },
          introText: "Velg den artikkelen du tror det er tryggest å stole på.",
          feedbackCorrect: "Ja. Troverdige saker lover sjelden noe helt utrolig, og de forklarer ofte hvem som har undersøkt saken.",
          feedbackIncorrect: "Hvis noe høres for utrolig ut til å være sant, er det ofte smart å dobbeltsjekke før du tror på det.",
          options: {
            x: {
              imageUrl: "https://placehold.co/600x400?text=Junior+Nyhet",
              imageAlt: "Artikkel fra Aftenposten Junior om mobilbruk i klasserommet",
              eyebrow: "Aftenposten Junior",
              title: "Forskere tester nye regler for mobilbruk i klasserommet",
              text: "Saken forklarer hva som testes, hvorfor det gjøres, og hvem som er intervjuet.",
            },
            y: {
              imageUrl: "https://placehold.co/600x400?text=For+godt+til+aa+vaere+sant",
              imageAlt: "Tvilsom artikkel som lover at en app gjør deg smart på ti sekunder",
              eyebrow: "Nyhetsraketten",
              title: "Denne ene appen gjør deg smart på 10 sekunder",
              text: "Når noe virker altfor utrolig eller lover for mye, er det lurt å bli ekstra skeptisk.",
            },
          },
          correctChoice: "x",
        },
      ],
    },
  ],
}
