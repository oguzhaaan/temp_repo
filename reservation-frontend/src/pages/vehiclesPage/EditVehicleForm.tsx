import { useEffect, useState } from "react";
import Select from "react-select";
import API from "../../api";
import toast from "react-hot-toast";

interface EditVehicleFormProps {
  vehicleId: number;
  refreshVehicles: () => void;
  onClose: () => void;
}

export const EditVehicleForm = ({
  vehicleId,
  refreshVehicles,
  onClose,
}: EditVehicleFormProps) => {
  const [form, setForm] = useState({
    carModelId: 0,
    licensePlate: "",
    vin: "",
    kilometersTravelled: 0,
    vehicleStatus: "AVAILABLE",
  });

  useEffect(() => {
    const loadVehicle = async () => {
      try {
        const data = await API.getVehicleById(vehicleId);
        setForm({
          carModelId: data.carModel.id,
          licensePlate: data.licensePlate,
          vin: data.vin,
          kilometersTravelled: data.kilometersTravelled,
          vehicleStatus: data.vehicleStatus,
        });
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    loadVehicle();
  }, []);

  const submitForm = async () => {
    await toast.promise(API.editVehicle(vehicleId, form), {
      loading: "Editing Vehicle Model...",
      success: "Vehicle edited successfully!",
      error: "Failed to edit Vehicle.",
    });
    refreshVehicles();
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-gray-800 bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-lg shadow-lg w-2/5 h-min overflow-y-visible relative">
        <i
          onClick={onClose}
          className="bi bi-x-circle-fill cursor-pointer absolute top-2 right-4 text-red-500 text-lg"
        />
        <div className="flex flex-col gap-6">
          <h2 className="text-2xl font-bold text-center mb-4">Edit Vehicle</h2>
          <div className="flex flex-col gap-4">
            <div className="flex flex-row gap-6">
              <div className="flex flex-col w-1/2">
                <label>License Plate</label>
                <input
                  type="text"
                  value={form.licensePlate}
                  onChange={(e) =>
                    setForm({ ...form, licensePlate: e.target.value })
                  }
                  className="border border-gray-300 rounded p-2 w-full"
                />
              </div>
              <div className="flex flex-col w-1/2">
                <label>VIN</label>
                <input
                  type="text"
                  value={form.vin}
                  onChange={(e) => setForm({ ...form, vin: e.target.value })}
                  className="border border-gray-300 rounded p-2 w-full"
                />
              </div>
            </div>
            <div className="flex flex-row gap-6">
              <div className="flex flex-col w-1/2">
                <label>Kilometers Travelled</label>
                <input
                  type="number"
                  value={form.kilometersTravelled}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      kilometersTravelled: Number(e.target.value),
                    })
                  }
                  className="border border-gray-300 rounded p-2 w-full"
                />
              </div>
              <div className="flex flex-col w-1/2">
                <label>Vehicle Status</label>
                <Select
                  options={[
                    { value: "AVAILABLE", label: "Available" },
                    { value: "RENTED", label: "Rented" },
                    { value: "UNDER_MAINTENANCE", label: "Under Maintenance" },
                  ]}
                  value={{
                    value: form.vehicleStatus,
                    label:
                      form.vehicleStatus === "AVAILABLE"
                        ? "Available"
                        : form.vehicleStatus === "RENTED"
                          ? "Rented"
                          : form.vehicleStatus === "UNDER_MAINTENANCE"
                            ? "Under Maintenance"
                            : "",
                  }}
                  onChange={(selectedOption) =>
                    setForm({
                      ...form,
                      vehicleStatus: selectedOption?.value || "",
                    })
                  }
                />
              </div>
            </div>
            <div className="flex justify-center">
              <button
                onClick={submitForm}
                className="bg-blue-500 text-white px-4 py-2 rounded-lg shadow-lg hover:bg-blue-600"
              >
                Edit Vehicle
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
