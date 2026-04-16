import placeholderImage from "~/assets/images/placeholder.jpeg";

export type StationBase = {
  id: string;
  name: string;
  description: string;
  image: string;
  isUnlocked: boolean;
  completedTasks: boolean[]
};

export class StationFactory {
  static mockData(): StationBase[] {
    const stationDataList: StationBase[] = [
      {
        id: "newsStand",
        name: "Nyhetskvartalet",
        description:
          "Laer a skille mellom ekte nyheter, clickbait og emosjonelt spraak.",
        image: placeholderImage,
        isUnlocked: true,
        completedTasks: [false, false, false],
      },
      {
        id: "photograph",
        name: "Fotografen",
        description:
          "Laer a kjenne igjen ekte, KI-genererte og manipulerte bilder.",
        image: placeholderImage,
        isUnlocked: false,
        completedTasks: [false, false, false],
      },
      {
        id: "postOffice",
        name: "Postkontoret",
        description:
          "Ov pa a avslore falske e-poster, mistenkelige lenker og rare avsendere.",
        image: placeholderImage,
        isUnlocked: false,
        completedTasks: [false, false, false],
      },
      {
        id: "passwordBank",
        name: "Passordbanken",
        description:
          "Laer hva som gjor et passord sterkt, trygt og lett a huske.",
        image: placeholderImage,
        isUnlocked: false,
        completedTasks: [false, false, false],
      },
      {
        id: "marketplace",
        name: "Markedsplassen",
        description:
          "Laer a vurdere om nettsider og nettbutikker er trygge eller mistenkelige.",
        image: placeholderImage,
        isUnlocked: false,
        completedTasks: [false, false, false],
      },
      {
        id: "socialMedia",
        name: "Den sosiale moteplassen",
        description:
          "Laer a tenke for du deler, og a oppdage manipulering og gruppepress.",
        image: placeholderImage,
        isUnlocked: false,
        completedTasks: [false, false, false],
      },
      {
        id: "datacenter",
        name: "Datasenteret",
        description:
          "Finalebanen der du bruker det du har laert for a bekjempe datatyven.",
        image: placeholderImage,
        isUnlocked: false,
        completedTasks: [false, false, false],
      },
    ];

    return stationDataList;
  }

  static findById(id: string): StationBase | undefined {
    return this.mockData().find((station) => station.id === id);
  }
}