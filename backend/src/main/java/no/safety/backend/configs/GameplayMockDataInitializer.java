package no.safety.backend.configs;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.excercise.repository.ExerciseRepository;
import no.safety.backend.notepad.model.NotepadEntry;
import no.safety.backend.notepad.model.NotepadEntryType;
import no.safety.backend.station.model.Station;
import no.safety.backend.station.repository.StationRepository;
import no.safety.backend.task.content.EmailHotspotsContent;
import no.safety.backend.task.content.MultipleChoiceContent;
import no.safety.backend.task.content.PasswordStrengthContent;
import no.safety.backend.task.content.SortIntoBucketsContent;
import no.safety.backend.task.content.XOrYContent;
import no.safety.backend.task.dto.TaskRequest;
import no.safety.backend.task.model.Task;
import no.safety.backend.world.model.World;
import no.safety.backend.world.repository.WorldRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GameplayMockDataInitializer implements ApplicationRunner {

  public static final UUID SCHOOLYARD_WORLD_UID = stableUuid("world:schoolyard");
  public static final UUID QUICKSTART_WORLD_UID = stableUuid("world:quickstart");
  public static final UUID CREATIVE_LAB_WORLD_UID = stableUuid("world:creative-lab");

  public static final UUID NEWSSTAND_STATION_UID = stableUuid("station:newsstand");
  public static final UUID PHOTOGRAPH_STATION_UID = stableUuid("station:photograph");
  public static final UUID POSTOFFICE_STATION_UID = stableUuid("station:postoffice");
  public static final UUID PASSWORDBANK_STATION_UID = stableUuid("station:passwordbank");
  public static final UUID MARKETPLACE_STATION_UID = stableUuid("station:marketplace");
  public static final UUID SOCIALMEDIA_STATION_UID = stableUuid("station:socialmedia");
  public static final UUID DATACENTER_STATION_UID = stableUuid("station:datacenter");

  public static final UUID NEWSSTAND_EXERCISE_1_UID = stableUuid("exercise:newsstand-exercise-1");
  public static final UUID NEWSSTAND_EXERCISE_2_UID = stableUuid("exercise:newsstand-exercise-2");
  public static final UUID PHOTOGRAPH_EXERCISE_1_UID = stableUuid("exercise:photograph-exercise-1");
  public static final UUID PHOTOGRAPH_EXERCISE_2_UID = stableUuid("exercise:photograph-exercise-2");
  public static final UUID POSTOFFICE_EXERCISE_1_UID = stableUuid("exercise:postoffice-exercise-1");
  public static final UUID POSTOFFICE_EXERCISE_2_UID = stableUuid("exercise:postoffice-exercise-2");
  public static final UUID PASSWORDBANK_EXERCISE_1_UID = stableUuid("exercise:passwordbank-exercise-1");
  public static final UUID PASSWORDBANK_EXERCISE_2_UID = stableUuid("exercise:passwordbank-exercise-2");
  public static final UUID MARKETPLACE_EXERCISE_1_UID = stableUuid("exercise:marketplace-exercise-1");
  public static final UUID MARKETPLACE_EXERCISE_2_UID = stableUuid("exercise:marketplace-exercise-2");
  public static final UUID SOCIALMEDIA_EXERCISE_1_UID = stableUuid("exercise:socialmedia-exercise-1");
  public static final UUID SOCIALMEDIA_EXERCISE_2_UID = stableUuid("exercise:socialmedia-exercise-2");
  public static final UUID DATACENTER_EXERCISE_UID = stableUuid("exercise:datacenter-exercise");

  private static final String PLACEHOLDER_IMAGE = "https://placehold.co/1200x720?text=Nettdetektivene";

  private final ExerciseRepository exerciseRepository;
  private final StationRepository stationRepository;
  private final WorldRepository worldRepository;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    seedExercises();
    seedStations();
    seedWorlds();
  }

  private void seedExercises() {
    upsertExercise(NEWSSTAND_EXERCISE_1_UID, "Troverdige nyheter", "Lett",
        "Undersøk hvilke nyhetssaker som virker ekte og hvilke faresignaler du bør sjekke.",
        List.of(
            multipleChoiceTask(
                "Velg den forklaringen som best viser hvorfor saken virker trygg å stole på.",
                "Bra observert. En tydelig avsender gjør det lettere å sjekke hvem som står bak informasjonen.",
                "Se etter tegn som hjelper deg å sjekke saken, som tydelig avsender og konkret informasjon.",
                "Hvilken forklaring passer best?",
                "Se på skjermbildet og velg det beste tegnet på at saken virker troverdig.",
                "https://placehold.co/1200x720?text=NRK%3A+Skolen+endrer+rutiner",
                "Skjermbilde av en rolig nyhetssak med tydelig avsender",
                List.of(
                    new MultipleChoiceContent.Option("a", "Saken har en tydelig avsender du kan sjekke"),
                    new MultipleChoiceContent.Option("b", "Saken bruker mange utropstegn og sterke ord")
                ),
                "a"
            ),
            xOrYTask(
                "Sammenlign de to overskriftene og velg den som virker mest troverdig.",
                "Bra. Den troverdige saken bruker rolig språk og gir deg noe konkret å sjekke.",
                "Se etter rolig språk, tydelig avsender og konkrete opplysninger.",
                "Hvilken overskrift virker tryggest?",
                "Velg saken du ville stolt mest på ved første blikk.",
                "https://placehold.co/1200x720?text=VG%3A+vinn+5+millioner+na",
                "Dramatisk overskrift med sterke ord og uklar avsender",
                "Sak X",
                "Sjokkerende avsløring uten kilde",
                "Overskriften prøver å stresse deg, men forklarer ikke hvem som vet dette.",
                "https://placehold.co/1200x720?text=Kommunen%3A+ny+regel+for+skoleveien",
                "Rolig overskrift med tydelig avsender",
                "Sak Y",
                "Kommunen forklarer ny regel for skoleveien",
                "Overskriften sier hvem som informerer og hva saken handler om.",
                XOrYContent.Choice.Y
            ),
            sortIntoBucketsTask(
                "Sorter tegnene i riktig boks.",
                "Riktig. Du skilte mellom gode tegn og faresignaler i en nyhetssak.",
                "Se en gang til på hvilke kort som hjelper deg a stole på saken, og hvilke som gjør deg mer skeptisk.",
                "Hva bør du stole på?",
                "Dra kortene til riktig kategori.",
                "Kort som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("safe", "Trygge tegn", "Tegn som styrker troverdigheten.", "#d9fbe6"),
                    new SortIntoBucketsContent.Bucket("warn", "Faresignaler", "Tegn som bør sjekkes ekstra.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("news-1", "Tydelig avsender", "safe", "Da kan du sjekke hvem som står bak."),
                    new SortIntoBucketsContent.Item("news-2", "Konkret kilde", "safe", "Kilder gjør det lettere a kontrollere innholdet."),
                    new SortIntoBucketsContent.Item("news-3", "Sjokk-overskrift", "warn", "Sterke ord kan brukes for å lure deg til å klikke."),
                    new SortIntoBucketsContent.Item("news-4", "Ingen forklaring på hvor informasjonen kommer fra", "warn", "Når kilden mangler, bør du være ekstra skeptisk.")
                )
            ),
            multipleChoiceTask(
                "Se på skjermbildet og velg den beste grunnen til at saken virker falsk.",
                "Riktig. Når en overskrift prøver å sjokkere og du ikke kjenner avsenderen, bør du stoppe opp og sjekke mer.",
                "Tenk på hvilke tegn som faktisk svekker troverdigheten, ikke bare hvordan saken ser ut visuelt.",
                "Hvorfor virker denne saken falsk?",
                "Velg forklaringen som passer best til det du ser i skjermbildet.",
                "https://placehold.co/1200x720?text=KlikkNytt%3A+sjokkfunn+i+kantina",
                "Skjermbilde av en tvilsom nyhetsartikkel",
                List.of(
                    new MultipleChoiceContent.Option("a", "Den har en tydelig avsender og gode kilder"),
                    new MultipleChoiceContent.Option("b", "Overskriften prover a sjokkere og avsenderen virker ukjent"),
                    new MultipleChoiceContent.Option("c", "Bildet er stort og fargerikt")
                ),
                "b"
            )
        )
    );

    upsertExercise(NEWSSTAND_EXERCISE_2_UID, "Nyhetsdetektiv", "Middels",
        "Sammenlign nyhetssaker og finn ut hva du bør sjekke før du tror eller deler.",
        List.of(
            multipleChoiceTask(
                "Velg saken som virker mest pålitelig når du ser på språk, avsender og forklaringer.",
                "Riktig. Den gode saken forklarer hva som har skjedd og peker til en kjent avsender.",
                "Se etter kjent avsender, konkret sprak og om saken forklarer hvor informasjonen kommer fra.",
                "Hva er det tydeligste faresignalet?",
                "Saken ser profesjonell ut, men ett tegn gjør at du bør være ekstra skeptisk.",
                "https://placehold.co/1200x720?text=Nyhetsrask%3A+hemmelig+rapport+lekket",
                "Skjermbilde av en nyhetsartikkel uten tydelige kilder",
                List.of(
                    new MultipleChoiceContent.Option("a", "Den bruker et bilde ved siden av teksten"),
                    new MultipleChoiceContent.Option("b", "Den mangler tydelige kilder eller forklaringer"),
                    new MultipleChoiceContent.Option("c", "Den er skrevet om et tema mange bryr seg om")
                ),
                "b"
            ),
            xOrYTask(
                "Velg den saken som virker mest troverdig når du sammenligner avsender og forklaring.",
                "Bra. Den beste saken gir deg nok informasjon til at du kan sjekke mer selv.",
                "Se etter hvem som står bak og om saken forklarer hvor opplysningene kommer fra.",
                "Hvilken sak ville du sjekket videre?",
                "Velg den som virker mest ryddig og palitelig.",
                "https://placehold.co/1200x720?text=TikTok%3A+hemmelig+tips+gar+viralt",
                "Nyhetssak med vag overskrift og ingen tydelig kilde",
                "Sak X",
                "Hemmelig tips skaper kaos",
                "Saken bruker sterke ord, men sier lite om hvem som faktisk vet noe.",
                "https://placehold.co/1200x720?text=Lokalavisa%3A+forklarer+hendelsen",
                "Nyhetssak med rolig overskrift og tydelig kilde",
                "Sak Y",
                "Lokalavis forklarer hendelsen ved idrettshallen",
                "Saken viser til vitner og forklarer hva som skjedde uten a overdrive.",
                XOrYContent.Choice.Y
            ),
            multipleChoiceTask(
                "Velg det beste neste steget når du blir usikker på om en nyhet stemmer.",
                "Ja. Det tryggeste er å sjekke kilden og se om andre troverdige steder skriver det samme.",
                "Tenk på hva som hjelper deg å undersøke saken, ikke hva som sprer den raskest.",
                "Hva bør du gjøre etterpå?",
                "Du er usikker på om saken stemmer. Velg det smarteste neste steget.",
                "https://placehold.co/1200x720?text=Faktisk%3A+slik+sjekker+du+kilder",
                "Illustrasjon av et barn som sammenligner flere nyhetskilder",
                List.of(
                    new MultipleChoiceContent.Option("a", "Sjekke om andre troverdige kilder sier det samme"),
                    new MultipleChoiceContent.Option("b", "Dele saken videre og be andre vurdere den"),
                    new MultipleChoiceContent.Option("c", "Tro pa saken hvis bildet ser ekte ut")
                ),
                "a"
            )
        )
    );

    upsertExercise(PHOTOGRAPH_EXERCISE_1_UID, "Bildeblikk", "Lett",
        "Laer a kjenne igjen ekte, KI-genererte og manipulerte bilder.",
        List.of(
            multipleChoiceTask(
                "Se pa bildet og velg den beste forklaringen.",
                "Bra. Rare detaljer i hender, tekst eller bakgrunn kan avslore at bildet ikke er ekte.",
                "Se etter rare detaljer som ikke passer naturlig sammen.",
                "Hva slags bilde ser du?",
                "Velg den forklaringen som passer best til bildet.",
                "https://placehold.co/1200x720?text=Insta%3A+robotkatt+pa+skoleplassen",
                "Skjermbilde av et mistenkelig bilde",
                List.of(
                    new MultipleChoiceContent.Option("a", "Et helt vanlig ekte bilde"),
                    new MultipleChoiceContent.Option("b", "Et bilde med detaljer som virker manipulerte")
                ),
                "b"
            ),
            xOrYTask(
                "Sammenlign to bilder og velg det som virker mest ekte.",
                "Bra. Det ekte bildet har detaljer som passer bedre naturlig sammen.",
                "Se etter rare hender, skjev tekst eller bakgrunner som ikke henger sammen.",
                "Hvilket bilde virker mest ekte?",
                "Velg bildet du stoler mest pa etter a ha sett pa detaljene.",
                "https://placehold.co/1200x720?text=Snap%3A+klassebilde+med+rare+hender",
                "Bilde med unaturlige hender og forvrengt tekst",
                "Bilde X",
                "Mange rare detaljer",
                "Flere detaljer ser ut som de ikke passer sammen naturlig.",
                "https://placehold.co/1200x720?text=Skolefoto%3A+turdag+i+parken",
                "Bilde av en vanlig skolegard med naturlige detaljer",
                "Bilde Y",
                "Naturlig skolemiljo",
                "Detaljene ser jevne og naturlige ut.",
                XOrYContent.Choice.Y
            ),
            sortIntoBucketsTask(
                "Sorter detaljene du oppdager i bildet.",
                "Riktig. Du fant hvilke detaljer som virker naturlige og hvilke som bor sjekkes ekstra.",
                "Se etter hvilke detaljer som ser vanlige ut, og hvilke som virker rare eller feil.",
                "Hva skurrer i bildet?",
                "Legg hvert kort i riktig boks.",
                "Detaljer som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("ok", "Ser naturlig ut", "Detaljer som ikke vekker mistanke.", "#dbeafe"),
                    new SortIntoBucketsContent.Bucket("check", "Bor sjekkes", "Detaljer som kan avslore manipulering.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("photo-1", "Rett skilt med lesbar tekst", "ok", "Lesbar tekst er et godt tegn."),
                    new SortIntoBucketsContent.Item("photo-2", "Hand med seks fingre", "check", "Rare hender er et vanlig faresignal."),
                    new SortIntoBucketsContent.Item("photo-3", "Skygger som peker samme vei", "ok", "Like skygger passer bedre med ekte lys."),
                    new SortIntoBucketsContent.Item("photo-4", "Bakgrunn som flyter inn i jakken", "check", "Nar former glir sammen, kan bildet vaere manipulert.")
                )
            )
        )
    );

    upsertExercise(PHOTOGRAPH_EXERCISE_2_UID, "Bildedetektiv", "Middels",
        "Sammenlign bilder og oppdag tegn pa at noe er generert eller manipulert.",
        List.of(
            multipleChoiceTask(
                "Velg det smarteste sporsmalet a starte med.",
                "Ja. Start med a se etter detaljer som ser unaturlige eller feil ut.",
                "Tenk pa hva som hjelper deg a undersoke bildet i stedet for a gjette raskt.",
                "Hva bor du sporre deg selv om forst?",
                "Du ser et bilde som virker litt rart. Hva er det beste forste sporsmalet?",
                "https://placehold.co/1200x720?text=Bildenytt%3A+ekte+eller+KI",
                "Skjermbilde av et bilde med rare detaljer",
                List.of(
                    new MultipleChoiceContent.Option("a", "Ser detaljer som hender, tekst og bakgrunn naturlige ut?"),
                    new MultipleChoiceContent.Option("b", "Har bildet mange fine farger?")
                ),
                "a"
            ),
            xOrYTask(
                "Velg bildet som virker mest troverdig etter at du har studert detaljene.",
                "Bra. Det tryggeste valget er bildet med mest naturlige detaljer og minst rare feil.",
                "Se etter tekst, lys, ansikter og bakgrunner som henger sammen.",
                "Hvilket bilde ville du stolt mest pa?",
                "Sammenlign de to bildene og velg det mest troverdige.",
                "https://placehold.co/1200x720?text=Insta%3A+premieutdeling+med+skjev+tekst",
                "Bilde med ujevn tekst og unaturlig lys",
                "Bilde X",
                "Mange sma feil",
                "Teksten er skjev og lyset passer ikke helt med resten av scenen.",
                "https://placehold.co/1200x720?text=Skolefoto%3A+klasse+7B+pa+tur",
                "Klassebilde med naturlige detaljer",
                "Bilde Y",
                "Ryddig og naturlig",
                "Ansikter, bakgrunn og skygger virker mer naturlige sammen.",
                XOrYContent.Choice.Y
            ),
            multipleChoiceTask(
                "Velg den beste forklaringen pa hvorfor du bor vaere skeptisk til et bilde.",
                "Riktig. Nar flere detaljer ikke henger sammen, bor du stoppe opp og sjekke bildet mer.",
                "Se etter forklaringen som peker pa unaturlige detaljer, ikke bare stil eller farger.",
                "Hva er det tydeligste faresignalet?",
                "Et bilde kan virke ekte ved forste blikk. Velg tegnet som bor fa deg til a sjekke mer.",
                "https://placehold.co/1200x720?text=Bildepost%3A+se+detaljene+forst",
                "Illustrasjon av et barn som gransker et bilde",
                List.of(
                    new MultipleChoiceContent.Option("a", "Hender, tekst og bakgrunn passer ikke helt sammen"),
                    new MultipleChoiceContent.Option("b", "Bildet bruker klare farger"),
                    new MultipleChoiceContent.Option("c", "Det er flere personer i bildet")
                ),
                "a"
            )
        )
    );

    upsertExercise(POSTOFFICE_EXERCISE_1_UID, "Mistenkelige e-poster", "Lett",
        "Ov pa a avslore falske e-poster, mistenkelige lenker og rare avsendere.",
        List.of(
            multipleChoiceTask(
                "Velg det tydeligste faresignalet i meldingen.",
                "Riktig. En rar avsenderadresse er et tydelig faresignal.",
                "Se etter noe som hjelper deg a oppdage om meldingen kan vaere falsk.",
                "Finn den ene tingen som virker rar",
                "Se pa e-posten og velg det som skurrer mest.",
                "https://placehold.co/1200x720?text=Skole-IT%3A+bekreft+kontoen+din",
                "Skjermbilde av en mistenkelig e-post",
                List.of(
                    new MultipleChoiceContent.Option("a", "Avsenderadressen ser rar ut"),
                    new MultipleChoiceContent.Option("b", "Meldingen er kort")
                ),
                "a"
            ),
            emailHotspotsTask(
                "Trykk pa de delene av e-posten som virker mistenkelige.",
                "Bra jobbet. Du fant faresignalene i meldingen.",
                "Se ekstra pa avsenderadressen og lenken i meldingen.",
                "Finn faresignalene",
                "Trykk pa det som virker utrygt i e-posten.",
                new EmailHotspotsContent.Email(
                    "Skole-IT",
                    "sender-name",
                    "hjelp@skole-login-alert.net",
                    "sender-address",
                    "Logg inn pa nytt i dag",
                    "subject",
                    "IT",
                    "I dag 08:15",
                    "received",
                    "Hei!",
                    "greeting",
                    List.of(
                        new EmailHotspotsContent.TextBlock("body-text", "Vi ma sjekke kontoen din med en gang.", "body-text-target"),
                        new EmailHotspotsContent.LinkBlock("body-link", "Bekreft kontoen", "skole-login-check.click", "Svar innen 20 minutter", "body-link-target")
                    ),
                    new EmailHotspotsContent.ActionButton("Logg inn", "skolenorge.no", "action-button"),
                    List.of(new EmailHotspotsContent.Attachment("attachment", "info.pdf", "Vanlig vedlegg", "attachment-target")),
                    "Hilsen supportteamet",
                    "closing"
                ),
                List.of(
                    new EmailHotspotsContent.Target("sender-name", false),
                    new EmailHotspotsContent.Target("sender-address", true),
                    new EmailHotspotsContent.Target("subject", false),
                    new EmailHotspotsContent.Target("received", false),
                    new EmailHotspotsContent.Target("greeting", false),
                    new EmailHotspotsContent.Target("body-text-target", false),
                    new EmailHotspotsContent.Target("body-link-target", true),
                    new EmailHotspotsContent.Target("action-button", false),
                    new EmailHotspotsContent.Target("attachment-target", false),
                    new EmailHotspotsContent.Target("closing", false)
                )
            ),
            xOrYTask(
                "Sammenlign de to meldingene og velg den som virker tryggest.",
                "Riktig. Den tryggeste meldingen bruker kjent avsender og en tydelig nettadresse.",
                "Se etter kjent avsender, rolig sprak og lenker du kjenner igjen.",
                "Hvilken e-post virker tryggest?",
                "Velg meldingen du ville stolt mest pa.",
                "https://placehold.co/1200x720?text=Skole-IT%3A+kontoen+stenges+na",
                "Mistenkelig e-post med stressende sprak",
                "Melding X",
                "Handle na",
                "Meldingen presser deg til a klikke med en gang og bruker en rar avsender.",
                "https://placehold.co/1200x720?text=Skolen%3A+ukebrev+for+fredag",
                "Vanlig skoleepost med tydelig avsender",
                "Melding Y",
                "Vanlig informasjon",
                "Meldingen forklarer rolig hva den handler om og kommer fra en kjent avsender.",
                XOrYContent.Choice.Y
            )
        )
    );

    upsertExercise(POSTOFFICE_EXERCISE_2_UID, "E-postdetektiv", "Middels",
        "Finn flere faresignaler i meldinger som ser nesten ekte ut.",
        List.of(
            multipleChoiceTask(
                "Velg den beste forklaringen pa hvorfor meldingen ikke virker trygg.",
                "Bra. Nar meldingen presser deg til a handle raskt og lenken virker rar, bor du stoppe opp.",
                "Se etter press, rare lenker eller avsendere du ikke kjenner.",
                "Finn de to farlige delene",
                "Hva er den beste forklaringen pa at denne meldingen er utrygg?",
                "https://placehold.co/1200x720?text=Admin%3A+viktig+skjema+i+dag",
                "Skjermbilde av en mistenkelig e-post med lenke",
                List.of(
                    new MultipleChoiceContent.Option("a", "Den bruker press og en lenke som virker rar"),
                    new MultipleChoiceContent.Option("b", "Den har skolens logo"),
                    new MultipleChoiceContent.Option("c", "Den nevner et kjent tema")
                ),
                "a"
            ),
            emailHotspotsTask(
                "Finn alle faresignalene i meldingen som ser nesten ekte ut.",
                "Bra. Du oppdaget flere ting som gjorde meldingen utrygg.",
                "Se etter press, rare lenker og vedlegg du ikke forventet.",
                "Hva skurrer i e-posten?",
                "Trykk pa alle delene som virker mistenkelige.",
                new EmailHotspotsContent.Email(
                    "Skolekontoret",
                    "sender-name-2",
                    "beskjed@skole-konto-verify.biz",
                    "sender-address-2",
                    "Viktig melding om skolekontoen din",
                    "subject-2",
                    "SK",
                    "I dag 09:40",
                    "received-2",
                    "Hei elev!",
                    "greeting-2",
                    List.of(
                        new EmailHotspotsContent.TextBlock("body-text-2", "Hvis du ikke svarer innen 10 minutter, blir kontoen din sperret.", "body-text-target-2"),
                        new EmailHotspotsContent.LinkBlock("body-link-2", "Aapne skolekonto", "secure-school-check.biz/login", "Maa gjores i dag", "body-link-target-2")
                    ),
                    new EmailHotspotsContent.ActionButton("Les mer", "skoleportalen.no", "action-button-2"),
                    List.of(new EmailHotspotsContent.Attachment("attachment-2", "skjema.zip", "Uventet komprimert fil", "attachment-target-2")),
                    "Hilsen administrasjonen",
                    "closing-2"
                ),
                List.of(
                    new EmailHotspotsContent.Target("sender-name-2", false),
                    new EmailHotspotsContent.Target("sender-address-2", true),
                    new EmailHotspotsContent.Target("subject-2", false),
                    new EmailHotspotsContent.Target("received-2", false),
                    new EmailHotspotsContent.Target("greeting-2", false),
                    new EmailHotspotsContent.Target("body-text-target-2", true),
                    new EmailHotspotsContent.Target("body-link-target-2", true),
                    new EmailHotspotsContent.Target("action-button-2", false),
                    new EmailHotspotsContent.Target("attachment-target-2", true),
                    new EmailHotspotsContent.Target("closing-2", false)
                )
            ),
            sortIntoBucketsTask(
                "Sorter tegnene fra meldingen i riktig boks.",
                "Riktig. Du skilte mellom trygge tegn og faresignaler i e-posten.",
                "Se pa hvilke kort som viser press eller rare detaljer, og hvilke som virker vanlige.",
                "Trygt tegn eller faresignal?",
                "Legg hvert kort i riktig kategori.",
                "Kort som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("safe-mail", "Trygge tegn", "Detaljer som ofte er normale.", "#d9fbe6"),
                    new SortIntoBucketsContent.Bucket("danger-mail", "Faresignaler", "Detaljer som bor fa deg til a stoppe opp.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("mail-1", "Kjent avsender fra skolen", "safe-mail", "En kjent avsender er lettere a kontrollere."),
                    new SortIntoBucketsContent.Item("mail-2", "Lenke med rar nettadresse", "danger-mail", "Rare nettadresser er et vanlig faresignal."),
                    new SortIntoBucketsContent.Item("mail-3", "Press om a svare med en gang", "danger-mail", "Stress brukes ofte for a fa deg til a handle fort."),
                    new SortIntoBucketsContent.Item("mail-4", "Rolig forklaring pa hva meldingen handler om", "safe-mail", "Tydelig informasjon gjor meldingen lettere a vurdere.")
                )
            )
        )
    );

    upsertExercise(PASSWORDBANK_EXERCISE_1_UID, "Passorddetektiv", "Lett",
        "Laer hva som gjor et passord sterkt, trygt og lett a huske.",
        List.of(
            multipleChoiceTask(
                "Velg forklaringen som best viser hvorfor passordet er svakt.",
                "Riktig. Navn og arstall er lett a gjette.",
                "Tenk pa hva andre lett kan vite om deg.",
                "Hva gjor passordet utrygt?",
                "Se pa passordet og velg den beste forklaringen.",
                "https://placehold.co/1200x720?text=Login%3A+Ola2015+avslort",
                "Illustrasjon av et svakt passord",
                List.of(
                    new MultipleChoiceContent.Option("a", "Det bruker navn og arstall som andre kan kjenne til"),
                    new MultipleChoiceContent.Option("b", "Det starter med stor bokstav")
                ),
                "a"
            ),
            passwordStrengthTask(
                "Skriv et passord som oppfyller kravene i oppgaven.",
                "Bra jobbet. Passordet ditt var langt nok og vanskeligere a gjette.",
                "Proov igjen med flere tegn og en bedre blanding av bokstaver, tall og symboler.",
                "Lag et sterkere passord",
                "Lag et passord som ikke er lett a gjette.",
                "Bruk minst 10 tegn og bland flere typer tegn.",
                PasswordStrengthContent.Mode.CREATE,
                "Skriv passordet her",
                null,
                10,
                4,
                List.of("123", "qwerty", "passord", "ola")
            ),
            sortIntoBucketsTask(
                "Sorter passord-vanene i riktig boks.",
                "Riktig. Du skilte mellom vaner som styrker passordet og vaner som gjor det svakere.",
                "Se pa hvilke valg som gjor passordet lett eller vanskelig a gjette.",
                "Sterk vane eller svak vane?",
                "Legg kortene i riktig kategori.",
                "Vaner som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("strong", "Sterk vane", "Valg som gjor passord tryggere.", "#d9fbe6"),
                    new SortIntoBucketsContent.Bucket("weak", "Svak vane", "Valg som gjor passord lettere a gjette.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("pw-1", "Bruke flere ord og symbol", "strong", "Lengre og mer variert er ofte tryggere."),
                    new SortIntoBucketsContent.Item("pw-2", "Bruke eget navn og fodselsar", "weak", "Dette er lett a gjette."),
                    new SortIntoBucketsContent.Item("pw-3", "Lage ulike passord til ulike kontoer", "strong", "Da blir ikke alle kontoene saret hvis ett passord lekker."),
                    new SortIntoBucketsContent.Item("pw-4", "Velge passordet 123456", "weak", "Vanlige monster er enkle a gjette.")
                )
            )
        )
    );

    upsertExercise(PASSWORDBANK_EXERCISE_2_UID, "Passordmester", "Middels",
        "Laer mer om passord-gjenbruk og hvordan du lager sterkere passord.",
        List.of(
            multipleChoiceTask(
                "Velg den beste forklaringen pa hvorfor passord-gjenbruk er dumt.",
                "Ja. Hvis ett passord lekker, kan samme passord proves pa flere kontoer.",
                "Tenk pa hva som skjer hvis noen far tak i ett av passordene dine.",
                "Hvorfor er passord-gjenbruk dumt?",
                "Velg den beste forklaringen.",
                "https://placehold.co/1200x720?text=Passordnytt%3A+ett+passord+overalt",
                "Illustrasjon av samme passord pa flere kontoer",
                List.of(
                    new MultipleChoiceContent.Option("a", "Da kan noen prove samme passord pa flere kontoer"),
                    new MultipleChoiceContent.Option("b", "Da blir passordet lettere a skrive")
                ),
                "a"
            ),
            passwordStrengthTask(
                "Forbedre startpassordet sa det blir mye tryggere.",
                "Bra. Du gjorde startpassordet vanskeligere a gjette og bedre a bruke.",
                "Du ma bygge videre pa startpassordet og gjore det sterkere med flere tegn og variasjon.",
                "Forbedre passordet",
                "Gjor startpassordet tryggere uten a bruke det helt alene.",
                "Behold litt av startpassordet, men gjor det lengre og sterkere.",
                PasswordStrengthContent.Mode.IMPROVE,
                "Skriv det forbedrede passordet her",
                "sommer2026",
                12,
                4,
                List.of("123", "qwerty", "passord")
            ),
            xOrYTask(
                "Velg passordet som virker sterkest.",
                "Riktig. Det sterkeste passordet er lengre og mindre lett a gjette.",
                "Se etter lengde, variasjon og om passordet bygger pa vanlig info om deg.",
                "Hvilket passord er smartest?",
                "Sammenlign de to forslagene og velg det tryggeste.",
                "https://placehold.co/1200x720?text=Passordforslag%3A+Ola2015",
                "Kort passord med navn og arstall",
                "Forslag X",
                "Ola2015",
                "Passordet bruker navn og arstall som kan vaere lett a gjette.",
                "https://placehold.co/1200x720?text=Passordforslag%3A+Blomst%21Sykkel9Maanen",
                "Langt passord med flere ord og symbol",
                "Forslag Y",
                "Blomst!Sykkel9Maanen",
                "Passordet er lengre og blander flere typer tegn.",
                XOrYContent.Choice.Y
            )
        )
    );

    upsertExercise(MARKETPLACE_EXERCISE_1_UID, "Trygg handel", "Lett",
        "Laer a vurdere om nettsider og nettbutikker er trygge eller mistenkelige.",
        List.of(
            multipleChoiceTask(
                "Velg det tydeligste faresignalet pa nettsiden.",
                "Riktig. En rar nettadresse er ofte et faresignal.",
                "Se etter noe som gjor at siden virker mindre troverdig.",
                "Hva er det tydeligste faresignalet?",
                "Velg det som skurrer mest.",
                "https://placehold.co/1200x720?text=SuperDeal%3A+spill+til+9+kr",
                "Skjermbilde av en tvilsom nettbutikk",
                List.of(
                    new MultipleChoiceContent.Option("a", "Butikken bruker en rar nettadresse som ikke ser norsk ut"),
                    new MultipleChoiceContent.Option("b", "Produktbildet er stort og fargerikt")
                ),
                "a"
            ),
            xOrYTask(
                "Sammenlign to butikker og velg den som virker mest troverdig.",
                "Bra. Den tryggeste butikken gir deg kontaktinfo og en nettadresse som virker ryddig.",
                "Se etter kjent navn, kontaktinfo og trygg betalingsinformasjon.",
                "Hvilken butikk virker tryggest?",
                "Velg butikken du ville stolt mest pa.",
                "https://placehold.co/1200x720?text=super-billige-spill-deals.biz%3A+90%25+rabatt",
                "Tvilsom butikk med rar nettadresse",
                "Butikk X",
                "Supertilbud pa alt",
                "Butikken lover veldig mye, men nettadressen og informasjonen virker rare.",
                "https://placehold.co/1200x720?text=spillbutikk.no%3A+trygg+bestilling",
                "Ryddig butikk med tydelig informasjon",
                "Butikk Y",
                "Tydelig og ryddig",
                "Butikken viser kontaktinfo og virker lettere a kontrollere.",
                XOrYContent.Choice.Y
            ),
            sortIntoBucketsTask(
                "Sorter tegnene fra nettbutikken i riktig boks.",
                "Riktig. Du fant hvilke tegn som gjor en butikk tryggere og hvilke som vekker mistanke.",
                "Se etter hva som hjelper deg a stole pa butikken, og hva som virker pressende eller uklart.",
                "Trygge tegn eller faresignaler?",
                "Legg hvert kort i riktig kategori.",
                "Kort som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("shop-safe", "Trygge tegn", "Detaljer som styrker troverdigheten.", "#d9fbe6"),
                    new SortIntoBucketsContent.Bucket("shop-warn", "Faresignaler", "Detaljer som bor gjore deg skeptisk.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("shop-1", "Tydelig kontaktinformasjon", "shop-safe", "Kontaktinfo gjor butikken lettere a sjekke."),
                    new SortIntoBucketsContent.Item("shop-2", "Rar nettadresse", "shop-warn", "Rare nettadresser kan vaere et faresignal."),
                    new SortIntoBucketsContent.Item("shop-3", "Trygg betalingslosning", "shop-safe", "Kjent betaling virker ofte mer troverdig."),
                    new SortIntoBucketsContent.Item("shop-4", "Nedtelling som presser deg til a kjope na", "shop-warn", "Press kan brukes for a fa deg til a handle fort.")
                )
            )
        )
    );

    upsertExercise(MARKETPLACE_EXERCISE_2_UID, "Nettbutikksjekk", "Middels",
        "Sammenlign nettbutikker og finn tegn pa om du bor stole pa dem eller ikke.",
        List.of(
            multipleChoiceTask(
                "Velg butikken som virker mest troverdig.",
                "Bra. Kjent navn, ryddig informasjon og trygg betaling er gode tegn.",
                "Se etter tydelig informasjon og trygg betaling, ikke bare tilbudspris.",
                "Hvilken butikk virker mest troverdig?",
                "Velg butikken du ville stolt mest pa.",
                "https://placehold.co/1200x720?text=Butikksjekk%3A+to+spillbutikker",
                "Sammenligning av to nettbutikker",
                List.of(
                    new MultipleChoiceContent.Option("a", "Spillbutikk.no med tydelig kontaktinfo og kjent betaling"),
                    new MultipleChoiceContent.Option("b", "Superdeal-game-click.biz med pressende tilbud")
                ),
                "a"
            ),
            multipleChoiceTask(
                "Velg den beste grunnen til at en butikk virker tryggere enn en annen.",
                "Riktig. Kontaktinfo, kjent betaling og ryddig informasjon er bedre tegn enn bare lave priser.",
                "Se etter konkrete ting du kan kontrollere, ikke bare hvor billig varen er.",
                "Hva styrker tilliten mest?",
                "To butikker selger samme vare. Velg det tegnet som gjor en av dem mest troverdig.",
                "https://placehold.co/1200x720?text=Spillbutikk%3A+kontaktinfo+og+Vipps",
                "Illustrasjon av trygg nettbutikk med kontaktinfo",
                List.of(
                    new MultipleChoiceContent.Option("a", "Butikken viser tydelig kontaktinfo og kjent betalingsmate"),
                    new MultipleChoiceContent.Option("b", "Butikken lover at tilbudet varer bare i to minutter"),
                    new MultipleChoiceContent.Option("c", "Butikken bruker veldig sterke farger")
                ),
                "a"
            ),
            xOrYTask(
                "Velg kassen som virker mest trygg nar du skal betale.",
                "Bra. Den tryggeste kassen forklarer betalingen tydelig og virker mindre stressende.",
                "Se etter tydelig betaling, ryddig informasjon og minst mulig press.",
                "Hvilken betaling virker tryggest?",
                "Velg kassen du ville brukt.",
                "https://placehold.co/1200x720?text=Kasse%3A+Vipps+na+ellers+mister+du+varen",
                "Kasseside med pressende tekst og lite informasjon",
                "Kasse X",
                "Handle fort",
                "Siden stresser deg og gir lite informasjon om hvem du betaler til.",
                "https://placehold.co/1200x720?text=Kasse%3A+ordrebekreftelse+og+kontaktinfo",
                "Ryddig kasseside med kontaktinfo og ordresammendrag",
                "Kasse Y",
                "Ryddig betaling",
                "Siden viser hva du kjoper og hvordan du kan kontakte butikken.",
                XOrYContent.Choice.Y
            )
        )
    );

    upsertExercise(SOCIALMEDIA_EXERCISE_1_UID, "Tenk for du deler", "Lett",
        "Laer a tenke for du deler og oppdage manipulering og gruppepress.",
        List.of(
            multipleChoiceTask(
                "Velg den smarteste reaksjonen nar et innlegg prover a stresse deg.",
                "Riktig. Det er lurt a stoppe opp og sjekke for du deler videre.",
                "Tenk pa hva som hjelper deg a ta et tryggere valg.",
                "Hva bor du gjore forst?",
                "Du ser et innlegg som gjor deg usikker. Hva er smartest?",
                "https://placehold.co/1200x720?text=TikTok%3A+alle+ma+dele+dette",
                "Skjermbilde av et stressende innlegg i sosiale medier",
                List.of(
                    new MultipleChoiceContent.Option("a", "Stoppe opp og sjekke innholdet for du deler"),
                    new MultipleChoiceContent.Option("b", "Dele raskt fordi mange andre gjor det")
                ),
                "a"
            ),
            xOrYTask(
                "Sammenlign to innlegg og velg det som virker mest ansvarlig a stole pa.",
                "Bra. Det tryggeste innlegget bruker rolig sprak og presser deg ikke til a handle fort.",
                "Se etter innlegg som forklarer mer og presser mindre.",
                "Hvilket innlegg virker tryggest?",
                "Velg innlegget du ville stolt mest pa.",
                "https://placehold.co/1200x720?text=Snap%3A+DEL+NA+ELLERS+ER+DET+FOR+SENT",
                "Stressende innlegg med sterke ord",
                "Innlegg X",
                "Del na",
                "Innlegget bruker gruppepress og sterke ord for a fa deg til a reagere fort.",
                "https://placehold.co/1200x720?text=Skolen%3A+forklarer+endring+i+timeplanen",
                "Rolig innlegg med forklaring",
                "Innlegg Y",
                "Les for du deler",
                "Innlegget forklarer rolig hva som skjer og virker lettere a sjekke.",
                XOrYContent.Choice.Y
            ),
            sortIntoBucketsTask(
                "Sorter reaksjonene i riktig boks.",
                "Riktig. Du skilte mellom smarte og usmarte valg nar du ser noe i sosiale medier.",
                "Se etter hvilke valg som gir deg tid til a tenke, og hvilke som sprer innholdet raskt videre.",
                "Smart trekk eller dumt trekk?",
                "Legg hvert kort i riktig kategori.",
                "Reaksjoner som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("smart", "Smart trekk", "Valg som hjelper deg a tenke deg om.", "#d9fbe6"),
                    new SortIntoBucketsContent.Bucket("hasty", "Dumt trekk", "Valg som kan spre feilinformasjon raskt.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("social-1", "Sjekke avsenderen for du deler", "smart", "Da undersoker du innholdet forst."),
                    new SortIntoBucketsContent.Item("social-2", "Dele fordi mange andre gjor det", "hasty", "Popularitet betyr ikke at noe er sant."),
                    new SortIntoBucketsContent.Item("social-3", "Stoppe opp hvis innlegget gjor deg stresset", "smart", "Sterke foleser er et tegn pa at du bor tenke deg om."),
                    new SortIntoBucketsContent.Item("social-4", "Trykke del med en gang", "hasty", "Da sprer du innholdet uten a sjekke det.")
                )
            )
        )
    );

    upsertExercise(SOCIALMEDIA_EXERCISE_2_UID, "Del eller vent", "Middels",
        "Laer hvordan du kan reagere smart nar innhold prover a presse fram raske valg.",
        List.of(
            multipleChoiceTask(
                "Velg den beste reaksjonen nar du blir usikker.",
                "Ja. Vent og sjekk er tryggere enn a dele pa impuls.",
                "Tenk pa hvilken handling som gir deg tid til a undersoke mer.",
                "Hvilken reaksjon er smartest?",
                "Du ser et innlegg som mange deler. Hva bor du gjore?",
                "https://placehold.co/1200x720?text=Gruppechat%3A+del+eller+vent",
                "Skjermbilde av et viralt innlegg",
                List.of(
                    new MultipleChoiceContent.Option("a", "Vent og sjekk"),
                    new MultipleChoiceContent.Option("b", "Del med en gang")
                ),
                "a"
            ),
            xOrYTask(
                "Velg reaksjonen som viser best digital drommekraft.",
                "Riktig. Den beste reaksjonen gir deg tid til a sjekke innholdet for du sprer det videre.",
                "Se etter valget som bremser delingen og gir rom for sjekk.",
                "Hva er smartest a gjore?",
                "Velg alternativet som viser mest omtanke.",
                "https://placehold.co/1200x720?text=Gruppechat%3A+del+for+a+advare+alle",
                "Innlegg som oppfordrer til rask deling",
                "Valg X",
                "Del for sikkerhets skyld",
                "Du sprer innholdet videre uten a vite om det stemmer.",
                "https://placehold.co/1200x720?text=Faktasjekk%3A+vent+og+sjekk+forst",
                "Rolig vurdering av innlegg for deling",
                "Valg Y",
                "Vent og sjekk",
                "Du stopper opp og sjekker mer for du reagerer.",
                XOrYContent.Choice.Y
            ),
            multipleChoiceTask(
                "Velg det smarteste neste steget nar et innlegg spiller pa sterke foleser.",
                "Bra. Nar et innlegg gjor deg sint eller redd, er det lurt a roe ned og sjekke kilden.",
                "Tenk pa hva som hjelper deg a undersoke innholdet, ikke bare reagere pa det.",
                "Hva bor du gjore hvis du blir sint?",
                "Du ser et innlegg som prover a gjore deg opprort. Velg det beste neste steget.",
                "https://placehold.co/1200x720?text=Kommentarfelt%3A+stopp+opp+forst",
                "Illustrasjon av et barn som stopper opp for deling",
                List.of(
                    new MultipleChoiceContent.Option("a", "Puste litt og sjekke hvem som star bak innlegget"),
                    new MultipleChoiceContent.Option("b", "Kommentere med en gang mens du er sint"),
                    new MultipleChoiceContent.Option("c", "Dele det i alle gruppechattene dine")
                ),
                "a"
            )
        )
    );

    upsertExercise(DATACENTER_EXERCISE_UID, "Datatyven", "Høy",
        "Bruk alt du har lært for å bekjempe datatyven!",
        List.of(
            multipleChoiceTask(
                "Datatyven sender en falsk nyhet. Velg det tydeligste faresignalet.",
                "Riktig. Mangel pa kilde og sjokkerende sprak er klassiske faresignaler.",
                "Tenk pa hva som hjelper deg a kontrollere om en sak stemmer.",
                "Hva avslorer den falske nyheten?",
                "Datatyven har lagt ut en sak som sprer seg fort. Velg det tydeligste faresignalet.",
                "https://placehold.co/1200x720?text=DATATYVEN+SLAAR+TIL",
                "Skjermbilde av en sjokkerende nyhetssak fra datatyven",
                List.of(
                    new MultipleChoiceContent.Option("a", "Saken har en kjent avsender og tydelige kilder"),
                    new MultipleChoiceContent.Option("b", "Saken bruker sjokk-overskrift og mangler kilder"),
                    new MultipleChoiceContent.Option("c", "Saken er kort og lett a lese")
                ),
                "b"
            ),
            xOrYTask(
                "Datatyven har sendt to bilder. Velg det som virker ekte.",
                "Bra. Det ekte bildet har naturlige detaljer og henger sammen.",
                "Se etter rare hender, skjev tekst eller bakgrunner som ikke passer.",
                "Hvilket bilde er ekte?",
                "Studer detaljene og velg bildet datatyven ikke har laget.",
                "https://placehold.co/1200x720?text=Rare+detaljer",
                "KI-generert bilde med unaturlige detaljer",
                "Bilde X",
                "Mange smaa feil",
                "Hender og tekst ser unaturlige ut.",
                "https://placehold.co/1200x720?text=Naturlig+bilde",
                "Ekte bilde med naturlige detaljer",
                "Bilde Y",
                "Naturlige detaljer",
                "Detaljene henger sammen og virker ekte.",
                XOrYContent.Choice.Y
            ),
            emailHotspotsTask(
                "Datatyven har sendt en e-post. Trykk pa alle faresignalene.",
                "Bra. Du fant lurene datatyven brukte for a stjele kontoen din.",
                "Se ekstra pa avsenderadressen, lenken og uventede vedlegg.",
                "Avslor datatyvens e-post",
                "Trykk pa alt som virker mistenkelig.",
                new EmailHotspotsContent.Email(
                    "Datasenter-support",
                    "boss-sender-name",
                    "support@datasenter-secure-login.click",
                    "boss-sender-address",
                    "Kontoen din blir stengt om 5 minutter",
                    "boss-subject",
                    "DS",
                    "I dag 10:02",
                    "boss-received",
                    "Hei bruker!",
                    "boss-greeting",
                    List.of(
                        new EmailHotspotsContent.TextBlock("boss-body-text", "Klikk lenken under na for at vi ikke skal slette alle filene dine.", "boss-body-text-target"),
                        new EmailHotspotsContent.LinkBlock("boss-body-link", "Bekreft kontoen", "datasenter-login-verify.click", "Maa gjores umiddelbart", "boss-body-link-target")
                    ),
                    new EmailHotspotsContent.ActionButton("Logg inn", "datasenteret.no", "boss-action-button"),
                    List.of(new EmailHotspotsContent.Attachment("boss-attachment", "konto.exe", "Uventet programfil", "boss-attachment-target")),
                    "Hilsen datasenteret",
                    "boss-closing"
                ),
                List.of(
                    new EmailHotspotsContent.Target("boss-sender-name", false),
                    new EmailHotspotsContent.Target("boss-sender-address", true),
                    new EmailHotspotsContent.Target("boss-subject", true),
                    new EmailHotspotsContent.Target("boss-received", false),
                    new EmailHotspotsContent.Target("boss-greeting", false),
                    new EmailHotspotsContent.Target("boss-body-text-target", true),
                    new EmailHotspotsContent.Target("boss-body-link-target", true),
                    new EmailHotspotsContent.Target("boss-action-button", false),
                    new EmailHotspotsContent.Target("boss-attachment-target", true),
                    new EmailHotspotsContent.Target("boss-closing", false)
                )
            ),
            passwordStrengthTask(
                "Lag et nytt sterkt passord for a stenge datatyven ute.",
                "Bra jobbet. Passordet ditt er langt nok og vanskelig a gjette.",
                "Proov igjen med flere tegn og en bedre blanding av bokstaver, tall og symboler.",
                "Lag et sterkt passord",
                "Datatyven prover a gjette passord. Lag et som ikke er lett a knekke.",
                "Bruk minst 12 tegn og bland flere typer tegn.",
                PasswordStrengthContent.Mode.CREATE,
                "Skriv passordet her",
                null,
                12,
                4,
                List.of("123", "qwerty", "passord", "datatyv", "admin")
            ),
            xOrYTask(
                "Datatyven driver to nettbutikker. Velg den trygge.",
                "Riktig. Den trygge butikken har kjent nettadresse og tydelig kontaktinfo.",
                "Se etter kjent nettadresse, kontaktinfo og trygg betaling.",
                "Hvilken butikk er trygg?",
                "Velg butikken du tor a handle i.",
                "https://placehold.co/1200x720?text=mega-deal-store.click",
                "Tvilsom butikk med rar nettadresse og pressende tilbud",
                "Butikk X",
                "Sjokktilbud",
                "Rar nettadresse, ingen kontaktinfo og pressende nedtelling.",
                "https://placehold.co/1200x720?text=trygghandel.no",
                "Ryddig butikk med kontaktinfo og kjent betaling",
                "Butikk Y",
                "Kjent og ryddig",
                "Tydelig kontaktinfo, kjent nettadresse og trygg betalingsmate.",
                XOrYContent.Choice.Y
            ),
            multipleChoiceTask(
                "Datatyven sprer et innlegg som gjor deg sint. Velg den smarteste reaksjonen.",
                "Bra. Stoppe opp og sjekke kilden er det smarteste forsvaret mot manipulering.",
                "Tenk pa hva som hjelper deg a unnga a spre feilinformasjon.",
                "Hva gjor du i sosiale medier?",
                "Du ser et innlegg som prover a fa deg til a dele med en gang.",
                "https://placehold.co/1200x720?text=DEL+NA",
                "Stressende innlegg fra datatyven i sosiale medier",
                List.of(
                    new MultipleChoiceContent.Option("a", "Dele med en gang slik at andre advares"),
                    new MultipleChoiceContent.Option("b", "Stoppe opp, sjekke kilden og vente med a dele"),
                    new MultipleChoiceContent.Option("c", "Kommentere mens du er sint")
                ),
                "b"
            ),
            sortIntoBucketsTask(
                "Datatyven prover mange triks. Sorter dem som faresignal eller trygt tegn.",
                "Riktig. Du gjenkjente datatyvens triks pa tvers av nyheter, e-post, butikker og sosiale medier.",
                "Tenk pa hvilke triks som presser deg, og hvilke tegn som hjelper deg a kontrollere.",
                "Faresignal eller trygt tegn?",
                "Legg hvert kort i riktig kategori.",
                "Triks og tegn fra datatyven",
                List.of(
                    new SortIntoBucketsContent.Bucket("boss-safe", "Trygge tegn", "Tegn som styrker tilliten.", "#d9fbe6"),
                    new SortIntoBucketsContent.Bucket("boss-warn", "Faresignaler", "Triks datatyven bruker for a lure deg.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("boss-1", "Tydelig avsender og kjent kilde", "boss-safe", "Da kan du sjekke hvem som star bak."),
                    new SortIntoBucketsContent.Item("boss-2", "Lenke med rar nettadresse", "boss-warn", "Rare adresser leder ofte til falske sider."),
                    new SortIntoBucketsContent.Item("boss-3", "Press om a handle innen fa minutter", "boss-warn", "Stress brukes for a hindre deg i a tenke."),
                    new SortIntoBucketsContent.Item("boss-4", "Kontaktinfo og kjent betalingsmate", "boss-safe", "Kjent betaling gjor handelen lettere a kontrollere."),
                    new SortIntoBucketsContent.Item("boss-5", "Bilde med rare hender og skjev tekst", "boss-warn", "Et tegn pa at bildet kan vaere KI-generert."),
                    new SortIntoBucketsContent.Item("boss-6", "Rolig sprak som forklarer hva saken handler om", "boss-safe", "Rolig informasjon er lettere a kontrollere.")
                )
            ),
            passwordStrengthTask(
                "Datatyven har sett startpassordet ditt. Forbedre det sa det blir trygt.",
                "Bra. Du gjorde passordet mye vanskeligere a gjette.",
                "Bygg videre med flere tegn og bedre variasjon.",
                "Forbedre passordet",
                "Gjor startpassordet sterkt nok til a stoppe datatyven.",
                "Behold litt av startpassordet, men gjor det lengre og sterkere.",
                PasswordStrengthContent.Mode.IMPROVE,
                "Skriv det forbedrede passordet her",
                "skole2026",
                14,
                4,
                List.of("123", "qwerty", "passord", "datatyv")
            ),
            multipleChoiceTask(
                "Datatyven viser deg et bilde og en e-post sammen. Velg det smarteste forste steget.",
                "Riktig. Sjekk avsenderen og se etter rare detaljer for du gjor noe annet.",
                "Tenk pa hva som hjelper deg a kontrollere om noe er ekte.",
                "Hva gjor du forst?",
                "Datatyven kombinerer flere triks. Velg det smarteste forste steget.",
                "https://placehold.co/1200x720?text=Sjekk+forst",
                "Illustrasjon av en elev som sjekker avsender og bilde",
                List.of(
                    new MultipleChoiceContent.Option("a", "Klikke pa lenken for a se hva som skjer"),
                    new MultipleChoiceContent.Option("b", "Sjekke avsenderen og se etter rare detaljer i bildet"),
                    new MultipleChoiceContent.Option("c", "Videresende meldingen til vennene dine")
                ),
                "b"
            ),
            xOrYTask(
                "Velg den endelige strategien som stopper datatyven.",
                "Riktig. Smart digital dommekraft er a stoppe opp, sjekke kilder og bruke sterke passord.",
                "Tenk pa det du har laert i alle stasjonene: nyheter, bilder, e-post, passord, butikker og sosiale medier.",
                "Hvordan stopper du datatyven?",
                "Velg den beste strategien.",
                "https://placehold.co/1200x720?text=Klikk+og+del+raskt",
                "Risikabel strategi som spiller datatyven i hendene",
                "Strategi X",
                "Klikk og del raskt",
                "Du faller for press og sprer datatyvens innhold videre.",
                "https://placehold.co/1200x720?text=Stopp+sjekk+og+sterkt+passord",
                "Trygg strategi med sjekk og sterke passord",
                "Strategi Y",
                "Stopp, sjekk og bruk sterke passord",
                "Du tar deg tid til a kontrollere kilder, lenker og bilder, og beskytter kontoene dine.",
                XOrYContent.Choice.Y
            )
        )
    );
  }

  private void seedStations() {
    upsertStation(NEWSSTAND_STATION_UID, "Nyhetskvartalet",
        "Nyhetsartikler og kildekritikk for skoleelever.",
        PLACEHOLDER_IMAGE, "#cfe9ff",
        List.of(NEWSSTAND_EXERCISE_1_UID, NEWSSTAND_EXERCISE_2_UID), false);

    upsertStation(PHOTOGRAPH_STATION_UID, "Fotografen",
        "Normal, manipulert og KI-genererte bilder.",
        PLACEHOLDER_IMAGE, "#5F5F5F",
        List.of(PHOTOGRAPH_EXERCISE_1_UID, PHOTOGRAPH_EXERCISE_2_UID), false);

    upsertStation(POSTOFFICE_STATION_UID, "Postkontoret",
        "Klikkbare e-posttester med avsendere, lenker og vedlegg.",
        PLACEHOLDER_IMAGE, "#fde68a",
        List.of(POSTOFFICE_EXERCISE_1_UID, POSTOFFICE_EXERCISE_2_UID), false);

    upsertStation(PASSWORDBANK_STATION_UID, "Passordbanken",
        "Passordsjekking og gode passordvaner.",
        PLACEHOLDER_IMAGE, "#1D5232",
        List.of(PASSWORDBANK_EXERCISE_1_UID, PASSWORDBANK_EXERCISE_2_UID), false);

    upsertStation(MARKETPLACE_STATION_UID, "Markedsplassen",
        "Reklame, tilbud og e-butikker som ma vurderes.",
        PLACEHOLDER_IMAGE, "#962C2C",
        List.of(MARKETPLACE_EXERCISE_1_UID, MARKETPLACE_EXERCISE_2_UID), false);

    upsertStation(SOCIALMEDIA_STATION_UID, "Den sosiale moteplassen",
        "Deling av innlegg, vurderinger og gruppepress i sosiale medier.",
        PLACEHOLDER_IMAGE, "#fbcfe8",
        List.of(SOCIALMEDIA_EXERCISE_1_UID, SOCIALMEDIA_EXERCISE_2_UID), false);

    upsertStation(DATACENTER_STATION_UID, "Datasenteret",
        "Finalebanen der du bruker det du har laert for a bekjempe datatyven.",
        PLACEHOLDER_IMAGE, "#8A2A2A",
        List.of(DATACENTER_EXERCISE_UID), true);
  }

  private void seedWorlds() {
    upsertWorld(SCHOOLYARD_WORLD_UID, "3-trinn",
        "Datasikkerhet for skoleelever: nyheter, bilder, e-post, passord, reklame, e-butikker og deling.",
        PLACEHOLDER_IMAGE,
        List.of(
            NEWSSTAND_STATION_UID,
            PHOTOGRAPH_STATION_UID,
            POSTOFFICE_STATION_UID,
            PASSWORDBANK_STATION_UID,
            MARKETPLACE_STATION_UID,
            SOCIALMEDIA_STATION_UID
        ));

    upsertWorld(QUICKSTART_WORLD_UID, "7-trinn",
        "Datasikkerhet for skoleelever: nyheter, bilder, e-post, passord, reklame, e-butikker og deling.",
        PLACEHOLDER_IMAGE,
        List.of(
            NEWSSTAND_STATION_UID,
            PHOTOGRAPH_STATION_UID,
            POSTOFFICE_STATION_UID,
            PASSWORDBANK_STATION_UID,
            MARKETPLACE_STATION_UID,
            SOCIALMEDIA_STATION_UID
        ));

    worldRepository.findByUid(CREATIVE_LAB_WORLD_UID).ifPresent(worldRepository::delete);
  }

  private void upsertExercise(UUID publicId, String title, String difficultyName, String description,
      List<TaskRequest> taskRequests) {
    upsertExercise(publicId, title, difficultyName, description,
        "Tips: Les oppgaven rolig og se etter detaljer som virker uvanlige eller vanskelige a kontrollere.",
        taskRequests);
  }

  private void upsertExercise(UUID publicId, String title, String difficultyName, String description,
      String hint, List<TaskRequest> taskRequests) {
    Exercise exercise = exerciseRepository.findByPublicId(publicId).orElseGet(Exercise::new);

    exercise.setPublicId(publicId);
    exercise.setTitle(title);
    exercise.setDifficultyName(difficultyName);
    exercise.setDescription(description);
    exercise.setHint(hint);
    exercise.setNotepadEntry(defaultNotepadEntry(publicId, title, description, hint));
    exercise.getTasks().clear();
    exercise.getTasks().addAll(newTasks(exercise, taskRequests));

    exerciseRepository.save(exercise);
  }

  private NotepadEntry defaultNotepadEntry(UUID publicId, String title, String description, String hint) {
    NotepadEntry notepadEntry = new NotepadEntry();
    notepadEntry.setSource(title);

    int variant = Math.floorMod(publicId.hashCode(), 3);
    switch (variant) {
      case 0 -> {
        notepadEntry.setTitle(title + " - huskelapp");
        notepadEntry.setType(NotepadEntryType.TIP);
        notepadEntry.setContent(limitTo256("Tips: " + hint));
      }
      case 1 -> {
        notepadEntry.setTitle(title + " - fakta");
        notepadEntry.setType(NotepadEntryType.INFO);
        notepadEntry.setContent(limitTo256("Dette trener du pa: " + description));
      }
      default -> {
        notepadEntry.setTitle(title + " - observasjon");
        notepadEntry.setType(NotepadEntryType.OBSERVATION);
        notepadEntry.setContent(limitTo256("Husk dette fra oppgaven: " + description + " Se etter: " + hint));
      }
    }

    return notepadEntry;
  }

  private String limitTo256(String value) {
    return value.length() <= 256 ? value : value.substring(0, 256);
  }

  private List<Task> newTasks(Exercise exercise, List<TaskRequest> taskRequests) {
    List<Task> tasks = new ArrayList<>();

    for (TaskRequest taskRequest : taskRequests) {
      Task task = new Task();
      task.setExercise(exercise);
      task.setIntroText(taskRequest.introText());
      task.setFeedbackCorrect(taskRequest.feedbackCorrect());
      task.setFeedbackIncorrect(taskRequest.feedbackIncorrect());
      task.setContent(taskRequest.content());
      task.setTemplateKind(null);
      tasks.add(task);
    }

    return tasks;
  }

  private void upsertStation(UUID uid, String title, String description, String image,
      String backgroundColor, List<UUID> exerciseUids, boolean isBoss) {
    Station station = stationRepository.findByUid(uid).orElseGet(Station::new);

    station.setUid(uid);
    station.setTitle(title);
    station.setDescription(description);
    station.setImage(image);
    station.setBackgroundColor(backgroundColor);
    station.setExerciseUids(new ArrayList<>(exerciseUids));
    station.setBoss(isBoss);

    stationRepository.save(station);
  }

  private void upsertWorld(UUID uid, String title, String description, String image, List<UUID> stationUids) {
    World world = worldRepository.findByUid(uid).orElseGet(World::new);

    world.setUid(uid);
    world.setTitle(title);
    world.setDescription(description);
    world.setImage(image);
    world.setStationUids(new ArrayList<>(stationUids));

    worldRepository.save(world);
  }

  private TaskRequest multipleChoiceTask(String introText, String feedbackCorrect,
      String feedbackIncorrect, String promptTitle, String promptText,
      String imageUrl, String imageAlt, List<MultipleChoiceContent.Option> options,
      String correctChoice) {
    return new TaskRequest(
        introText,
        feedbackCorrect,
        feedbackIncorrect,
        new MultipleChoiceContent(
            new MultipleChoiceContent.Prompt(imageUrl, imageAlt, promptTitle, promptText),
            options,
            correctChoice
        )
    );
  }

  private TaskRequest xOrYTask(String introText, String feedbackCorrect, String feedbackIncorrect,
      String promptTitle, String promptText,
      String xImageUrl, String xImageAlt, String xEyebrow, String xTitle, String xText,
      String yImageUrl, String yImageAlt, String yEyebrow, String yTitle, String yText,
      XOrYContent.Choice correctChoice) {
    return new TaskRequest(
        introText,
        feedbackCorrect,
        feedbackIncorrect,
        new XOrYContent(
            new XOrYContent.Prompt(promptTitle, promptText),
            new XOrYContent.Options(
                new XOrYContent.Option(xImageUrl, xImageAlt, xEyebrow, xTitle, xText),
                new XOrYContent.Option(yImageUrl, yImageAlt, yEyebrow, yTitle, yText)
            ),
            correctChoice
        )
    );
  }

  private TaskRequest emailHotspotsTask(String introText, String feedbackCorrect,
      String feedbackIncorrect, String promptTitle, String promptText,
      EmailHotspotsContent.Email email, List<EmailHotspotsContent.Target> targets) {
    return new TaskRequest(
        introText,
        feedbackCorrect,
        feedbackIncorrect,
        new EmailHotspotsContent(
            new EmailHotspotsContent.Prompt(promptTitle, promptText),
            email,
            targets
        )
    );
  }

  private TaskRequest sortIntoBucketsTask(String introText, String feedbackCorrect,
      String feedbackIncorrect, String promptTitle, String promptText, String unplacedLabel,
      List<SortIntoBucketsContent.Bucket> buckets, List<SortIntoBucketsContent.Item> items) {
    return new TaskRequest(
        introText,
        feedbackCorrect,
        feedbackIncorrect,
        new SortIntoBucketsContent(
            new SortIntoBucketsContent.Prompt(promptTitle, promptText),
            unplacedLabel,
            buckets,
            items
        )
    );
  }

  private TaskRequest passwordStrengthTask(String introText, String feedbackCorrect,
      String feedbackIncorrect, String promptTitle, String promptText, String helperText,
      PasswordStrengthContent.Mode mode, String placeholder, String startingPassword,
      Integer minLength, int minScoreToPass, List<String> commonPatterns) {
    return new TaskRequest(
        introText,
        feedbackCorrect,
        feedbackIncorrect,
        new PasswordStrengthContent(
            new PasswordStrengthContent.Prompt(promptTitle, promptText, helperText),
            mode,
            placeholder,
            startingPassword,
            minLength,
            minScoreToPass,
            commonPatterns
        )
    );
  }

  private static UUID stableUuid(String value) {
    return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8));
  }
}
