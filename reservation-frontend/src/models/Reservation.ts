import { Vehicle } from "./Vehicle.ts";
import { Transfer } from "./Transfer.ts";

export interface Reservation {
  reservationId: number;
  userId: number;
  vehicle: Vehicle;
  startDate: string;
  endDate: string;
  reservationStatus: string;
  createdAt: string;
  updatedAt: string;
  totalPrice: number;
  cancellationDate: string;
  pickup: Transfer;
  dropoff: Transfer;
}
