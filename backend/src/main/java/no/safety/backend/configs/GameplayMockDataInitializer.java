package no.safety.backend.configs;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.achievements.model.Achievement;
import no.safety.backend.achievements.model.AchievementCategory;
import no.safety.backend.achievements.repository.AchievementRepository;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.excercise.repository.ExerciseRepository;
import no.safety.backend.media.service.MediaStorageService;
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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initializes mock data for gameplay including worlds, stations, exercises, and achievements.
 * Implements ApplicationRunner to seed the database on application startup.
 */
@Component
@Order(5)
@RequiredArgsConstructor
public class GameplayMockDataInitializer implements ApplicationRunner {

    /**
     * Generates a stable UUID from a string value for consistent mock data identifiers.
     * @param value the string value to convert to UUID
     * @return a deterministic UUID based on the input string
     */
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
  public static final UUID PHOTOGRAPH_EXERCISE_3_UID = stableUuid("exercise:photograph-exercise-3");
  public static final UUID POSTOFFICE_EXERCISE_1_UID = stableUuid("exercise:postoffice-exercise-1");
  public static final UUID POSTOFFICE_EXERCISE_2_UID = stableUuid("exercise:postoffice-exercise-2");
  public static final UUID PASSWORDBANK_EXERCISE_1_UID = stableUuid("exercise:passwordbank-exercise-1");
  public static final UUID PASSWORDBANK_EXERCISE_2_UID = stableUuid("exercise:passwordbank-exercise-2");
  public static final UUID MARKETPLACE_EXERCISE_1_UID = stableUuid("exercise:marketplace-exercise-1");
  public static final UUID MARKETPLACE_EXERCISE_2_UID = stableUuid("exercise:marketplace-exercise-2");
  public static final UUID SOCIALMEDIA_EXERCISE_1_UID = stableUuid("exercise:socialmedia-exercise-1");
  public static final UUID SOCIALMEDIA_EXERCISE_2_UID = stableUuid("exercise:socialmedia-exercise-2");
  public static final UUID DATACENTER_EXERCISE_UID = stableUuid("exercise:datacenter-exercise");

  private static final String NEWS_SAFE_IMAGE = "seed/tasks/news-safe.svg";
  private static final String NEWS_WARNING_IMAGE = "seed/tasks/news-warning.svg";
  private static final String PHOTO_SAFE_IMAGE = "seed/tasks/photo-safe.svg";
  private static final String PHOTO_WARNING_IMAGE = "seed/tasks/photo-warning.svg";
  private static final String EMAIL_SAFE_IMAGE = "seed/tasks/email-safe.svg";
  private static final String EMAIL_WARNING_IMAGE = "seed/tasks/email-warning.svg";
  private static final String PASSWORD_SAFE_IMAGE = "seed/tasks/password-safe.svg";
  private static final String PASSWORD_WARNING_IMAGE = "seed/tasks/password-warning.svg";
  private static final String SHOP_SAFE_IMAGE = "seed/tasks/shop-safe.svg";
  private static final String SHOP_WARNING_IMAGE = "seed/tasks/shop-warning.svg";
  private static final String SOCIAL_SAFE_IMAGE = "seed/tasks/social-safe.svg";
  private static final String SOCIAL_WARNING_IMAGE = "seed/tasks/social-warning.svg";
  private static final String DETECTIVE_HUB_IMAGE = "seed/tasks/detective-hub.svg";

  private static final String NEWSSTAND_STATION_IMAGE = "seed/stations/nyhetskvtartalet.png";
  private static final String PHOTOGRAPH_STATION_IMAGE = "seed/stations/fotografen.png";
  private static final String POSTOFFICE_STATION_IMAGE = "seed/stations/postkontoret.png";
  private static final String PASSWORDBANK_STATION_IMAGE = "seed/stations/passordbanken.png";
  private static final String MARKETPLACE_STATION_IMAGE = "seed/stations/markedsplassen.png";
  private static final String SOCIALMEDIA_STATION_IMAGE = "seed/stations/sosial-moteplassen.png";

  private static final String NEWSSTAND_SAFE_LIBRARY_IMAGE = "seed/tasks/nyhetskvartalet/safe-lesehjorne.png";
  private static final String NEWSSTAND_SAFE_READING_IMAGE = "seed/tasks/nyhetskvartalet/safe-barn-leser-mer.png";
  private static final String NEWSSTAND_WARNING_PHONE_IMAGE = "seed/tasks/nyhetskvartalet/warning-gratis-mobil.png";
  private static final String NEWSSTAND_WARNING_SCHOOL_IMAGE = "seed/tasks/nyhetskvartalet/warning-skoler-stenger.png";
  private static final String NEWSSTAND_WARNING_PLAYGROUND_IMAGE = "seed/tasks/nyhetskvartalet/warning-bot-lekeplass.png";

  private static final String PHOTOGRAPH_REAL_DOG_IMAGE = "seed/tasks/fotografen/real-dog-frisbee.png";
  private static final String PHOTOGRAPH_REAL_FOOTBALL_IMAGE = "seed/tasks/fotografen/real-football-player-kicking-ball.jpeg";
  private static final String PHOTOGRAPH_REAL_PEOPLE_IMAGE = "seed/tasks/fotografen/real-people-holding-hands.png";
  private static final String PHOTOGRAPH_AI_HANDS_IMAGE = "seed/tasks/fotografen/ai-folded-hands-many-fingers.jpeg";
  private static final String PHOTOGRAPH_AI_BANANA_IMAGE = "seed/tasks/fotografen/ai-kid-giant-banana.jpeg";
  private static final String PHOTOGRAPH_AI_SIGNS_IMAGE = "seed/tasks/fotografen/ai-road-sign-arrows-both-ways.png";
  private static final String PHOTOGRAPH_AI_MARS_IMAGE = "seed/tasks/fotografen/ai-eiffel-tower-mars-landscape.jpeg";
  private static final String PHOTOGRAPH_AI_SKY_IMAGE = "seed/tasks/fotografen/ai-sunset-spiral-clouds.jpeg";

  private final ExerciseRepository exerciseRepository;
  private final StationRepository stationRepository;
  private final WorldRepository worldRepository;
  private final MediaStorageService mediaStorageService;
  private final AchievementRepository achievementRepository;

    /**
     * Runs the mock data initialization on application startup.
     * Seeds exercises, stations, worlds, and achievements into the database.
     * @param args application arguments (not used)
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
    seedExercises();
    seedStations();
    seedWorlds();
  }

    /**
     * Seeds exercises into the database with predefined UUIDs.
     * Creates various exercise types including multiple choice, X-or-Y, sort into buckets,
     * email hotspots, and password strength tasks.
     */
    private void seedExercises() {
    upsertExercise(NEWSSTAND_EXERCISE_1_UID, "Troverdige nyheter", "Lett",
        "Undersøk hvilke nyhetssaker som virker ekte, og hvilke faresignaler du bør sjekke.",
        List.of(
            multipleChoiceTask(
                "Velg den forklaringen som best viser hvorfor saken virker trygg å stole på.",
                "Bra observert. En tydelig avsender gjør det lettere å sjekke hvem som står bak informasjonen.",
                "Se etter tegn som hjelper deg å sjekke saken, som tydelig avsender og konkret informasjon.",
                "Hvilken forklaring passer best?",
                "Se på skjermbildet og velg det beste tegnet på at saken virker troverdig.",
                media(NEWSSTAND_SAFE_LIBRARY_IMAGE),
                "Skjermbilde av en rolig nyhetssak om et nytt lesehjørne på skolen",
                List.of(
                    new MultipleChoiceContent.Option("a", "Saken oppgir hvem som har skrevet den og når den er publisert"),
                    new MultipleChoiceContent.Option("b", "Saken har et stort bilde som ser ryddig ut")
                ),
                "a"
            ),
            xOrYTask(
                "Sammenlign de to overskriftene og velg den som virker mest troverdig.",
                "Bra. Den troverdige saken bruker rolig språk og gir deg noe konkret å sjekke.",
                "Se etter rolig språk, tydelig avsender og konkrete opplysninger.",
                "Hvilken overskrift virker tryggest?",
                "Velg saken du ville stolt mest på ved første blikk.",
                media(NEWSSTAND_WARNING_PHONE_IMAGE),
                "Dramatisk nyhet om gratis mobil uten tydelig avsender",
                "",
                "Gratis mobil til alle elever",
                "Utsnitt fra en nyhetssak om skole og mobilbruk.",
                media(NEWSSTAND_SAFE_READING_IMAGE),
                "Rolig overskrift med tydelig avsender",
                "",
                "Barn leser mer etter nytt lesehjørne på skolebiblioteket",
                "Utsnitt fra en nyhetssak om skolebiblioteket.",
                XOrYContent.Choice.Y
            ),
            sortIntoBucketsTask(
                "Sorter tegnene i riktig boks.",
                "Riktig. Du skilte mellom gode tegn og faresignaler i en nyhetssak.",
                "Se en gang til på hvilke kort som hjelper deg å stole på saken, og hvilke som gjør deg mer skeptisk.",
                "Hva bør du stole på?",
                "Dra kortene til riktig kategori.",
                "Kort som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("safe", "Trygge tegn", "Tegn som styrker troverdigheten.", "#d9fbe6"),
                    new SortIntoBucketsContent.Bucket("warn", "Faresignaler", "Tegn som bør sjekkes ekstra.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("news-1", "Navngitt avsender", "safe", "Da kan du sjekke hvem som står bak."),
                    new SortIntoBucketsContent.Item("news-2", "Dato og klokkeslett", "safe", "Det gjør det lettere å forstå når saken ble skrevet."),
                    new SortIntoBucketsContent.Item("news-3", "Overskrift som lover mer enn den forklarer", "warn", "Da bør du være ekstra forsiktig."),
                    new SortIntoBucketsContent.Item("news-4", "Sak uten kilde eller forklaring", "warn", "Når kilden mangler, bør du være ekstra skeptisk.")
                )
            ),
            multipleChoiceTask(
                "Se på skjermbildet og velg den beste grunnen til at saken virker falsk.",
                "Riktig. Når en overskrift prøver å sjokkere og du ikke kjenner avsenderen, bør du stoppe opp og sjekke mer.",
                "Tenk på hvilke tegn som faktisk svekker troverdigheten, ikke bare hvordan saken ser ut visuelt.",
                "Hvorfor virker denne saken falsk?",
                "Velg forklaringen som passer best til det du ser i skjermbildet.",
                media(NEWSSTAND_WARNING_PLAYGROUND_IMAGE),
                "Skjermbilde av en tvilsom nyhetsartikkel om at lekeplassen skal stenge",
                List.of(
                    new MultipleChoiceContent.Option("a", "Den har en tydelig avsender og viser til flere kilder"),
                    new MultipleChoiceContent.Option("b", "Den mangler navn på hvem som står bak saken"),
                    new MultipleChoiceContent.Option("c", "Den har en stor illustrasjon øverst")
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
                "Se etter kjent avsender, konkret språk og om saken forklarer hvor informasjonen kommer fra.",
                "Hva er det tydeligste faresignalet?",
                "Saken ser profesjonell ut, men ett tegn gjør at du bør være ekstra skeptisk.",
                media(NEWSSTAND_WARNING_PHONE_IMAGE),
                "Skjermbilde av en nyhetsartikkel om gratis mobil uten tydelige kilder",
                List.of(
                    new MultipleChoiceContent.Option("a", "Den har en kort overskrift og et stort bilde"),
                    new MultipleChoiceContent.Option("b", "Den mangler tydelige kilder eller forklaringer"),
                    new MultipleChoiceContent.Option("c", "Den handler om noe mange elever er nysgjerrige på")
                ),
                "b"
            ),
            xOrYTask(
                "Velg den saken som virker mest troverdig når du sammenligner avsender og forklaring.",
                "Bra. Den beste saken gir deg nok informasjon til at du kan sjekke mer selv.",
                "Se etter hvem som står bak og om saken forklarer hvor opplysningene kommer fra.",
                "Hvilken sak ville du sjekket videre?",
                "Velg den som virker mest ryddig og pålitelig.",
                media(NEWSSTAND_WARNING_SCHOOL_IMAGE),
                "Nyhetssak med vag overskrift og ingen tydelig kilde",
                "",
                "Det skjer noe stort ved skolen i morgen",
                "Utsnitt fra en nyhetssak om skolen.",
                media(NEWSSTAND_SAFE_READING_IMAGE),
                "Nyhetssak med rolig overskrift og tydelig kilde",
                "",
                "Barn leser mer etter nytt lesehjørne på skolebiblioteket",
                "Utsnitt fra en nyhetssak om lesehjørnet på skolen.",
                XOrYContent.Choice.Y
            ),
            multipleChoiceTask(
                "Velg det beste neste steget når du blir usikker på om en nyhet stemmer.",
                "Ja. Det tryggeste er å sjekke kilden og se om andre troverdige steder skriver det samme.",
                "Tenk på hva som hjelper deg å undersøke saken, ikke hva som sprer den raskest.",
                "Hva bør du gjøre etterpå?",
                "Du er usikker på om saken stemmer. Velg det smarteste neste steget.",
                media(NEWSSTAND_WARNING_PLAYGROUND_IMAGE),
                "Skjermbilde av en nyhetssak som bør dobbeltsjekkes før den deles",
                List.of(
                    new MultipleChoiceContent.Option("a", "Sjekke om andre troverdige kilder sier det samme"),
                    new MultipleChoiceContent.Option("b", "Spørre en venn om hva han eller hun tror"),
                    new MultipleChoiceContent.Option("c", "Lese kommentarfeltet først")
                ),
                "a"
            )
        )
    );

    upsertExercise(PHOTOGRAPH_EXERCISE_1_UID, "Bildeblikk", "Lett",
        "Se etter tydelige tegn på at et bilde kan være laget eller endret.",
        List.of(
            multipleChoiceTask(
                "Hva bør du se nærmere på i dette bildet?",
                "Bra. Når en detalj ikke passer helt med resten av bildet, er det lurt å dobbeltsjekke.",
                "Se etter ting som virker litt rare eller som ikke henger helt sammen.",
                "Hva er det beste tegnet på at bildet bør sjekkes nærmere?",
                "Velg det du ville lagt mest merke til først.",
                media(PHOTOGRAPH_AI_HANDS_IMAGE),
                "En hånd med flere fingre enn forventet",
                List.of(
                    new MultipleChoiceContent.Option("a", "Hendene har for mange fingre"),
                    new MultipleChoiceContent.Option("b", "Bakgrunnen er litt uskarp"),
                    new MultipleChoiceContent.Option("c", "Bildet er tatt i dagslys")
                ),
                "a"
            ),
            xOrYTask(
                "Hvilket bilde virker mest naturlig ved første blikk?",
                "Bra. Det ene bildet har flere små ting som skurrer, mens det andre virker mer vanlig.",
                "Se etter om kropp, lys og ting i bildet passer sammen på en naturlig måte.",
                "Hvilket bilde virker mest troverdig?",
                "Velg bildet som virker mest vanlig og rolig.",
                media(PHOTOGRAPH_AI_BANANA_IMAGE),
                "Barn holder en veldig stor banan",
                "",
                "Bilde X",
                "Et barn holder noe over hodet.",
                media(PHOTOGRAPH_REAL_DOG_IMAGE),
                "Hund som fanger en frisbee",
                "",
                "Bilde Y",
                "En hund er ute i aktivitet.",
                XOrYContent.Choice.Y
            )
        )
    );

    upsertExercise(PHOTOGRAPH_EXERCISE_2_UID, "Foto eller fiks?", "Middels",
        "Sammenlign bilder og se etter små ting som gjør at noe føles feil.",
        List.of(
            sortIntoBucketsTask(
                "Sorter detaljene i riktig boks.",
                "Riktig. Du skilte mellom detaljer som passer godt inn, og detaljer som bør sjekkes nærmere.",
                "Se etter små ting som ikke helt passer sammen i bildet.",
                "Hva virker mest trygt å stole på?",
                "Dra kortene til riktig kategori.",
                "Detaljer som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("ok", "Virker naturlig", "Ting som passer godt inn i et ekte bilde.", "#dbeafe"),
                    new SortIntoBucketsContent.Bucket("check", "Bør sjekkes", "Ting som ofte avslører at noe er laget eller endret.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("photo-1", "Skygger peker samme vei", "ok", "Det passer bedre med ekte lys."),
                    new SortIntoBucketsContent.Item("photo-2", "Skilt med piler som peker begge veier", "check", "Retningen virker ikke helt logisk."),
                    new SortIntoBucketsContent.Item("photo-3", "Ansikter og klær ser jevne ut", "ok", "Det gir mindre grunn til mistanke."),
                    new SortIntoBucketsContent.Item("photo-4", "Utydelig kant rundt motivet", "check", "Når kanter blir rare, bør du bli skeptisk.")
                )
            ),
            multipleChoiceTask(
                "Hva bør du se nærmere på i skiltet?",
                "Bra. Når retningen ikke virker helt logisk, er det et tegn på at noe bør sjekkes.",
                "Tenk på hva som ikke helt stemmer med det du forventer å se på et skilt.",
                "Hva virker mest usikkert?",
                "Velg det som bør få deg til å stoppe opp.",
                media(PHOTOGRAPH_AI_SIGNS_IMAGE),
                "Et veiskilt med piler i to retninger",
                List.of(
                    new MultipleChoiceContent.Option("a", "Retningen på pilene virker ikke helt logisk"),
                    new MultipleChoiceContent.Option("b", "Skiltet har tydelige farger"),
                    new MultipleChoiceContent.Option("c", "Det står ved en vei")
                ),
                "a"
            )
        )
    );

    upsertExercise(PHOTOGRAPH_EXERCISE_3_UID, "Bildedetektiv", "Vanskelig",
        "Undersøk bilder som ser nesten ekte ut, men som har små detaljer som ikke stemmer.",
        List.of(
            xOrYTask(
                "Hvilket bilde virker mest troverdig ved første blikk?",
                "Riktig. Det ene motivet passer dårlig sammen, mens det andre virker mer som et vanlig bilde av noe på himmelen.",
                "Se etter hvilket bilde som virker mest naturlig og minst kunstig.",
                "Hvilket bilde ville du sjekket minst først?",
                "Velg bildet som virker mest vanlig og minst overraskende.",
                media(PHOTOGRAPH_AI_MARS_IMAGE),
                "Eiffeltårnet står på Mars",
                "",
                "Bilde X",
                "Et kjent tårn i et rødt landskap.",
                media(PHOTOGRAPH_AI_SKY_IMAGE),
                "Sære skyer over en solnedgang",
                "",
                "Bilde Y",
                "Solnedgang med skyer i himmelen.",
                XOrYContent.Choice.Y
            ),
            multipleChoiceTask(
                "Hva bør du undersøke først i dette bildet?",
                "Bra. Når bevegelse og kropp ikke helt henger sammen, er det lurt å se nærmere på bildet.",
                "Se etter noe som virker litt feil i måten motivet er satt sammen på.",
                "Hva er det beste spørsmålet å stille?",
                "Velg det som hjelper deg å vurdere om bildet er ekte.",
                media(PHOTOGRAPH_REAL_FOOTBALL_IMAGE),
                "En spiller som sparker ballen i en kamp",
                List.of(
                    new MultipleChoiceContent.Option("a", "Ser bevegelsen og kroppen naturlig ut?"),
                    new MultipleChoiceContent.Option("b", "Har bildet mange ulike farger?"),
                    new MultipleChoiceContent.Option("c", "Er bildet tatt ute på en bane?")
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
                media(EMAIL_WARNING_IMAGE),
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
                media(EMAIL_WARNING_IMAGE),
                "Mistenkelig e-post med stressende sprak",
                "Melding X",
                "Handle na",
                "Meldingen presser deg til a klikke med en gang og bruker en rar avsender.",
                media(EMAIL_SAFE_IMAGE),
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
                media(EMAIL_WARNING_IMAGE),
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
                media(PASSWORD_WARNING_IMAGE),
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
                media(PASSWORD_SAFE_IMAGE),
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
                media(PASSWORD_WARNING_IMAGE),
                "Kort passord med navn og arstall",
                "Forslag X",
                "Ola2015",
                "Passordet bruker navn og arstall som kan vaere lett a gjette.",
                media(PASSWORD_SAFE_IMAGE),
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
                media(SHOP_WARNING_IMAGE),
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
                media(SHOP_WARNING_IMAGE),
                "Tvilsom butikk med rar nettadresse",
                "Butikk X",
                "Supertilbud pa alt",
                "Butikken lover veldig mye, men nettadressen og informasjonen virker rare.",
                media(SHOP_SAFE_IMAGE),
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
                media(SHOP_SAFE_IMAGE),
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
                media(SHOP_SAFE_IMAGE),
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
                media(SHOP_WARNING_IMAGE),
                "Kasseside med pressende tekst og lite informasjon",
                "Kasse X",
                "Handle fort",
                "Siden stresser deg og gir lite informasjon om hvem du betaler til.",
                media(SHOP_SAFE_IMAGE),
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
                media(SOCIAL_WARNING_IMAGE),
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
                media(SOCIAL_WARNING_IMAGE),
                "Stressende innlegg med sterke ord",
                "Innlegg X",
                "Del na",
                "Innlegget bruker gruppepress og sterke ord for a fa deg til a reagere fort.",
                media(SOCIAL_SAFE_IMAGE),
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
                media(SOCIAL_WARNING_IMAGE),
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
                media(SOCIAL_WARNING_IMAGE),
                "Innlegg som oppfordrer til rask deling",
                "Valg X",
                "Del for sikkerhets skyld",
                "Du sprer innholdet videre uten a vite om det stemmer.",
                media(SOCIAL_SAFE_IMAGE),
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
                media(DETECTIVE_HUB_IMAGE),
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
        "Bruk alt du har lært for å bekjempe datatyven.",
        List.of(
            multipleChoiceTask(
                "Datatyven sender en falsk nyhet. Velg det tydeligste faresignalet.",
                "Riktig. Mangel pa kilde og sjokkerende sprak er klassiske faresignaler.",
                "Tenk pa hva som hjelper deg a kontrollere om en sak stemmer.",
                "Hva avslorer den falske nyheten?",
                "Datatyven har lagt ut en sak som sprer seg fort. Velg det tydeligste faresignalet.",
                media(NEWS_WARNING_IMAGE),
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
                media(PHOTO_WARNING_IMAGE),
                "KI-generert bilde med unaturlige detaljer",
                "Bilde X",
                "Mange smaa feil",
                "Hender og tekst ser unaturlige ut.",
                media(PHOTO_SAFE_IMAGE),
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
                media(SHOP_WARNING_IMAGE),
                "Tvilsom butikk med rar nettadresse og pressende tilbud",
                "Butikk X",
                "Sjokktilbud",
                "Rar nettadresse, ingen kontaktinfo og pressende nedtelling.",
                media(SHOP_SAFE_IMAGE),
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
                media(SOCIAL_WARNING_IMAGE),
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
                media(DETECTIVE_HUB_IMAGE),
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
                media(SOCIAL_WARNING_IMAGE),
                "Risikabel strategi som spiller datatyven i hendene",
                "Strategi X",
                "Klikk og del raskt",
                "Du faller for press og sprer datatyvens innhold videre.",
                media(DETECTIVE_HUB_IMAGE),
                "Trygg strategi med sjekk og sterke passord",
                "Strategi Y",
                "Stopp, sjekk og bruk sterke passord",
                "Du tar deg tid til a kontrollere kilder, lenker og bilder, og beskytter kontoene dine.",
                XOrYContent.Choice.Y
            )
        )
    );
  }

    /**
     * Seeds stations into the database with predefined UUIDs.
     * Creates stations and associates them with exercises.
     */
  private void seedStations() {
    upsertStation(NEWSSTAND_STATION_UID, "Nyhetskvartalet",
        "Nyhetsartikler og kildekritikk for skoleelever.",
        "Start med a sjekke hvem som er avsender, og om saken bruker sterke ord for a presse deg til a tro pa den.",
        media(NEWSSTAND_STATION_IMAGE), "#cfe9ff",
        List.of(NEWSSTAND_EXERCISE_1_UID, NEWSSTAND_EXERCISE_2_UID), false);

    upsertStation(PHOTOGRAPH_STATION_UID, "Fotografen",
        "Normal, manipulert og KI-genererte bilder.",
        "Se ekstra nøye pa detaljer som hender, tekst, skygger og bakgrunn nar du vurderer bilder.",
        media(PHOTOGRAPH_STATION_IMAGE), "#5F5F5F",
        List.of(PHOTOGRAPH_EXERCISE_1_UID, PHOTOGRAPH_EXERCISE_2_UID, PHOTOGRAPH_EXERCISE_3_UID), false);

    upsertStation(POSTOFFICE_STATION_UID, "Postkontoret",
        "Klikkbare e-posttester med avsendere, lenker og vedlegg.",
        "Ikke klikk med en gang. Sjekk avsender, lenkeadresse og om meldingen prover a stresse deg.",
        media(POSTOFFICE_STATION_IMAGE), "#fde68a",
        List.of(POSTOFFICE_EXERCISE_1_UID, POSTOFFICE_EXERCISE_2_UID), false);

    upsertStation(PASSWORDBANK_STATION_UID, "Passordbanken",
        "Passordsjekking og gode passordvaner.",
        "Et sterkt passord er langt, unikt og vanskelig for andre a gjette, men fortsatt mulig for deg a huske.",
        media(PASSWORDBANK_STATION_IMAGE), "#1D5232",
        List.of(PASSWORDBANK_EXERCISE_1_UID, PASSWORDBANK_EXERCISE_2_UID), false);

    upsertStation(MARKETPLACE_STATION_UID, "Markedsplassen",
        "Reklame, tilbud og e-butikker som ma vurderes.",
        "Hvis et tilbud virker for godt eller haster veldig, bor du stoppe opp og sjekke butikken forst.",
        media(MARKETPLACE_STATION_IMAGE), "#962C2C",
        List.of(MARKETPLACE_EXERCISE_1_UID, MARKETPLACE_EXERCISE_2_UID), false);

    upsertStation(SOCIALMEDIA_STATION_UID, "Den sosiale moteplassen",
        "Deling av innlegg, vurderinger og gruppepress i sosiale medier.",
        "Vent litt for du deler. Sjekk om innholdet har en tydelig kilde og om det prover a gjore deg sint eller redd.",
        media(SOCIALMEDIA_STATION_IMAGE), "#fbcfe8",
        List.of(SOCIALMEDIA_EXERCISE_1_UID, SOCIALMEDIA_EXERCISE_2_UID), false);

    upsertStation(DATACENTER_STATION_UID, "Datasenteret",
        "Finalebanen der du bruker alt du har lært for å bekjempe datatyven.",
        "Bruk alt du har lært: stopp opp, sjekk kilden, vurder detaljer og velg trygge handlinger.",
        media("seed/stations/datacenter.svg"), "#8A2A2A",
        List.of(DATACENTER_EXERCISE_UID), true);
  }

    /**
     * Seeds worlds into the database with predefined UUIDs.
     * Creates worlds and associates them with stations.
     */
    private void seedWorlds() {
    upsertWorld(SCHOOLYARD_WORLD_UID, "3-trinn",
        "Datasikkerhet for skoleelever: nyheter, bilder, e-post, passord, reklame, e-butikker og deling.",
        media("seed/worlds/schoolyard.svg"),
        List.of(
            NEWSSTAND_STATION_UID,
            PHOTOGRAPH_STATION_UID,
            POSTOFFICE_STATION_UID,
            PASSWORDBANK_STATION_UID,
            MARKETPLACE_STATION_UID,
            SOCIALMEDIA_STATION_UID,
            DATACENTER_STATION_UID
        ));

    upsertWorld(QUICKSTART_WORLD_UID, "7-trinn",
        "Datasikkerhet for skoleelever: nyheter, bilder, e-post, passord, reklame, e-butikker og deling.",
        media("seed/worlds/quickstart.svg"),
        List.of(
            NEWSSTAND_STATION_UID,
            PHOTOGRAPH_STATION_UID,
            POSTOFFICE_STATION_UID,
            PASSWORDBANK_STATION_UID,
            MARKETPLACE_STATION_UID,
            SOCIALMEDIA_STATION_UID,
            DATACENTER_STATION_UID
        ));

    worldRepository.findByUid(CREATIVE_LAB_WORLD_UID).ifPresent(worldRepository::delete);
  }

    /**
     * Creates or updates an exercise with the specified properties.
     * @param publicId the public UUID for the exercise
     * @param title the title of the exercise
     * @param difficultyName the difficulty level name
     * @param description the description of the exercise
     * @param taskRequests the list of task requests for the exercise
     */
    private void upsertExercise(UUID publicId, String title, String difficultyName, String description,
        List<TaskRequest> taskRequests) {
    upsertExercise(publicId, title, difficultyName, description,
        "Les oppgaven rolig og se etter detaljer som virker uvanlige eller vanskelige a kontrollere.",
        taskRequests);
  }

    /**
     * Creates or updates an exercise with hint included.
     * @param publicId the public UUID for the exercise
     * @param title the title of the exercise
     * @param difficultyName the difficulty level name
     * @param description the description of the exercise
     * @param hint the hint for the exercise
     * @param taskRequests the list of task requests for the exercise
     */
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

    /**
     * Creates a default notepad entry for an exercise based on its properties.
     * @param publicId the public UUID of the exercise
     * @param title the title of the exercise
     * @param description the description of the exercise
     * @param hint the hint for the exercise
     * @return a NotepadEntry configured based on the exercise properties
     */
    private NotepadEntry defaultNotepadEntry(UUID publicId, String title, String description, String hint) {
    NotepadEntry notepadEntry = new NotepadEntry();
    notepadEntry.setSource(title);

    int variant = Math.floorMod(publicId.hashCode(), 3);
    switch (variant) {
      case 0 -> {
        notepadEntry.setTitle(title);
        notepadEntry.setType(NotepadEntryType.TIP);
        notepadEntry.setContent(limitTo256(hint));
      }
      case 1 -> {
        notepadEntry.setTitle(title);
        notepadEntry.setType(NotepadEntryType.INFO);
        notepadEntry.setContent(limitTo256("Dette trener du pa: " + description));
      }
      default -> {
        notepadEntry.setTitle(title);
        notepadEntry.setType(NotepadEntryType.TIP);
        notepadEntry.setContent(limitTo256("Husk dette fra oppgaven: " + description + " Se etter: " + hint));
      }
    }

    return notepadEntry;
  }

    /**
     * Limits a string value to 256 characters.
     * @param value the string to limit
     * @return the truncated string if longer than 256 characters, otherwise the original
     */
    private String limitTo256(String value) {
    return value.length() <= 256 ? value : value.substring(0, 256);
  }

    /**
     * Creates new Task entities from a list of TaskRequests.
     * @param exercise the exercise to associate the tasks with
     * @param taskRequests the list of task requests to convert
     * @return a list of Task entities
     */
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

    /**
     * Creates or updates a station with the specified properties.
     * Also creates an achievement for the station.
     * @param uid the UUID for the station
     * @param title the title of the station
     * @param description the description of the station
     * @param hint the hint for the station
     * @param image the image URL for the station
     * @param backgroundColor the background color for the station
     * @param exerciseUids the list of exercise UUIDs associated with the station
     * @param isBoss whether this is a boss station
     */
    private void upsertStation(UUID uid, String title, String description, String hint, String image,
        String backgroundColor, List<UUID> exerciseUids, boolean isBoss) {
    Station station = stationRepository.findByUid(uid).orElseGet(Station::new);

    station.setUid(uid);
    station.setTitle(title);
    station.setDescription(description);
    station.setHint(hint);
    station.setImage(image);
    station.setBackgroundColor(backgroundColor);
    station.setExerciseUids(new ArrayList<>(exerciseUids));
    station.setBoss(isBoss);

    stationRepository.save(station);

    createStationAchievement(uid, title, description, isBoss);
  }

    /**
     * Creates an achievement for a station if it doesn't already exist.
     * @param title the title of the station (used as achievement title)
     * @param description the description of the station (used as achievement description)
     * @param isBoss whether this is a boss station (affects achievement category)
     */
  private void createStationAchievement(UUID stationUid, String title, String description, boolean isBoss) {
    if (achievementRepository.findBySource(stationUid.toString()).isPresent()) {
      return;
    }

    Achievement achievement = new Achievement();
    achievement.setUUID(UUID.randomUUID());
    achievement.setTitle(title);
    achievement.setDescription(description);
    achievement.setCategory(isBoss ? AchievementCategory.SPECIAL : AchievementCategory.STOP);
    achievement.setSource(stationUid.toString());
    achievement.setReward(isBoss ? "Spesialmedalje for å ha slått bossen" : null);

    achievementRepository.save(achievement);
  }

    /**
     * Creates or updates a world with the specified properties.
     * @param uid the UUID for the world
     * @param title the title of the world
     * @param description the description of the world
     * @param image the image URL for the world
     * @param stationUids the list of station UUIDs associated with the world
     */
    private void upsertWorld(UUID uid, String title, String description, String image, List<UUID> stationUids) {
    World world = worldRepository.findByUid(uid).orElseGet(World::new);

    world.setUid(uid);
    world.setTitle(title);
    world.setDescription(description);
    world.setImage(image);
    world.setStationUids(new ArrayList<>(stationUids));

    worldRepository.save(world);
  }


  private String media(String objectKey) {
    return mediaStorageService.publicUrl(objectKey);
  }

  /**
     * Creates a multiple choice task request.
     * @param introText the introduction text for the task
     * @param feedbackCorrect feedback to show when answer is correct
     * @param feedbackIncorrect feedback to show when answer is incorrect
     * @param promptTitle the title for the prompt
     * @param promptText the text for the prompt
     * @param imageUrl the URL of the image to display
     * @param imageAlt alt text for the image
     * @param options the list of options to choose from
     * @param correctChoice the ID of the correct option
     * @return a TaskRequest configured for multiple choice
     */
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

    /**
     * Creates an X-or-Y task request where users choose between two options.
     * @param introText the introduction text for the task
     * @param feedbackCorrect feedback to show when answer is correct
     * @param feedbackIncorrect feedback to show when answer is incorrect
     * @param promptTitle the title for the prompt
     * @param promptText the text for the prompt
     * @param xImageUrl the image URL for option X
     * @param xImageAlt alt text for option X image
     * @param xEyebrow the eyebrow text for option X
     * @param xTitle the title for option X
     * @param xText the description text for option X
     * @param yImageUrl the image URL for option Y
     * @param yImageAlt alt text for option Y image
     * @param yEyebrow the eyebrow text for option Y
     * @param yTitle the title for option Y
     * @param yText the description text for option Y
     * @param correctChoice the correct choice (X or Y)
     * @return a TaskRequest configured for X-or-Y comparison
     */
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

    /**
     * Creates an email hotspots task request where users click on suspicious parts.
     * @param introText the introduction text for the task
     * @param feedbackCorrect feedback to show when answer is correct
     * @param feedbackIncorrect feedback to show when answer is incorrect
     * @param promptTitle the title for the prompt
     * @param promptText the text for the prompt
     * @param email the email content with hotspots
     * @param targets the list of targets (hotspots) to click
     * @return a TaskRequest configured for email hotspots
     */
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

    /**
     * Creates a sort-into-buckets task request where users categorize items.
     * @param introText the introduction text for the task
     * @param feedbackCorrect feedback to show when answer is correct
     * @param feedbackIncorrect feedback to show when answer is incorrect
     * @param promptTitle the title for the prompt
     * @param promptText the text for the prompt
     * @param unplacedLabel label for items not yet sorted
     * @param buckets the list of buckets to sort into
     * @param items the list of items to sort
     * @return a TaskRequest configured for sort-into-buckets
     */
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

    /**
     * Creates a password strength task request.
     * @param introText the introduction text for the task
     * @param feedbackCorrect feedback to show when answer is correct
     * @param feedbackIncorrect feedback to show when answer is incorrect
     * @param promptTitle the title for the prompt
     * @param promptText the text for the prompt
     * @param helperText helper text to display
     * @param mode the mode (CREATE or IMPROVE)
     * @param placeholder placeholder text for the input
     * @param startingPassword the starting password (for IMPROVE mode)
     * @param minLength minimum password length required
     * @param minScoreToPass minimum score required to pass
     * @param commonPatterns list of common patterns to reject
     * @return a TaskRequest configured for password strength
     */
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

    /**
     * Generates a stable UUID from a string value for consistent mock data identifiers.
     * @param value the string value to convert to UUID
     * @return a deterministic UUID based on the input string
     */
    private static UUID stableUuid(String value) {
    return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8));
  }
}
