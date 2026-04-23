import type {
  EmailHotspotTaskData,
  MultipleChoiceTaskData,
  PasswordStrengthTaskData,
  SortIntoBucketsTaskData,
  XOrYTaskData,
} from "~/composables/types/TaskTypes";

export type ExerciseTask =
  | XOrYTaskData
  | MultipleChoiceTaskData
  | EmailHotspotTaskData
  | SortIntoBucketsTaskData
  | PasswordStrengthTaskData;

export class Exercise {
  publicId: string;
  title: string;
  difficultyName: string;
  description?: string;
  tasks: ExerciseTask[];

  constructor(data: {
    publicId?: string;
    uid?: string;
    title: string;
    difficultyName?: string;
    entryName?: string;
    description?: string;
    tasks: ExerciseTask[];
  }) {
    this.publicId = data.publicId ?? data.uid ?? "";
    this.title = data.title;
    this.difficultyName = data.difficultyName ?? data.entryName ?? "";
    this.description = data.description;
    this.tasks = data.tasks;
  }

  get uid(): string {
    return this.publicId;
  }

  get entryName(): string {
    return this.difficultyName;
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
      new Exercise({
        uid: "postoffice-exercise-2",
        title: "E-postdetektiv",
        entryName: "Middels",
        description: "Bruk detektivblikket ditt til å finne flere faresignaler i e-poster som ser mer overbevisende ut.",
        tasks: [
          {
            id: "postoffice-medium-task-1",
            templateKind: "email-hotspots",
            prompt: {
              title: "Finn to faresignaler i skolemeldingen",
              text: "Denne meldingen prøver aa se trygg ut. Trykk paa de to tingene som boer faa deg til aa stoppe opp.",
            },
            introText: "Se gjennom skolemeldingen og finn de to mistenkelige delene.",
            feedbackCorrect: "Riktig. Baade den rare avsenderadressen og lenken som peker til en ukjent side er faresignaler.",
            feedbackIncorrect: "Se ekstra paa hvem meldingen faktisk kommer fra, og hvor lenken vil sende deg.",
            email: {
              senderName: "Skoleplattformen",
              senderNameTargetId: "postoffice-medium-1-sender-name",
              senderAddress: "varsel@skoleplattform-info.net",
              senderAddressTargetId: "postoffice-medium-1-sender-address",
              subject: "Ny beskjed om ukeplanen",
              subjectTargetId: "postoffice-medium-1-subject",
              senderAvatarText: "SP",
              receivedLabel: "I dag 09:18",
              receivedLabelTargetId: "postoffice-medium-1-received-label",
              greeting: "Hei!",
              greetingTargetId: "postoffice-medium-1-greeting",
              bodyBlocks: [
                {
                  id: "postoffice-medium-1-body-text",
                  type: "text",
                  text: "Du har faatt en ny beskjed om ukeplanen for neste uke.",
                  targetId: "postoffice-medium-1-body-text-target",
                },
                {
                  id: "postoffice-medium-1-body-link",
                  type: "link",
                  label: "Aapne meldingen",
                  urlText: "skoleplattform-melding-login.click",
                  helperText: "Logg inn for aa lese beskjeden",
                  targetId: "postoffice-medium-1-body-link-target",
                },
              ],
              actionButton: {
                label: "Aapne i skoleplattformen",
                helperText: "skoleplattform.no",
                targetId: "postoffice-medium-1-action-button",
              },
              attachments: [
                {
                  id: "postoffice-medium-1-attachment",
                  name: "ukeplan.pdf",
                  details: "PDF-dokument",
                  targetId: "postoffice-medium-1-attachment-target",
                },
              ],
              closing: "Vennlig hilsen skoleplattformen",
              closingTargetId: "postoffice-medium-1-closing",
            },
            targets: [
              { id: "postoffice-medium-1-sender-name", isSuspicious: false },
              { id: "postoffice-medium-1-sender-address", isSuspicious: true },
              { id: "postoffice-medium-1-received-label", isSuspicious: false },
              { id: "postoffice-medium-1-subject", isSuspicious: false },
              { id: "postoffice-medium-1-greeting", isSuspicious: false },
              { id: "postoffice-medium-1-body-text-target", isSuspicious: false },
              { id: "postoffice-medium-1-body-link-target", isSuspicious: true },
              { id: "postoffice-medium-1-action-button", isSuspicious: false },
              { id: "postoffice-medium-1-attachment-target", isSuspicious: false },
              { id: "postoffice-medium-1-closing", isSuspicious: false },
            ],
          },
          {
            id: "postoffice-medium-task-2",
            templateKind: "email-hotspots",
            prompt: {
              title: "Finn de to farlige delene",
              text: "Noen deler av denne meldingen virker helt vanlige. To ting er likevel utrygge. Finn begge.",
            },
            introText: "Se etter hvilke deler som faktisk kan vaere farlige aa aapne eller stole paa.",
            feedbackCorrect: "Bra jobbet. Den stressende teksten og vedlegget med uvanlig filtype er tydelige faresignaler.",
            feedbackIncorrect: "Tenk over hva meldingen ber deg om aa gjore raskt, og om noe ser farlig ut aa aapne.",
            email: {
              senderName: "Biblioteket",
              senderNameTargetId: "postoffice-medium-2-sender-name",
              senderAddress: "info@biblioteket.no",
              senderAddressTargetId: "postoffice-medium-2-sender-address",
              subject: "Laaneoversikt for denne uka",
              subjectTargetId: "postoffice-medium-2-subject",
              senderAvatarText: "BI",
              receivedLabel: "I dag 13:42",
              receivedLabelTargetId: "postoffice-medium-2-received-label",
              greeting: "Hei!",
              greetingTargetId: "postoffice-medium-2-greeting",
              bodyBlocks: [
                {
                  id: "postoffice-medium-2-body-text",
                  type: "text",
                  text: "Hvis du ikke svarer innen 15 minutter, blir brukeren din sperret for resten av maaneden.",
                  targetId: "postoffice-medium-2-body-text-target",
                },
                {
                  id: "postoffice-medium-2-body-link",
                  type: "link",
                  label: "Se laaneoversikten",
                  urlText: "biblioteket.no/min-side",
                  helperText: "Trygg innlogging",
                  targetId: "postoffice-medium-2-body-link-target",
                },
              ],
              actionButton: {
                label: "Gaa til biblioteket",
                helperText: "biblioteket.no",
                targetId: "postoffice-medium-2-action-button",
              },
              attachments: [
                {
                  id: "postoffice-medium-2-attachment",
                  name: "laanestatus.scr",
                  details: "Skjermsparerfil",
                  targetId: "postoffice-medium-2-attachment-target",
                },
              ],
              closing: "Hilsen biblioteket",
              closingTargetId: "postoffice-medium-2-closing",
            },
            targets: [
              { id: "postoffice-medium-2-sender-name", isSuspicious: false },
              { id: "postoffice-medium-2-sender-address", isSuspicious: false },
              { id: "postoffice-medium-2-received-label", isSuspicious: false },
              { id: "postoffice-medium-2-subject", isSuspicious: false },
              { id: "postoffice-medium-2-greeting", isSuspicious: false },
              { id: "postoffice-medium-2-body-text-target", isSuspicious: true },
              { id: "postoffice-medium-2-body-link-target", isSuspicious: false },
              { id: "postoffice-medium-2-action-button", isSuspicious: false },
              { id: "postoffice-medium-2-attachment-target", isSuspicious: true },
              { id: "postoffice-medium-2-closing", isSuspicious: false },
            ],
          },
          {
            id: "postoffice-medium-task-3",
            templateKind: "email-hotspots",
            prompt: {
              title: "Finn tre faresignaler",
              text: "Denne meldingen blander trygge og utrygge tegn. Trykk paa alle de tre delene som virker mistenkelige.",
            },
            introText: "Bruk detektivblikket ditt og finn alle tre faresignalene i meldingen.",
            feedbackCorrect: "Riktig. Den ukjente avsenderadressen, den rare lenken og den stressende tidsfristen er tre tydelige faresignaler.",
            feedbackIncorrect: "Se baade paa avsenderen, spraket og hvor lenken leder. Flere sma detaljer avslorer meldingen.",
            email: {
              senderName: "Spillbutikk Premium",
              senderNameTargetId: "postoffice-medium-3-sender-name",
              senderAddress: "gevinst@premium-spillbonus.biz",
              senderAddressTargetId: "postoffice-medium-3-sender-address",
              subject: "Du har vunnet nytt gamingutstyr",
              subjectTargetId: "postoffice-medium-3-subject",
              senderAvatarText: "GP",
              receivedLabel: "I dag 17:06",
              receivedLabelTargetId: "postoffice-medium-3-received-label",
              greeting: "Gratulerer!",
              greetingTargetId: "postoffice-medium-3-greeting",
              bodyBlocks: [
                {
                  id: "postoffice-medium-3-body-text",
                  type: "text",
                  text: "Du maa hente premien innen 20 minutter, ellers mister du den.",
                  targetId: "postoffice-medium-3-body-text-target",
                },
                {
                  id: "postoffice-medium-3-body-link",
                  type: "link",
                  label: "Hent premien din",
                  urlText: "premium-spillbonus-free.win",
                  helperText: "Bekreft adressen din",
                  targetId: "postoffice-medium-3-body-link-target",
                },
              ],
              actionButton: {
                label: "Se premien",
                helperText: "spillbutikk.no",
                targetId: "postoffice-medium-3-action-button",
              },
              attachments: [
                {
                  id: "postoffice-medium-3-attachment",
                  name: "premieinfo.pdf",
                  details: "PDF-dokument",
                  targetId: "postoffice-medium-3-attachment-target",
                },
              ],
              closing: "Hilsen kundeservice",
              closingTargetId: "postoffice-medium-3-closing",
            },
            targets: [
              { id: "postoffice-medium-3-sender-name", isSuspicious: false },
              { id: "postoffice-medium-3-sender-address", isSuspicious: true },
              { id: "postoffice-medium-3-received-label", isSuspicious: false },
              { id: "postoffice-medium-3-subject", isSuspicious: false },
              { id: "postoffice-medium-3-greeting", isSuspicious: false },
              { id: "postoffice-medium-3-body-text-target", isSuspicious: true },
              { id: "postoffice-medium-3-body-link-target", isSuspicious: true },
              { id: "postoffice-medium-3-action-button", isSuspicious: false },
              { id: "postoffice-medium-3-attachment-target", isSuspicious: false },
              { id: "postoffice-medium-3-closing", isSuspicious: false },
            ],
          },
          {
            id: "postoffice-medium-task-4",
            templateKind: "email-hotspots",
            prompt: {
              title: "Finn tre ting som skurrer",
              text: "Meldingen virker kanskje kjent ved foerste blikk, men tre detaljer avslorer at noe er galt.",
            },
            introText: "Les meldingen rolig og finn de tre delene som virker utrygge.",
            feedbackCorrect: "Bra observert. Den rare adressen, knappen som leder feil sted og det farlige vedlegget gjoer meldingen utrygg.",
            feedbackIncorrect: "Se etter om avsenderen passer, om knappen leder dit den sier, og om vedlegget virker trygt aa aapne.",
            email: {
              senderName: "Skoleadmin",
              senderNameTargetId: "postoffice-medium-4-sender-name",
              senderAddress: "melding@skoleadmin-portal.help",
              senderAddressTargetId: "postoffice-medium-4-sender-address",
              subject: "Oppdatert samtykkeskjema",
              subjectTargetId: "postoffice-medium-4-subject",
              senderAvatarText: "SA",
              receivedLabel: "I dag 07:55",
              receivedLabelTargetId: "postoffice-medium-4-received-label",
              greeting: "Hei foresatt og elev!",
              greetingTargetId: "postoffice-medium-4-greeting",
              bodyBlocks: [
                {
                  id: "postoffice-medium-4-body-text",
                  type: "text",
                  text: "Vi trenger et nytt samtykke for skoleaktiviteter denne maaneden.",
                  targetId: "postoffice-medium-4-body-text-target",
                },
                {
                  id: "postoffice-medium-4-body-link",
                  type: "link",
                  label: "Les informasjonen",
                  urlText: "skolen.no/samtykke",
                  helperText: "Mer informasjon",
                  targetId: "postoffice-medium-4-body-link-target",
                },
              ],
              actionButton: {
                label: "Signer skjemaet",
                helperText: "skoleadmin-login.ru",
                targetId: "postoffice-medium-4-action-button",
              },
              attachments: [
                {
                  id: "postoffice-medium-4-attachment",
                  name: "samtykke.exe",
                  details: "Kjoer fil for aa signere",
                  targetId: "postoffice-medium-4-attachment-target",
                },
              ],
              closing: "Hilsen administrasjonen",
              closingTargetId: "postoffice-medium-4-closing",
            },
            targets: [
              { id: "postoffice-medium-4-sender-name", isSuspicious: false },
              { id: "postoffice-medium-4-sender-address", isSuspicious: true },
              { id: "postoffice-medium-4-received-label", isSuspicious: false },
              { id: "postoffice-medium-4-subject", isSuspicious: false },
              { id: "postoffice-medium-4-greeting", isSuspicious: false },
              { id: "postoffice-medium-4-body-text-target", isSuspicious: false },
              { id: "postoffice-medium-4-body-link-target", isSuspicious: false },
              { id: "postoffice-medium-4-action-button", isSuspicious: true },
              { id: "postoffice-medium-4-attachment-target", isSuspicious: true },
              { id: "postoffice-medium-4-closing", isSuspicious: false },
            ],
          },
        ],
      }),
      new Exercise({
        uid: "passwordbank-exercise-1",
        title: "Passorddetektiv",
        entryName: "Lett",
        description: "Sorter passord, oppdag svake valg og ov pa a lage passord som er trygge og lette a huske.",
        tasks: [
          {
            id: "passwordbank-light-task-1",
            templateKind: "sort-into-buckets",
            introText: "Dra passordene til riktig boks. Et trygt passord er langt og vanskelig a gjette.",
            feedbackCorrect: "Bra jobbet. Du fant forskjellen pa passord som er lette a gjette og passord som gir bedre beskyttelse.",
            feedbackIncorrect: "Se etter navn, arstall og vanlige monstre. Trygge passord er lengre og mindre personlige.",
            prompt: {
              title: "Sorter passordene",
              text: "Hvilke passord virker trygge, og hvilke virker utrygge?",
            },
            unplacedLabel: "Passord som skal sorteres",
            buckets: [
              {
                key: "safe",
                title: "Trygge passord",
                description: "Lange passord som ikke avslorer noe personlig.",
                backgroundColor: "#edf9f0",
              },
              {
                key: "unsafe",
                title: "Utrygge passord",
                description: "Korte eller lette a gjette passord.",
                backgroundColor: "#fff1f1",
              },
            ],
            items: [
              {
                id: "passwordbank-light-sort-1",
                label: "123456",
                correctBucketKey: "unsafe",
                explanation: "Dette er et veldig vanlig passord som mange prover forst.",
              },
              {
                id: "passwordbank-light-sort-2",
                label: "Emma2016",
                correctBucketKey: "unsafe",
                explanation: "Navn og arstall er personlig informasjon som er lettere a gjette.",
              },
              {
                id: "passwordbank-light-sort-3",
                label: "SolFisk!27",
                correctBucketKey: "safe",
                explanation: "Dette er lengre og blander bokstaver, tall og symbol.",
              },
              {
                id: "passwordbank-light-sort-4",
                label: "KattOgTaco#9",
                correctBucketKey: "safe",
                explanation: "Flere ord og et symbol gjor passordet sterkere og lettere a huske.",
              },
            ],
          },
          {
            id: "passwordbank-light-task-2",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva gjor passordet utrygt?",
              text: "Oskar bruker passordet Oskar2015 pa spillkontoen sin. Hva er det storste problemet?",
              imageUrl: "https://placehold.co/1200x720?text=Passord+med+navn+og+arstall",
              imageAlt: "Illustrasjon av et passord med navn og arstall",
            },
            introText: "Velg den forklaringen som best viser hvorfor passordet er svakt.",
            feedbackCorrect: "Riktig. Navn og arstall er personlig informasjon som andre lettere kan gjette.",
            feedbackIncorrect: "Tenk pa hva som gjor passordet lett a gjette, ikke bare hvor langt det er.",
            options: [
              { key: "a", title: "Det bruker navn og arstall som andre kan kjenne til" },
              { key: "b", title: "Det har en stor bokstav forst" },
              { key: "c", title: "Det inneholder bare ett ord" },
            ],
            correctChoice: "a",
          },
          {
            id: "passwordbank-light-task-3",
            templateKind: "password-strength",
            introText: "Gjor passordet tryggere ved a unnga personlige spor og bruke flere tegn eller ord.",
            feedbackCorrect: "Bra forbedret. Det nye passordet er mindre personlig og mye vanskeligere a gjette.",
            feedbackIncorrect: "Passordet trenger litt mer arbeid. Prov med flere tegn, og unnga fodselsar, vanlige monster og gjentakelser.",
            prompt: {
              title: "Forbedre et svakt passord",
              text: "Bytt ut et lett passord med noe tryggere som du fortsatt kan huske.",
              helperText: "Tips: bruk mange tegn, bland bokstaver, tall og symbol, og unnga fodselsar og gjentakelser.",
            },
            mode: "improve",
            startingPassword: "Luna2015",
            placeholder: "Skriv et tryggere passord her",
            minLength: 10,
            minScoreToPass: 3,
            commonPatterns: ["123", "qwerty", "passord"],
          },
        ],
      }),
      new Exercise({
        uid: "passwordbank-exercise-2",
        title: "Passordmester",
        entryName: "Middels",
        description: "Sammenlign passordvalg, forsta hvorfor gjenbruk er risikabelt, og bygg et sterkt passord fra bunnen av.",
        tasks: [
          {
            id: "passwordbank-medium-task-1",
            templateKind: "sort-into-buckets",
            introText: "Noen passord ser sterke ut ved forste blikk, men kan fortsatt vare utrygge hvis de inneholder personlige spor.",
            feedbackCorrect: "Bra sett. Du oppdaget at et passord kan se avansert ut og likevel vare lett a gjette hvis det bruker navn eller favorittlag.",
            feedbackIncorrect: "Se etter om passordet bygger pa noe andre kan vite om deg, selv om det har symbol eller tall.",
            prompt: {
              title: "Sorter de litt vanskeligere passordene",
              text: "Hvilke passord er trygge nok, og hvilke avslorer for mye?",
            },
            unplacedLabel: "Passord som skal sorteres",
            buckets: [
              {
                key: "safe",
                title: "Trygge passord",
                description: "Passord som er lange og ikke bygger pa lett gjettbar info.",
                backgroundColor: "#edf9f0",
              },
              {
                key: "unsafe",
                title: "Utrygge passord",
                description: "Passord som andre fortsatt kan gjette seg fram til.",
                backgroundColor: "#fff1f1",
              },
            ],
            items: [
              {
                id: "passwordbank-medium-sort-1",
                label: "Ada!Fotball09",
                correctBucketKey: "unsafe",
                explanation: "Navn og favorittaktivitet kan vare kjent for andre.",
              },
              {
                id: "passwordbank-medium-sort-2",
                label: "SkyBanan!Tog14",
                correctBucketKey: "safe",
                explanation: "Dette bruker flere ord og blander tegn uten a avslore noe personlig.",
              },
              {
                id: "passwordbank-medium-sort-3",
                label: "qwerty!",
                correctBucketKey: "unsafe",
                explanation: "Selv med symbol er dette et kjent tastaturmonster.",
              },
              {
                id: "passwordbank-medium-sort-4",
                label: "Manesykkel#28",
                correctBucketKey: "safe",
                explanation: "Dette er lengre, har flere deler og er vanskeligere a gjette.",
              },
            ],
          },
          {
            id: "passwordbank-medium-task-2",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hvorfor er passord-gjenbruk dumt?",
              text: "Mia bruker samme passord pa spillkontoen, e-posten og skolekontoen. Hva kan skje hvis noen finner ett av passordene hennes?",
              imageUrl: "https://placehold.co/1200x720?text=Samme+passord+overalt",
              imageAlt: "Illustrasjon av samme passord brukt pa flere kontoer",
            },
            introText: "Velg det beste svaret pa hvorfor det er lurt a ha forskjellige passord flere steder.",
            feedbackCorrect: "Riktig. Hvis samme passord brukes overalt, kan ett lekket passord gi tilgang til mange kontoer.",
            feedbackIncorrect: "Tenk pa hva som skjer hvis noen klarer a gjette eller stjele ett passord som brukes flere steder.",
            options: [
              { key: "a", title: "Da kan noen prove samme passord pa flere kontoer" },
              { key: "b", title: "Da blir passordet vanskeligere a skrive feil" },
              { key: "c", title: "Da ser alle kontoene mer like ut" },
            ],
            correctChoice: "a",
          },
          {
            id: "passwordbank-medium-task-3",
            templateKind: "password-strength",
            introText: "Lag et nytt passord som er trygt nok til a beskytte en viktig konto og fortsatt lett nok a huske.",
            feedbackCorrect: "Supert. Du laget et passord som er langt, mindre gjettbart og bedre egnet til a brukes bare ett sted.",
            feedbackIncorrect: "Prov igjen med flere tegn, et symbol eller tall, og unnga fodselsar, vanlige monster og gjentakelser.",
            prompt: {
              title: "Lag et sterkt og huskbart passord",
              text: "Tenk deg at du lager et nytt passord til skolekontoen din. Skriv et passord som er trygt og mulig a huske.",
              helperText: "Tips: bruk mange tegn, bland bokstaver, tall og symbol, og unnga gjentakelser.",
            },
            mode: "create",
            placeholder: "Skriv det nye passordet ditt her",
            minLength: 10,
            minScoreToPass: 3,
            commonPatterns: ["123", "qwerty", "passord", "abc"],
          },
        ],
      }),
      new Exercise({
        uid: "marketplace-exercise-1",
        title: "Trygg handel",
        entryName: "Lett",
        description: "Se etter faresignaler i nettbutikker og velg den tryggeste handlingen før du handler.",
        tasks: [
          {
            id: "marketplace-light-task-1",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva er det tydeligste faresignalet?",
              text: "En butikk lover superrabatt på et populært headset. Hva bør få deg til å stoppe opp?",
              imageUrl: "https://placehold.co/1200x720?text=Markedsplassen+billig+headset",
              imageAlt: "Skjermbilde av nettbutikk med stort avslag på headset",
            },
            introText: "Velg det tegnet som virker mest mistenkelig på nettsiden.",
            feedbackCorrect: "Riktig. En rar nettadresse kan være et tydelig tegn på at butikken ikke er trygg.",
            feedbackIncorrect: "Se etter detaljer som gjør det vanskelig å stole på butikken, som rare nettadresser eller uklare opplysninger.",
            options: [
              { key: "a", title: "Butikken bruker en rar nettadresse som ikke ser norsk ut" },
              { key: "b", title: "Produktbildet er stort og fargerikt" },
              { key: "c", title: "Det står at varen kommer raskt" },
            ],
            correctChoice: "a",
          },
          {
            id: "marketplace-light-task-2",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva bør du gjøre?",
              text: "Du ser en butikk med 90 prosent rabatt, men du kjenner den ikke igjen. Hva er smartest?",
              imageUrl: "https://placehold.co/1200x720?text=Markedsplassen+90+prosent+rabatt",
              imageAlt: "Skjermbilde av nettbutikk med svært høye rabatter",
            },
            introText: "Velg handlingen som viser god nettvett før du kjøper noe.",
            feedbackCorrect: "Bra valg. Når noe virker for godt til å være sant, bør du undersøke mer først.",
            feedbackIncorrect: "Det tryggeste er å undersøke mer før du betaler i en ukjent butikk.",
            options: [
              { key: "a", title: "Kjøpe med en gang før tilbudet forsvinner" },
              { key: "b", title: "Undersøke mer om butikken og betalingen" },
              { key: "c", title: "Sende lenken til venner så de kan kjøpe også" },
            ],
            correctChoice: "b",
          },
          {
            id: "marketplace-light-task-3",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hvilken betaling virker tryggest?",
              text: "Du vil kjøpe noe på nett. Hvilken løsning gir best trygghet hvis noe går galt?",
              imageUrl: "https://placehold.co/1200x720?text=Trygg+betaling+pa+nett",
              imageAlt: "Illustrasjon av ulike betalingsvalg i nettbutikk",
            },
            introText: "Velg betalingsmåten som vanligvis er tryggest i en seriøs nettbutikk.",
            feedbackCorrect: "Ja. Kjente og sikre betalingsløsninger gjør det lettere å få hjelp hvis noe går galt.",
            feedbackIncorrect: "Tenk på hvilken løsning som gir mest beskyttelse og er vanlig i seriøse nettbutikker.",
            options: [
              { key: "a", title: "Bankkort eller kjent betalingstjeneste med sikker betaling" },
              { key: "b", title: "Sende penger direkte til et privat kontonummer" },
              { key: "c", title: "Betale i gavekortkoder" },
            ],
            correctChoice: "a",
          },
        ],
      }),
      new Exercise({
        uid: "marketplace-exercise-2",
        title: "Nettbutikksjekk",
        entryName: "Middels",
        description: "Sammenlign nettbutikker, vurder anmeldelser og finn ut når du bør la være å handle.",
        tasks: [
          {
            id: "marketplace-medium-task-1",
            templateKind: "x-or-y",
            prompt: {
              title: "Hvilken butikk virker mest troverdig?",
              text: "Begge selger den samme spillmusen. Velg butikken du ville stolt mest på.",
            },
            introText: "Se etter trygg nettadresse, tydelig informasjon og en vanlig måte å betale på.",
            feedbackCorrect: "Riktig. En kjent nettadresse, tydelig kontaktinfo og trygg betaling gjør butikken mer troverdig.",
            feedbackIncorrect: "Se etter hvem som står bak butikken, og om informasjonen virker tydelig og trygg.",
            options: {
              x: {
                imageUrl: "https://placehold.co/1200x720?text=Butikk+X+trygg",
                imageAlt: "Skjermbilde av seriøs nettbutikk med tydelig kontaktinformasjon",
                eyebrow: "Butikk X",
                title: "Spillbutikk.no",
                text: "Butikken har norsk nettadresse, tydelig kundeservice og betaling med kjente løsninger.",
              },
              y: {
                imageUrl: "https://placehold.co/1200x720?text=Butikk+Y+mistenkelig",
                imageAlt: "Skjermbilde av ukjent nettbutikk med uvanlig nettadresse",
                eyebrow: "Butikk Y",
                title: "Superdeal-game-click.biz",
                text: "Butikken lover enorme rabatter, men viser lite kontaktinformasjon og en uvanlig nettadresse.",
              },
            },
            correctChoice: "x",
          },
          {
            id: "marketplace-medium-task-2",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva sier anmeldelsene deg?",
              text: "Du ser bare femstjerners anmeldelser som alle er lagt inn samme dag og sier nesten det samme. Hva tenker du?",
              imageUrl: "https://placehold.co/1200x720?text=Like+anmeldelser",
              imageAlt: "Skjermbilde av mange like anmeldelser i en nettbutikk",
            },
            introText: "Velg den forklaringen som passer best når anmeldelser ser litt rare ut.",
            feedbackCorrect: "Bra sett. Når anmeldelser er veldig like, kan de være falske eller lite troverdige.",
            feedbackIncorrect: "Tenk på om anmeldelsene virker ekte og skrevet av forskjellige personer.",
            options: [
              { key: "a", title: "Det kan være falske eller kopierte anmeldelser" },
              { key: "b", title: "Det betyr at butikken sikkert er best" },
              { key: "c", title: "Det spiller ingen rolle så lenge prisen er lav" },
            ],
            correctChoice: "a",
          },
          {
            id: "marketplace-medium-task-3",
            templateKind: "multiple-choice",
            prompt: {
              title: "Når bør du la være å handle?",
              text: "Butikken ber deg betale raskt med bankoverføring og viser ingen enkel måte å kontakte dem på. Hva bør du gjøre?",
              imageUrl: "https://placehold.co/1200x720?text=Ingen+kundeservice",
              imageAlt: "Skjermbilde av nettbutikk uten tydelig kontaktinformasjon",
            },
            introText: "Velg det klokeste svaret når flere ting virker utrygge samtidig.",
            feedbackCorrect: "Ja. Når både betaling og kontaktinformasjon virker utrygt, er det best å ikke handle.",
            feedbackIncorrect: "Hvis flere ting virker mistenkelige, er det tryggest å la være å kjøpe.",
            options: [
              { key: "a", title: "Ikke handle" },
              { key: "b", title: "Kjøpe litt først for å teste" },
              { key: "c", title: "Be en venn kjøpe for deg" },
            ],
            correctChoice: "a",
          },
        ],
      }),
      new Exercise({
        uid: "socialmedia-exercise-1",
        title: "Tenk før du deler",
        entryName: "Lett",
        description: "Oppdag når innlegg prøver å skape sterke følelser, og velg en trygg reaksjon før du deler videre.",
        tasks: [
          {
            id: "socialmedia-light-task-1",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva bør du gjøre først?",
              text: "Et innlegg sier at alle må dele dette nå fordi noe farlig skjer på skolen i morgen. Hva er smartest?",
              imageUrl: "https://placehold.co/1200x720?text=Del+dette+na",
              imageAlt: "Skjermbilde av dramatisk innlegg som ber eleven dele raskt",
            },
            introText: "Velg det beste første steget når et innlegg prøver å stresse deg.",
            feedbackCorrect: "Bra. Når et innlegg prøver å få deg til å reagere raskt, bør du sjekke kilder først.",
            feedbackIncorrect: "Sterke og stressende innlegg bør sjekkes før de deles videre.",
            options: [
              { key: "a", title: "Dele med en gang" },
              { key: "b", title: "Sjekke kilder" },
              { key: "c", title: "Skrive at alle må få panikk" },
            ],
            correctChoice: "b",
          },
          {
            id: "socialmedia-light-task-2",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hvilken følelse prøver innlegget å skape?",
              text: "Innlegget bruker ord som fare, hemmelig og for sent. Hvilken følelse prøver det mest å vekke?",
              imageUrl: "https://placehold.co/1200x720?text=Skremmende+innlegg",
              imageAlt: "Skjermbilde av innlegg med dramatisk og skremmende språk",
            },
            introText: "Velg følelsen som passer best til språket i innlegget.",
            feedbackCorrect: "Riktig. Slike innlegg prøver ofte å skape frykt for at du skal reagere raskt.",
            feedbackIncorrect: "Tenk på om innlegget prøver å gjøre deg rolig, glad eller redd.",
            options: [
              { key: "a", title: "Frykt" },
              { key: "b", title: "Ro" },
              { key: "c", title: "Kjedsomhet" },
            ],
            correctChoice: "a",
          },
          {
            id: "socialmedia-light-task-3",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva hvis alle andre deler?",
              text: "Mange i klassen har allerede delt et rykte. Betyr det at ryktet er sant?",
              imageUrl: "https://placehold.co/1200x720?text=Alle+deler+det",
              imageAlt: "Illustrasjon av mange elever som deler samme innlegg",
            },
            introText: "Velg det beste svaret når gruppepress gjør det vanskelig å stoppe opp.",
            feedbackCorrect: "Bra tenkt. At mange deler noe, betyr ikke at det stemmer.",
            feedbackIncorrect: "Mange delinger er ikke det samme som bevis.",
            options: [
              { key: "a", title: "Ja, mange delinger betyr at det er sant" },
              { key: "b", title: "Nei, jeg bør fortsatt sjekke om det stemmer" },
              { key: "c", title: "Ja, hvis en venn sendte det først" },
            ],
            correctChoice: "b",
          },
        ],
      }),
      new Exercise({
        uid: "socialmedia-exercise-2",
        title: "Del eller vent",
        entryName: "Middels",
        description: "Vurder innlegg som spiller på sinne og gruppepress, og øv på å velge trygg handling og god kildekritikk.",
        tasks: [
          {
            id: "socialmedia-medium-task-1",
            templateKind: "x-or-y",
            prompt: {
              title: "Hvilken reaksjon er smartest?",
              text: "Du får en melding som påstår at noen jukser i en konkurranse. Velg den reaksjonen som viser best dømmekraft.",
            },
            introText: "Velg den reaksjonen som stopper ryktet til du vet mer.",
            feedbackCorrect: "Ja. Det er lurt å vente og sjekke mer før du sprer noe som kan skade andre.",
            feedbackIncorrect: "Når et innlegg kan skade noen, bør du bremse og undersøke før du deler.",
            options: {
              x: {
                imageUrl: "https://placehold.co/1200x720?text=Reaksjon+X+del",
                imageAlt: "Skjermbilde av rask deling av et rykte",
                eyebrow: "Reaksjon X",
                title: "Del med en gang",
                text: "Du sender meldingen videre til flere venner så alle får vite det raskt.",
              },
              y: {
                imageUrl: "https://placehold.co/1200x720?text=Reaksjon+Y+vent",
                imageAlt: "Skjermbilde av elev som stopper opp og sjekker saken",
                eyebrow: "Reaksjon Y",
                title: "Vent og sjekk",
                text: "Du lar være å dele og prøver å finne ut om informasjonen faktisk stemmer.",
              },
            },
            correctChoice: "y",
          },
          {
            id: "socialmedia-medium-task-2",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva prøver posten å få deg til å føle?",
              text: "Posten sier at voksne skjuler sannheten, og at du må bli sint og si fra til alle. Hvilken følelse brukes mest for å manipulere deg?",
              imageUrl: "https://placehold.co/1200x720?text=Sint+innlegg",
              imageAlt: "Skjermbilde av innlegg som prøver å gjøre leseren sint",
            },
            introText: "Velg følelsen posten prøver å skru opp for å få deg til å reagere.",
            feedbackCorrect: "Riktig. Sinne kan få oss til å reagere fort uten å sjekke fakta først.",
            feedbackIncorrect: "Tenk på hvilken sterk følelse som kan få folk til å dele noe raskt.",
            options: [
              { key: "a", title: "Sinne" },
              { key: "b", title: "Takknemlighet" },
              { key: "c", title: "Avslapping" },
            ],
            correctChoice: "a",
          },
          {
            id: "socialmedia-medium-task-3",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hvem kan hjelpe når du er usikker?",
              text: "Du ser en post som gjør deg både redd og usikker. Hva er et godt valg hvis du ikke finner sikre kilder selv?",
              imageUrl: "https://placehold.co/1200x720?text=Sporsmal+til+voksen",
              imageAlt: "Illustrasjon av elev som vurderer å spørre en voksen om hjelp",
            },
            introText: "Velg handlingen som er trygg når du fortsatt er usikker.",
            feedbackCorrect: "Bra. Å spørre en voksen eller lærer kan hjelpe deg å stoppe feilinformasjon før den sprer seg.",
            feedbackIncorrect: "Når du er usikker, er det bedre å be om hjelp enn å dele videre.",
            options: [
              { key: "a", title: "Spørre en voksen eller lærer" },
              { key: "b", title: "Dele posten i klassechatten" },
              { key: "c", title: "Lage et nytt rykte av det" },
            ],
            correctChoice: "a",
          },
        ],
      }),
      new Exercise({
        uid: "photograph-exercise-1",
        title: "Bildeblikk",
        entryName: "Lett",
        description: "Øv på å oppdage om et bilde virker ekte, KI-generert eller manipulert, og finn tegn som ser rare ut.",
        tasks: [
          {
            id: "photograph-light-task-1",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva slags bilde ser du?",
              text: "Se på bildet av skolegården. Virker det ekte, KI-generert eller manipulert?",
              imageUrl: "https://placehold.co/1200x720?text=Fotografen+skolegard",
              imageAlt: "Illustrasjon av skolegård med litt rare detaljer",
            },
            introText: "Velg den forklaringen som passer best til bildet du ser.",
            feedbackCorrect: "Riktig. Flere små detaljer ser litt rare ut, noe som ofte skjer i KI-genererte bilder.",
            feedbackIncorrect: "Se ekstra etter detaljer som hender, skilt eller ting i bakgrunnen som ikke helt henger sammen.",
            options: [
              { key: "a", title: "Ekte" },
              { key: "b", title: "KI-generert" },
              { key: "c", title: "Manipulert" },
            ],
            correctChoice: "b",
          },
          {
            id: "photograph-light-task-2",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva ser mest rart ut her?",
              text: "Hvilket tegn hjelper deg mest med å tenke at bildet kan være laget med KI?",
              imageUrl: "https://placehold.co/1200x720?text=Fotografen+skolegard+detaljer",
              imageAlt: "Illustrasjon av skolegård der detaljer ser kunstige ut",
            },
            introText: "Velg det tydeligste tegnet på at bildet ikke virker helt ekte.",
            feedbackCorrect: "Bra observert. Rare hender eller detaljer som ikke passer sammen er vanlige tegn på KI-bilder.",
            feedbackIncorrect: "Tenk på om noe i bildet ser unaturlig ut, som rare hender, skygger eller ting som flyter sammen.",
            options: [
              { key: "a", title: "Noen detaljer, som hender eller skygger, ser unaturlige ut" },
              { key: "b", title: "Det er mange farger i bildet" },
              { key: "c", title: "Bildet viser barn som leker ute" },
            ],
            correctChoice: "a",
          },
          {
            id: "photograph-light-task-3",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva slags bilde ser du nå?",
              text: "Noen har endret et bilde av en fotballkamp. Hvilken type passer best?",
              imageUrl: "https://placehold.co/1200x720?text=Fotografen+fotballkamp",
              imageAlt: "Illustrasjon av fotballbilde som kan være redigert",
            },
            introText: "Velg typen som passer best når et ekte bilde er endret etterpå.",
            feedbackCorrect: "Ja. Når et ekte bilde er endret for å lure deg, er det manipulert.",
            feedbackIncorrect: "Tenk på forskjellen mellom et helt nytt KI-bilde og et ekte bilde som er endret i etterkant.",
            options: [
              { key: "a", title: "Ekte" },
              { key: "b", title: "KI-generert" },
              { key: "c", title: "Manipulert" },
            ],
            correctChoice: "c",
          },
          {
            id: "photograph-light-task-4",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hvorfor virker bildet manipulert?",
              text: "Velg det beste tegnet på at noen har endret bildet med vilje.",
              imageUrl: "https://placehold.co/1200x720?text=Fotografen+fotballkamp+detaljer",
              imageAlt: "Illustrasjon av fotballbilde med redigerte detaljer",
            },
            introText: "Velg begrunnelsen som best passer til et manipulert bilde.",
            feedbackCorrect: "Riktig. Når noe er limt inn eller ikke passer med lys og bakgrunn, er det et tydelig tegn på manipulering.",
            feedbackIncorrect: "Se etter ting som ikke passer inn i resten av bildet, som lys, størrelse eller kanter.",
            options: [
              { key: "a", title: "Noe i bildet passer ikke med lys, størrelse eller bakgrunn" },
              { key: "b", title: "Bildet er tatt ute" },
              { key: "c", title: "Det er mange mennesker på bildet" },
            ],
            correctChoice: "a",
          },
        ],
      }),
      new Exercise({
        uid: "photograph-exercise-2",
        title: "Bildedetektiv",
        entryName: "Middels",
        description: "Sammenlign bilder, still spørsmål ved rare detaljer og øv på å forklare hvorfor et bilde ikke virker troverdig.",
        tasks: [
          {
            id: "photograph-medium-task-1",
            templateKind: "x-or-y",
            prompt: {
              title: "Hvilket bilde virker mest ekte?",
              text: "Begge viser et dyr i byen, men bare ett av dem virker naturlig og troverdig.",
            },
            introText: "Se etter små detaljer som pels, skygger og ting i bakgrunnen.",
            feedbackCorrect: "Bra. Det mest ekte bildet har detaljer som passer bedre sammen og ser mer naturlige ut.",
            feedbackIncorrect: "Se etter om detaljer som øyne, skygger og bakgrunn henger naturlig sammen.",
            options: {
              x: {
                imageUrl: "https://placehold.co/1200x720?text=Bilde+X+ekte+dyr",
                imageAlt: "Illustrasjon av et dyr som ser naturlig ut i bymiljø",
                eyebrow: "Bilde X",
                title: "Rev ved parken",
                text: "Lyset, skyggen og pelsen passer godt sammen med resten av bildet.",
              },
              y: {
                imageUrl: "https://placehold.co/1200x720?text=Bilde+Y+KI+dyr",
                imageAlt: "Illustrasjon av et dyr med unaturlige detaljer i bymiljø",
                eyebrow: "Bilde Y",
                title: "Rev med rare detaljer",
                text: "Pelsen flyter litt inn i bakgrunnen, og noen detaljer ser unaturlige ut.",
              },
            },
            correctChoice: "x",
          },
          {
            id: "photograph-medium-task-2",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hva bør du spørre deg selv om først?",
              text: "Du blir usikker på om et bilde er ekte. Hvilket spørsmål hjelper mest?",
              imageUrl: "https://placehold.co/1200x720?text=Still+sporsmal+om+bildet",
              imageAlt: "Illustrasjon av barn som gransker et bilde nøye",
            },
            introText: "Velg spørsmålet som best hjelper deg å undersøke bildet kritisk.",
            feedbackCorrect: "Ja. Å lete etter rare detaljer er et godt første steg når du vurderer bilder på nett.",
            feedbackIncorrect: "Tenk på hvilket spørsmål som hjelper deg å oppdage om noe ser kunstig eller feil ut.",
            options: [
              { key: "a", title: "Hva ser rart ut her?" },
              { key: "b", title: "Er bildet morsomt?" },
              { key: "c", title: "Har bildet mange farger?" },
            ],
            correctChoice: "a",
          },
          {
            id: "photograph-medium-task-3",
            templateKind: "multiple-choice",
            prompt: {
              title: "Hvordan bør du reagere?",
              text: "Et bilde påstår å vise noe utrolig, men du ser flere rare detaljer. Hva bør du gjøre før du tror på det?",
              imageUrl: "https://placehold.co/1200x720?text=Utrolig+bilde+pa+nett",
              imageAlt: "Illustrasjon av et oppsiktsvekkende bilde på nett",
            },
            introText: "Velg det smarteste neste steget når et bilde virker rart.",
            feedbackCorrect: "Riktig. Det er lurt å stoppe opp og undersøke bildet før du tror på eller deler det.",
            feedbackIncorrect: "Når et bilde virker utrolig eller rart, bør du undersøke mer før du deler det.",
            options: [
              { key: "a", title: "Undersøke mer og være skeptisk" },
              { key: "b", title: "Tro på det med en gang" },
              { key: "c", title: "Sende det videre uten å tenke" },
            ],
            correctChoice: "a",
          },
        ],
      }),
    ];
  }

  static findByUid(uid: string): Exercise | null {
    return this.mockData().find((exercise) => exercise.uid === uid) ?? null;
  }

  static findByPublicId(publicId: string): Exercise | null {
    return this.mockData().find((exercise) => exercise.publicId === publicId) ?? null;
  }

  static findByUids(uids: string[]): Exercise[] {
    return uids
      .map((uid) => this.findByUid(uid))
      .filter((exercise): exercise is Exercise => exercise !== null);
  }

  static backendFetchAllExercises(): Exercise[] {
    return this.mockData();
  }

  static backendFetchExerciseByUid(uid: string): Exercise | null {
    return this.findByUid(uid);
  }

  static backendFetchExerciseByPublicId(publicId: string): Exercise | null {
    return this.findByPublicId(publicId);
  }

  static backendFetchExercisesByUids(uids: string[]): Exercise[] {
    return this.findByUids(uids);
  }
}
