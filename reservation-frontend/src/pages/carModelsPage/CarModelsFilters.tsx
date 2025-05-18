import { CarModelQueryParams } from "../../models/CarModelQueryParams.ts";
import Select from "react-select";
import { useEffect, useState } from "react";
import API from "../../api";
import toast from "react-hot-toast";

interface CarModelsFiltersProps {
  filters: CarModelQueryParams;
  setFilters: (filters: CarModelQueryParams) => void;
  rowsPerPage: number;
  setRowsPerPage: (rows: number) => void;
  refreshCarModels: () => void;
  onClose: () => void;
}

export const CarModelsFilters = ({
  filters,
  setFilters,
  rowsPerPage,
  setRowsPerPage,
  refreshCarModels,
  onClose,
}: CarModelsFiltersProps) => {
  const [brands, setBrands] = useState<string[]>([]);
  const [brandModels, setBrandModels] = useState<Record<string, string[]>>({});
  const [features, setFeatures] = useState<string[]>([]);
  const [segments, setSegments] = useState<string[]>([]);

  const [selectedBrands, setSelectedBrands] = useState<string[]>([]);
  const [selectedModels, setSelectedModels] = useState<
    Record<string, string[]>
  >({});

  const [selectedFeatures, setSelectedFeatures] = useState<string[]>(
    filters.features || [],
  );
  const [selectedSegments, setSelectedSegments] = useState<string[]>(
    filters.segments || [],
  );

  const [enteredMinRentalPrice, setEnteredMinRentalPrice] = useState<
    number | undefined
  >(filters.minRentalPrice || undefined);
  const [enteredMaxRentalPrice, setEnteredMaxRentalPrice] = useState<
    number | undefined
  >(filters.maxRentalPrice || undefined);

  const [selectedModelYears, setSelectedModelYears] = useState<number[]>(
    filters.modelYears || [],
  );
  const [selectedCategories, setSelectedCategories] = useState<string[]>(
    filters.categories || [],
  );
  const [selectedEngineTypes, setSelectedEngineTypes] = useState<string[]>(
    filters.engineTypes || [],
  );
  const [selectedTransmissionTypes, setSelectedTransmissionTypes] = useState<
    string[]
  >(filters.transmissionTypes || []);
  const [selectedDrivetrains, setSelectedDrivetrains] = useState<string[]>(
    filters.drivetrains || [],
  );

  useEffect(() => {
    const initialSelectedBrands = filters.brandModels
      ? filters.brandModels.map((filter) => filter.split(":")[0])
      : [];
    setSelectedBrands(initialSelectedBrands);

    const initialSelectedModels: Record<string, string[]> = {};
    filters.brandModels?.forEach((filter) => {
      const [brand, models] = filter.split(":");
      if (models) {
        initialSelectedModels[brand] = models.split("|");
      }
    });
    setSelectedModels(initialSelectedModels);
  }, []);

  const clearFilters = () => {
    setSelectedBrands([]);
    setSelectedModels({});
    setSelectedFeatures([]);
    setSelectedSegments([]);
    setEnteredMinRentalPrice(undefined);
    setEnteredMaxRentalPrice(undefined);
    setSelectedModelYears([]);
    setSelectedCategories([]);
    setSelectedEngineTypes([]);
    setSelectedTransmissionTypes([]);
    setSelectedDrivetrains([]);
    setFilters({});
    setRowsPerPage(10);
    refreshCarModels();
  };

  const applyFilters = () => {
    const brandModelFilterStrings = selectedBrands.map((brand) => {
      const models = selectedModels[brand];
      if (!models || models.length === 0) return brand;
      return `${brand}:${models.join("|")}`;
    });

    const filters: CarModelQueryParams = {
      brandModels: brandModelFilterStrings,
      features: selectedFeatures,
      segments: selectedSegments,
      minRentalPrice: enteredMinRentalPrice,
      maxRentalPrice: enteredMaxRentalPrice,
      modelYears: selectedModelYears,
      categories: selectedCategories,
      engineTypes: selectedEngineTypes,
      transmissionTypes: selectedTransmissionTypes,
      drivetrains: selectedDrivetrains,
    };
    setFilters(filters);
    refreshCarModels();
    onClose();
  };

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

  // Fetch models when selected brands change
  useEffect(() => {
    const fetchModels = async () => {
      for (const brand of selectedBrands) {
        if (!brandModels[brand]) {
          try {
            const data = await API.getModelsByBrand(brand);
            setBrandModels((prev) => ({
              ...prev,
              [brand]: data,
            }));
          } catch (error) {
            toast.error((error as Error).message);
          }
        }
      }
    };

    fetchModels();
  }, [selectedBrands]);

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

  return (
    <div className="animate-slide-up fixed bottom-10 left-10 right-10 top-20 z-50 w-full max-w-xs overflow-y-scroll rounded-3xl border border-white/20 bg-white/50 p-4 shadow-2xl backdrop-blur-xl">
      <div className="flex max-h-full flex-col overflow-y-visible">
        <button
          onClick={onClose}
          className="absolute right-4 top-4 text-red-500 hover:text-red-700"
        >
          <i className="bi bi-x-circle"></i>
        </button>
        <h2 className="mb-4 text-2xl font-bold text-black">Filters</h2>

        <div className="w-full space-y-4">
          <div>
            <label className="text-black">Brands</label>
            <Select
              isMulti
              options={brands.map((brand) => ({
                value: brand,
                label: brand,
              }))}
              placeholder="Select brand(s)"
              onChange={(selected) => {
                const selectedBrandValues = selected.map((s) => s.value);
                setSelectedBrands(selectedBrandValues);

                // Remove deselected brands from selectedModels
                setSelectedModels((prev) =>
                  Object.fromEntries(
                    Object.entries(prev).filter(([brand]) =>
                      selectedBrandValues.includes(brand),
                    ),
                  ),
                );
              }}
              value={selectedBrands.map((brand) => ({
                value: brand,
                label: brand,
              }))}
            />
          </div>

          {selectedBrands.map((brand) => (
            <div key={brand} className="w-full">
              <label className="mb-1 block text-sm text-black">
                Models for {brand}
              </label>
              <Select
                isMulti
                options={(brandModels[brand] || []).map((model) => ({
                  value: model,
                  label: model,
                }))}
                placeholder={`Select ${brand} model(s)`}
                onChange={(selected) => {
                  setSelectedModels((prev) => ({
                    ...prev,
                    [brand]: selected ? selected.map((s) => s.value) : [],
                  }));
                }}
                value={(selectedModels[brand] || []).map((model) => ({
                  value: model,
                  label: model,
                }))}
              />
              <p className="mt-1 text-xs text-gray-600">
                If no models are selected, <strong>all models</strong> for{" "}
                <strong>{brand}</strong> will be included.
              </p>
            </div>
          ))}
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
            <label className="text-black">Features</label>
            <Select
              isMulti
              options={features.map((feature) => ({
                value: feature,
                label: feature,
              }))}
              placeholder="Select feature(s)"
              onChange={(selected) => {
                const selectedFeatureValues = selected.map((s) => s.value);
                setSelectedFeatures(selectedFeatureValues);
              }}
              value={selectedFeatures?.map((feature) => ({
                value: feature,
                label: feature,
              }))}
            />
          </div>
          <div>
            <label className="text-black">Segments</label>
            <Select
              isMulti
              options={segments.map((segment) => ({
                value: segment,
                label: segment,
              }))}
              placeholder="Select segment(s)"
              onChange={(selected) => {
                const selectedSegmentValues = selected.map((s) => s.value);
                setSelectedSegments(selectedSegmentValues);
              }}
              value={selectedSegments?.map((segment) => ({
                value: segment,
                label: segment,
              }))}
            />
          </div>
          <div>
            <label className="text-black">Min Rental Price</label>
            <input
              type="number"
              value={enteredMinRentalPrice || ""}
              onChange={(e) =>
                setEnteredMinRentalPrice(
                  e.target.value ? parseFloat(e.target.value) : undefined,
                )
              }
              placeholder="Enter min rental price"
              className="w-full rounded-lg border p-2"
            />
          </div>
          <div>
            <label className="text-black">Max Rental Price</label>
            <input
              type="number"
              value={enteredMaxRentalPrice || ""}
              onChange={(e) =>
                setEnteredMaxRentalPrice(
                  e.target.value ? parseFloat(e.target.value) : undefined,
                )
              }
              placeholder="Enter max rental price"
              className="w-full rounded-lg border p-2"
            />
          </div>
          <div>
            <label className="text-black">Model Years</label>
            <Select
              isMulti
              options={Array.from({ length: 30 }, (_, i) => ({
                value: new Date().getFullYear() - i,
                label: (new Date().getFullYear() - i).toString(),
              }))}
              placeholder="Select model year(s)"
              onChange={(selected) => {
                const selectedModelYearValues = selected.map((s) => s.value);
                setSelectedModelYears(selectedModelYearValues);
              }}
              value={selectedModelYears?.map((year) => ({
                value: year,
                label: year.toString(),
              }))}
            />
          </div>
          <div>
            <label className="text-black">Categories</label>
            <Select
              isMulti
              options={["LUXURY", "ECONOMY", "PREMIUM"].map((category) => ({
                value: category,
                label: category,
              }))}
              placeholder="Select category(s)"
              onChange={(selected) => {
                const selectedCategoryValues = selected.map((s) => s.value);
                setSelectedCategories(selectedCategoryValues);
              }}
              value={selectedCategories?.map((category) => ({
                value: category,
                label: category,
              }))}
            />
          </div>
          <div>
            <label className="text-black">Engine Types</label>
            <Select
              isMulti
              options={["PETROL", "DIESEL", "ELECTRIC", "HYBRID"].map(
                (engineType) => ({
                  value: engineType,
                  label: engineType,
                }),
              )}
              placeholder="Select engine type(s)"
              onChange={(selected) => {
                const selectedEngineTypeValues = selected.map((s) => s.value);
                setSelectedEngineTypes(selectedEngineTypeValues);
              }}
              value={selectedEngineTypes?.map((engineType) => ({
                value: engineType,
                label: engineType,
              }))}
            />
          </div>
          <div>
            <label className="text-black">Transmission Types</label>
            <Select
              isMulti
              options={["MANUAL", "AUTOMATIC"].map((transmissionType) => ({
                value: transmissionType,
                label: transmissionType,
              }))}
              placeholder="Select transmission type(s)"
              onChange={(selected) => {
                const selectedTransmissionTypeValues = selected.map(
                  (s) => s.value,
                );
                setSelectedTransmissionTypes(selectedTransmissionTypeValues);
              }}
              value={selectedTransmissionTypes?.map((transmissionType) => ({
                value: transmissionType,
                label: transmissionType,
              }))}
            />
          </div>
          <div>
            <label className="text-black">Drivetrains</label>
            <Select
              isMulti
              options={["FWD", "RWD", "AWD"].map((drivetrain) => ({
                value: drivetrain,
                label: drivetrain,
              }))}
              placeholder="Select drivetrain(s)"
              onChange={(selected) => {
                const selectedDrivetrainValues = selected.map((s) => s.value);
                setSelectedDrivetrains(selectedDrivetrainValues);
              }}
              value={selectedDrivetrains?.map((drivetrain) => ({
                value: drivetrain,
                label: drivetrain,
              }))}
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
