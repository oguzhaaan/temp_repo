import { useEffect, useState } from "react";
import { Reservation } from "../../models/Reservation.ts";
import API from "../../api";
import toast from "react-hot-toast";
import dayjs from "dayjs";
import { ReservationManagementModal } from "./ReservationManagementModal.tsx";

export const ReservationManagementPage = () => {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [selectedReservationId, setSelectedReservationId] = useState<
    number | null
  >(null);
  const [triggerRefresh, setTriggerRefresh] = useState(false);

  const refreshReservations = () => {
    setTriggerRefresh((prev) => !prev);
  };

  useEffect(() => {
    const loadReservations = async () => {
      try {
        const reservations = await API.getAllReservations();
        setReservations(reservations);
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    loadReservations();
  }, [triggerRefresh]);

  return (
    <>
      {selectedReservationId && (
        <ReservationManagementModal
          reservationId={selectedReservationId}
          onClose={() => setSelectedReservationId(null)}
          refreshReservations={refreshReservations}
        />
      )}
      <div className="fixed inset-0 flex flex-col bg-white bg-gradient-to-br from-gray-100 via-blue-50 to-white">
        <div className="py-6 pt-20">
          <h1 className="text-center text-4xl font-bold text-blue-600">
            Manage Reservations
          </h1>
        </div>
        <div className="flex-1 overflow-y-auto px-4 pb-32">
          {/* pb-32 gives space for bottom panel */}
          <div className="flex justify-center">
            <div className="overflow-x-visible rounded-2xl border border-gray-200 shadow-xl">
              <table className="min-w-full rounded-2xl bg-white text-sm text-gray-700">
                <thead className="sticky top-0 z-10">
                  <tr className="bg-gradient-to-r from-blue-600 to-blue-500 text-xs uppercase tracking-wider text-white">
                    <th className="rounded-tl-2xl px-2 py-3 text-left">
                      Reservation ID
                    </th>
                    <th className="px-2 py-3 text-left">User ID</th>
                    <th className="px-2 py-3 text-left">Vehicle ID</th>
                    <th className="px-2 py-3 text-left">Start Date</th>
                    <th className="px-2 py-3 text-left">End Date</th>
                    <th className="px-2 py-3 text-left">Status</th>
                    <th className="px-2 py-3 text-left">Pickup Date</th>
                    <th className="px-2 py-3 text-left">Dropoff Date</th>
                    <th className="px-2 py-3 text-left">Created</th>
                    <th className="px-2 py-3 text-left">Updated</th>
                    <th className="px-2 py-3 text-left">Total Price</th>
                    <th className="px-2 py-3 text-left">Cancelled</th>
                    <th className="rounded-tr-2xl px-2 py-3 text-left">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {reservations.map((reservation) => (
                    <tr
                      key={reservation.reservationId}
                      className="transition-colors duration-200 hover:bg-blue-50"
                    >
                      <td className="px-4 py-3">{reservation.reservationId}</td>
                      <td className="px-4 py-3">{reservation.userId}</td>
                      <td className="px-4 py-3">{reservation.vehicle.id}</td>
                      <td className="px-4 py-3">
                        {dayjs(reservation.startDate).format("MMM D, YYYY")}
                      </td>
                      <td className="px-4 py-3">
                        {dayjs(reservation.endDate).format("MMM D, YYYY")}
                      </td>
                      <td className="px-4 py-3">
                        <span
                          className={`inline-block rounded-full px-2 py-1 text-xs font-semibold ${
                            reservation.reservationStatus === "CANCELLED"
                              ? "bg-red-200 text-red-800"
                              : reservation.reservationStatus === "CONFIRMED"
                                ? "bg-green-200 text-green-800"
                                : reservation.reservationStatus === "ONGOING"
                                  ? "bg-blue-200 text-blue-800"
                                  : "bg-yellow-200 text-yellow-800"
                          }`}
                        >
                          {reservation.reservationStatus}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        {reservation.pickup
                          ? dayjs(reservation.pickup.transferTime).format(
                              "MMM D, YYYY",
                            )
                          : "—"}
                      </td>
                      <td className="px-4 py-3">
                        {reservation.dropoff
                          ? dayjs(reservation.dropoff.transferTime).format(
                              "MMM D, YYYY",
                            )
                          : "—"}
                      </td>
                      <td className="px-4 py-3">
                        {dayjs(reservation.createdAt).format("MMM D, YYYY")}
                      </td>
                      <td className="px-4 py-3">
                        {dayjs(reservation.updatedAt).format("MMM D, YYYY")}
                      </td>
                      <td className="px-4 py-3 font-medium">
                        €{reservation.totalPrice.toFixed(2)}
                      </td>
                      <td className="px-4 py-3">
                        {reservation.cancellationDate
                          ? dayjs(reservation.cancellationDate).format(
                              "MMM D, YYYY",
                            )
                          : "—"}
                      </td>
                      <td className="px-4 py-3">
                        <button
                          className="rounded-full border border-purple-600 px-3 py-1 text-purple-600 transition hover:bg-purple-600 hover:text-white"
                          onClick={() => {
                            setSelectedReservationId(reservation.reservationId);
                          }}
                        >
                          Manage
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
