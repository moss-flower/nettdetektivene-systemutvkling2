import placeholderImage from "~/assets/images/placeholder.jpeg";
import type { Exercise } from "~/composables/types/ExerciseType";

export type StationStatus = {
  completedExercises: boolean[];
};

type StationStatusMock = {
  completedExercises?: boolean[];
};

export type StationData = {
  id: string;
  slug: string;
  title: string;
  description: string;
  image: string;
  exerciseIds?: Exercise["id"][];
};

const STATION_STATUS_MOCKS: Record<string, StationStatusMock> = {
  "station-newsstand": {},
  "station-photograph": {},
  "station-postoffice": {},
  "station-passwordbank": {},
  "station-marketplace": {},
  "station-socialmedia": {},
  "station-datacenter": {},
};

const buildCompletedExercises = (exerciseCount: number, completedExercises: boolean[] = []) =>
  Array.from({ length: exerciseCount }, (_, index) => completedExercises[index] ?? false);

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

  getMockStatus(): StationStatus {
    return Station.getMockStatusById(this.id, this.exerciseIds.length);
  }

  static getMockStatusById(id: string, exerciseCount = 0): StationStatus {
    const mockStatus = STATION_STATUS_MOCKS[id];

    return {
      completedExercises: buildCompletedExercises(exerciseCount, mockStatus?.completedExercises),
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

  static findById(id: string): Station | undefined {
    return this.mockData().find((station) => station.id === id);
  }
}
