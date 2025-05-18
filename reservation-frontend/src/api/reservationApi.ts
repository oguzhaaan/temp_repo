import { reservationApi as api } from "./axios";
import { CarModel } from "../models/CarModel";
import { Vehicle } from "../models/Vehicle";
import { CarModelQueryParams } from "../models/CarModelQueryParams.ts";
import { Note } from "../models/Note.ts";
import { MaintenanceHistory } from "../models/MaintenanceHistory.ts";
import { VehicleQueryParams } from "../models/VehicleQueryParams.ts";
import { Reservation } from "../models/Reservation.ts";
import { Transfer } from "../models/Transfer.ts";
import { TransferType } from "../types/TransferType.ts";

// CAR MODELS
export const getAllCarModels = async (
  params: CarModelQueryParams = {},
): Promise<CarModel[]> => {
  const queryParams = new URLSearchParams();

  // Set pagination and sorting defaults
  queryParams.set("page", String(params.page ?? 0));
  queryParams.set("size", String(params.size ?? 10));
  queryParams.set("sortBy", params.sortBy ?? "id");
  queryParams.set("direction", params.direction ?? "asc");

  // Add array params (multiple values)
  const arrayParams: (keyof CarModelQueryParams)[] = [
    "brandModels",
    "features",
    "segments",
    "modelYears",
    "categories",
    "engineTypes",
    "transmissionTypes",
    "drivetrains",
  ];

  arrayParams.forEach((key) => {
    const values = params[key];
    if (Array.isArray(values)) {
      values.forEach((val) => queryParams.append(key, String(val)));
    }
  });

  // Add optional numeric filters
  if (params.minRentalPrice !== undefined) {
    queryParams.set("minRentalPrice", String(params.minRentalPrice));
  }
  if (params.maxRentalPrice !== undefined) {
    queryParams.set("maxRentalPrice", String(params.maxRentalPrice));
  }

  const { data } = await api.get<CarModel[]>(
    `/models?${queryParams.toString()}`,
  );
  return data;
};

export const getCarModelById = async (id: number): Promise<CarModel> => {
  const { data } = await api.get<CarModel>(`/models/${id}`);
  return data;
};

export const createCarModel = async (form: unknown): Promise<CarModel> => {
  const { data } = await api.post<CarModel>("/models", form);
  return data;
};

export const editCarModel = async (
  id: number,
  form: unknown,
): Promise<CarModel> => {
  const { data } = await api.put<CarModel>(`/models/${id}`, form);
  return data;
};

export const deleteCarModel = async (id: number): Promise<void> => {
  await api.delete(`/models/${id}`);
};

export const getCarModelsAvailableByDateRange = async (
  startDate: string,
  endDate: string,
): Promise<CarModel[]> => {
  const { data } = await api.get<CarModel[]>("/models/available", {
    params: {
      startDate,
      endDate,
    },
  });
  return data;
};

// ====================* RESERVATION *====================>
export const reserveVehicle = async (
  userId: number,
  carModelId: number,
  startDate: string,
  endDate: string,
): Promise<Reservation> => {
  const { data } = await api.post(
    `/reservations/users/${userId}/cars/${carModelId}`,
    {
      startDate,
      endDate,
    },
  );
  return data;
};

export const getAllReservations = async (): Promise<Reservation[]> => {
  const { data } = await api.get<Reservation[]>("/reservations");
  return data;
};

export const updateReservationStatus = async (
  reservationId: number,
  status: string,
): Promise<Reservation> => {
  const { data } = await api.put<Reservation>(
    `/reservations/${reservationId}/status/${status}`,
  );
  return data;
};

export const getReservationById = async (
  reservationId: number,
): Promise<Reservation> => {
  const { data } = await api.get<Reservation>(`/reservations/${reservationId}`);
  return data;
};

export const getAllReservationsByUserId = async (
  userId: number,
): Promise<Reservation[]> => {
  const { data } = await api.get<Reservation[]>(
    `/reservations/users/${userId}`,
  );
  return data;
};

export const deleteReservationById = async (
  reservationId: number,
): Promise<void> => {
  await api.delete(`/reservations/${reservationId}`);
};

export const updateReservation = async (
  reservationId: number,
  details: {
    carModelId: number;
    reservationTimeInterval: { startDate: string; endDate: string };
  },
): Promise<Reservation> => {
  const { data } = await api.put<Reservation>(
    `/reservations/${reservationId}`,
    details,
  );
  return data;
};

// ====================* TRANSFERS (pickup & dropoff) *====================>

export const getAllTransfersByType = async (
  transferType: TransferType,
): Promise<Transfer[]> => {
  const { data } = await api.get<Transfer[]>(`/transfers/${transferType}`);
  return data;
};
export const getTransferByTypeAndReservationId = async (
  transferType: TransferType,
  reservationId: number,
): Promise<Transfer> => {
  const { data } = await api.get<Transfer>(
    `/transfers/${transferType}/${reservationId}`,
  );
  return data;
};
export const deleteTransferByTypeAndId = async (
  transferType: TransferType,
  transferId: number,
): Promise<void> => {
  await api.delete(`/transfers/${transferType}/${transferId}`);
};

export const addTransferByTypeAndReservationId = async (
  transferType: TransferType,
  reservationId: number,
  transfer: Transfer,
): Promise<Transfer> => {
  const { data } = await api.post<Transfer>(
    `/transfers/${transferType}/${reservationId}`,
    transfer,
  );
  return data;
};

export const editTransferByTypeAndReservationId = async (
  transferType: TransferType,
  reservationId: number,
  transfer: Transfer,
): Promise<Transfer> => {
  const { data } = await api.put<Transfer>(
    `/transfers/${transferType}/${reservationId}`,
    transfer,
  );
  return data;
};
// <====================* VEHICLES *====================>
export const getAllVehicles = async (
  params: VehicleQueryParams = {},
): Promise<Vehicle[]> => {
  const defaultParams: VehicleQueryParams = {
    page: 0,
    size: 10,
    sortBy: "id",
    direction: "asc",
  };

  const finalParams = { ...defaultParams, ...params };

  const { data } = await api.get<Vehicle[]>("/vehicles", {
    params: finalParams,
    paramsSerializer: (params) => {
      const searchParams = new URLSearchParams();
      Object.entries(params || {}).forEach(([key, value]) => {
        if (Array.isArray(value)) {
          value.forEach((val) => searchParams.append(key, val.toString()));
        } else if (value !== undefined && value !== null) {
          searchParams.append(key, value.toString());
        }
      });
      return searchParams.toString();
    },
  });

  return data;
};

export const getVehicleById = async (id: number): Promise<Vehicle> => {
  const { data } = await api.get<Vehicle>(`/vehicles/${id}`);
  return data;
};

export const addVehicle = async (form: unknown): Promise<Vehicle> => {
  const { data } = await api.post<Vehicle>("/vehicles", form);
  return data;
};

export const editVehicle = async (
  id: number,
  form: unknown,
): Promise<Vehicle> => {
  const { data } = await api.put<Vehicle>(`/vehicles/${id}`, form);
  return data;
};

export const deleteVehicle = async (id: number): Promise<void> => {
  await api.delete(`/vehicles/${id}`);
};

// NOTE
export const getAllNotesByVehicleId = async (
  vehicleId: number,
): Promise<Note[]> => {
  const { data } = await api.get<Note[]>(`/vehicles/${vehicleId}/notes/`);
  return data;
};

// MAINTENANCE HISTORY
export const getAllMaintenanceHistoryByVehicleId = async (
  vehicleId: number,
): Promise<MaintenanceHistory[]> => {
  const { data } = await api.get<MaintenanceHistory[]>(
    `/vehicles/${vehicleId}/maintenances/`,
  );
  return data;
};

// DATA
export const getAllBrands = async (): Promise<string[]> => {
  const { data } = await api.get<string[]>("/brands");
  return data;
};

export const getModelsByBrand = async (brand: string): Promise<string[]> => {
  const { data } = await api.get<string[]>(`/brandModels/${brand}`);
  return data;
};

export const getAllSegments = async (): Promise<string[]> => {
  const { data } = await api.get<string[]>("/segments");
  return data;
};

export const getAllFeatures = async (): Promise<string[]> => {
  const { data } = await api.get<string[]>("/features");
  return data;
};

export const payReservation = async (reservationId: number) => {
  const { data } = await api.post(`/reservations/${reservationId}/pay`);
  return data;
};
