import placeholderImage from "~/assets/images/placeholder.jpeg";
import { Exercise } from "~/composables/types/ExerciseType";

export type StationStatus = {
  completedExercises: boolean[];
};

export type StationData = {
  uid: string;
  title: string;
  description: string;
  image: string;
  exerciseUids?: Exercise["uid"][];
};

const buildCompletedExercises = (exerciseCount: number) =>
  Array.from({ length: exerciseCount }, () => false);

export class Station {
  uid: string;
  title: string;
  description: string;
  image: string;
  exerciseUids: Exercise["uid"][];

  constructor(data: StationData) {
    this.uid = data.uid;
    this.title = data.title;
    this.description = data.description;
    this.image = data.image;
    this.exerciseUids = data.exerciseUids ?? [];
  }

  get name() {
    return this.title;
  }

  isUnlocked(): boolean {
    return true;
  }

  isExerciseUnlocked(exerciseUid: Exercise["uid"]): boolean {
    if (!this.isUnlocked()) {
      return false;
    }

    const exerciseIndex = this.exerciseUids.findIndex((uid) => uid === exerciseUid);

    if (exerciseIndex === -1) {
      return false;
    }

    if (exerciseIndex === 0) {
      return true;
    }

    return this.getMockStatus().completedExercises.slice(0, exerciseIndex).every(Boolean);
  }

  getMockStatus(): StationStatus {
    return Station.getMockStatusById(this.exerciseUids.length);
  }

  static getMockStatusById(exerciseCount = 0): StationStatus {
    return {
      completedExercises: buildCompletedExercises(exerciseCount),
    };
  }

  static mockData(): Station[] {
    return [
      new Station({
        uid: "newsstand",
        title: "Nyhetskvartalet",
        description: "Laer a skille mellom ekte nyheter, clickbait og emosjonelt spraak.",
        image: placeholderImage,
        exerciseUids: ["newsstand-exercise-1", "newsstand-exercise-2"],
      }),
      new Station({
        uid: "photograph",
        title: "Fotografen",
        description: "Laer a kjenne igjen ekte, KI-genererte og manipulerte bilder.",
        image: placeholderImage,
      }),
      new Station({
        uid: "postoffice",
        title: "Postkontoret",
        description: "Ov pa a avslore falske e-poster, mistenkelige lenker og rare avsendere.",
        image: placeholderImage,
        exerciseUids: ["postoffice-exercise-1"],
      }),
      new Station({
        uid: "passwordbank",
        title: "Passordbanken",
        description: "Laer hva som gjor et passord sterkt, trygt og lett a huske.",
        image: placeholderImage,
      }),
      new Station({
        uid: "marketplace",
        title: "Markedsplassen",
        description: "Laer a vurdere om nettsider og nettbutikker er trygge eller mistenkelige.",
        image: placeholderImage,
      }),
      new Station({
        uid: "socialmedia",
        title: "Den sosiale moteplassen",
        description: "Laer a tenke for du deler, og a oppdage manipulering og gruppepress.",
        image: placeholderImage,
      }),
      new Station({
        uid: "datacenter",
        title: "Datasenteret",
        description: "Finalebanen der du bruker det du har laert for a bekjempe datatyven.",
        image: placeholderImage,
      }),
    ];
  }

  static findByUid(uid: string): Station | null {
    const normalizedUid = uid.trim().toLowerCase();

    return this.mockData().find((station) => station.uid.trim().toLowerCase() === normalizedUid) ?? null;
  }

  static backendFetchAllStations(): Station[] {
    return this.mockData();
  }

  // push a station to backend and retriev a uid from backend, then return the uid
  static backendCreateStation(station: Station): string {
    if (station.uid) {
      throw new Error("Station already has a uid. Cannot create station with existing uid.");
    }
    return "some uid here from backend";
  }

  static backendFetchStationByUid(uid: string): Station | null {
    return this.findByUid(uid);
  }

}
