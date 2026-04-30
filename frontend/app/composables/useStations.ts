import {
  mapStationFromResponse,
  mapStationProgressFromResponse,
  type Station,
  type StationData,
  type StationProgressData,
  type StationProgressResponse,
  type StationRequest,
} from "~/composables/types/StationTypes";
import type { World } from "~/composables/types/WorldTypes";

export type StudentStationsResult = {
  world: World | null;
  stations: StationProgressData[];
};

type StudentStationsResponse = {
  world: World | null;
  stations: StationProgressResponse[];
};

export function useStations() {
  const { $api } = useNuxtApp();

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

  const getStationDataByUid = async (uid: string): Promise<StationProgressData | null> => {
    try {
      const station = await $api<StationProgressResponse>(`/stations/${uid}/data`, {
        method: "GET",
      });

      return mapStationProgressFromResponse(station);
    } catch {
      return null;
    }
  };

  const getMyStations = async (): Promise<StudentStationsResult> => {
    const response = await $api<StudentStationsResponse>("/stations/me/data", {
      method: "GET",
    });

    return {
      world: response.world,
      stations: response.stations.map(mapStationProgressFromResponse),
    };
  };

  return {
    getAllStations,
    getStationByUid,
    createStation,
    updateStation,
    deleteStation,
    getStationsByUids,
    getStationDataByUid,
    getMyStations,
  };
}
