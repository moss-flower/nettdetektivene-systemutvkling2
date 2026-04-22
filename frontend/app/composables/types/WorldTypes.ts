import placeholderImage from "~/assets/images/placeholder.jpeg";
import { Station } from "~/composables/types/StationType";

export type WorldData = {
	uid: string;
	description: string;
	image: string;
	stationUids?: Station["uid"][];
};

export class World {
	uid: string;
	description: string;
	image: string;
	stationUids: Station["uid"][];

	constructor(data: WorldData) {
		this.uid = data.uid;
		this.description = data.description;
		this.image = data.image;
		this.stationUids = data.stationUids ?? [];
	}

	static mockData(): World[] {
		return [
			new World({
				uid: "schoolyard",
				description: "Hovedløypa som tar eleven gjennom nyheter, bilder, e-post, passord, nettbutikker, sosiale medier og finalen.",
				image: placeholderImage,
				stationUids: [
					"newsstand",
					"photograph",
					"postoffice",
					"passwordbank",
					"marketplace",
					"socialmedia",
					"datacenter",
				],
			}),
			new World({
				uid: "quickstart",
				description: "En kortere verden for testing og rask gjennomspilling av utvalgte stasjoner.",
				image: placeholderImage,
				stationUids: ["newsstand", "postoffice", "datacenter"],
			}),
			new World({
				uid: "creative-lab",
				description: "En verden for klassen som tester ulike kombinasjoner av stasjoner i en egen rekkefølge.",
				image: placeholderImage,
				stationUids: ["photograph", "socialmedia", "marketplace", "datacenter"],
			}),
		];
	}

	static findByUid(uid: string): World | null {
		const normalizedUid = uid.trim().toLowerCase();

		return this.mockData().find((world) => world.uid.trim().toLowerCase() === normalizedUid) ?? null;
	}

	static backendFetchAllWorlds(): World[] {
		return this.mockData();
	}

	// push a world to backend and retriev a uid from backend, then return the uid
	static backendCreateWorld(world: World): string {
		if (world.uid) {
			throw new Error("World already has a uid. Cannot create world with existing uid.");
		}

		return "some uid here from backend";
	}

	static backendFetchWorldByUid(uid: string): World | null {
		return this.findByUid(uid);
	}
}
