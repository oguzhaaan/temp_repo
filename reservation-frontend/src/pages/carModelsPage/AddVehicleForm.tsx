import { useState } from "react";
import Select from "react-select";
import API from "../../api";
import toast from "react-hot-toast";

interface AddVehicleFormProps {
  carModelId: number;
  refreshCarModels: () => void;
  onClose: () => void;
}

export const AddVehicleForm = ({
  carModelId,
  refreshCarModels,
  onClose,
}: AddVehicleFormProps) => {
  const [form, setForm] = useState({
    carModelId: carModelId,
    licensePlate: "",
    vin: "",
    kilometersTravelled: 0,
    vehicleStatus: "AVAILABLE",
  });

  const submitForm = async () => {
    console.log(JSON.stringify(form));
    await toast.promise(API.addVehicle(form), {
      loading: "Adding Vehicle Model...",
      success: "Vehicle added successfully!",
      error: "Failed to add Vehicle.",
    });
    refreshCarModels();
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-gray-800 bg-opacity-50">
      <div className="relative h-min w-2/5 overflow-y-visible rounded-lg bg-white p-6 shadow-lg">
        <i
          onClick={onClose}
          className="bi bi-x-circle-fill absolute right-4 top-2 cursor-pointer text-lg font-bold text-red-500"
        />
        <div className="flex flex-col gap-6">
          <h2 className="mb-4 text-center text-2xl font-bold">Add Vehicle</h2>
          <div className="flex flex-col gap-4">
            <div className="flex flex-row gap-6">
              <div className="flex w-1/2 flex-col">
                <label>License Plate</label>
                <input
                  type="text"
                  value={form.licensePlate}
                  onChange={(e) =>
                    setForm({ ...form, licensePlate: e.target.value })
                  }
                  className="input"
                />
              </div>
              <div className="flex w-1/2 flex-col">
                <label>VIN</label>
                <input
                  type="text"
                  value={form.vin}
                  onChange={(e) => setForm({ ...form, vin: e.target.value })}
                  className="input"
                />
              </div>
            </div>
            <div className="flex flex-row gap-6">
              <div className="flex w-1/2 flex-col">
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
                  className="input"
                />
              </div>
              <div className="flex w-1/2 flex-col">
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
            <div className="flex flex-row justify-end gap-4">
              <button onClick={submitForm} className="button bg-blue-500">
                Create Vehicle
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
