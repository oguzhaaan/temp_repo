import { useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import API from "../../api";
import toast from "react-hot-toast";
import dayjs from "dayjs";
import { CarModel } from "../../models/CarModel.ts";
import { ConfirmationModal } from "../../components/ConfirmationModal.tsx";
import { Vehicle } from "../../models/Vehicle.ts";
import { FaCar, FaCalendarAlt } from "react-icons/fa"; // Adding car and calendar icons for visual appeal

export const CreateReservationPage = () => {
  const [availableCarModels, setAvailableCarModels] = useState<CarModel[]>([]);
  const [userId, setUserId] = useState<number | null>(null);
  const [startDate, setStartDate] = useState<Date | null>(null);
  const [endDate, setEndDate] = useState<Date | null>(null);
  const [selectedCarModel, setSelectedCarModel] = useState<CarModel | null>(
    null,
  );
  const [reservedVehicle, setReservedVehicle] = useState<Vehicle | null>(null);

  const reserveCarModel = async () => {
    if (!userId || !startDate || !endDate || !selectedCarModel) return;

    try {
      const formattedStart = dayjs(startDate).format("YYYY-MM-DD");
      const formattedEnd = dayjs(endDate).format("YYYY-MM-DD");
      const reservation = await API.reserveVehicle(
        userId,
        selectedCarModel.id,
        formattedStart,
        formattedEnd,
      );
      setReservedVehicle(reservation.vehicle);
      toast.success("Successfully reserved!");
      setSelectedCarModel(null);
      console.log(reservation.vehicle);
      await searchForAvailableCarModels();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  const searchForAvailableCarModels = async () => {
    if (!startDate || !endDate) return;

    try {
      const formattedStart = dayjs(startDate).format("YYYY-MM-DD");
      const formattedEnd = dayjs(endDate).format("YYYY-MM-DD");
      const carModels = await API.getCarModelsAvailableByDateRange(
        formattedStart,
        formattedEnd,
      );
      toast.success("Available vehicles fetched successfully!");
      setAvailableCarModels(carModels);
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  return (
    <>
      <ConfirmationModal
        isOpen={!!selectedCarModel}
        onClose={() => setSelectedCarModel(null)}
        onConfirm={reserveCarModel}
        title="Confirm Reservation"
        message={`Are you sure you want to confirm the reservation for ${selectedCarModel?.brand} ${selectedCarModel?.model} from ${dayjs(startDate).format("MMM D, YYYY")} to ${dayjs(endDate).format("MMM D, YYYY")}?`}
        cancelText="Cancel"
        confirmText="Confirm"
      />
      <ConfirmationModal
        isOpen={!!reservedVehicle}
        onClose={() => setReservedVehicle(null)}
        onConfirm={() => setReservedVehicle(null)}
        title="Reservation Successful"
        message={`Your reservation has been confirmed! The vehicle with license plate ${reservedVehicle?.licensePlate} will be ready for you starting from ${dayjs().format("MMM D, YYYY")}.`}
        cancelText="Close"
        confirmText="Got It"
      />

      <div className="flex h-screen flex-col overflow-hidden bg-gradient-to-br from-blue-100 via-purple-50 to-pink-50 p-4 md:flex-row md:p-8">
        {/* Left Column */}
        <div className="flex flex-col rounded-2xl bg-white p-6 shadow-xl md:h-full md:w-1/4 md:p-8">
          <h2 className="mb-6 flex items-center justify-center text-center text-3xl font-extrabold text-blue-800 md:text-4xl">
            <FaCalendarAlt className="mr-2 inline-block text-blue-600" />
            Step 1: Select Dates
          </h2>
          <p className="mb-6 text-center text-sm text-gray-600 md:text-base">
            Enter your user ID and select the dates for your reservation.
          </p>

          {/* User ID Input */}
          <div className="mb-6 flex w-full flex-col">
            <label
              htmlFor="userId"
              className="mb-2 text-sm font-semibold uppercase tracking-wide text-gray-600"
            >
              User ID
            </label>
            <input
              id="userId"
              type="number"
              min={1}
              placeholder="Your User ID"
              className="input mb-4"
              onChange={(e) => setUserId(Number(e.target.value))}
            />
          </div>

          {/* Date Range Picker */}
          <RangeDatePicker
            startDate={startDate}
            endDate={endDate}
            setStartDate={setStartDate}
            setEndDate={setEndDate}
          />
          {/* Search Button */}
          {startDate && endDate && (
            <div className="mt-6 flex justify-center">
              <button
                className="button w-full max-w-xs rounded-md bg-gradient-to-r from-blue-600 to-indigo-500 py-3 text-white shadow-md transition hover:brightness-110"
                onClick={searchForAvailableCarModels}
              >
                Search Vehicles
              </button>
            </div>
          )}
        </div>

        {/* Right Column */}
        <div className="mt-8 overflow-y-auto rounded-2xl bg-white p-6 shadow-xl md:ml-6 md:mt-0 md:h-full md:w-3/4 md:p-8">
          <h2 className="mb-6 flex items-center justify-center text-center text-3xl font-extrabold text-blue-800 md:text-4xl">
            <FaCar className="mr-2 inline-block text-blue-600" />
            Step 2: Select Your Vehicle
          </h2>
          <p className="mb-6 text-center text-sm text-gray-600 md:text-base">
            Choose from our available vehicles for the selected dates.
          </p>
          <div className="mt-8 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {availableCarModels.length > 0 ? (
              availableCarModels.map((carModel) => (
                <CarModelCard
                  key={carModel.id}
                  carModel={carModel}
                  onCardClick={(carModel1) => {
                    if (!userId) {
                      toast.error("Please enter a valid user ID.");
                      return;
                    }
                    setSelectedCarModel(carModel1);
                  }}
                />
              ))
            ) : (
              <p className="col-span-full text-center text-gray-600">
                No vehicles available for the selected dates.
              </p>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

// RangeDatePicker Component
interface RangeDatePickerProps {
  startDate: Date | null;
  endDate: Date | null;
  setStartDate: (date: Date | null) => void;
  setEndDate: (date: Date | null) => void;
}
const RangeDatePicker = ({
  startDate,
  endDate,
  setEndDate,
  setStartDate,
}: RangeDatePickerProps) => {
  const tomorrow = dayjs().add(1, "day").toDate();

  const handleStartDateChange = (date: Date | null) => {
    setStartDate(date);
    // Reset endDate if it's before the new startDate
    if (endDate && date && dayjs(endDate).isBefore(dayjs(date).add(1, "day"))) {
      setEndDate(null);
    }
  };

  const handleEndDateChange = (date: Date | null) => {
    setEndDate(date);
  };

  return (
    <div className="flex flex-col gap-6">
      {/* Start Date Picker */}
      <div className="flex w-full flex-col">
        <label
          htmlFor="startDate"
          className="mb-2 text-sm font-semibold uppercase tracking-wide text-gray-600"
        >
          Start Date
        </label>
        <DatePicker
          id="startDate"
          placeholderText={"Select Start Date"}
          selected={startDate}
          onChange={handleStartDateChange}
          minDate={tomorrow}
          dateFormat="MMM d, yyyy"
          className="input"
        />
      </div>

      {/* End Date Picker */}
      <div className="flex w-full flex-col">
        <label
          htmlFor="endDate"
          className="mb-2 text-sm font-semibold uppercase tracking-wide text-gray-600"
        >
          End Date
        </label>
        <DatePicker
          id="endDate"
          placeholderText={"Select End Date"}
          selected={endDate}
          onChange={handleEndDateChange}
          minDate={
            startDate ? dayjs(startDate).add(1, "day").toDate() : tomorrow
          }
          disabled={!startDate}
          dateFormat="MMM d, yyyy"
          className="input"
        />
      </div>
    </div>
  );
};

// CarModelCard Component
interface CarModelCardProps {
  carModel: CarModel;
  onCardClick: (carModel: CarModel) => void;
}
const CarModelCard = ({ carModel, onCardClick }: CarModelCardProps) => {
  return (
    <div
      className="flex transform cursor-pointer flex-col rounded-2xl border border-gray-200 bg-white p-6 shadow-lg transition hover:scale-105 hover:border-blue-500 hover:bg-blue-50 hover:shadow-2xl"
      onClick={() => onCardClick(carModel)}
    >
      <div className="mb-4 flex items-center justify-between">
        <div>
          <h3 className="text-xl font-bold text-gray-800">
            {carModel.brand} {carModel.model}
          </h3>
          <p className="text-sm text-gray-500">
            {carModel.modelYear} • {carModel.segment}
          </p>
        </div>
        <div className="text-right">
          <p className="text-lg font-semibold text-blue-600">
            €{carModel.rentalPricePerDay}
            <span className="text-sm text-gray-500">/day</span>
          </p>
        </div>
      </div>

      <div className="flex-1">
        <ul className="mb-4 space-y-1 text-sm text-gray-600">
          <li>
            <span className="font-medium text-gray-800">Category:</span>{" "}
            {carModel.category}
          </li>
          <li>
            <span className="font-medium text-gray-800">Engine:</span>{" "}
            {carModel.engineType}, {carModel.motorDisplacement}cc
          </li>
          <li>
            <span className="font-medium text-gray-800">Transmission:</span>{" "}
            {carModel.transmissionType}
          </li>
          <li>
            <span className="font-medium text-gray-800">Drivetrain:</span>{" "}
            {carModel.drivetrain}
          </li>
          <li>
            <span className="font-medium text-gray-800">Seating:</span>{" "}
            {carModel.seatingCapacity} people
          </li>
          <li>
            <span className="font-medium text-gray-800">Luggage:</span>{" "}
            {carModel.luggageCapacity} liters
          </li>
          <li>
            <span className="font-medium text-gray-800">Doors:</span>{" "}
            {carModel.numberOfDoors}
          </li>
        </ul>

        <div className="flex flex-wrap gap-2">
          {carModel.features.map((feature, index) => (
            <span
              key={index}
              className="rounded-full bg-blue-100 px-3 py-1 text-xs font-semibold text-blue-700"
            >
              {feature}
            </span>
          ))}
        </div>
      </div>
    </div>
  );
};
