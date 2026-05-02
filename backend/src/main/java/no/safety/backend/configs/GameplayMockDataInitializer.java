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
import no.safety.backend.task.repository.TaskRepository;
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

  private static final String POSTOFFICE_FAKE_IPHONE_IMAGE = "seed/tasks/postkontoret/fake-you-won-iphone.jpeg";
  private static final String POSTOFFICE_FAKE_ACCOUNT_IMAGE = "seed/tasks/postkontoret/fake-your-account-will-be-closed.jpeg";
  private static final String POSTOFFICE_REAL_BOOK_IMAGE = "seed/tasks/postkontoret/real-book-reservation-reminder.png";
  private static final String POSTOFFICE_REAL_HIKE_IMAGE = "seed/tasks/postkontoret/real-hike-reminder.jpeg";
  private static final String POSTOFFICE_REAL_TIMETABLE_IMAGE = "seed/tasks/postkontoret/real-timeplan-next-week.png";
  private static final String POSTOFFICE_REAL_LIBRARY_IMAGE = "seed/tasks/postkontoret/real-welcome-to-library.jpeg";

  private static final String MARKETPLACE_FAKE_BIKE_IMAGE = "seed/tasks/markedsplassen/fake-bike-ad.jpeg";
  private static final String MARKETPLACE_FAKE_IPHONE_IMAGE = "seed/tasks/markedsplassen/fake-iphone-ad.jpeg";
  private static final String MARKETPLACE_FAKE_LAPTOP_IMAGE = "seed/tasks/markedsplassen/fake-laptop-ad.jpeg";
  private static final String MARKETPLACE_REAL_BIKE_IMAGE = "seed/tasks/markedsplassen/real-bike-ad.jpeg";
  private static final String MARKETPLACE_REAL_LAPTOP_IMAGE = "seed/tasks/markedsplassen/real-laptop-ad.jpeg";

  private static final String SOCIAL_BAD_SHARE_IMAGE = "seed/tasks/sosiale-moteplassen/bad-alle-ma-dele-laerer-skjuler-sannhet.jpeg";
  private static final String SOCIAL_BAD_SCHOOL_IMAGE = "seed/tasks/sosiale-moteplassen/bad-alle-skoler-stenger-neste-uke.jpeg";
  private static final String SOCIAL_BAD_CHIPS_IMAGE = "seed/tasks/sosiale-moteplassen/bad-farlige-chips-gjor-barn-syke.jpeg";
  private static final String SOCIAL_GOOD_TIPS_IMAGE = "seed/tasks/sosiale-moteplassen/good-vaer-skeptisk-til-trender.jpeg";
  private static final String SOCIAL_GOOD_CHIPS_IMAGE = "seed/tasks/sosiale-moteplassen/good-faktasjekk-farlige-chips.jpeg";
  private static final String SOCIAL_GOOD_SCHOOL_IMAGE = "seed/tasks/sosiale-moteplassen/good-udir-skoler-stenger-ikke.jpeg";

  private final ExerciseRepository exerciseRepository;
  private final StationRepository stationRepository;
  private final WorldRepository worldRepository;
  private final MediaStorageService mediaStorageService;
  private final AchievementRepository achievementRepository;
  private final TaskRepository taskRepository;

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
                "Den store banenen",
                "En gutt holder en kjempestor banan over hodet.",
                media(PHOTOGRAPH_REAL_DOG_IMAGE),
                "Hund som fanger en frisbee",
                "",
                "Hund med frisbee",
                "En hund som hopper etter en frisbee",
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
                "Hvilket bilde ville du stolt mest på?",
                "Velg bildet som virker mest vanlig og minst overraskende.",
                media(PHOTOGRAPH_AI_MARS_IMAGE),
                "Eiffeltårnet står på Mars",
                "",
                "Alternativ 1",
                "Et kjent tårn i et rødt landskap.",
                media(PHOTOGRAPH_REAL_PEOPLE_IMAGE),
                "Personer som går tur sammen",
                "",
                "Alternativ 2",
                "Mange personer går ute sammen.",
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

    upsertExercise(POSTOFFICE_EXERCISE_1_UID, "Før du klikker", "Lett",
        "Lær å kjenne igjen vanlige e-poster og de første tegnene på at noe er rart.",
        List.of(
            multipleChoiceTask(
                "Hva er det første du bør sjekke i denne meldingen?",
                "Bra. Avsenderen er ofte det viktigste å kontrollere først.",
                "Se etter noe som hjelper deg å forstå om meldingen er trygg å åpne.",
                "Hva bør du se på først?",
                "Velg det som sier mest om hvem som sendte meldingen.",
                media(POSTOFFICE_REAL_LIBRARY_IMAGE),
                "En vanlig e-post med tydelig avsender",
                List.of(
                    new MultipleChoiceContent.Option("a", "Hvem som sendte e-posten"),
                    new MultipleChoiceContent.Option("b", "Hvor mange linjer teksten har"),
                    new MultipleChoiceContent.Option("c", "Om det er et bilde øverst")
                ),
                "a"
            ),
            xOrYTask(
                "Hvilken melding virker mest som en vanlig e-post?",
                "Riktig. Den ene prøver å lokke deg raskt, mens den andre ser ut som en vanlig påminnelse.",
                "Se etter hvilken melding som virker mest rolig og vanlig.",
                "Hvilken melding ville du åpnet først?",
                "Velg den som virker mest hverdagslig.",
                media(POSTOFFICE_FAKE_IPHONE_IMAGE),
                "Du har vunnet en iPhone",
                "",
                "Melding 1",
                "Meldingen lover en premie og vil at du skal klikke raskt.",
                media(POSTOFFICE_REAL_BOOK_IMAGE),
                "Påminnelse om en reservert bok",
                "",
                "Melding 2",
                "Meldingen minner deg bare om en bok du har lånt.",
                XOrYContent.Choice.Y
            ),
            emailHotspotsTask(
                "Trykk på de delene av e-posten som virker mest mistenkelige.",
                "Bra jobbet. Du fant flere tegn på at meldingen ikke er trygg.",
                "Se etter avsenderen, lenken og ting som prøver å stresse deg.",
                "Finn faresignalene",
                "Trykk på det som gjør meldingen mistenkelig.",
                new EmailHotspotsContent.Email(
                    "Skole-IT",
                    "sender-name",
                    "support@skolekonto-trygg.info",
                    "sender-address",
                    "Kontoen din blir stengt i dag",
                    "subject",
                    "IT",
                    "I dag 08:15",
                    "received",
                    "Hei elev!",
                    "greeting",
                    List.of(
                        new EmailHotspotsContent.TextBlock("body-text", "Du må oppdatere kontoen din med en gang for at den ikke skal bli sperret.", "body-text-target"),
                        new EmailHotspotsContent.LinkBlock("body-link", "Oppdater her", "skolekonto-trygg.info/login", "Gjør det nå", "body-link-target")
                    ),
                    new EmailHotspotsContent.ActionButton("Logg inn", "skolenorge.no", "action-button"),
                    List.of(new EmailHotspotsContent.Attachment("attachment", "oppdatering.zip", "Uvanlig vedlegg", "attachment-target")),
                    "Hilsen support",
                    "closing"
                ),
                List.of(
                    new EmailHotspotsContent.Target("sender-name", false),
                    new EmailHotspotsContent.Target("sender-address", true),
                    new EmailHotspotsContent.Target("subject", true),
                    new EmailHotspotsContent.Target("received", false),
                    new EmailHotspotsContent.Target("greeting", false),
                    new EmailHotspotsContent.Target("body-text-target", true),
                    new EmailHotspotsContent.Target("body-link-target", true),
                    new EmailHotspotsContent.Target("action-button", false),
                    new EmailHotspotsContent.Target("attachment-target", true),
                    new EmailHotspotsContent.Target("closing", false)
                )
            )
        )
    );

    upsertExercise(POSTOFFICE_EXERCISE_2_UID, "E-postdetektiv", "Middels",
        "Sammenlign meldinger og finn tegn som hjelper deg å avgjøre om de er trygge.",
        List.of(
            multipleChoiceTask(
                "Hva virker mest normalt i denne meldingen?",
                "Riktig. En tydelig og kjent avsender er et godt tegn.",
                "Se etter det som gjør meldingen lett å forstå og kontrollere.",
                "Hva er det beste tegnet på at meldingen er grei?",
                "Velg det som virker mest som en vanlig skolemelding.",
                media(POSTOFFICE_REAL_TIMETABLE_IMAGE),
                "Melding om neste ukes timeplan",
                List.of(
                    new MultipleChoiceContent.Option("a", "Den kommer fra en tydelig avsender"),
                    new MultipleChoiceContent.Option("b", "Den lover en premie"),
                    new MultipleChoiceContent.Option("c", "Den ber deg logge inn med en gang")
                ),
                "a"
            ),
            xOrYTask(
                "Hvilken melding virker mest troverdig ved første blikk?",
                "Bra. Den ene prøver å få deg til å stresse, mens den andre ligner mer på en vanlig melding.",
                "Se etter hvilken melding som virker mest rolig og vanlig.",
                "Hvilken melding ville du sjekket først?",
                "Velg den som virker minst mistenkelig.",
                media(POSTOFFICE_FAKE_ACCOUNT_IMAGE),
                "Kontoen må oppdateres nå",
                "",
                "Melding 1",
                "Meldingen presser deg til å handle raskt.",
                media(POSTOFFICE_REAL_HIKE_IMAGE),
                "Påminnelse om tur i helgen",
                "",
                "Melding 2",
                "Meldingen virker som en vanlig beskjed uten press.",
                XOrYContent.Choice.Y
            ),
            multipleChoiceTask(
                "Hva er det tydeligste faresignalet i denne e-posten?",
                "Bra. Når en e-post lover noe stort og vil ha deg til å klikke raskt, bør du være ekstra forsiktig.",
                "Se etter det som gjør at du stopper opp og sjekker mer.",
                "Hva skurrer mest?",
                "Velg det tegnet som gjør e-posten mest mistenkelig.",
                media(POSTOFFICE_FAKE_IPHONE_IMAGE),
                "E-post om at du har vunnet en iPhone",
                List.of(
                    new MultipleChoiceContent.Option("a", "Den lover en premie og ber deg klikke raskt"),
                    new MultipleChoiceContent.Option("b", "Den har en kort overskrift"),
                    new MultipleChoiceContent.Option("c", "Den bruker et bilde øverst")
                ),
                "a"
            )
        )
    );

    upsertExercise(PASSWORDBANK_EXERCISE_1_UID, "Passorddetektiv", "Lett",
        "Lær hva som gjør et passord sterkt, trygt og mulig å huske.",
        List.of(
            sortIntoBucketsTask(
                "Sorter delene i riktig boks.",
                "Riktig. Du skilte mellom ting som ofte styrker et passord, og ting som ofte gjør det lettere å gjette.",
                "Tenk på hvilke valg som gjør passordet lengre og mindre opplagt.",
                "Hva er smart å bruke i et passord?",
                "Legg hvert kort i riktig kategori.",
                "Deler som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("smart", "Smart å bruke", "Valg som ofte gjør passordet sterkere.", "#d9fbe6"),
                    new SortIntoBucketsContent.Bucket("avoid", "Bør unngås", "Valg som ofte gjør passordet lettere å gjette.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("pw-1", "Flere ord som bare du husker", "smart", "Lengde og variasjon hjelper."),
                    new SortIntoBucketsContent.Item("pw-2", "Eget navn og fødselsår", "avoid", "Det kan andre ofte vite om deg."),
                    new SortIntoBucketsContent.Item("pw-3", "Et symbol eller tall som ikke er opplagt", "smart", "Små ekstra detaljer kan gjøre passordet vanskeligere å gjette."),
                    new SortIntoBucketsContent.Item("pw-4", "123 eller qwerty", "avoid", "Vanlige mønstre blir ofte prøvd først.")
                )
            ),
            passwordStrengthTask(
                "Lag et nytt passord til skolekontoen din.",
                "Bra jobbet. Passordet ditt var langt nok og vanskeligere å gjette.",
                "Prøv igjen med flere tegn og en bedre blanding av bokstaver, tall og symboler.",
                "Lag et nytt passord",
                "Lag et passord som ikke er lett å gjette, men som du fortsatt kan huske.",
                "Bruk minst 12 tegn og bland flere typer tegn uten å bruke navn eller enkle mønstre.",
                PasswordStrengthContent.Mode.CREATE,
                "Skriv passordet her",
                null,
                12,
                4,
                List.of("123", "qwerty", "passord", "skole", "ada")
            ),
            passwordStrengthTask(
                "Gjør startpassordet tryggere.",
                "Bra. Du bygde videre på startpassordet og gjorde det mye vanskeligere å gjette.",
                "Du må beholde noe av startpassordet, men gjøre det lengre og mindre opplagt.",
                "Forbedre passordet",
                "Tenk deg at noen bruker dette svake passordet allerede. Gjør det bedre uten å bytte det helt ut.",
                "Behold startpassordet som en del av det nye passordet, men legg til flere tegn og mer variasjon.",
                PasswordStrengthContent.Mode.IMPROVE,
                "Skriv det forbedrede passordet her",
                "Ada2026",
                12,
                4,
                List.of("123", "qwerty", "passord", "ada")
            )
        )
    );

    upsertExercise(PASSWORDBANK_EXERCISE_2_UID, "Passordmester", "Middels",
        "Øv på gjenbruk, forbedring og gode passordvaner i flere situasjoner.",
        List.of(
            sortIntoBucketsTask(
                "Sorter vanene i riktig boks.",
                "Riktig. Du skilte mellom vaner som beskytter kontoene dine, og vaner som gjør dem mer sårbare.",
                "Tenk på hva som skjer hvis noen får tak i ett av passordene dine.",
                "Hvilke vaner er lure?",
                "Legg hvert kort i riktig kategori.",
                "Vaner som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("smart", "Lur vane", "Valg som beskytter kontoene dine bedre.", "#d9fbe6"),
                    new SortIntoBucketsContent.Bucket("unsafe", "Utrygg vane", "Valg som kan gjøre flere kontoer sårbare.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("pw-5", "Bruke ulike passord til skole og spill", "smart", "Da blir ikke alt utsatt hvis ett passord lekker."),
                    new SortIntoBucketsContent.Item("pw-6", "Bruke samme passord overalt", "unsafe", "Da kan flere kontoer bli åpnet med ett funn."),
                    new SortIntoBucketsContent.Item("pw-7", "Lage et langt passord du kan huske", "smart", "Lengde hjelper mye."),
                    new SortIntoBucketsContent.Item("pw-8", "Dele passord med en venn", "unsafe", "Da mister du kontrollen over hvem som kan logge inn.")
                )
            ),
            passwordStrengthTask(
                "Forbedre et passord som brukes flere steder.",
                "Bra. Du gjorde passordet sterkere uten å la det bli stående nesten likt.",
                "Du må bygge videre på startpassordet med flere tegn og mer variasjon.",
                "Forbedre passordet",
                "Tenk deg at dette passordet allerede er brukt på flere kontoer. Gjør det bedre.",
                "Behold litt av startpassordet, men gjør det lengre og mindre lett å gjette.",
                PasswordStrengthContent.Mode.IMPROVE,
                "Skriv det forbedrede passordet her",
                "fotball123",
                13,
                4,
                List.of("123", "qwerty", "passord", "fotball")
            ),
            passwordStrengthTask(
                "Lag et nytt passord til en spillkonto.",
                "Bra jobbet. Passordet ditt ble langt nok og vanskeligere å gjette.",
                "Prøv igjen med mer lengde og mindre opplagte mønstre.",
                "Lag et nytt spillpassord",
                "Lag et passord som ikke bygger på navnet ditt, favorittlaget ditt eller en enkel tallrekke.",
                "Bruk minst 14 tegn og bland flere typer tegn på en måte du fortsatt kan huske.",
                PasswordStrengthContent.Mode.CREATE,
                "Skriv spillpassordet her",
                null,
                14,
                4,
                List.of("123", "qwerty", "passord", "fotball", "gaming")
            )
        )
    );

    upsertExercise(MARKETPLACE_EXERCISE_1_UID, "Trygg handel", "Lett",
        "Lær å oppdage de første tegnene på at en annonse eller butikk kan være mistenkelig.",
        List.of(
            multipleChoiceTask(
                "Hva bør få deg til å stoppe opp i denne annonsen?",
                "Bra. Når en annonse virker altfor god til å være sann, bør du sjekke mer før du trykker.",
                "Se etter det som gjør annonsen vanskelig å stole på med en gang.",
                "Hva skurrer mest?",
                "Velg det tydeligste faresignalet i annonsen.",
                media(MARKETPLACE_FAKE_IPHONE_IMAGE),
                "En annonse for en iPhone som virker mistenkelig",
                List.of(
                    new MultipleChoiceContent.Option("a", "Prisen og tilbudet virker altfor gode til å være sanne"),
                    new MultipleChoiceContent.Option("b", "Annonsen viser et tydelig produktbilde"),
                    new MultipleChoiceContent.Option("c", "Det står hva varen heter")
                ),
                "a"
            ),
            xOrYTask(
                "Hvilken annonse virker mest som en vanlig salgsannonse?",
                "Riktig. Den ene annonsen virker mer vanlig, mens den andre lover mye uten å virke like ryddig.",
                "Se etter hvilken annonse som virker mest rolig og vanlig.",
                "Hvilken annonse ville du sett nærmere på først?",
                "Velg den som virker minst mistenkelig ved første blikk.",
                media(MARKETPLACE_FAKE_BIKE_IMAGE),
                "En sykkelannonse som virker mistenkelig",
                "",
                "Annonse 1",
                "En sykkel ligger til salgs.",
                media(MARKETPLACE_REAL_BIKE_IMAGE),
                "En vanlig sykkelannonse",
                "",
                "Annonse 2",
                "En annen sykkel ligger til salgs.",
                XOrYContent.Choice.Y
            ),
            sortIntoBucketsTask(
                "Sorter tegnene i riktig boks.",
                "Riktig. Du skilte mellom ting som gjør en annonse lettere å stole på, og ting som bør sjekkes nærmere.",
                "Tenk på hvilke detaljer som virker ryddige, og hvilke som virker pressende eller uklare.",
                "Hva er greit, og hva bør sjekkes?",
                "Legg hvert kort i riktig kategori.",
                "Kort som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("shop-safe", "Trygge tegn", "Detaljer som ofte gjør en annonse lettere å kontrollere.", "#d9fbe6"),
                    new SortIntoBucketsContent.Bucket("shop-warn", "Faresignaler", "Detaljer som bør få deg til å stoppe opp.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("shop-1", "Tydelig informasjon om varen", "shop-safe", "Da blir annonsen lettere å forstå og sjekke."),
                    new SortIntoBucketsContent.Item("shop-2", "Pris som virker altfor lav", "shop-warn", "Et ekstremt godt tilbud kan være et faresignal."),
                    new SortIntoBucketsContent.Item("shop-3", "Selger svarer rolig og konkret", "shop-safe", "Rolige og tydelige svar gjør det lettere å vurdere annonsen."),
                    new SortIntoBucketsContent.Item("shop-4", "Melding om at du må betale med en gang", "shop-warn", "Press brukes ofte for å få deg til å handle for fort.")
                )
            )
        )
    );

    upsertExercise(MARKETPLACE_EXERCISE_2_UID, "Nettbutikksjekk", "Middels",
        "Sammenlign annonser og kjøpssituasjoner, og finn tegn som hjelper deg å vurdere dem bedre.",
        List.of(
            multipleChoiceTask(
                "Hva gjør denne annonsen lettere å stole på?",
                "Bra. Tydelig informasjon og en vanlig pris gjør det lettere å vurdere annonsen.",
                "Se etter konkrete ting som gjør annonsen lettere å kontrollere.",
                "Hva styrker tilliten mest?",
                "Velg det beste tegnet på at annonsen virker grei.",
                media(MARKETPLACE_REAL_LAPTOP_IMAGE),
                "En vanlig annonse for en brukt laptop",
                List.of(
                    new MultipleChoiceContent.Option("a", "Annonsen forklarer varen rolig og med vanlige detaljer"),
                    new MultipleChoiceContent.Option("b", "Annonsen sier at tilbudet forsvinner om to minutter"),
                    new MultipleChoiceContent.Option("c", "Annonsen bruker store bokstaver i overskriften")
                ),
                "a"
            ),
            multipleChoiceTask(
                "Hva er det tydeligste faresignalet i denne annonsen?",
                "Riktig. Når en annonse prøver å lokke med veldig lav pris uten å virke ryddig, bør du være forsiktig.",
                "Se etter det som gjør annonsen vanskelig å stole på.",
                "Hva skurrer mest?",
                "Velg det sterkeste faresignalet i annonsen.",
                media(MARKETPLACE_FAKE_LAPTOP_IMAGE),
                "En laptopannonse som virker mistenkelig",
                List.of(
                    new MultipleChoiceContent.Option("a", "Annonsen virker altfor billig uten å forklare godt hvorfor"),
                    new MultipleChoiceContent.Option("b", "Annonsen viser hva slags vare som selges"),
                    new MultipleChoiceContent.Option("c", "Annonsen har et bilde av produktet")
                ),
                "a"
            ),
            xOrYTask(
                "Hvilken annonse ville du vært minst skeptisk til?",
                "Bra. Den ene annonsen virker mer vanlig, mens den andre prøver hardere å lokke deg raskt.",
                "Se etter hvilken annonse som virker mest rolig og mest mulig som et vanlig salg.",
                "Hvilken annonse virker mest troverdig?",
                "Velg den som virker enklest å kontrollere videre.",
                media(MARKETPLACE_FAKE_IPHONE_IMAGE),
                "En mistenkelig annonse for en iPhone",
                "",
                "Alternativ 1",
                "En telefon ligger ute til salgs.",
                media(MARKETPLACE_REAL_LAPTOP_IMAGE),
                "En vanlig annonse for en brukt laptop",
                "",
                "Alternativ 2",
                "En datamaskin ligger ute til salgs.",
                XOrYContent.Choice.Y
            )
        )
    );

    upsertExercise(SOCIALMEDIA_EXERCISE_1_UID, "Tenk for du deler", "Lett",
        "Lær å stoppe opp når innlegg spiller på sterke følelser eller ber deg dele raskt.",
        List.of(
            multipleChoiceTask(
                "Hva bør få deg til å stoppe opp i dette innlegget?",
                "Bra. Når et innlegg prøver å få deg til å dele med en gang, bør du være ekstra forsiktig.",
                "Se etter det som gjør at innlegget prøver å presse fram en rask reaksjon.",
                "Hva skurrer mest?",
                "Velg det tydeligste faresignalet i innlegget.",
                media(SOCIAL_BAD_SHARE_IMAGE),
                "Et innlegg som sier at alle må dele fordi læreren skjuler sannheten",
                List.of(
                    new MultipleChoiceContent.Option("a", "Innlegget prøver å få deg til å dele raskt uten å sjekke"),
                    new MultipleChoiceContent.Option("b", "Innlegget har mye tekst i bildet"),
                    new MultipleChoiceContent.Option("c", "Innlegget handler om skolen")
                ),
                "a"
            ),
            xOrYTask(
                "Hvilket innlegg virker mest som en rolig faktasjekk?",
                "Riktig. Det ene innlegget virker mer som en vanlig oppklaring, mens det andre prøver å få deg til å reagere raskt.",
                "Se etter hvilket innlegg som virker mest rolig og konkret.",
                "Hvilket innlegg ville du lest først?",
                "Velg innlegget som virker minst stressende.",
                media(SOCIAL_BAD_SCHOOL_IMAGE),
                "Et innlegg som sier at alle skoler stenger neste uke",
                "",
                "Innlegg 1",
                "Et innlegg om at skoler skal stenge neste uke.",
                media(SOCIAL_GOOD_SCHOOL_IMAGE),
                "Et innlegg fra Udir som forklarer at skolene ikke stenger",
                "",
                "Innlegg 2",
                "Et innlegg om hva som faktisk gjelder for skolene.",
                XOrYContent.Choice.Y
            ),
            sortIntoBucketsTask(
                "Sorter reaksjonene i riktig boks.",
                "Riktig. Du skilte mellom valg som gir deg tid til å tenke, og valg som sprer noe for fort.",
                "Tenk på hvilke valg som bremser delingen, og hvilke valg som sprer innholdet videre med en gang.",
                "Hva er smart å gjøre?",
                "Legg hvert kort i riktig kategori.",
                "Reaksjoner som skal sorteres",
                List.of(
                    new SortIntoBucketsContent.Bucket("smart", "Smart trekk", "Valg som hjelper deg å tenke deg om.", "#d9fbe6"),
                    new SortIntoBucketsContent.Bucket("hasty", "Dumt trekk", "Valg som kan spre noe videre for fort.", "#fee2e2")
                ),
                List.of(
                    new SortIntoBucketsContent.Item("social-1", "Sjekke hvem som står bak innlegget", "smart", "Da undersøker du innholdet først."),
                    new SortIntoBucketsContent.Item("social-2", "Dele fordi mange andre allerede har delt", "hasty", "Popularitet betyr ikke at noe stemmer."),
                    new SortIntoBucketsContent.Item("social-3", "Vente litt hvis innlegget gjør deg sint eller redd", "smart", "Sterke følelser er et tegn på at du bør tenke deg om."),
                    new SortIntoBucketsContent.Item("social-4", "Trykke del med en gang", "hasty", "Da sprer du innholdet uten å sjekke det.")
                )
            )
        )
    );

    upsertExercise(SOCIALMEDIA_EXERCISE_2_UID, "Del eller vent", "Middels",
        "Øv på å sammenligne innlegg og velge det som hjelper deg å sjekke før du deler.",
        List.of(
            multipleChoiceTask(
                "Hva er det smarteste første steget når du blir usikker på et innlegg?",
                "Ja. Det er tryggere å stoppe opp og sjekke enn å reagere med en gang.",
                "Tenk på hvilket valg som gir deg mer tid til å undersøke videre.",
                "Hva bør du gjøre først?",
                "Velg reaksjonen som hjelper deg å finne ut mer før du deler.",
                media(SOCIAL_GOOD_TIPS_IMAGE),
                "Et innlegg med tips om å være skeptisk til sosiale medier-trender",
                List.of(
                    new MultipleChoiceContent.Option("a", "Stoppe opp og sjekke om andre troverdige kilder sier det samme"),
                    new MultipleChoiceContent.Option("b", "Dele innlegget videre og spørre etterpå om det stemmer"),
                    new MultipleChoiceContent.Option("c", "Tro på innlegget hvis mange har kommentert det")
                ),
                "a"
            ),
            xOrYTask(
                "Hvilket innlegg virker mest troverdig ved første blikk?",
                "Bra. Det ene innlegget virker mer som en vanlig faktasjekk, mens det andre prøver hardere å skape reaksjon.",
                "Se etter hvilket innlegg som virker mest rolig og mest konkret.",
                "Hvilket innlegg ville du stolt minst på?",
                "Velg innlegget som virker enklest å kontrollere videre.",
                media(SOCIAL_BAD_CHIPS_IMAGE),
                "Et innlegg som sier at farlige chips gjør barn syke og at alle må se det",
                "",
                "Innlegg 1",
                "Et innlegg om at farlige chips gjør barn syke.",
                media(SOCIAL_GOOD_CHIPS_IMAGE),
                "Et rolig innlegg som faktasjekker ryktet om farlige chips",
                "",
                "Innlegg 2",
                "Et innlegg som undersøker ryktet om chipsene.",
                XOrYContent.Choice.Y
            ),
            multipleChoiceTask(
                "Hva er det beste tegnet på at et innlegg bør sjekkes ekstra før du deler det?",
                "Bra. Når et innlegg vil at alle skal dele med en gang og bruker sterke ord, bør du være ekstra forsiktig.",
                "Se etter det som gjør at innlegget prøver å styre følelsene dine raskt.",
                "Hva er det sterkeste faresignalet?",
                "Velg det som gjør innlegget mest mistenkelig.",
                media(SOCIAL_BAD_SHARE_IMAGE),
                "Et innlegg som sier at alle må dele fordi læreren skjuler sannheten",
                List.of(
                    new MultipleChoiceContent.Option("a", "Innlegget ber alle dele raskt og spiller på sinne eller frykt"),
                    new MultipleChoiceContent.Option("b", "Innlegget handler om noe på skolen"),
                    new MultipleChoiceContent.Option("c", "Innlegget har et stort bilde og mye tekst")
                ),
                "a"
            )
        )
    );

    upsertExercise(DATACENTER_EXERCISE_UID, "Datatyven", "Høy",
        "Bruk det vanskeligste du har lært i de andre stasjonene for å stoppe datatyven.",
        List.of(
            multipleChoiceTask(
                "Datatyven pakker en falsk nyhet inn som en vanlig sak. Hva er det tydeligste faresignalet?",
                "Riktig. Når en sak mangler tydelige kilder eller forklaringer, bør du stoppe opp.",
                "Se etter det som gjør saken vanskelig å kontrollere videre.",
                "Hva skurrer mest?",
                "Velg det sterkeste tegnet på at saken bør sjekkes mer.",
                media(NEWSSTAND_WARNING_PHONE_IMAGE),
                "En nyhet om gratis mobil uten tydelige kilder",
                List.of(
                    new MultipleChoiceContent.Option("a", "Den mangler tydelige kilder eller forklaringer"),
                    new MultipleChoiceContent.Option("b", "Den handler om noe mange elever er nysgjerrige på"),
                    new MultipleChoiceContent.Option("c", "Den har et stort bilde ved siden av teksten")
                ),
                "a"
            ),
            xOrYTask(
                "Datatyven viser to bilder. Hvilket virker mest troverdig ved første blikk?",
                "Bra. Det ene motivet virker mer som et vanlig bilde, mens det andre ikke henger like godt sammen.",
                "Se etter hvilket bilde som virker mest naturlig og minst kunstig.",
                "Hvilket bilde ville du stolt mest på?",
                "Velg bildet som virker enklest å tro på ved første blikk.",
                media(PHOTOGRAPH_AI_MARS_IMAGE),
                "Et kjent tårn i et rødt landskap",
                "",
                "Alternativ 1",
                "Et kjent tårn står i et rødt landskap.",
                media(PHOTOGRAPH_REAL_PEOPLE_IMAGE),
                "Personer som går tur sammen",
                "",
                "Alternativ 2",
                "To personer går ute sammen.",
                XOrYContent.Choice.Y
            ),
            emailHotspotsTask(
                "Datatyven sender en e-post som ser nesten ekte ut. Trykk på alle faresignalene.",
                "Bra. Du fant de viktigste tegnene på at e-posten ikke er trygg.",
                "Se etter press, rar avsender, uventet vedlegg og lenker du ikke kjenner igjen.",
                "Finn alt som skurrer",
                "Trykk på delene av e-posten som gjør den mistenkelig.",
                new EmailHotspotsContent.Email(
                    "Skolekonto",
                    "boss-sender-name",
                    "melding@skole-konto-verify.biz",
                    "boss-sender-address",
                    "Viktig: konto må oppdateres nå",
                    "boss-subject",
                    "SK",
                    "I dag 10:02",
                    "boss-received",
                    "Hei elev!",
                    "boss-greeting",
                    List.of(
                        new EmailHotspotsContent.TextBlock("boss-body-text", "Hvis du ikke svarer innen 10 minutter, blir kontoen din sperret.", "boss-body-text-target"),
                        new EmailHotspotsContent.LinkBlock("boss-body-link", "Åpne skolekonto", "secure-school-check.biz/login", "Må gjøres i dag", "boss-body-link-target")
                    ),
                    new EmailHotspotsContent.ActionButton("Les mer", "skoleportalen.no", "boss-action-button"),
                    List.of(new EmailHotspotsContent.Attachment("boss-attachment", "skjema.zip", "Uventet komprimert fil", "boss-attachment-target")),
                    "Hilsen administrasjonen",
                    "boss-closing"
                ),
                List.of(
                    new EmailHotspotsContent.Target("boss-sender-name", false),
                    new EmailHotspotsContent.Target("boss-sender-address", true),
                    new EmailHotspotsContent.Target("boss-subject", false),
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
                "Datatyven kjenner et gammelt passord. Forbedre det så det blir mye tryggere.",
                "Bra. Du gjorde passordet sterkere uten å la det bli stående nesten likt.",
                "Du må bygge videre på startpassordet med mer lengde og bedre variasjon.",
                "Forbedre passordet",
                "Tenk deg at dette passordet allerede er brukt flere steder. Gjør det vanskeligere å gjette.",
                "Behold litt av startpassordet, men gjør det lengre og mindre opplagt.",
                PasswordStrengthContent.Mode.IMPROVE,
                "Skriv det forbedrede passordet her",
                "fotball123",
                13,
                4,
                List.of("123", "qwerty", "passord", "fotball", "datatyv")
            ),
            xOrYTask(
                "Datatyven lokker med to annonser. Hvilken virker mest troverdig?",
                "Bra. Den ene annonsen virker mer som et vanlig salg, mens den andre prøver hardere å lokke deg raskt.",
                "Se etter hvilken annonse som virker mest rolig og enklest å kontrollere videre.",
                "Hvilken annonse ville du vært minst skeptisk til?",
                "Velg annonsen som virker mest vanlig ved første blikk.",
                media(MARKETPLACE_FAKE_IPHONE_IMAGE),
                "En mistenkelig annonse for en iPhone",
                "",
                "Alternativ 1",
                "En telefon ligger ute til salgs.",
                media(MARKETPLACE_REAL_LAPTOP_IMAGE),
                "En vanlig annonse for en brukt laptop",
                "",
                "Alternativ 2",
                "En datamaskin ligger ute til salgs.",
                XOrYContent.Choice.Y
            ),
            xOrYTask(
                "Datatyven sprer to innlegg. Hvilket virker mest som en faktisk oppklaring?",
                "Riktig. Det ene innlegget virker mer som en vanlig faktasjekk, mens det andre prøver å få deg til å reagere raskt.",
                "Se etter hvilket innlegg som virker mest rolig og mest konkret.",
                "Hvilket innlegg ville du lest først?",
                "Velg innlegget som virker minst stressende.",
                media(SOCIAL_BAD_CHIPS_IMAGE),
                "Et innlegg om at farlige chips gjør barn syke",
                "",
                "Innlegg 1",
                "Et innlegg om at farlige chips gjør barn syke.",
                media(SOCIAL_GOOD_CHIPS_IMAGE),
                "Et innlegg som faktasjekker ryktet om farlige chips",
                "",
                "Innlegg 2",
                "Et innlegg som undersøker ryktet om chipsene.",
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
    boolean existingExercise = exercise.getInternalId() != null;

    exercise.setPublicId(publicId);
    exercise.setTitle(title);
    exercise.setDifficultyName(difficultyName);
    exercise.setDescription(description);
    exercise.setHint(hint);
    exercise.setNotepadEntry(defaultNotepadEntry(publicId, title, description, hint));

    if (existingExercise) {
      taskRepository.deleteByExerciseId(exercise.getInternalId());
    }

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
