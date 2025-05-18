import { useEffect, useState } from "react";
import { MaintenanceHistory } from "../../models/MaintenanceHistory.ts";
import API from "../../api";
import toast from "react-hot-toast";
import dayjs from "dayjs";

interface VehicleMaintenanceHistoriesProps {
  vehicleId: number;
  onClose: () => void;
}

export const VehicleMaintenanceHistories = ({
  vehicleId,
  onClose,
}: VehicleMaintenanceHistoriesProps) => {
  const [histories, setHistories] = useState<MaintenanceHistory[]>([]);

  useEffect(() => {
    const loadHistories = async () => {
      try {
        const data = await API.getAllMaintenanceHistoryByVehicleId(vehicleId);
        setHistories(data);
      } catch (error) {
        toast.error((error as Error).message);
      }
    };

    loadHistories();
  }, [vehicleId]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-5xl max-h-[80vh] overflow-y-auto p-6 animate-fade-in">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-red-500 hover:text-red-700 text-lg"
        >
          <i className="bi bi-x-circle-fill" />
        </button>

        <h1 className="text-2xl font-bold text-center mb-6">
          Vehicle Maintenance History
        </h1>

        <div className="overflow-x-auto">
          <table className="min-w-full border-collapse border border-gray-300 text-sm">
            <thead className="bg-gray-100">
              <tr>
                <th className="border border-gray-300 px-4 py-2 text-left">
                  ID
                </th>
                <th className="border border-gray-300 px-4 py-2 text-left">
                  Date
                </th>
                <th className="border border-gray-300 px-4 py-2 text-left">
                  Status
                </th>
                <th className="border border-gray-300 px-4 py-2 text-left">
                  Defect
                </th>
                <th className="border border-gray-300 px-4 py-2 text-left">
                  Service
                </th>
                <th className="border border-gray-300 px-4 py-2 text-left">
                  Description
                </th>
              </tr>
            </thead>
            <tbody>
              {histories.map((history) => (
                <tr key={history.id} className="hover:bg-gray-50">
                  <td className="border border-gray-300 px-4 py-2">
                    {history.id}
                  </td>
                  <td className="border border-gray-300 px-4 py-2">
                    {dayjs(history.maintenanceDate).format(
                      "MMM D, YYYY h:mm A",
                    )}
                  </td>
                  <td className="border border-gray-300 px-4 py-2">
                    {history.maintenanceStatus}
                  </td>
                  <td className="border border-gray-300 px-4 py-2">
                    {history.defect}
                  </td>
                  <td className="border border-gray-300 px-4 py-2">
                    {history.service}
                  </td>
                  <td className="border border-gray-300 px-4 py-2">
                    {history.maintenanceDescription}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
