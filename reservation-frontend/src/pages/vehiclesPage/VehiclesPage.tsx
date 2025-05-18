import { useEffect, useState } from "react";
import API from "../../api";
import { Vehicle } from "../../models/Vehicle.ts";
import { VehiclesTable } from "./VehiclesTable.tsx";
import toast from "react-hot-toast";
import { EditVehicleForm } from "./EditVehicleForm.tsx";
import { VehicleNotes } from "./VehicleNotes.tsx";
import { VehicleMaintenanceHistories } from "./VehicleMaintenanceHistories.tsx";
import { VehicleQueryParams } from "../../models/VehicleQueryParams.ts";
import { VehiclesFilters } from "./VehiclesFilters.tsx";
import { CarModelDetails } from "./CarModelDetails.tsx";

export const VehiclesPage = () => {
  const [vehicles, setVehicles] = useState<Vehicle[]>([]);
  const [selectedVehicleId, setSelectedVehicleId] = useState<number>(0);
  const [isEditVehicleFormOpen, setIsEditVehicleFormOpen] = useState(false);
  const [isVehiclesNotesOpen, setIsVehiclesNotesOpen] = useState(false);
  const [isCarModelDetailsOpen, setIsCarModelDetailsOpen] = useState(false);
  const [
    isVehicleMaintenanceHistoriesOpen,
    setIsVehicleMaintenanceHistoriesOpen,
  ] = useState(false);
  const [isFilterMenuOpen, setIsFilterMenuOpen] = useState(false);

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [sortBy, setSortBy] = useState<string>("id");
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("asc");
  const [filters, setFilters] = useState<VehicleQueryParams>({});

  const [trigger, setTrigger] = useState(false);
  const refreshVehicles = () => {
    setTrigger(!trigger);
  };
  useEffect(() => {
    const loadVehicles = async () => {
      try {
        const data = await API.getAllVehicles({
          ...filters,
          page: page,
          size: rowsPerPage,
          sortBy: sortBy,
          direction: sortDirection,
        });
        setVehicles(data);
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    loadVehicles();
  }, [trigger, page]);

  const editVehicle = (id: number) => {
    setSelectedVehicleId(id);
    setIsEditVehicleFormOpen(true);
  };
  const closeEditForm = () => {
    setIsEditVehicleFormOpen(false);
    setSelectedVehicleId(0);
  };

  const openVehiclesNotes = (id: number) => {
    setSelectedVehicleId(id);
    setIsVehiclesNotesOpen(true);
  };
  const closeVehiclesNotes = () => {
    setIsVehiclesNotesOpen(false);
    setSelectedVehicleId(0);
  };
  const openVehicleMaintenanceHistories = (id: number) => {
    setSelectedVehicleId(id);
    setIsVehicleMaintenanceHistoriesOpen(true);
  };
  const closeVehicleMaintenanceHistories = () => {
    setIsVehicleMaintenanceHistoriesOpen(false);
    setSelectedVehicleId(0);
  };
  const openCarModelDetails = (id: number) => {
    setSelectedVehicleId(id);
    setIsCarModelDetailsOpen(true);
  };
  const closeCarModelDetails = () => {
    setIsCarModelDetailsOpen(false);
    setSelectedVehicleId(0);
  };

  return (
    <div className="fixed inset-0 flex flex-col bg-white bg-gradient-to-br from-gray-100 via-blue-50 to-white">
      {/* Header */}
      <div className="py-6 pt-20">
        <h1 className="text-center text-4xl font-bold text-blue-600">
          Vehicles
        </h1>
      </div>

      {/* Table Scrollable Section */}
      <div className="flex-1 overflow-y-auto px-4 pb-32">
        {/* pb-32 gives space for bottom panel */}
        <div className="flex justify-center">
          <VehiclesTable
            sortBy={sortBy}
            setSortBy={setSortBy}
            sortDirection={sortDirection}
            setSortDirection={setSortDirection}
            firstElementIndex={page * rowsPerPage + 1}
            vehicles={vehicles}
            refreshVehicles={refreshVehicles}
            editVehicle={editVehicle}
            openVehiclesNotes={openVehiclesNotes}
            openVehicleMaintenanceHistories={openVehicleMaintenanceHistories}
            openCarModelDetails={openCarModelDetails}
          />
        </div>
      </div>

      {/* Fixed Bottom Panel */}
      <div className="fixed bottom-4 left-1/2 z-50 flex -translate-x-1/2 items-center gap-4 rounded-3xl bg-gradient-to-r from-blue-500 via-blue-600 to-indigo-600 px-6 py-4 shadow-2xl transition-all duration-300 ease-in-out hover:scale-105">
        {/* Filter Button */}
        <button
          onClick={() => setIsFilterMenuOpen((prev) => !prev)}
          className="flex items-center gap-2 rounded-xl bg-white/10 px-4 py-2 text-sm text-white backdrop-blur-md transition hover:bg-white/20"
        >
          <i className="bi bi-filter text-base"></i>
          Filters
        </button>

        {/* Pagination */}
        <div className="flex items-center gap-3 rounded-xl bg-white/10 px-4 py-2 text-sm text-white backdrop-blur-md">
          <i
            onClick={() => page > 0 && setPage((prev) => prev - 1)}
            className={`bi bi-arrow-left cursor-pointer ${
              page === 0
                ? "pointer-events-none opacity-30"
                : "hover:text-white/80"
            }`}
          />
          <span>Page {page + 1}</span>
          <i
            onClick={() =>
              vehicles.length === rowsPerPage && setPage((prev) => prev + 1)
            }
            className={`bi bi-arrow-right cursor-pointer ${
              vehicles.length < rowsPerPage
                ? "pointer-events-none opacity-30"
                : "hover:text-white/80"
            }`}
          />
        </div>
      </div>

      {/* Dialogs */}
      {isEditVehicleFormOpen && (
        <EditVehicleForm
          vehicleId={selectedVehicleId}
          refreshVehicles={refreshVehicles}
          onClose={closeEditForm}
        />
      )}
      {isVehiclesNotesOpen && (
        <VehicleNotes
          vehicleId={selectedVehicleId}
          onClose={closeVehiclesNotes}
        />
      )}
      {isVehicleMaintenanceHistoriesOpen && (
        <VehicleMaintenanceHistories
          vehicleId={selectedVehicleId}
          onClose={closeVehicleMaintenanceHistories}
        />
      )}
      {isCarModelDetailsOpen && (
        <CarModelDetails
          onClose={closeCarModelDetails}
          vehicleId={selectedVehicleId}
        />
      )}
      {isFilterMenuOpen && (
        <VehiclesFilters
          rowsPerPage={rowsPerPage}
          setRowsPerPage={setRowsPerPage}
          filters={filters}
          setFilters={setFilters}
          refreshVehicles={refreshVehicles}
          onClose={() => setIsFilterMenuOpen(false)}
        />
      )}
    </div>
  );
};
