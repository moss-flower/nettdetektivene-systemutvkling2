export type WorldData = {
  uid: string;
  title: string;
  description: string;
  image: string;
  stationUids?: string[];
};

export class World {
  uid: string;
  title: string;
  description: string;
  image: string;
  stationUids: string[];

  constructor(data: WorldData) {
    this.uid = data.uid;
    this.title = data.title;
    this.description = data.description;
    this.image = data.image;
    this.stationUids = data.stationUids ?? [];
  }

  static fromBackend(data: WorldData): World {
    return new World(data);
  }

  static async backendFetchAllWorlds(): Promise<World[]> {
    const { $api } = useNuxtApp();
    const worlds = await $api<WorldData[]>("/worlds", {
      method: "GET",
    });

    return worlds.map((world) => World.fromBackend(world));
  }

  static async backendFetchWorldByUid(uid: string): Promise<World | null> {
    const { $api } = useNuxtApp();

    try {
      const world = await $api<WorldData>(`/worlds/${uid}`, {
        method: "GET",
      });

      return World.fromBackend(world);
    } catch {
      return null;
    }
  }
}
