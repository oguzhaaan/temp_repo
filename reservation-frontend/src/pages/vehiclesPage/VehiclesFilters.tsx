import {
  VehicleQueryParams,
  VehicleStatus,
} from "../../models/VehicleQueryParams.ts";
import Select from "react-select";
import { useState } from "react";

interface VehiclesFiltersProps {
  rowsPerPage: number;
  setRowsPerPage: (rowsPerPage: number) => void;
  filters: VehicleQueryParams;
  setFilters: (filters: VehicleQueryParams) => void;
  onClose: () => void;
  refreshVehicles: () => void;
}

export const VehiclesFilters = ({
  rowsPerPage,
  setRowsPerPage,
  filters,
  setFilters,
  onClose,
  refreshVehicles,
}: VehiclesFiltersProps) => {
  const clearFilters = () => {
    setFilters({});
    setEnteredVIN(undefined);
    setEnteredLicensePlate(undefined);
    setEnteredMinKilometers(undefined);
    setEnteredMaxKilometers(undefined);
    setSelectedVehicleStatuses([]);
    setRowsPerPage(10);
    refreshVehicles();
  };

  const [selectedVehicleStatuses, setSelectedVehicleStatuses] = useState<
    VehicleStatus[]
  >(filters.statuses || []);

  const [enteredVIN, setEnteredVIN] = useState<string | undefined>(
    filters.vin || undefined,
  );
  const [enteredLicensePlate, setEnteredLicensePlate] = useState<
    string | undefined
  >(filters.licensePlate || undefined);
  const [enteredMinKilometers, setEnteredMinKilometers] = useState<
    number | undefined
  >(filters.minKilometers || undefined);
  const [enteredMaxKilometers, setEnteredMaxKilometers] = useState<
    number | undefined
  >(filters.maxKilometers || undefined);

  const applyFilters = () => {
    const newFilters: VehicleQueryParams = {
      ...filters,
      statuses: selectedVehicleStatuses,
      vin: enteredVIN,
      licensePlate: enteredLicensePlate,
      minKilometers: enteredMinKilometers,
      maxKilometers: enteredMaxKilometers,
    };

    setFilters(newFilters);
    refreshVehicles();
    onClose();
  };

  return (
    <div className="animate-slide-up fixed bottom-10 left-10 right-10 top-20 z-50 w-full max-w-xs overflow-y-scroll rounded-3xl border border-white/20 bg-white/50 p-4 shadow-2xl backdrop-blur-xl">
      <div className="flex max-h-max flex-col overflow-y-visible">
        <button
          onClick={onClose}
          className="absolute right-4 top-4 text-red-500 hover:text-red-700"
        >
          <i className="bi bi-x-circle"></i>
        </button>
        <h2 className="mb-4 text-2xl font-bold text-black">Filters</h2>

        <div className="w-full space-y-4">
          <div>
            <label className="text-black">Rows per page</label>
            <Select
              options={[
                { value: 5, label: "5" },
                { value: 10, label: "10" },
                { value: 20, label: "20" },
                { value: 50, label: "50" },
              ]}
              placeholder="Select rows per page"
              onChange={(selected) => {
                const selectedValue = selected?.value || 10;
                setRowsPerPage(selectedValue);
              }}
              value={{
                value: rowsPerPage,
                label: rowsPerPage.toString(),
              }}
            />
          </div>
          <div>
            <label className="text-black">Vehicle Status</label>
            <Select
              options={[
                { value: "AVAILABLE", label: "Available" },
                { value: "RENTED", label: "Rented" },
                { value: "UNDER_MAINTENANCE", label: "Under Maintenance" },
              ]}
              isMulti
              placeholder="Select vehicle status"
              onChange={(selected) => {
                const selectedValues =
                  selected?.map((option) => option.value) || [];
                setSelectedVehicleStatuses(selectedValues as VehicleStatus[]);
              }}
              value={selectedVehicleStatuses.map((status) => ({
                value: status,
                label: status.toString(),
              }))}
            />
          </div>
          <div>
            <label className="text-black">VIN</label>
            <input
              type="text"
              placeholder="Enter VIN"
              value={enteredVIN}
              onChange={(e) => setEnteredVIN(e.target.value)}
              className="w-full rounded-lg border border-gray-300 p-2"
            />
          </div>
          <div>
            <label className="text-black">License Plate</label>
            <input
              type="text"
              placeholder="Enter License Plate"
              value={enteredLicensePlate}
              onChange={(e) => setEnteredLicensePlate(e.target.value)}
              className="w-full rounded-lg border border-gray-300 p-2"
            />
          </div>
          <div>
            <label className="text-black">Min Kilometers</label>
            <input
              type="number"
              placeholder="Enter Min Kilometers"
              value={enteredMinKilometers}
              onChange={(e) => setEnteredMinKilometers(Number(e.target.value))}
              className="w-full rounded-lg border border-gray-300 p-2"
            />
          </div>
          <div>
            <label className="text-black">Max Kilometers</label>
            <input
              type="number"
              placeholder="Enter Max Kilometers"
              value={enteredMaxKilometers}
              onChange={(e) => setEnteredMaxKilometers(Number(e.target.value))}
              className="w-full rounded-lg border border-gray-300 p-2"
            />
          </div>

          <div className="flex flex-row space-x-2">
            <button
              onClick={clearFilters}
              className="w-full rounded-lg bg-gray-600 px-4 py-2 text-white"
            >
              Clear Filters
            </button>
            <button
              onClick={applyFilters}
              className="w-full rounded-lg bg-blue-600 px-4 py-2 text-white"
            >
              Apply Filters
            </button>
          </div>
          <div className="w-80"></div>
        </div>
      </div>
    </div>
  );
};
