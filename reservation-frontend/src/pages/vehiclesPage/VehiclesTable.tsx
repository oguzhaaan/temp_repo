import { Vehicle } from "../../models/Vehicle.ts";
import { useState } from "react";
import { ConfirmationModal } from "../../components/ConfirmationModal.tsx";
import toast from "react-hot-toast";
import API from "../../api";

interface VehiclesTableProps {
  sortBy: string;
  setSortBy: (sortBy: string) => void;
  sortDirection: "asc" | "desc";
  setSortDirection: (sortDirection: "asc" | "desc") => void;
  firstElementIndex: number;
  vehicles: Vehicle[];
  refreshVehicles: () => void;
  editVehicle: (id: number) => void;
  openVehiclesNotes: (id: number) => void;
  openVehicleMaintenanceHistories: (id: number) => void;
  openCarModelDetails: (id: number) => void;
}

export const VehiclesTable = ({
  sortBy,
  setSortBy,
  sortDirection,
  setSortDirection,
  firstElementIndex,
  vehicles,
  refreshVehicles,
  editVehicle,
  openVehiclesNotes,
  openVehicleMaintenanceHistories,
  openCarModelDetails,
}: VehiclesTableProps) => {
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [vehicleIdToDelete, setVehicleIdToDelete] = useState<number>(0);
  const [selectedVehicleId, setSelectedVehicleId] = useState<number>(0);

  const deleteVehicle = async (id: number) => {
    await toast.promise(API.deleteVehicle(id), {
      loading: "Deleting vehicle...",
      success: "Vehicle deleted successfully!",
      error: "Failed to delete vehicle.",
    });
    refreshVehicles();
  };

  return (
    <>
      <ConfirmationModal
        isOpen={isDeleteModalOpen}
        title={"Warning!"}
        message={"Are you sure you want to delete this vehicle?"}
        cancelText={"Cancel"}
        confirmText={"Delete"}
        onClose={() => {
          setIsDeleteModalOpen(false);
          setVehicleIdToDelete(0);
        }}
        onConfirm={() => {
          deleteVehicle(vehicleIdToDelete);
          setIsDeleteModalOpen(false);
        }}
      />
      <div className="overflow-x-visible rounded-2xl border border-gray-200 shadow-xl">
        <table className="min-w-full bg-white text-sm text-gray-700">
          <thead className="sticky top-0 z-10">
            <tr className="bg-gradient-to-r from-blue-600 to-blue-500 text-xs uppercase tracking-wider text-white">
              <th
                onClick={() => {
                  setSortBy("id");
                  setSortDirection(
                    sortBy === "id" && sortDirection === "asc" ? "desc" : "asc",
                  );
                  refreshVehicles();
                }}
                className="cursor-pointer rounded-tl-2xl px-6 py-4"
              >
                #
                <i
                  className={`bi ${
                    sortBy === "id"
                      ? sortDirection === "asc"
                        ? "bi-caret-up-fill"
                        : "bi-caret-down-fill"
                      : "bi-caret-up"
                  } ml-1`}
                />
              </th>
              <th className="px-6 py-4 text-left">Brand</th>
              <th className="px-6 py-4 text-left">Model</th>
              <th className="px-6 py-4 text-left">Year</th>
              <th
                onClick={() => {
                  setSortBy("licensePlate");
                  setSortDirection(
                    sortBy === "licensePlate" && sortDirection === "asc"
                      ? "desc"
                      : "asc",
                  );
                  refreshVehicles();
                }}
                className="cursor-pointer px-6 py-4 text-left"
              >
                License Plate
                <i
                  className={`bi ${
                    sortBy === "licensePlate"
                      ? sortDirection === "asc"
                        ? "bi-caret-up-fill"
                        : "bi-caret-down-fill"
                      : "bi-caret-up"
                  } ml-1`}
                />
              </th>
              <th
                onClick={() => {
                  setSortBy("vin");
                  setSortDirection(
                    sortBy === "vin" && sortDirection === "asc"
                      ? "desc"
                      : "asc",
                  );
                  refreshVehicles();
                }}
                className="cursor-pointer px-6 py-4 text-left"
              >
                VIN
                <i
                  className={`bi ${
                    sortBy === "vin"
                      ? sortDirection === "asc"
                        ? "bi-caret-up-fill"
                        : "bi-caret-down-fill"
                      : "bi-caret-up"
                  } ml-1`}
                />
              </th>
              <th
                onClick={() => {
                  setSortBy("kilometersTravelled");
                  setSortDirection(
                    sortBy === "kilometersTravelled" && sortDirection === "asc"
                      ? "desc"
                      : "asc",
                  );
                  refreshVehicles();
                }}
                className="cursor-pointer px-6 py-4 text-left"
              >
                Kilometers Travelled
                <i
                  className={`bi ${
                    sortBy === "kilometersTravelled"
                      ? sortDirection === "asc"
                        ? "bi-caret-up-fill"
                        : "bi-caret-down-fill"
                      : "bi-caret-up"
                  } ml-1`}
                />
              </th>
              <th className="px-6 py-4 text-left">Status</th>
              <th className="rounded-tr-2xl px-6 py-4">Actions</th>
            </tr>
          </thead>
          <tbody>
            {vehicles.map((vehicle, id) => (
              <tr
                key={vehicle.id}
                className="transition duration-200 odd:bg-white even:bg-gray-50 hover:bg-blue-50"
              >
                <td className="whitespace-nowrap px-6 py-4 font-medium">
                  {id + firstElementIndex}
                </td>
                <td className="whitespace-nowrap px-6 py-4 font-medium">
                  {vehicle.carModel.brand}
                </td>
                <td className="whitespace-nowrap px-6 py-4">
                  {vehicle.carModel.model}
                </td>
                <td className="whitespace-nowrap px-6 py-4">
                  {vehicle.carModel.modelYear}
                </td>
                <td className="whitespace-nowrap px-6 py-4">
                  {vehicle.licensePlate}
                </td>
                <td className="whitespace-nowrap px-6 py-4">{vehicle.vin}</td>
                <td className="whitespace-nowrap px-6 py-4">
                  {vehicle.kilometersTravelled.toLocaleString()} km
                </td>
                <td className="whitespace-nowrap px-6 py-4">
                  <span
                    className={`inline-block rounded-full px-3 py-1 text-xs font-semibold ${
                      vehicle.vehicleStatus === "AVAILABLE"
                        ? "bg-green-100 text-green-800"
                        : vehicle.vehicleStatus === "RENTED"
                          ? "bg-gray-200 text-gray-700"
                          : "bg-yellow-100 text-yellow-700"
                    }`}
                  >
                    {vehicle.vehicleStatus
                      .toLowerCase()
                      .replace(/_/g, " ")
                      .replace(/\b\w/g, (char) => char.toUpperCase())}
                  </span>
                </td>
                <td className="relative whitespace-nowrap px-6 py-4">
                  <div className="relative">
                    <button
                      title="Open actions"
                      onClick={() =>
                        setSelectedVehicleId(
                          selectedVehicleId === vehicle.id ? 0 : vehicle.id,
                        )
                      }
                      className="rounded-full border border-purple-600 px-3 py-1 text-purple-600 transition hover:bg-purple-600 hover:text-white"
                    >
                      <i className="bi bi-three-dots"></i>
                    </button>

                    {selectedVehicleId === vehicle.id && (
                      <div className="absolute right-0 z-20 mt-2 rounded-xl border border-gray-200 bg-white shadow-xl">
                        <ul className="flex flex-col space-y-1 p-2 text-sm">
                          <li>
                            <button
                              onClick={() => openCarModelDetails(vehicle.id)}
                              className="w-full rounded-md px-3 py-1 text-left text-purple-600 transition hover:bg-purple-600 hover:text-white"
                            >
                              View Car Model
                            </button>
                          </li>
                          <li>
                            <button
                              onClick={() => {
                                openVehiclesNotes(vehicle.id);
                              }}
                              className="w-full rounded-md px-3 py-1 text-left text-blue-600 transition hover:bg-blue-600 hover:text-white"
                            >
                              Notes
                            </button>
                          </li>
                          <li>
                            <button
                              onClick={() =>
                                openVehicleMaintenanceHistories(vehicle.id)
                              }
                              className="w-full rounded-md px-3 py-1 text-left text-green-600 transition hover:bg-green-600 hover:text-white"
                            >
                              Maintenance History
                            </button>
                          </li>
                          <li>
                            <button
                              onClick={() => editVehicle(vehicle.id)}
                              className="w-full rounded-md px-3 py-1 text-left text-yellow-600 transition hover:bg-yellow-500 hover:text-white"
                            >
                              Edit
                            </button>
                          </li>
                          <li>
                            <button
                              onClick={() => {
                                setIsDeleteModalOpen(true);
                                setVehicleIdToDelete(vehicle.id);
                              }}
                              className="w-full rounded-md px-3 py-1 text-left text-red-600 transition hover:bg-red-500 hover:text-white"
                            >
                              Delete
                            </button>
                          </li>
                        </ul>
                      </div>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
};
