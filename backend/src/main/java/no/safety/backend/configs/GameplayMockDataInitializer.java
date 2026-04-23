package no.safety.backend.configs;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import no.safety.backend.excercise.model.Exercise;
import no.safety.backend.excercise.repository.ExerciseRepository;
import no.safety.backend.station.model.Station;
import no.safety.backend.station.repository.StationRepository;
import no.safety.backend.task.content.MultipleChoiceContent;
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

  private static final String PLACEHOLDER_IMAGE = "https://placehold.co/1200x720?text=Safety+Lab";

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
        "Undersok hvilke nyhetssaker som virker ekte og hvilke faresignaler du bor sjekke.",
        List.of(
            multipleChoiceTask(
                "Velg den forklaringen som best viser hvorfor saken virker trygg a stole pa.",
                "Bra observert. En tydelig avsender gjor det lettere a sjekke hvem som star bak informasjonen.",
                "Se etter tegn som hjelper deg a sjekke saken, som tydelig avsender og konkret informasjon.",
                "Hvilken forklaring passer best?",
                "Se pa skjermbildet og velg det beste tegnet pa at saken virker troverdig.",
                "https://placehold.co/1200x720?text=Troverdig+nyhet",
                "Skjermbilde av en rolig nyhetssak med tydelig avsender",
                List.of(
                    new MultipleChoiceContent.Option("a", "Saken har en tydelig avsender du kan sjekke"),
                    new MultipleChoiceContent.Option("b", "Saken bruker mange utropstegn og sterke ord")
                ),
                "a"
            ),
            multipleChoiceTask(
                "Se pa skjermbildet og velg den beste grunnen til at saken virker falsk.",
                "Riktig. Nar en overskrift prover a sjokkere og du ikke kjenner avsenderen, bor du stoppe opp og sjekke mer.",
                "Tenk pa hvilke tegn som faktisk svekker troverdigheten, ikke bare hvordan saken ser ut visuelt.",
                "Hvorfor virker denne saken falsk?",
                "Velg forklaringen som passer best til det du ser i skjermbildet.",
                "https://placehold.co/1200x720?text=Nyhetsskjermbilde",
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
        "Sammenlign nyhetssaker og finn ut hva du bor sjekke for du tror eller deler.",
        List.of(
            multipleChoiceTask(
                "Velg saken som virker mest palitelig nar du ser pa sprak, avsender og forklaringer.",
                "Riktig. Den gode saken forklarer hva som har skjedd og peker til en kjent avsender.",
                "Se etter kjent avsender, konkret sprak og om saken forklarer hvor informasjonen kommer fra.",
                "Hva er det tydeligste faresignalet?",
                "Saken ser profesjonell ut, men ett tegn gjor at du bor vaere ekstra skeptisk.",
                "https://placehold.co/1200x720?text=Nyhet+uten+kilde",
                "Skjermbilde av en nyhetsartikkel uten tydelige kilder",
                List.of(
                    new MultipleChoiceContent.Option("a", "Den bruker et bilde ved siden av teksten"),
                    new MultipleChoiceContent.Option("b", "Den mangler tydelige kilder eller forklaringer"),
                    new MultipleChoiceContent.Option("c", "Den er skrevet om et tema mange bryr seg om")
                ),
                "b"
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
                "https://placehold.co/1200x720?text=Bildeblikk",
                "Skjermbilde av et mistenkelig bilde",
                List.of(
                    new MultipleChoiceContent.Option("a", "Et helt vanlig ekte bilde"),
                    new MultipleChoiceContent.Option("b", "Et bilde med detaljer som virker manipulerte")
                ),
                "b"
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
                "https://placehold.co/1200x720?text=Bildedetektiv",
                "Skjermbilde av et bilde med rare detaljer",
                List.of(
                    new MultipleChoiceContent.Option("a", "Ser detaljer som hender, tekst og bakgrunn naturlige ut?"),
                    new MultipleChoiceContent.Option("b", "Har bildet mange fine farger?")
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
                "https://placehold.co/1200x720?text=E-post",
                "Skjermbilde av en mistenkelig e-post",
                List.of(
                    new MultipleChoiceContent.Option("a", "Avsenderadressen ser rar ut"),
                    new MultipleChoiceContent.Option("b", "Meldingen er kort")
                ),
                "a"
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
                "https://placehold.co/1200x720?text=E-postdetektiv",
                "Skjermbilde av en mistenkelig e-post med lenke",
                List.of(
                    new MultipleChoiceContent.Option("a", "Den bruker press og en lenke som virker rar"),
                    new MultipleChoiceContent.Option("b", "Den har skolens logo"),
                    new MultipleChoiceContent.Option("c", "Den nevner et kjent tema")
                ),
                "a"
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
                "https://placehold.co/1200x720?text=Passord",
                "Illustrasjon av et svakt passord",
                List.of(
                    new MultipleChoiceContent.Option("a", "Det bruker navn og arstall som andre kan kjenne til"),
                    new MultipleChoiceContent.Option("b", "Det starter med stor bokstav")
                ),
                "a"
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
                "https://placehold.co/1200x720?text=Passordmester",
                "Illustrasjon av samme passord pa flere kontoer",
                List.of(
                    new MultipleChoiceContent.Option("a", "Da kan noen prove samme passord pa flere kontoer"),
                    new MultipleChoiceContent.Option("b", "Da blir passordet lettere a skrive")
                ),
                "a"
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
                "https://placehold.co/1200x720?text=Trygg+handel",
                "Skjermbilde av en tvilsom nettbutikk",
                List.of(
                    new MultipleChoiceContent.Option("a", "Butikken bruker en rar nettadresse som ikke ser norsk ut"),
                    new MultipleChoiceContent.Option("b", "Produktbildet er stort og fargerikt")
                ),
                "a"
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
                "https://placehold.co/1200x720?text=Nettbutikk",
                "Sammenligning av to nettbutikker",
                List.of(
                    new MultipleChoiceContent.Option("a", "Spillbutikk.no med tydelig kontaktinfo og kjent betaling"),
                    new MultipleChoiceContent.Option("b", "Superdeal-game-click.biz med pressende tilbud")
                ),
                "a"
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
                "https://placehold.co/1200x720?text=Sosiale+medier",
                "Skjermbilde av et stressende innlegg i sosiale medier",
                List.of(
                    new MultipleChoiceContent.Option("a", "Stoppe opp og sjekke innholdet for du deler"),
                    new MultipleChoiceContent.Option("b", "Dele raskt fordi mange andre gjor det")
                ),
                "a"
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
                "https://placehold.co/1200x720?text=Del+eller+vent",
                "Skjermbilde av et viralt innlegg",
                List.of(
                    new MultipleChoiceContent.Option("a", "Vent og sjekk"),
                    new MultipleChoiceContent.Option("b", "Del med en gang")
                ),
                "a"
            )
        )
    );
  }

  private void seedStations() {
    upsertStation(NEWSSTAND_STATION_UID, "Nyhetskvartalet",
        "Laer a skille mellom ekte nyheter, clickbait og emosjonelt sprak.",
        PLACEHOLDER_IMAGE, "#cfe9ff",
        List.of(NEWSSTAND_EXERCISE_1_UID, NEWSSTAND_EXERCISE_2_UID));

    upsertStation(PHOTOGRAPH_STATION_UID, "Fotografen",
        "Laer a kjenne igjen ekte, KI-genererte og manipulerte bilder.",
        PLACEHOLDER_IMAGE, "#5F5F5F",
        List.of(PHOTOGRAPH_EXERCISE_1_UID, PHOTOGRAPH_EXERCISE_2_UID));

    upsertStation(POSTOFFICE_STATION_UID, "Postkontoret",
        "Ov pa a avslore falske e-poster, mistenkelige lenker og rare avsendere.",
        PLACEHOLDER_IMAGE, "#fde68a",
        List.of(POSTOFFICE_EXERCISE_1_UID, POSTOFFICE_EXERCISE_2_UID));

    upsertStation(PASSWORDBANK_STATION_UID, "Passordbanken",
        "Laer hva som gjor et passord sterkt, trygt og lett a huske.",
        PLACEHOLDER_IMAGE, "#1D5232",
        List.of(PASSWORDBANK_EXERCISE_1_UID, PASSWORDBANK_EXERCISE_2_UID));

    upsertStation(MARKETPLACE_STATION_UID, "Markedsplassen",
        "Laer a vurdere om nettsider og nettbutikker er trygge eller mistenkelige.",
        PLACEHOLDER_IMAGE, "#962C2C",
        List.of(MARKETPLACE_EXERCISE_1_UID, MARKETPLACE_EXERCISE_2_UID));

    upsertStation(SOCIALMEDIA_STATION_UID, "Den sosiale moteplassen",
        "Laer a tenke for du deler, og a oppdage manipulering og gruppepress.",
        PLACEHOLDER_IMAGE, "#fbcfe8",
        List.of(SOCIALMEDIA_EXERCISE_1_UID, SOCIALMEDIA_EXERCISE_2_UID));

    upsertStation(DATACENTER_STATION_UID, "Datasenteret",
        "Finalebanen der du bruker det du har laert for a bekjempe datatyven.",
        PLACEHOLDER_IMAGE, "#bfdbfe",
        List.of());
  }

  private void seedWorlds() {
    upsertWorld(SCHOOLYARD_WORLD_UID,
        "Hovedloypa som tar eleven gjennom nyheter, bilder, e-post, passord, nettbutikker, sosiale medier og finalen.",
        PLACEHOLDER_IMAGE,
        List.of(
            NEWSSTAND_STATION_UID,
            PHOTOGRAPH_STATION_UID,
            POSTOFFICE_STATION_UID,
            PASSWORDBANK_STATION_UID,
            MARKETPLACE_STATION_UID,
            SOCIALMEDIA_STATION_UID,
            DATACENTER_STATION_UID
        ));

    upsertWorld(QUICKSTART_WORLD_UID,
        "En kortere verden for testing og rask gjennomspilling av utvalgte stasjoner.",
        PLACEHOLDER_IMAGE,
        List.of(NEWSSTAND_STATION_UID, POSTOFFICE_STATION_UID, DATACENTER_STATION_UID));

    upsertWorld(CREATIVE_LAB_WORLD_UID,
        "En verden for klassen som tester ulike kombinasjoner av stasjoner i en egen rekkefolge.",
        PLACEHOLDER_IMAGE,
        List.of(PHOTOGRAPH_STATION_UID, SOCIALMEDIA_STATION_UID, MARKETPLACE_STATION_UID, DATACENTER_STATION_UID));
  }

  private void upsertExercise(UUID publicId, String title, String difficultyName, String description,
      List<TaskRequest> taskRequests) {
    Exercise exercise = exerciseRepository.findByPublicId(publicId).orElseGet(Exercise::new);

    exercise.setPublicId(publicId);
    exercise.setTitle(title);
    exercise.setDifficultyName(difficultyName);
    exercise.setDescription(description);
    exercise.getTasks().clear();
    exercise.getTasks().addAll(newTasks(exercise, taskRequests));

    exerciseRepository.save(exercise);
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
      String backgroundColor, List<UUID> exerciseUids) {
    Station station = stationRepository.findById(uid).orElseGet(Station::new);

    station.setUid(uid);
    station.setTitle(title);
    station.setDescription(description);
    station.setImage(image);
    station.setBackgroundColor(backgroundColor);
    station.setExerciseUids(new ArrayList<>(exerciseUids));

    stationRepository.save(station);
  }

  private void upsertWorld(UUID uid, String description, String image, List<UUID> stationUids) {
    World world = worldRepository.findById(uid).orElseGet(World::new);

    world.setUid(uid);
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

  private static UUID stableUuid(String value) {
    return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8));
  }
}
