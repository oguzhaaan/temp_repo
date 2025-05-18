import { CarModel } from "./CarModel.ts";

export interface Vehicle {
  carModel: CarModel;
  licensePlate: string;
  vin: string;
  kilometersTravelled: number;
  vehicleStatus: string;
  id: number;
}
