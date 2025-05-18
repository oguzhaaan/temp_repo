import { CarModel } from "../../models/CarModel.ts";
import API from "../../api";
import { ConfirmationModal } from "../../components/ConfirmationModal.tsx";
import { useState } from "react";
import toast from "react-hot-toast";

//("id", "modelYear", "luggageCapacity", "rentalPricePerDay")

interface CarModelsTableProps {
  sortBy: string;
  setSortBy: (sortBy: string) => void;
  sortDirection: string;
  setSortDirection: (sortDirection: string) => void;
  firstElementIndex: number;
  carModels: CarModel[];
  editCarModel: (id: number) => void;
  addVehicle: (id: number) => void;
  refreshCarModels: () => void;
}

export const CarModelsTable = ({
  sortBy,
  setSortBy,
  sortDirection,
  setSortDirection,
  firstElementIndex,
  carModels,
  editCarModel,
  addVehicle,
  refreshCarModels,
}: CarModelsTableProps) => {
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [carModelIdToDelete, setCarModelIdToDelete] = useState<number>(0);
  const [selectedCarModelId, setSelectedCarModelId] = useState<number>(0);

  const deleteCarModel = async (id: number) => {
    await toast.promise(API.deleteCarModel(id), {
      loading: "Deleting car model...",
      success: "Car model deleted successfully!",
      error: "Failed to delete car model.",
    });
    refreshCarModels();
  };

  return (
    <>
      <ConfirmationModal
        isOpen={isDeleteModalOpen}
        title={"Warning!"}
        message={
          "Are you sure you want to delete this car model? All vehicles from this model will be deleted as well."
        }
        cancelText={"Cancel"}
        confirmText={"Delete"}
        onClose={() => {
          setIsDeleteModalOpen(false);
          setCarModelIdToDelete(0);
        }}
        onConfirm={() => {
          deleteCarModel(carModelIdToDelete);
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
                  refreshCarModels();
                }}
                className="cursor-pointer rounded-tl-2xl px-2 py-3 text-left"
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
              <th className="px-2 py-3 text-left">Brand</th>
              <th className="px-2 py-3 text-left">Model</th>
              <th
                onClick={() => {
                  setSortBy("modelYear");
                  setSortDirection(
                    sortBy === "modelYear" && sortDirection === "asc"
                      ? "desc"
                      : "asc",
                  );
                  refreshCarModels();
                }}
                className="cursor-pointer px-2 py-3 text-left"
              >
                Model Year
                <i
                  className={`bi ${
                    sortBy === "modelYear"
                      ? sortDirection === "asc"
                        ? "bi-caret-up-fill"
                        : "bi-caret-down-fill"
                      : "bi-caret-up"
                  } ml-1`}
                />
              </th>
              <th className="px-2 py-3 text-left">Segment</th>
              <th className="px-2 py-3 text-left">Doors</th>
              <th className="px-2 py-3 text-left">Seats</th>
              <th className="px-2 py-3 text-left">Luggage</th>
              <th className="px-2 py-3 text-left">Category</th>
              <th className="px-2 py-3 text-left">Engine</th>
              <th className="px-2 py-3 text-left">Transmission</th>
              <th className="px-2 py-3 text-left">Drivetrain</th>
              <th className="px-2 py-3 text-left">Displacement</th>
              <th className="px-2 py-3 text-left">Features</th>
              <th
                onClick={() => {
                  setSortBy("rentalPricePerDay");
                  setSortDirection(
                    sortBy === "rentalPricePerDay" && sortDirection === "asc"
                      ? "desc"
                      : "asc",
                  );
                  refreshCarModels();
                }}
                className="cursor-pointer px-2 py-3 text-left"
              >
                Price/Day (€)
                <i
                  className={`bi ${
                    sortBy === "rentalPricePerDay"
                      ? sortDirection === "asc"
                        ? "bi-caret-up-fill"
                        : "bi-caret-down-fill"
                      : "bi-caret-up"
                  } ml-1`}
                />
              </th>
              <th className="rounded-tr-2xl px-2 py-3 text-left">Actions</th>
            </tr>
          </thead>
          <tbody>
            {carModels.map((carModel, id) => (
              <tr
                key={carModel.id}
                className="transition duration-150 odd:bg-white even:bg-gray-50 hover:bg-blue-50"
              >
                <td className="px-2 py-3">{id + firstElementIndex}</td>
                <td className="px-2 py-3">{carModel.brand}</td>
                <td className="px-2 py-3">{carModel.model}</td>
                <td className="px-2 py-3">{carModel.modelYear}</td>
                <td className="px-2 py-3">{carModel.segment}</td>
                <td className="px-2 py-3">{carModel.numberOfDoors}</td>
                <td className="px-2 py-3">{carModel.seatingCapacity}</td>
                <td className="px-2 py-3">{carModel.luggageCapacity}</td>
                <td className="px-2 py-3">{carModel.category}</td>
                <td className="px-2 py-3">{carModel.engineType}</td>
                <td className="px-2 py-3">{carModel.transmissionType}</td>
                <td className="px-2 py-3">{carModel.drivetrain}</td>
                <td className="px-2 py-3">{carModel.motorDisplacement}</td>
                <td className="px-2 py-3">
                  <div className="flex flex-wrap gap-1">
                    {carModel.features.map((feature, i) => (
                      <span
                        key={i}
                        className="rounded-full bg-blue-100 px-2 py-1 text-xs font-medium text-blue-800 shadow-sm"
                      >
                        {feature}
                      </span>
                    ))}
                  </div>
                </td>
                <td className="px-4 py-3 font-semibold text-gray-800">
                  €{carModel.rentalPricePerDay}
                </td>
                <td className="relative whitespace-nowrap px-4 py-3">
                  <div className="relative">
                    <button
                      onClick={() =>
                        setSelectedCarModelId(
                          selectedCarModelId === carModel.id ? 0 : carModel.id,
                        )
                      }
                      title="Show actions"
                      className="rounded-full border border-purple-600 px-3 py-1 text-purple-600 transition hover:bg-purple-600 hover:text-white"
                    >
                      <i className="bi bi-three-dots" />
                    </button>

                    {selectedCarModelId === carModel.id && (
                      <div className="absolute right-1 z-10 mt-2 rounded-xl border border-gray-200 bg-white shadow-xl">
                        <ul className="flex flex-col space-y-1 p-2 text-sm">
                          <li>
                            <button
                              onClick={() => {
                                addVehicle(carModel.id);
                                setSelectedCarModelId(0);
                              }}
                              className="w-full rounded-md px-3 py-1 text-left text-green-600 transition hover:bg-green-500 hover:text-white"
                            >
                              <i className="bi bi-plus-lg mr-2"></i>
                              Add Vehicle
                            </button>
                          </li>
                          <li>
                            <button
                              onClick={() => {
                                editCarModel(carModel.id);
                                setSelectedCarModelId(0);
                              }}
                              className="w-full rounded-md px-3 py-1 text-left text-yellow-600 transition hover:bg-yellow-500 hover:text-white"
                            >
                              <i className="bi bi-pencil mr-2"></i>
                              Edit
                            </button>
                          </li>
                          <li>
                            <button
                              onClick={() => {
                                setCarModelIdToDelete(carModel.id);
                                setIsDeleteModalOpen(true);
                                setSelectedCarModelId(0);
                              }}
                              className="w-full rounded-md px-3 py-1 text-left text-red-600 transition hover:bg-red-500 hover:text-white"
                            >
                              <i className="bi bi-trash mr-2"></i>
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
