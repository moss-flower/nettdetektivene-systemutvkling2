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
  uid: string;
  title: string;
  entryName: string;
  description?: string;
  tasks: ExerciseTask[];

  constructor(data: { uid: string; title: string; entryName: string; description?: string; tasks: ExerciseTask[] }) {
    this.uid = data.uid;
    this.title = data.title;
    this.entryName = data.entryName;
    this.description = data.description;
    this.tasks = data.tasks;
  }

  static mockData(): Exercise[] {
    return [
      new Exercise({
        uid: "newsstand-exercise-1",
        title: "Troverdige nyheter",
        entryName: "Lett",
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
          {
            id: "newsstand-task-4",
            templateKind: "x-or-y",
            prompt: {
              title: "Hvilken sak virker mest troverdig?",
              text: "Velg den saken du ville stolt mest på etter et raskt første blikk.",
            },
            introText: "Se på begge sakene og velg den som virker mest pålitelig.",
            feedbackCorrect: "Riktig. Den troverdige saken har tydelig avsender, rolig språk og forklarer hva som har skjedd.",
            feedbackIncorrect: "Se etter hvem som står bak saken, og om overskriften prøver å sjokkere eller forklare.",
            options: {
              x: {
                imageUrl: "https://placehold.co/1200x720?text=Lokalavis+med+kilder",
                imageAlt: "Skjermbilde av en lokalavis med tydelig avsender og sitater",
                eyebrow: "Sak X",
                title: "Lokalavis forklarer nytt skoleprosjekt",
                text: "Artikkelen viser hvem som har skrevet saken og bruker sitater fra rektor og elever.",
              },
              y: {
                imageUrl: "https://placehold.co/1200x720?text=Sjokkoverskrift",
                imageAlt: "Skjermbilde av en ukjent nettside med dramatisk overskrift",
                eyebrow: "Sak Y",
                title: "ALLE elever blir overvaket fra mandag",
                text: "Saken bruker sterke ord, men viser ikke hvem som står bak eller hvor informasjonen kommer fra.",
              },
            },
            correctChoice: "x",
          },
          {
            id: "newsstand-task-5",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva bør du sjekke først?",
              text: "Du ser en nyhet som gjør deg usikker. Velg den beste første handlingen.",
              imageUrl: "https://placehold.co/1200x720?text=Sjekk+kilden+forst",
              imageAlt: "Skjermbilde av en nyhetssak med ukjent avsender",
            },
            introText: "Velg det smarteste første steget når en nyhet virker litt rar.",
            feedbackCorrect: "Bra valg. Det første du bør gjøre er å sjekke hvem som har publisert saken.",
            feedbackIncorrect: "Tenk på hva som hjelper deg å undersøke saken, ikke hva som sprer den raskest videre.",
            options: [
              { key: "a", title: "Sjekke avsenderen og hvor saken er publisert" },
              { key: "b", title: "Dele saken med venner for å høre hva de tror" },
              { key: "c", title: "Stole på saken hvis bildet ser ekte ut" },
            ],
            correctChoice: "a",
          },
          {
            id: "newsstand-task-6",
            templateKind: "x-or-y",
            prompt: {
              title: "Hvilken overskrift er minst clickbait?",
              text: "Velg overskriften som prøver å informere i stedet for å lokke deg med sjokk.",
            },
            introText: "Les begge overskriftene og velg den som virker mest saklig.",
            feedbackCorrect: "Ja. En saklig overskrift forklarer hva saken handler om uten å overdrive.",
            feedbackIncorrect: "Clickbait prøver ofte å gjøre deg nysgjerrig eller sjokkert uten å si så mye konkret.",
            options: {
              x: {
                imageUrl: "https://placehold.co/1200x720?text=Du+vil+ikke+tro+dette",
                imageAlt: "Skjermbilde av en sensasjonell overskrift",
                eyebrow: "Overskrift X",
                title: "Du vil ikke tro hva som skjedde i klasserommet",
                text: "Overskriften skjuler viktig informasjon og prøver å få deg til å klikke av ren nysgjerrighet.",
              },
              y: {
                imageUrl: "https://placehold.co/1200x720?text=Klassen+vant+miljopris",
                imageAlt: "Skjermbilde av en konkret og rolig overskrift",
                eyebrow: "Overskrift Y",
                title: "Klassen vant miljøpris etter ryddeaksjon",
                text: "Overskriften sier tydelig hva som skjedde og bruker et rolig, forklarende språk.",
              },
            },
            correctChoice: "y",
          },
        ],
      }),
      new Exercise({
        uid: "newsstand-exercise-2",
        title: "Nyhetsdetektiv",
        entryName: "Middels",
        description: "Sammenlign nyhetssaker, oppdag clickbait og finn ut hva du bør undersøke før du tror eller deler noe.",
        tasks: [
          {
            id: "newsstand-medium-task-1",
            templateKind: "x-or-y",
            prompt: {
              title: "Hvilken sak bør du stole mest på?",
              text: "Begge sakene handler om det samme, men bare den ene viser tydelige tegn på troverdighet.",
            },
            introText: "Velg saken som virker mest pålitelig når du ser på språk, avsender og forklaringer.",
            feedbackCorrect: "Riktig. Den gode saken forklarer hva som har skjedd og peker til en kjent avsender.",
            feedbackIncorrect: "Se etter kjent avsender, konkret språk og om saken forklarer hvor informasjonen kommer fra.",
            options: {
              x: {
                imageUrl: "https://placehold.co/1200x720?text=Kjent+avis+med+fakta",
                imageAlt: "Skjermbilde av en kjent avis med faktaboks og kildehenvisning",
                eyebrow: "Sak X",
                title: "Kommunen forklarer nye regler for elsparkesykler",
                text: "Saken viser dato, avsender og sitater fra kommunen om hvorfor reglene endres.",
              },
              y: {
                imageUrl: "https://placehold.co/1200x720?text=Viral+advarsel",
                imageAlt: "Skjermbilde av en viral post med dramatisk tekst",
                eyebrow: "Sak Y",
                title: "Nye regler tar fra barn friheten i sommer",
                text: "Saken bruker sterke ord og påstander, men forklarer ikke hvem som har skrevet den.",
              },
            },
            correctChoice: "x",
          },
          {
            id: "newsstand-medium-task-2",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva er det tydeligste faresignalet?",
              text: "Saken ser profesjonell ut, men ett tegn gjør at du bør være ekstra skeptisk.",
              imageUrl: "https://placehold.co/1200x720?text=Nyhet+uten+kilde",
              imageAlt: "Skjermbilde av en nyhetsartikkel uten tydelige kilder",
            },
            introText: "Velg det sterkeste varselsignalet i saken.",
            feedbackCorrect: "Bra sett. Når en sak påstår mye uten å vise hvor informasjonen kommer fra, bør du være skeptisk.",
            feedbackIncorrect: "Tenk på hvilke tegn som gjør det vanskelig å kontrollere om saken faktisk stemmer.",
            options: [
              { key: "a", title: "Den bruker et bilde ved siden av teksten" },
              { key: "b", title: "Den mangler tydelige kilder eller forklaringer" },
              { key: "c", title: "Den er skrevet om et tema mange bryr seg om" },
              { key: "d", title: "Den er delt mange ganger" },
            ],
            correctChoice: "b",
          },
          {
            id: "newsstand-medium-task-3",
            templateKind: "x-or-y",
            prompt: {
              title: "Hvilken delingstekst er tryggest?",
              text: "Du skal sende en nyhet videre til en venn. Velg meldingen som viser god kildekritikk.",
            },
            introText: "Velg den teksten som viser at du tenker deg om før du deler.",
            feedbackCorrect: "Ja. Det er lurt å være usikker og sjekke kilden før man deler videre.",
            feedbackIncorrect: "En trygg delingstekst oppfordrer til å sjekke saken, ikke bare reagere raskt.",
            options: {
              x: {
                imageUrl: "https://placehold.co/1200x720?text=Del+fort",
                imageAlt: "Skjermbilde av en rask og følelsesstyrt delingstekst",
                eyebrow: "Melding X",
                title: "Se dette na! Dette maa vaere sant",
                text: "Meldingen ber folk reagere raskt uten at noen har sjekket om saken stemmer.",
              },
              y: {
                imageUrl: "https://placehold.co/1200x720?text=Sjekk+for+deling",
                imageAlt: "Skjermbilde av en rolig delingstekst",
                eyebrow: "Melding Y",
                title: "Jeg fant denne saken, men vil sjekke kilden forst",
                text: "Meldingen viser at det er smart aa undersoke mer foer man tror eller deler.",
              },
            },
            correctChoice: "y",
          },
          {
            id: "newsstand-medium-task-4",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva gjor du hvis du fortsatt er usikker?",
              text: "Du har sjekket overskriften, men saken virker fortsatt rar. Velg det beste neste steget.",
              imageUrl: "https://placehold.co/1200x720?text=Sporsmal+om+kilder",
              imageAlt: "Skjermbilde av en nyhet som er vanskelig aa vurdere",
            },
            introText: "Velg det klokeste neste steget naar du fortsatt er usikker.",
            feedbackCorrect: "Riktig. Da er det lurt aa sammenligne med andre paalitelige kilder eller spoerre en voksen.",
            feedbackIncorrect: "Hvis du fortsatt er usikker, boer du undersoke mer i stedet for aa gjette eller dele saken.",
            options: [
              { key: "a", title: "Sammenligne med andre paalitelige kilder eller spoerre en voksen" },
              { key: "b", title: "Dele saken for aa se hva andre sier i kommentarfeltet" },
              { key: "c", title: "Tro paa saken hvis mange allerede har likt den" },
            ],
            correctChoice: "a",
          },
        ],
      }),
      new Exercise({
        uid: "postoffice-exercise-1",
        title: "Mistenkelige e-poster",
        entryName: "Lett",
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
              senderNameTargetId: "easy-sender-name",
              senderAddress: "hjelp@skole-it-varsling.net",
              senderAddressTargetId: "easy-sender-address",
              subject: "Ny innlogging til elevportalen",
              subjectTargetId: "easy-subject",
              senderAvatarText: "IT",
              receivedLabel: "I dag 08:14",
              receivedLabelTargetId: "easy-received-label",
              greeting: "Hei!",
              greetingTargetId: "easy-greeting",
              bodyBlocks: [
                {
                  id: "easy-body-1",
                  type: "text",
                  text: "Vi har oppdatert innloggingen til elevportalen denne uka.",
                  targetId: "easy-body-text",
                },
                {
                  id: "easy-link-1",
                  type: "link",
                  label: "Gå til elevportalen",
                  urlText: "skoleportalen.no/logg-inn",
                  helperText: "Les mer om endringen",
                  targetId: "easy-body-link",
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
              closingTargetId: "easy-closing",
            },
            targets: [
              { id: "easy-sender-name", isSuspicious: false },
              { id: "easy-sender-address", isSuspicious: true },
              { id: "easy-received-label", isSuspicious: false },
              { id: "easy-subject", isSuspicious: false },
              { id: "easy-greeting", isSuspicious: false },
              { id: "easy-body-text", isSuspicious: false },
              { id: "easy-body-link", isSuspicious: false },
              { id: "easy-action", isSuspicious: false },
              { id: "easy-attachment", isSuspicious: false },
              { id: "easy-closing", isSuspicious: false },
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
              senderNameTargetId: "medium-sender-name",
              senderAddress: "support@rabatt-premier.xyz",
              senderAddressTargetId: "medium-sender-address",
              subject: "Du har fått gavekort til spillutstyr",
              subjectTargetId: "medium-subject",
              senderAvatarText: "GS",
              receivedLabel: "I dag 11:27",
              receivedLabelTargetId: "medium-received-label",
              greeting: "Hei!",
              greetingTargetId: "medium-greeting",
              bodyBlocks: [
                {
                  id: "medium-body-1",
                  type: "text",
                  text: "Du er valgt ut til å få et gavekort på 500 kroner.",
                  targetId: "medium-body-text",
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
              actionButton: {
                label: "Se gavekortet",
                helperText: "butikk-spill.no",
                targetId: "medium-action",
              },
              attachments: [
                {
                  id: "medium-attachment-1",
                  name: "Vilkår.pdf",
                  details: "Kan leses senere",
                  targetId: "medium-attachment",
                },
              ],
              closing: "Hilsen kundeservice",
              closingTargetId: "medium-closing",
            },
            targets: [
              { id: "medium-sender-name", isSuspicious: false },
              { id: "medium-sender-address", isSuspicious: true },
              { id: "medium-received-label", isSuspicious: false },
              { id: "medium-subject", isSuspicious: false },
              { id: "medium-greeting", isSuspicious: false },
              { id: "medium-body-text", isSuspicious: false },
              { id: "medium-link", isSuspicious: true },
              { id: "medium-action", isSuspicious: false },
              { id: "medium-attachment", isSuspicious: false },
              { id: "medium-closing", isSuspicious: false },
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
              senderNameTargetId: "medium-two-sender-name",
              senderAddress: "melding@skoleplattform.no",
              senderAddressTargetId: "medium-two-sender-address",
              subject: "Nytt dokument fra kontaktlærer",
              subjectTargetId: "medium-two-subject",
              senderAvatarText: "SP",
              receivedLabel: "I dag 13:09",
              receivedLabelTargetId: "medium-two-received-label",
              greeting: "Hei!",
              greetingTargetId: "medium-two-greeting",
              bodyBlocks: [
                {
                  id: "medium-two-body-1",
                  type: "text",
                  text: "Kontaktlæreren din har delt et nytt dokument med informasjon om neste uke.",
                  targetId: "medium-two-body-text",
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
              actionButton: {
                label: "Åpne i skoleplattformen",
                helperText: "skoleplattform.no",
                targetId: "medium-two-action",
              },
              attachments: [
                {
                  id: "medium-two-attachment-1",
                  name: "ukeplan.scr",
                  details: "Skjermsparerfil",
                  targetId: "medium-two-attachment",
                },
              ],
              closing: "Vennlig hilsen skoleplattformen",
              closingTargetId: "medium-two-closing",
            },
            targets: [
              { id: "medium-two-sender-name", isSuspicious: false },
              { id: "medium-two-sender-address", isSuspicious: false },
              { id: "medium-two-received-label", isSuspicious: false },
              { id: "medium-two-subject", isSuspicious: false },
              { id: "medium-two-greeting", isSuspicious: false },
              { id: "medium-two-body-text", isSuspicious: false },
              { id: "medium-two-link", isSuspicious: true },
              { id: "medium-two-action", isSuspicious: false },
              { id: "medium-two-attachment", isSuspicious: true },
              { id: "medium-two-closing", isSuspicious: false },
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
              senderNameTargetId: "hard-sender-name",
              senderAddress: "varsling@laererportalen-sikkerhet.info",
              senderAddressTargetId: "hard-sender-address",
              subject: "Kontoen din blir stengt i kveld",
              subjectTargetId: "hard-subject",
              senderAvatarText: "LP",
              receivedLabel: "I dag 18:46",
              receivedLabelTargetId: "hard-received-label",
              greeting: "Hei elev!",
              greetingTargetId: "hard-greeting",
              bodyBlocks: [
                {
                  id: "hard-body-1",
                  type: "text",
                  text: "Vi har oppdaget unormal aktivitet på kontoen din. Hvis du ikke svarer innen 10 minutter, mister du tilgangen til skolearbeidet ditt.",
                  targetId: "hard-body-text",
                },
                {
                  id: "hard-link-1",
                  type: "link",
                  label: "Sikker innlogging",
                  urlText: "portal-skole-login.com",
                  helperText: "Kontroller kontoen din",
                  targetId: "hard-link",
                },
              ],
              actionButton: {
                label: "Bekreft kontoen nå",
                helperText: "laererportalen.no",
                targetId: "hard-action",
              },
              attachments: [
                {
                  id: "hard-attachment-1",
                  name: "skolekonto.exe",
                  details: "Kjør filen for å låse opp kontoen",
                  targetId: "hard-attachment",
                },
              ],
              closing: "Automatisk melding fra sikkerhetssystemet",
              closingTargetId: "hard-closing",
            },
            targets: [
              { id: "hard-sender-name", isSuspicious: false },
              { id: "hard-sender-address", isSuspicious: true },
              { id: "hard-received-label", isSuspicious: false },
              { id: "hard-subject", isSuspicious: false },
              { id: "hard-greeting", isSuspicious: false },
              { id: "hard-body-text", isSuspicious: true },
              { id: "hard-link", isSuspicious: false },
              { id: "hard-action", isSuspicious: false },
              { id: "hard-attachment", isSuspicious: true },
              { id: "hard-closing", isSuspicious: false },
            ],
          },
        ],
      }),
    ];
  }

  static findByUid(uid: string): Exercise | null {
    return this.mockData().find((exercise) => exercise.uid === uid) ?? null;
  }

  static findByUids(uids: string[]): Exercise[] {
    return uids
      .map((uid) => this.findByUid(uid))
      .filter((exercise): exercise is Exercise => exercise !== null);
  }

  static backendFetchExerciseByUid(uid: string): Exercise | null {
    return this.findByUid(uid);
  }

  static backendFetchExercisesByUids(uids: string[]): Exercise[] {
    return this.findByUids(uids);
  }
}
