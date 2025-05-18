export type VehicleStatus = "AVAILABLE" | "RENTED" | "UNDER_MAINTENANCE";

export interface VehicleQueryParams {
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: "asc" | "desc";
  licensePlate?: string;
  vin?: string;
  minKilometers?: number;
  maxKilometers?: number;
  statuses?: VehicleStatus[];
  modelIds?: number[];
}
