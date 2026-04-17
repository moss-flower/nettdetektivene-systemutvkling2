import type {
  EmailHotspotTaskData,
  MultipleChoiceTaskData,
  XOrYTaskData,
} from "~/composables/types/TaskTypes";

export type ExerciseTask =
  | XOrYTaskData
  | MultipleChoiceTaskData
  | EmailHotspotTaskData;

export class Exercise {
  id: string;
  title: string;
  difficultyText: string;
  description?: string;
  tasks: ExerciseTask[];

  constructor(data: { id: string; title: string; difficultyText: string; description?: string; tasks: ExerciseTask[] }) {
    this.id = data.id;
    this.title = data.title;
    this.difficultyText = data.difficultyText;
    this.description = data.description;
    this.tasks = data.tasks;
  }

  static mockData(): Exercise[] {
    return [
      new Exercise({
        id: "newsstand-exercise-1",
        title: "Troverdige nyheter",
        difficultyText: "Lett",
        description: "Undersøk hvilke nyhetssaker som virker ekte, og hvilke faresignaler som bør sjekkes ekstra godt.",
        tasks: [
          {
            id: "newsstand-task-1",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hvilken forklaring passer best?",
              text: "Se på skjermbildet og velg det beste tegnet på at saken virker troverdig.",
              imageUrl: "https://placehold.co/1200x720?text=Troverdig+nyhet",
              imageAlt: "Skjermbilde av en rolig nyhetssak med tydelig avsender",
            },
            introText: "Velg den forklaringen som best viser hvorfor saken virker trygg å stole på.",
            feedbackCorrect: "Bra observert. En tydelig avsender gjør det lettere å sjekke hvem som står bak informasjonen.",
            feedbackIncorrect: "Se etter tegn som hjelper deg å sjekke saken, som tydelig avsender og konkret informasjon.",
            options: [
              { key: "a", title: "Saken har en tydelig avsender du kan sjekke" },
              { key: "b", title: "Saken bruker mange utropstegn og sterke ord" },
            ],
            correctChoice: "a",
          },
          {
            id: "newsstand-task-2",
            templateKind: "multiple-choice",
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
              { key: "a", title: "Den har en tydelig avsender og gode kilder" },
              { key: "b", title: "Overskriften prøver å sjokkere og avsenderen virker ukjent" },
              { key: "c", title: "Bildet er stort og fargerikt" },
            ],
            correctChoice: "b",
          },
          {
            id: "newsstand-task-3",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva er det største varselsignalet?",
              text: "Se på saken og velg det alternativet som best forklarer hvorfor du bør være skeptisk.",
              imageUrl: "https://placehold.co/1200x720?text=Varselsignaler+i+nyheter",
              imageAlt: "Skjermbilde av en tvilsom nyhetssak med dramatisk språk",
            },
            introText: "Velg det tydeligste varselsignalet i saken.",
            feedbackCorrect: "Ja. Når en sak lover noe helt utrolig uten gode forklaringer, er det et tydelig varselsignal.",
            feedbackIncorrect: "Tenk på hva som faktisk gjør saken mindre troverdig: sterke løfter, uklar avsender eller manglende forklaringer.",
            options: [
              { key: "a", title: "Den bruker et rolig språk og forklarer saken steg for steg" },
              { key: "b", title: "Den lover noe utrolig uten å vise gode kilder" },
              { key: "c", title: "Den har et bilde som passer til saken" },
              { key: "d", title: "Den nevner at noen har blitt intervjuet" },
            ],
            correctChoice: "b",
          },
        ],
      }),
      new Exercise({
        id: "postoffice-exercise-1",
        title: "Mistenkelige e-poster",
        difficultyText: "Lett",
        description: "Se etter rare avsendere, farlige lenker og andre tegn på at en e-post ikke er trygg.",
        tasks: [
          {
            id: "postoffice-task-1",
            templateKind: "email-hotspots",
            prompt: {
              title: "Finn den ene tingen som virker rar",
              text: "I denne oppgaven er det bare ett tydelig faresignal. Trykk på det elementet som gjør deg mest usikker.",
            },
            introText: "Se nøye på e-posten og trykk på den delen som virker mistenkelig.",
            feedbackCorrect: "Riktig. Avsenderadressen ser ikke ut som en vanlig skoleadresse, selv om navnet prøver å høres trygt ut.",
            feedbackIncorrect: "Se ekstra på hvem som faktisk har sendt e-posten. Navnet kan se greit ut, men selve adressen kan avsløre mye.",
            email: {
              senderName: "Skole-IT",
              senderAddress: "hjelp@skole-it-varsling.net",
              senderAddressTargetId: "easy-sender-address",
              subject: "Ny innlogging til elevportalen",
              subjectTargetId: "easy-subject",
              senderAvatarText: "IT",
              receivedLabel: "I dag 08:14",
              greeting: "Hei!",
              bodyBlocks: [
                {
                  id: "easy-body-1",
                  type: "text",
                  text: "Vi har oppdatert innloggingen til elevportalen denne uka.",
                },
                {
                  id: "easy-body-2",
                  type: "text",
                  text: "Ta kontakt med læreren din hvis du er usikker på noe.",
                },
              ],
              actionButton: {
                label: "Les mer om endringen",
                helperText: "skoleportalen.no",
                targetId: "easy-action",
              },
              attachments: [
                {
                  id: "easy-attachment-1",
                  name: "Informasjon.pdf",
                  details: "Trygg filtype",
                  targetId: "easy-attachment",
                },
              ],
              closing: "Hilsen skoleadministrasjonen",
            },
            targets: [
              { id: "easy-sender-address", isSuspicious: true },
              { id: "easy-subject", isSuspicious: false },
              { id: "easy-action", isSuspicious: false },
              { id: "easy-attachment", isSuspicious: false },
            ],
          },
          {
            id: "postoffice-task-2",
            templateKind: "email-hotspots",
            prompt: {
              title: "Finn to ting som gjør e-posten utrygg",
              text: "Nå må du oppdage mer enn ett faresignal. Trykk på begge de mistenkelige elementene.",
            },
            introText: "Denne e-posten har to faresignaler. Finn begge før du går videre.",
            feedbackCorrect: "Bra jobbet. Både den rare avsenderadressen og lenken som ikke passer til navnet sitt er varselsignaler.",
            feedbackIncorrect: "Du må se både på avsenderen og på hvor lenken faktisk peker. Nettopp slike små detaljer gjør det lettere å avsløre svindel.",
            email: {
              senderName: "Nettbutikk-kundeservice",
              senderAddress: "support@rabatt-premier.xyz",
              senderAddressTargetId: "medium-sender-address",
              subject: "Du har fått gavekort til spillutstyr",
              subjectTargetId: "medium-subject",
              senderAvatarText: "GS",
              receivedLabel: "I dag 11:27",
              greeting: "Hei!",
              bodyBlocks: [
                {
                  id: "medium-body-1",
                  type: "text",
                  text: "Du er valgt ut til å få et gavekort på 500 kroner.",
                },
                {
                  id: "medium-link-1",
                  type: "link",
                  label: "Hent gavekortet ditt",
                  urlText: "spare-gaver-login.ru",
                  helperText: "Trykk her innen 30 minutter",
                  targetId: "medium-link",
                },
              ],
              attachments: [
                {
                  id: "medium-attachment-1",
                  name: "Vilkår.pdf",
                  details: "Kan leses senere",
                  targetId: "medium-attachment",
                },
              ],
              closing: "Hilsen kundeservice",
            },
            targets: [
              { id: "medium-sender-address", isSuspicious: true },
              { id: "medium-subject", isSuspicious: false },
              { id: "medium-link", isSuspicious: true },
              { id: "medium-attachment", isSuspicious: false },
            ],
          },
          {
            id: "postoffice-task-3",
            templateKind: "email-hotspots",
            prompt: {
              title: "Finn de to faresignalene i skolemeldingen",
              text: "Denne e-posten prøver å se vanlig ut, men to ting skurrer. Trykk på begge.",
            },
            introText: "Se gjennom skolemeldingen og finn de to mistenkelige elementene.",
            feedbackCorrect: "Riktig. Både lenken som peker til en rar adresse og vedlegget med en farlig filtype er tydelige faresignaler.",
            feedbackIncorrect: "Tenk over hvilke elementer som faktisk kan være farlige å åpne eller trykke på, ikke bare hva som høres stressende ut.",
            email: {
              senderName: "Skoleplattformen",
              senderAddress: "melding@skoleplattform.no",
              senderAddressTargetId: "medium-two-sender-address",
              subject: "Nytt dokument fra kontaktlærer",
              subjectTargetId: "medium-two-subject",
              senderAvatarText: "SP",
              receivedLabel: "I dag 13:09",
              greeting: "Hei!",
              bodyBlocks: [
                {
                  id: "medium-two-body-1",
                  type: "text",
                  text: "Kontaktlæreren din har delt et nytt dokument med informasjon om neste uke.",
                },
                {
                  id: "medium-two-link-1",
                  type: "link",
                  label: "Åpne dokumentet",
                  urlText: "skoleplattform-dokumenter.click",
                  helperText: "Se dokumentet nå",
                  targetId: "medium-two-link",
                },
              ],
              attachments: [
                {
                  id: "medium-two-attachment-1",
                  name: "ukeplan.scr",
                  details: "Skjermsparerfil",
                  targetId: "medium-two-attachment",
                },
              ],
              closing: "Vennlig hilsen skoleplattformen",
            },
            targets: [
              { id: "medium-two-sender-address", isSuspicious: false },
              { id: "medium-two-subject", isSuspicious: false },
              { id: "medium-two-link", isSuspicious: true },
              { id: "medium-two-attachment", isSuspicious: true },
            ],
          },
          {
            id: "postoffice-task-4",
            templateKind: "email-hotspots",
            prompt: {
              title: "Finn tre faresignaler i meldingen",
              text: "Her må du bruke detektivblikket ditt. Trykk på alle tre tingene som gjør e-posten mistenkelig.",
            },
            introText: "Denne meldingen skjuler tre faresignaler. Finn alle tre.",
            feedbackCorrect: "Riktig. Den rare avsenderen, den truende teksten og den farlige filtypen er tre tydelige faresignaler.",
            feedbackIncorrect: "Se etter mer enn bare sterke ord. Både avsender, språk og vedlegg kan avsløre om en e-post ikke er trygg.",
            email: {
              senderName: "Lærerportalen",
              senderAddress: "varsling@laererportalen-sikkerhet.info",
              senderAddressTargetId: "hard-sender-address",
              subject: "Kontoen din blir stengt i kveld",
              subjectTargetId: "hard-subject",
              senderAvatarText: "LP",
              receivedLabel: "I dag 18:46",
              greeting: "Hei elev!",
              bodyBlocks: [
                {
                  id: "hard-body-1",
                  type: "text",
                  text: "Vi har oppdaget unormal aktivitet på kontoen din.",
                },
                {
                  id: "hard-body-2",
                  type: "text",
                  text: "Hvis du ikke svarer innen 10 minutter, mister du tilgangen til skolearbeidet ditt.",
                  targetId: "hard-urgent-text",
                },
                {
                  id: "hard-link-1",
                  type: "link",
                  label: "Sikker innlogging",
                  urlText: "portal-skole-login.com",
                  helperText: "Kontroller kontoen din",
                },
              ],
              attachments: [
                {
                  id: "hard-attachment-1",
                  name: "skolekonto.exe",
                  details: "Kjør filen for å låse opp kontoen",
                  targetId: "hard-attachment",
                },
              ],
              closing: "Automatisk melding fra sikkerhetssystemet",
            },
            targets: [
              { id: "hard-sender-address", isSuspicious: true },
              { id: "hard-subject", isSuspicious: false },
              { id: "hard-urgent-text", isSuspicious: true },
              { id: "hard-attachment", isSuspicious: true },
            ],
          },
        ],
      }),
    ];
  }

  static findById(id: string): Exercise | null {
    return this.mockData().find((exercise) => exercise.id === id) ?? null;
  }

  static findByIds(ids: string[]): Exercise[] {
    return ids
      .map((id) => this.findById(id))
      .filter((exercise): exercise is Exercise => exercise !== null);
  }

  static backendFetchExerciseById(id: string): Exercise | null {
    return this.findById(id);
  }

  static backendFetchExercisesByIds(ids: string[]): Exercise[] {
    return this.findByIds(ids);
  }
}
