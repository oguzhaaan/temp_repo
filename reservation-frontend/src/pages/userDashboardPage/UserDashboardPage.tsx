import { useEffect, useState } from "react";
import { Reservation } from "../../models/Reservation.ts";
import API from "../../api";
import toast from "react-hot-toast";
import dayjs from "dayjs";

import {
  FaBan,
  FaCalendar,
  FaCar,
  FaCheckCircle,
  FaCheckDouble,
  FaClock,
  FaMoneyBill,
  FaMoneyCheckAlt,
  FaTimesCircle,
} from "react-icons/fa";
import { ConfirmationModal } from "../../components/ConfirmationModal.tsx";

export const UserDashboardPage = () => {
  const [userId, setUserId] = useState<number | null>(null);
  const [userReservations, setUserReservations] = useState<Reservation[]>([]);
  const [triggerRefresh, setTriggerRefresh] = useState(false);
  const [selectedReservationIdToCancel, setSelectedReservationIdToCancel] =
    useState<number | null>(null);

  useEffect(() => {
    if (!userId) return;

    const fetchUserData = async () => {
      try {
        const reservations = await API.getAllReservationsByUserId(userId);
        setUserReservations(reservations);
        toast.success("Reservations fetched successfully!");
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    fetchUserData();
  }, [triggerRefresh]);

  const handleCancelReservation = async (reservationId: number) => {
    try {
      await API.updateReservationStatus(reservationId, "CANCELLED");
      toast.success("Reservation cancelled successfully!");
      setTriggerRefresh((prev) => !prev);
      setSelectedReservationIdToCancel(null);
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  const handlePayment = async (reservationId: number) => {
    const data = await API.payReservation(reservationId);
    console.log("Payment initiation response:", data);
    window.location.href = data.approvalUrl;
  };

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case "confirmed":
        return "text-green-600 bg-green-100";
      case "ongoing":
        return "text-yellow-700 bg-yellow-100";
      case "cancelled":
        return "text-red-600 bg-red-100";
      case "completed":
        return "text-blue-700 bg-blue-100";
      default:
        return "text-gray-600 bg-gray-100";
    }
  };

  return (
    <>
      <ConfirmationModal
        isOpen={!!selectedReservationIdToCancel}
        onClose={() => setSelectedReservationIdToCancel(null)}
        onConfirm={() =>
          handleCancelReservation(selectedReservationIdToCancel!)
        }
        title="Cancel Reservation"
        message="Are you sure you want to cancel this reservation?"
        confirmText="Yes"
        cancelText="No"
      />
      <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-white p-6">
        <div className="mx-auto mb-8 max-w-xl text-center">
          <h2 className="mb-4 text-3xl font-bold text-indigo-900">
            Your Reservations
          </h2>
          <div className="flex flex-col items-center gap-3 sm:flex-row sm:justify-center">
            <input
              type="number"
              min={1}
              placeholder="Enter your User ID"
              className="w-full rounded-lg border border-gray-300 px-4 py-2 shadow-sm focus:border-indigo-500 focus:outline-none sm:w-64"
              onChange={(e) => setUserId(Number(e.target.value))}
            />
            <button
              onClick={() => {
                if (userId) {
                  setTriggerRefresh((prev) => !prev);
                } else {
                  toast.error("Please enter a valid User ID");
                }
              }}
              className="rounded-lg bg-indigo-600 px-4 py-2 text-white transition hover:bg-indigo-700"
            >
              Fetch Reservations
            </button>
          </div>
        </div>

        <div className="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
          {userReservations.map((reservation) => (
            <div
              key={reservation.reservationId}
              className="flex flex-col justify-between rounded-2xl border border-gray-200 bg-white/70 p-6 shadow-md backdrop-blur-md transition-all hover:shadow-xl"
            >
              <div>
                <h3 className="flex items-center gap-2 text-xl font-semibold text-indigo-900">
                  <FaCar className="h-5 w-5 text-indigo-700" />
                  {reservation.vehicle.carModel.brand}{" "}
                  {reservation.vehicle.carModel.model}
                </h3>
                <p className="mt-2 text-gray-700">
                  <span className="font-medium">License Plate:</span>{" "}
                  {reservation.vehicle.licensePlate}
                </p>

                <div className="mt-4 flex flex-col gap-1 text-sm text-gray-600">
                  <p className="flex items-center gap-2">
                    <FaCalendar className="h-4 w-4" />
                    <span>
                      <strong>Start:</strong>{" "}
                      {dayjs(reservation.startDate).format("MMM D, YYYY")}
                    </span>
                  </p>
                  <p className="flex items-center gap-2">
                    <FaCalendar className="h-4 w-4" />
                    <span>
                      <strong>End:</strong>{" "}
                      {dayjs(reservation.endDate).format("MMM D, YYYY")}
                    </span>
                  </p>
                </div>
              </div>

              <div className="mt-4 flex items-center justify-between">
                <div
                  className={`inline-flex items-center gap-2 rounded-full px-3 py-1 text-sm font-medium ${getStatusColor(
                    reservation.reservationStatus,
                  )}`}
                >
                  {reservation.reservationStatus.toLowerCase() ===
                  "confirmed" ? (
                    <FaCheckCircle className="h-4 w-4" />
                  ) : reservation.reservationStatus.toLowerCase() ===
                    "cancelled" ? (
                    <FaTimesCircle className="h-4 w-4" />
                  ) : reservation.reservationStatus.toLowerCase() ===
                    "ongoing" ? (
                    <FaClock className="h-4 w-4" />
                  ) : reservation.reservationStatus.toLowerCase() ===
                    "completed" ? (
                    <FaCheckDouble className="h-4 w-4" />
                  ) : reservation.reservationStatus.toLowerCase() ===
                    "waiting_for_payment" ? (
                    <FaMoneyCheckAlt className="h-4 w-4" />
                  ) : null}
                  {reservation.reservationStatus
                    .toLowerCase()
                    .split("_")
                    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
                    .join(" ")}
                </div>

                {reservation.reservationStatus.toLowerCase() ===
                  "waiting_for_payment" && (
                  <div className="flex flex-row justify-end space-x-4">
                    <button
                      onClick={() => handlePayment(reservation.reservationId)}
                      className="text-s flex items-center gap-1 rounded-md bg-blue-600 px-3 py-1 font-semibold text-white shadow hover:bg-blue-800"
                    >
                      <FaMoneyBill className="h-3 w-3" />
                      Pay
                    </button>
                    <button
                      onClick={() =>
                        setSelectedReservationIdToCancel(
                          reservation.reservationId,
                        )
                      }
                      className="text-s flex items-center gap-1 rounded-md bg-red-500 px-3 py-1 font-semibold text-white shadow hover:bg-red-600"
                    >
                      <FaBan className="h-3 w-3" />
                      Cancel
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </>
  );
};
