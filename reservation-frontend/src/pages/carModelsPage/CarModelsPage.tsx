import { useEffect, useState } from "react";
import { CarModel } from "../../models/CarModel.ts";
import API from "../../api";
import { CarModelsTable } from "./CarModelsTable.tsx";
import { AddCarModelForm } from "./AddCarModelForm.tsx";
import { EditCarModelForm } from "./EditCarModelForm.tsx";
import { AddVehicleForm } from "./AddVehicleForm.tsx";
import toast from "react-hot-toast";
import { CarModelsFilters } from "./CarModelsFilters.tsx";
import { CarModelQueryParams } from "../../models/CarModelQueryParams.ts";

export const CarModelsPage = () => {
  const [carModels, setCarModels] = useState<CarModel[]>([]);
  const [isAddCarModelFormOpen, setIsAddCarModelFormOpen] =
    useState<boolean>(false);
  const [isEditCarModelFormOpen, setIsEditCarModelFormOpen] =
    useState<boolean>(false);
  const [isAddVehicleModalOpen, setIsAddVehicleModalOpen] =
    useState<boolean>(false);
  const [selectedCarModelId, setSelectedCarModelId] = useState<number>(0);
  const [isFilterMenuOpen, setIsFilterMenuOpen] = useState(false);

  const [trigger, setTrigger] = useState(false);
  const refreshCarModels = () => {
    setTrigger(!trigger);
  };

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [sortBy, setSortBy] = useState("id");
  const [sortDirection, setSortDirection] = useState("asc");

  const [filters, setFilters] = useState<CarModelQueryParams>({});

  useEffect(() => {
    const loadModels = async () => {
      try {
        const data = await API.getAllCarModels({
          ...filters,
          page: page,
          size: rowsPerPage,
          sortBy: sortBy,
          direction: sortDirection,
        });
        if (data.length === 0 && page > 0) {
          setPage((prev) => prev - 1);
          return;
        }
        setCarModels(data);
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    loadModels();
  }, [trigger, page]);

  const editCarModel = (id: number) => {
    setSelectedCarModelId(id);
    setIsEditCarModelFormOpen(true);
  };
  const closeEditForm = () => {
    setIsEditCarModelFormOpen(false);
    setSelectedCarModelId(0);
  };
  const closeAddVehicleForm = () => {
    setIsAddVehicleModalOpen(false);
    setSelectedCarModelId(0);
  };
  const addVehicle = (id: number) => {
    setSelectedCarModelId(id);
    setIsAddVehicleModalOpen(true);
  };

  return (
    <div className="fixed inset-0 flex flex-col bg-white bg-gradient-to-br from-gray-100 via-blue-50 to-white">
      {/* Header */}
      <div className="py-6 pt-20">
        <h1 className="text-center text-4xl font-bold text-blue-600">
          Car Models
        </h1>
      </div>

      {/* Table Scrollable Section */}
      <div className="flex-1 overflow-y-auto px-4 pb-32">
        <div className="flex justify-center">
          <CarModelsTable
            sortBy={sortBy}
            setSortBy={setSortBy}
            sortDirection={sortDirection}
            setSortDirection={setSortDirection}
            firstElementIndex={page * rowsPerPage + 1}
            carModels={carModels}
            editCarModel={editCarModel}
            addVehicle={addVehicle}
            refreshCarModels={refreshCarModels}
          />
        </div>
      </div>

      {/* Fixed Bottom Panel */}
      <div className="fixed bottom-4 left-1/2 z-50 flex -translate-x-1/2 items-center gap-4 rounded-3xl bg-gradient-to-r from-blue-500 via-blue-600 to-indigo-600 px-6 py-4 shadow-2xl transition-all duration-300 ease-in-out hover:scale-105">
        <button
          onClick={() => setIsAddCarModelFormOpen(true)}
          className="flex items-center gap-2 rounded-xl bg-white/10 px-4 py-2 text-sm text-white backdrop-blur-md transition hover:bg-white/20"
        >
          <i className="bi bi-plus-circle text-base"></i>
          Add Model
        </button>
        <button
          onClick={() => setIsFilterMenuOpen((prev) => !prev)}
          className="flex items-center gap-2 rounded-xl bg-white/10 px-4 py-2 text-sm text-white backdrop-blur-md transition hover:bg-white/20"
        >
          <i className="bi bi-filter text-base"></i>
          Filters
        </button>
        <div className="flex items-center gap-3 rounded-xl bg-white/10 px-4 py-2 text-white backdrop-blur-md">
          <i
            onClick={() => page > 0 && setPage((prev) => prev - 1)}
            className={`bi bi-arrow-left cursor-pointer text-base ${
              page === 0
                ? "pointer-events-none opacity-30"
                : "hover:text-white/80"
            }`}
          />
          <span className="text-sm">Page {page + 1}</span>
          <i
            onClick={() =>
              carModels.length === rowsPerPage && setPage((prev) => prev + 1)
            }
            className={`bi bi-arrow-right cursor-pointer text-base ${
              carModels.length < rowsPerPage
                ? "pointer-events-none opacity-30"
                : "hover:text-white/80"
            }`}
          />
        </div>
      </div>

      {/* Dialogs */}
      {isAddCarModelFormOpen && (
        <AddCarModelForm
          refreshCarModels={refreshCarModels}
          onClose={() => setIsAddCarModelFormOpen(false)}
        />
      )}
      {isEditCarModelFormOpen && (
        <EditCarModelForm
          carModelId={selectedCarModelId}
          refreshCarModels={refreshCarModels}
          onClose={closeEditForm}
        />
      )}
      {isAddVehicleModalOpen && (
        <AddVehicleForm
          carModelId={selectedCarModelId}
          refreshCarModels={refreshCarModels}
          onClose={closeAddVehicleForm}
        />
      )}
      {isFilterMenuOpen && (
        <CarModelsFilters
          filters={filters}
          setFilters={setFilters}
          rowsPerPage={rowsPerPage}
          setRowsPerPage={setRowsPerPage}
          refreshCarModels={refreshCarModels}
          onClose={() => setIsFilterMenuOpen(false)}
        />
      )}
    </div>
  );
};
