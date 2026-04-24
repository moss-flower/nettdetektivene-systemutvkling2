export type WorldData = {
  uid: string;
  title: string;
  description: string;
  image: string;
  stationUids?: string[];
};

const seededWorldUidAliases: Record<string, string> = {
  "schoolyard": "c63c53d3-23bb-3f8c-b511-6f3d106f1fa9",
  "quickstart": "0bc7b944-1356-300f-84ed-5bccb0bbc7ce",
  "creative-lab": "fa26a14f-445a-35bb-9c76-a25ec44579f7",
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

  static resolveBackendUid(uid: string): string {
    return seededWorldUidAliases[uid] ?? uid;
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
      const world = await $api<WorldData>(`/worlds/${World.resolveBackendUid(uid)}`, {
        method: "GET",
      });

      return World.fromBackend(world);
    } catch {
      return null;
    }
  }
}
