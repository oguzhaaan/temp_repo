import { CarModel } from "../../models/CarModel.ts";
import { useEffect, useState } from "react";
import API from "../../api";
import toast from "react-hot-toast";

interface CarModelDetailsProps {
  onClose: () => void;
  vehicleId: number;
}

export const CarModelDetails = ({
  vehicleId,
  onClose,
}: CarModelDetailsProps) => {
  const [carModel, setCarModel] = useState<CarModel | null>(null);

  useEffect(() => {
    const loadCarModel = async () => {
      try {
        const data = await API.getVehicleById(vehicleId);
        setCarModel(data.carModel);
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    loadCarModel();
  }, [vehicleId]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-60 backdrop-blur-sm">
      <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-4xl max-h-[85vh] overflow-y-auto p-8 animate-fade-in">
        <button
          onClick={onClose}
          className="absolute top-5 right-5 text-red-500 hover:text-red-700 text-lg"
        >
          <i className="bi bi-x-circle-fill"></i>
        </button>

        <h1 className="text-2xl font-bold text-center mb-8 text-gray-800">
          Vehicle Details
        </h1>

        {carModel ? (
          <div className="space-y-6">
            {/* Basic Info */}
            <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-gray-700">
              <div>
                <span className="font-semibold">Brand:</span> {carModel.brand}
              </div>
              <div>
                <span className="font-semibold">Model:</span> {carModel.model}
              </div>
              <div>
                <span className="font-semibold">Year:</span>{" "}
                {carModel.modelYear}
              </div>
              <div>
                <span className="font-semibold">Segment:</span>{" "}
                {carModel.segment}
              </div>
              <div>
                <span className="font-semibold">Doors:</span>{" "}
                {carModel.numberOfDoors}
              </div>
              <div>
                <span className="font-semibold">Seating Capacity:</span>{" "}
                {carModel.seatingCapacity}
              </div>
              <div>
                <span className="font-semibold">Luggage Capacity:</span>{" "}
                {carModel.luggageCapacity} L
              </div>
              <div>
                <span className="font-semibold">Category:</span>{" "}
                {carModel.category}
              </div>
              <div>
                <span className="font-semibold">Engine:</span>{" "}
                {carModel.engineType}
              </div>
              <div>
                <span className="font-semibold">Transmission:</span>{" "}
                {carModel.transmissionType}
              </div>
              <div>
                <span className="font-semibold">Drivetrain:</span>{" "}
                {carModel.drivetrain}
              </div>
              <div>
                <span className="font-semibold">Displacement:</span>{" "}
                {carModel.motorDisplacement} cc
              </div>
              <div>
                <span className="font-semibold">Rental Price/Day:</span> $
                {carModel.rentalPricePerDay.toFixed(2)}
              </div>
            </div>

            {/* Features */}
            <div>
              <h2 className="text-xl font-semibold mb-2 text-gray-800">
                Features
              </h2>
              <div className="flex flex-wrap gap-2">
                {carModel.features.map((feature, index) => (
                  <span
                    key={index}
                    className="bg-blue-100 text-blue-800 text-sm font-medium px-3 py-1 rounded-full shadow-sm"
                  >
                    {feature}
                  </span>
                ))}
              </div>
            </div>
          </div>
        ) : (
          <div className="text-center text-gray-500">
            Loading vehicle details...
          </div>
        )}
      </div>
    </div>
  );
};
