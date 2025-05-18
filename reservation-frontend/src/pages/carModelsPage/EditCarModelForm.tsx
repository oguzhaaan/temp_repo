import { useState, useEffect } from "react";
import API from "../../api";
import Select from "react-select";
import CreatableSelect from "react-select/creatable";
import toast from "react-hot-toast";

interface EditCarModelFormProps {
  carModelId: number;
  refreshCarModels: () => void;
  onClose: () => void;
}

export const EditCarModelForm = ({
  carModelId,
  refreshCarModels,
  onClose,
}: EditCarModelFormProps) => {
  const [brands, setBrands] = useState<string[]>([]);
  const [models, setModels] = useState<string[]>([]);
  const [segments, setSegments] = useState<string[]>([]);
  const [features, setFeatures] = useState<string[]>([]);

  const [form, setForm] = useState({
    brand: "",
    model: "",
    modelYear: 2020,
    segment: "",
    numberOfDoors: 4,
    seatingCapacity: 4,
    luggageCapacity: 0,
    category: "ECONOMY",
    engineType: "PETROL",
    transmissionType: "MANUAL",
    drivetrain: "FWD",
    motorDisplacement: 0,
    features: [] as string[],
    rentalPricePerDay: 0,
  });

  const handleChange = (field: string, value: unknown) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  useEffect(() => {
    const fetchCarModelById = async () => {
      try {
        const carModel = await API.getCarModelById(carModelId);
        setForm({
          brand: carModel.brand,
          model: carModel.model,
          modelYear: carModel.modelYear,
          segment: carModel.segment,
          numberOfDoors: carModel.numberOfDoors,
          seatingCapacity: carModel.seatingCapacity,
          luggageCapacity: carModel.luggageCapacity,
          category: carModel.category,
          engineType: carModel.engineType,
          transmissionType: carModel.transmissionType,
          drivetrain: carModel.drivetrain,
          motorDisplacement: carModel.motorDisplacement,
          features: carModel.features,
          rentalPricePerDay: carModel.rentalPricePerDay,
        });
      } catch (error) {
        console.error("Error fetching car model:", error);
      }
    };
    fetchCarModelById();
  }, []);

  useEffect(() => {
    const fetchBrands = async () => {
      try {
        const data = await API.getAllBrands();
        setBrands(data);
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    fetchBrands();
  }, []);

  useEffect(() => {
    //handleChange("model", null); // Clear model when brand changes
    const fetchModels = async () => {
      if (!form.brand) return;
      try {
        const data = await API.getModelsByBrand(form.brand);
        setModels(data);
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    fetchModels();
  }, [form.brand]);

  useEffect(() => {
    const fetchSegments = async () => {
      try {
        const data = await API.getAllSegments();
        setSegments(data);
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    fetchSegments();
  }, []);

  useEffect(() => {
    const fetchFeatures = async () => {
      try {
        const data = await API.getAllFeatures();
        setFeatures(data);
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    fetchFeatures();
  }, []);

  const submitAndEdit = async () => {
    await toast.promise(API.editCarModel(carModelId, form), {
      loading: "Editing Car Model...",
      success: "Car Model edited successfully!",
      error: "Failed to edit car model.",
    });
    refreshCarModels();
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-gray-800 bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-lg shadow-lg w-4/5 h-min overflow-y-auto relative">
        <i
          onClick={onClose}
          className="bi bi-x-circle-fill cursor-pointer absolute top-2 right-4 text-red-500 font-bold text-lg"
        />
        <div className="flex flex-col gap-6">
          <h2 className="text-2xl font-bold text-center mb-4">
            Edit Car Model
          </h2>
          <div className="flex flex-row gap-4">
            <div className="flex flex-col w-2/5">
              <label>Brand</label>
              <CreatableSelect<{ value: string; label: string }>
                isClearable
                options={brands.map((brand: string) => ({
                  value: brand,
                  label: brand,
                }))}
                placeholder="Select or add brand..."
                formatCreateLabel={(inputValue) =>
                  `Add "${inputValue}" as a new brand.`
                }
                onChange={(selectedOption) => {
                  handleChange(
                    "brand",
                    selectedOption ? selectedOption.value : null,
                  );
                }}
                value={
                  form.brand ? { value: form.brand, label: form.brand } : null
                }
              />
            </div>
            <div className="flex flex-col w-2/5">
              <label>Model</label>
              <CreatableSelect<{ value: string; label: string }>
                isClearable
                isDisabled={!form.brand}
                options={models.map((model: string) => ({
                  value: model,
                  label: model,
                }))}
                placeholder="Select or add model..."
                formatCreateLabel={(inputValue) =>
                  `Add "${inputValue}" as a new model.`
                }
                onChange={(selectedOption) => {
                  handleChange(
                    "model",
                    selectedOption ? selectedOption.value : null,
                  );
                }}
                value={
                  form.model ? { value: form.model, label: form.model } : null
                }
              />
            </div>

            <div className="flex flex-col w-1/5">
              <label>Model Year</label>
              <Select<{ value: number; label: number }>
                options={Array.from(
                  { length: new Date().getFullYear() - 2000 + 1 },
                  (_, index) => 2000 + index,
                ).map((year) => ({
                  value: year,
                  label: year,
                }))}
                placeholder="Select model year..."
                onChange={(selectedOption) => {
                  handleChange(
                    "modelYear",
                    selectedOption ? selectedOption.value : null,
                  );
                }}
                value={
                  form.modelYear
                    ? { value: form.modelYear, label: form.modelYear }
                    : null
                }
              />
            </div>
          </div>
          <div className="flex flex-row gap-4">
            <div className="flex flex-col w-2/5">
              <label>Segment</label>
              <CreatableSelect<{ value: string; label: string }>
                isClearable
                options={segments.map((segment: string) => ({
                  value: segment,
                  label: segment,
                }))}
                placeholder="Select or add segment..."
                formatCreateLabel={(inputValue) =>
                  `Add "${inputValue}" as a new segment.`
                }
                onChange={(selectedOption) => {
                  handleChange(
                    "segment",
                    selectedOption ? selectedOption.value : null,
                  );
                }}
                value={
                  form.segment
                    ? { value: form.segment, label: form.segment }
                    : null
                }
              />
            </div>
            <div className="flex flex-col w-1/5">
              <label>Number of Doors</label>
              <Select
                options={[2, 3, 4, 5].map((num) => ({
                  value: num,
                  label: `${num} Doors`,
                }))}
                placeholder="Select number of doors"
                value={{
                  value: form.numberOfDoors,
                  label: `${form.numberOfDoors} Seats`,
                }}
                onChange={(selectedOption) =>
                  handleChange("numberOfDoors", selectedOption?.value || null)
                }
              />
            </div>

            <div className="flex flex-col w-1/5">
              <label>Seating Capacity</label>
              <Select
                options={[2, 4, 5, 6, 7, 8].map((num) => ({
                  value: num,
                  label: `${num} Seats`,
                }))}
                placeholder="Select seating capacity"
                value={{
                  value: form.seatingCapacity,
                  label: `${form.seatingCapacity} Seats`,
                }}
                onChange={(selectedOption) =>
                  handleChange("seatingCapacity", selectedOption?.value || null)
                }
              />
            </div>

            <div className="flex flex-col w-1/5">
              <label>Luggage Capacity</label>
              <Select
                options={[0, 50, 100, 150, 200, 250].map((num) => ({
                  value: num,
                  label: `${num} L`,
                }))}
                placeholder="Select luggage capacity"
                value={{
                  value: form.luggageCapacity,
                  label: `${form.luggageCapacity} L`,
                }}
                onChange={(selectedOption) =>
                  handleChange("luggageCapacity", selectedOption?.value || null)
                }
              />
            </div>
          </div>
          <div className="flex flex-row gap-4">
            <div className="flex flex-col  w-1/4">
              <label>Category</label>
              <Select<{ value: string; label: string }>
                options={["LUXURY", "ECONOMY", "PREMIUM"].map((c) => ({
                  value: c,
                  label: c,
                }))}
                placeholder="Select category"
                value={{
                  value: form.category,
                  label: form.category,
                }}
                onChange={(selectedOption) =>
                  handleChange("category", selectedOption?.value || null)
                }
              />
            </div>

            <div className="flex flex-col w-1/4">
              <label>Engine Type</label>
              <Select
                options={["PETROL", "DIESEL", "ELECTRIC", "HYBRID"].map(
                  (e) => ({
                    value: e,
                    label: e,
                  }),
                )}
                placeholder="Select engine type"
                value={{
                  value: form.engineType,
                  label: form.engineType,
                }}
                onChange={(selectedOption) =>
                  handleChange("engineType", selectedOption?.value || null)
                }
              />
            </div>

            <div className="flex flex-col w-1/4">
              <label>Transmission Type</label>
              <Select
                options={["MANUAL", "AUTOMATIC"].map((t) => ({
                  value: t,
                  label: t,
                }))}
                placeholder="Select transmission type"
                value={{
                  value: form.transmissionType,
                  label: form.transmissionType,
                }}
                onChange={(selectedOption) =>
                  handleChange(
                    "transmissionType",
                    selectedOption?.value || null,
                  )
                }
              />
            </div>

            <div className="flex flex-col w-1/4">
              <label>Drivetrain</label>
              <Select
                options={["FWD", "RWD", "AWD"].map((d) => ({
                  value: d,
                  label: d,
                }))}
                placeholder="Select drivetrain"
                value={{
                  value: form.drivetrain,
                  label: form.drivetrain,
                }}
                onChange={(selectedOption) =>
                  handleChange("drivetrain", selectedOption?.value || null)
                }
              />
            </div>
          </div>
          <div className="flex flex-row gap-4">
            <div className="flex flex-col w-1/2">
              <label>Features</label>
              <CreatableSelect
                isMulti
                options={features.map((f) => ({
                  value: f,
                  label: f,
                }))}
                placeholder="Select or add feature(s)..."
                formatCreateLabel={(inputValue) =>
                  `Add "${inputValue}" as a new feature.`
                }
                value={form.features.map((feature) => ({
                  value: feature,
                  label: feature,
                }))}
                onChange={(selectedOptions) => {
                  handleChange(
                    "features",
                    selectedOptions.map((opt: { value: string }) => opt.value),
                  );
                }}
              />
            </div>

            <div className="flex flex-col w-1/4">
              <label>Motor Displacement</label>
              <input
                type="number"
                step="0.1"
                placeholder="Motor Displacement (L)"
                value={form.motorDisplacement}
                onChange={(e) =>
                  handleChange("motorDisplacement", parseFloat(e.target.value))
                }
                className="border px-3 py-2 rounded"
                required
                min={0}
              />
            </div>

            <div className="flex flex-col w-1/4">
              <label>Rental Price Per Day (€)</label>
              <input
                type="number"
                step="0.01"
                placeholder="Rental Price Per Day"
                value={form.rentalPricePerDay}
                onChange={(e) =>
                  handleChange("rentalPricePerDay", parseFloat(e.target.value))
                }
                className="border px-3 py-2 rounded"
                required
                min={0}
              />
            </div>
          </div>

          <div className="flex flex-row gap-4 justify-end">
            <button
              className="bg-blue-400 p-2 rounded-2xl w-1/4"
              onClick={submitAndEdit}
            >
              Edit
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
