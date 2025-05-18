export interface CarModel {
  id: number;
  brand: string;
  model: string;
  modelYear: number;
  segment: string;
  numberOfDoors: number;
  seatingCapacity: number;
  luggageCapacity: number;
  category: string;
  engineType: string;
  transmissionType: string;
  drivetrain: string;
  motorDisplacement: number;
  features: string[];
  rentalPricePerDay: number;
}
