export type StationStatus = {
  completedExercises: boolean[];
};

export type StationData = {
  uid: string;
  title: string;
  description: string;
  image: string;
  backgroundColor?: string;
  exerciseUids?: string[];
  isBoss: boolean;
};

export class Station {
  uid: string;
  title: string;
  description: string;
  image: string;
  backgroundColor: string;
  exerciseUids: string[];
  isBoss: boolean;

  constructor(data: StationData) {
    this.uid = data.uid;
    this.title = data.title;
    this.description = data.description;
    this.image = data.image;
    this.backgroundColor = data.backgroundColor ?? "#cfe9ff";
    this.exerciseUids = data.exerciseUids ?? [];
    this.isBoss = true; //TODO: true is just for testing, replace with: data.isBoss ?? false;
  }

  get name() {
    return this.title;
  }

  static fromBackend(data: StationData): Station {
    return new Station(data);
  }

  static async backendFetchAllStations(): Promise<Station[]> {
    const { $api } = useNuxtApp();
    const stations = await $api<StationData[]>("/stations", {
      method: "GET",
    });

    return stations.map((station) => Station.fromBackend(station));
  }

  static async backendFetchStationByUid(uid: string): Promise<Station | null> {
    const { $api } = useNuxtApp();

    try {
      const station = await $api<StationData>(`/stations/${uid}`, {
        method: "GET",
      });

      return Station.fromBackend(station);
    } catch {
      return null;
    }
  }
}
