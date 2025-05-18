import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  FaCheckCircle,
  FaTimesCircle,
  FaReceipt,
  FaArrowLeft,
} from "react-icons/fa";
import API from "../../api";
import toast from "react-hot-toast";
import { Reservation } from "../../models/Reservation.ts";

export const PaymentResultPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const params = new URLSearchParams(location.search);

  const wasPaymentSuccessful = params.get("Success") === "true";
  const reservationId = parseInt(params.get("reservationId") || "0");

  const [userReservation, setUserReservation] = useState<Reservation | null>(
    null,
  );

  useEffect(() => {
    const fetchReservation = async (id: number) => {
      try {
        const reservation = await API.getReservationById(id);
        setUserReservation(reservation);
        toast.success("Reservation fetched successfully!");
      } catch (error) {
        toast.error((error as Error).message);
      }
    };

    if (reservationId) {
      fetchReservation(reservationId);
    } else {
      toast.error("No reservation ID found.");
    }
  }, [reservationId]);

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-br from-indigo-100 to-purple-200 px-4 py-12">
      <div className="w-full max-w-lg rounded-2xl bg-white p-10 shadow-2xl ring-1 ring-gray-200">
        <div className="mb-8 text-center">
          {wasPaymentSuccessful ? (
            <div className="animate-fadeIn flex flex-col items-center text-green-600">
              <FaCheckCircle className="mb-2 text-5xl" />
              <h2 className="text-3xl font-bold">Payment Successful!</h2>
              <p className="mt-1 text-lg">Thank you for your reservation.</p>
            </div>
          ) : (
            <div className="animate-fadeIn flex flex-col items-center text-red-600">
              <FaTimesCircle className="mb-2 text-5xl" />
              <h2 className="text-3xl font-bold">Payment Failed</h2>
              <p className="mt-1 text-lg">
                Please try again or contact support.
              </p>
            </div>
          )}
        </div>

        <div className="mb-8 text-left">
          <h3 className="flex items-center gap-2 text-xl font-semibold text-gray-700">
            <FaReceipt /> Reservation Details
          </h3>
          <div className="mt-4 rounded-lg border border-gray-200 bg-gray-50 p-4 shadow-sm">
            {userReservation ? (
              <ul className="space-y-2 text-gray-700">
                <li>
                  <strong>Reservation ID:</strong>{" "}
                  {userReservation.reservationId}
                </li>
                <li>
                  <strong>Car Brand:</strong>{" "}
                  {userReservation.vehicle.carModel.brand}
                </li>
                <li>
                  <strong>Car Model:</strong>{" "}
                  {userReservation.vehicle.carModel.model}
                </li>
                <li>
                  <strong>License Plate:</strong>{" "}
                  {userReservation.vehicle.licensePlate}
                </li>
              </ul>
            ) : (
              <p className="italic text-gray-500">
                No reservation data available.
              </p>
            )}
          </div>
        </div>

        <div className="text-center">
          <button
            onClick={() => navigate("/user-dashboard")}
            className="inline-flex items-center gap-2 rounded-full bg-indigo-600 px-6 py-3 text-white shadow-lg transition duration-200 hover:scale-105 hover:bg-indigo-700"
          >
            <FaArrowLeft />
            Return to Dashboard
          </button>
        </div>
      </div>
    </div>
  );
};
