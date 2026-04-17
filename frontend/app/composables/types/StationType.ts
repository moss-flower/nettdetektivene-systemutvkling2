import placeholderImage from "~/assets/images/placeholder.jpeg";
import { Exercise } from "~/composables/types/ExerciseType";

export type StationStatus = {
  completedExercises: boolean[];
};

export type StationData = {
  id: string;
  slug: string;
  title: string;
  description: string;
  image: string;
  exerciseIds?: Exercise["id"][];
};

const buildCompletedExercises = (exerciseCount: number) =>
  Array.from({ length: exerciseCount }, () => false);

export class Station {
  id: string;
  slug: string;
  title: string;
  description: string;
  image: string;
  exerciseIds: Exercise["id"][];

  constructor(data: StationData) {
    this.id = data.id;
    this.slug = data.slug;
    this.title = data.title;
    this.description = data.description;
    this.image = data.image;
    this.exerciseIds = data.exerciseIds ?? [];
  }

  get name() {
    return this.title;
  }

  isUnlocked(): boolean {
    return true;
  }

  isExerciseUnlocked(exerciseId: Exercise["id"]): boolean {
    if (!this.isUnlocked()) {
      return false;
    }

    const exerciseIndex = this.exerciseIds.findIndex((id) => id === exerciseId);

    if (exerciseIndex === -1) {
      return false;
    }

    if (exerciseIndex === 0) {
      return true;
    }

    return this.getMockStatus().completedExercises.slice(0, exerciseIndex).every(Boolean);
  }

  getMockStatus(): StationStatus {
    return Station.getMockStatusById(this.exerciseIds.length);
  }

  static getMockStatusById(exerciseCount = 0): StationStatus {
    return {
      completedExercises: buildCompletedExercises(exerciseCount),
    };
  }

  static mockData(): Station[] {
    return [
      new Station({
        id: "station-newsstand",
        slug: "newsstand",
        title: "Nyhetskvartalet",
        description: "Laer a skille mellom ekte nyheter, clickbait og emosjonelt spraak.",
        image: placeholderImage,
        exerciseIds: ["newsstand-exercise-1", "newsstand-exercise-2"],
      }),
      new Station({
        id: "station-photograph",
        slug: "photograph",
        title: "Fotografen",
        description: "Laer a kjenne igjen ekte, KI-genererte og manipulerte bilder.",
        image: placeholderImage,
      }),
      new Station({
        id: "station-postoffice",
        slug: "postoffice",
        title: "Postkontoret",
        description: "Ov pa a avslore falske e-poster, mistenkelige lenker og rare avsendere.",
        image: placeholderImage,
        exerciseIds: ["postoffice-exercise-1"],
      }),
      new Station({
        id: "station-passwordbank",
        slug: "passwordbank",
        title: "Passordbanken",
        description: "Laer hva som gjor et passord sterkt, trygt og lett a huske.",
        image: placeholderImage,
      }),
      new Station({
        id: "station-marketplace",
        slug: "marketplace",
        title: "Markedsplassen",
        description: "Laer a vurdere om nettsider og nettbutikker er trygge eller mistenkelige.",
        image: placeholderImage,
      }),
      new Station({
        id: "station-socialmedia",
        slug: "socialmedia",
        title: "Den sosiale moteplassen",
        description: "Laer a tenke for du deler, og a oppdage manipulering og gruppepress.",
        image: placeholderImage,
      }),
      new Station({
        id: "station-datacenter",
        slug: "datacenter",
        title: "Datasenteret",
        description: "Finalebanen der du bruker det du har laert for a bekjempe datatyven.",
        image: placeholderImage,
      }),
    ];
  }

  static findById(id: string): Station | null {
    return this.mockData().find((station) => station.id === id) ?? null;
  }

  static findBySlug(slug: string): Station | null {
    const normalizedSlug = slug.trim().toLowerCase();

    return this.mockData().find((station) => station.slug.trim().toLowerCase() === normalizedSlug) ?? null;
  }

  static backendFetchAllStations(): Station[] {
    return this.mockData();
  }

  // push a station to backend and retriev a id from backend, then return the id
  static backendCreateStation(station: Station): string {
    if (station.id) {
      throw new Error("Station already has an id. Cannot create station with existing id.");
    }
    return "some id here from backend";
  }

  static backendFetchStationById(id: string): Station | null {
    return this.findById(id);
  }

  static backendFetchStationBySlug(slug: string): Station | null {
    return this.findBySlug(slug);
  }
}
