export interface CarModelQueryParams {
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: string;
  brandModels?: string[];
  features?: string[];
  segments?: string[];
  minRentalPrice?: number;
  maxRentalPrice?: number;
  modelYears?: number[];
  categories?: string[];
  engineTypes?: string[];
  transmissionTypes?: string[];
  drivetrains?: string[];
}
