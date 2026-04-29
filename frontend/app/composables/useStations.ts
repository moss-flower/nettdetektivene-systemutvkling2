import {
  mapStationFromResponse,
  type Station,
  type StationData,
  type StationRequest,
} from "~/composables/types/StationTypes";
import { World } from "~/composables/types/WorldTypes";

export type StudentStationsResult = {
  world: World | null;
  stations: Station[];
};

export function useStations() {
  const { $api } = useNuxtApp();
  const { student } = useClassrooms();

  const getAllStations = async (): Promise<Station[]> => {
    const stations = await $api<StationData[]>("/stations", {
      method: "GET",
    });

    return stations.map(mapStationFromResponse);
  };

  const getStationByUid = async (uid: string): Promise<Station | null> => {
    try {
      const station = await $api<StationData>(`/stations/${uid}`, {
        method: "GET",
      });

      return mapStationFromResponse(station);
    } catch {
      return null;
    }
  };

  const createStation = async (payload: StationRequest): Promise<Station> => {
    const station = await $api<StationData>("/stations", {
      method: "POST",
      body: payload,
    });

    return mapStationFromResponse(station);
  };

  const updateStation = async (
    uid: string,
    payload: StationRequest,
  ): Promise<Station> => {
    const station = await $api<StationData>(`/stations/${uid}`, {
      method: "PUT",
      body: payload,
    });

    return mapStationFromResponse(station);
  };

  const deleteStation = async (uid: string): Promise<void> => {
    await $api(`/stations/${uid}`, {
      method: "DELETE",
    });
  };

  const getStationsByUids = async (stationUids: string[]): Promise<Station[]> => {
    const loadedStations = await Promise.all(
      stationUids.map((stationUid) => getStationByUid(stationUid)),
    );

    return loadedStations.filter((station): station is Station => station !== null);
  };

  const getMyStations = async (): Promise<StudentStationsResult> => {
    const classroom = await student.getMyClassroom().catch(() => null);
    const worldUid = classroom?.worldUid?.trim() ?? "";

    if (!worldUid) {
      return {
        world: null,
        stations: [],
      };
    }

    const world = await World.backendFetchWorldByUid(worldUid);
    const stationUids = world?.stationUids ?? [];

    if (stationUids.length === 0) {
      return {
        world,
        stations: [],
      };
    }

    return {
      world,
      stations: await getStationsByUids(stationUids),
    };
  };

  return {
    getAllStations,
    getStationByUid,
    createStation,
    updateStation,
    deleteStation,
    getStationsByUids,
    getMyStations,
  };
}
