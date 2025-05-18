import { Reservation } from "../../models/Reservation.ts";
import { useEffect, useState } from "react";
import { Transfer } from "../../models/Transfer.ts";
import { TransferType } from "../../types/TransferType.ts";
import API from "../../api";
import toast from "react-hot-toast";
import dayjs from "dayjs";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

interface ReservationManagementModalProps {
  reservationId: number;
  onClose: () => void;
  refreshReservations: () => void;
}

export const ReservationManagementModal = ({
  reservationId,
  onClose,
  refreshReservations,
}: ReservationManagementModalProps) => {
  const [reservation, setReservation] = useState<Reservation | null>(null);
  const [triggerRefresh, setTriggerRefresh] = useState(false);
  const [newTransfer, setNewTransfer] = useState<Transfer>({
    transferTime: "",
    location: "",
    handledByStaffId: undefined,
  });
  const [updatedReservation, setUpdatedReservation] = useState({
    carModelId: reservationId,
    reservationTimeInterval: {
      startDate: "",
      endDate: "",
    },
  });

  const [activeForm, setActiveForm] = useState<
    | "EDIT_RESERVATION"
    | "ADD_PICKUP"
    | "EDIT_PICKUP"
    | "ADD_DROPOFF"
    | "EDIT_DROPOFF"
    | null
  >(null);

  useEffect(() => {
    const loadReservation = async () => {
      try {
        const reservationData = await API.getReservationById(reservationId);
        setReservation(reservationData);
        setUpdatedReservation({
          carModelId: reservationData.vehicle.carModel.id,
          reservationTimeInterval: {
            startDate: reservationData.startDate,
            endDate: reservationData.endDate,
          },
        });
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    loadReservation();
  }, [triggerRefresh]);

  const refresh = () => {
    setTriggerRefresh((prev) => !prev);
    refreshReservations();
  };

  if (!reservation) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
        <div className="w-full max-w-4xl rounded-2xl bg-white p-8 shadow-2xl">
          <p className="text-center text-lg text-gray-700">
            Loading reservation...
          </p>
        </div>
      </div>
    );
  }

  const handleStatusChange = async (newStatus: string) => {
    try {
      await API.updateReservationStatus(reservation.reservationId, newStatus);
      toast.success("Status updated successfully");
      refresh();
    } catch (err) {
      toast.error((err as Error).message);
    }
  };

  const handleAddTransfer = async (transferType: TransferType) => {
    try {
      await API.addTransferByTypeAndReservationId(
        transferType,
        reservation.reservationId,
        newTransfer,
      );
      toast.success(`${transferType} added successfully`);
      setActiveForm(null);
      refresh();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  const handleEditTransfer = async (transferType: TransferType) => {
    try {
      await API.editTransferByTypeAndReservationId(
        transferType,
        reservation.reservationId,
        newTransfer,
      );
      toast.success(`${transferType} updated successfully`);
      setActiveForm(null);
      refresh();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  const handleDeleteTransfer = async (type: TransferType) => {
    try {
      await API.deleteTransferByTypeAndId(type, reservation.reservationId);
      toast.success(`${type} deleted`);
      refresh();
    } catch (err) {
      toast.error((err as Error).message);
    }
  };

  const handleDeleteReservation = async () => {
    try {
      await API.deleteReservationById(reservation.reservationId);
      toast.success("Reservation deleted successfully");
      onClose();
      refresh();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  const handleEditReservation = async () => {
    try {
      await API.updateReservation(reservationId, updatedReservation);
      toast.success("Reservation updated successfully");
      setActiveForm(null);
      refresh();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
      <div className="max-h-full w-full max-w-3xl overflow-y-auto rounded-2xl bg-white p-4 shadow-2xl">
        <div className="mb-6 flex items-center justify-between border-b pb-4">
          <h2 className="text-2xl font-semibold text-slate-800">
            Manage Reservation #{reservation.reservationId}
          </h2>
          <button
            onClick={onClose}
            className="rounded-full p-2 text-gray-500 transition hover:bg-gray-100 hover:text-gray-900"
          >
            ✕
          </button>
        </div>

        <div className="mb-8 rounded-2xl bg-white/60 p-3 shadow-xl ring-1 ring-slate-200 backdrop-blur-md">
          <div className="flex items-center justify-between">
            <p className="text-xl font-semibold text-slate-800">
              Reservation Status:{" "}
              <span
                className={`ml-2 inline-flex items-center gap-2 rounded-full px-4 py-1 text-sm font-bold ${
                  reservation.reservationStatus === "CONFIRMED"
                    ? "bg-indigo-100 text-indigo-600"
                    : reservation.reservationStatus === "ONGOING"
                      ? "animate-pulse bg-yellow-100 text-yellow-600"
                      : reservation.reservationStatus === "COMPLETED"
                        ? "bg-emerald-100 text-emerald-600"
                        : "bg-rose-100 text-rose-600"
                }`}
              >
                {reservation.reservationStatus}
              </span>
            </p>
          </div>

          <div className="mt-6 flex flex-wrap gap-4">
            {reservation.reservationStatus === "CONFIRMED" && (
              <>
                <button
                  onClick={() => handleStatusChange("CANCELLED")}
                  className="button bg-gradient-to-br from-rose-500 to-rose-600"
                >
                  Cancel Reservation
                </button>
                <button
                  onClick={() => handleStatusChange("ONGOING")}
                  className="button bg-gradient-to-br from-yellow-400 to-yellow-500"
                >
                  Mark Reservation as Started
                </button>
              </>
            )}
            {reservation.reservationStatus === "ONGOING" && (
              <button
                onClick={() => handleStatusChange("COMPLETED")}
                className="button bg-gradient-to-br from-emerald-500 to-emerald-600"
              >
                Mark Reservation as Completed
              </button>
            )}
          </div>
        </div>

        {activeForm !== "EDIT_RESERVATION" ? (
          reservation.reservationStatus === "CONFIRMED" && (
            <button
              onClick={() => {
                setUpdatedReservation({
                  carModelId: reservation.vehicle.carModel.id,
                  reservationTimeInterval: {
                    startDate: reservation.startDate,
                    endDate: reservation.endDate,
                  },
                });
                setActiveForm("EDIT_RESERVATION");
              }}
              className="button bg-gradient-to-r from-blue-600 to-indigo-500 hover:scale-105"
            >
              Edit Reservation Details
            </button>
          )
        ) : (
          <div className="mb-6 rounded-3xl bg-gradient-to-br from-slate-100 via-white to-slate-50 p-3 shadow-xl ring-1 ring-slate-200">
            <h3 className="mb-2 text-xl font-semibold text-slate-800">
              Edit Reservation Details
            </h3>
            <p className="mb-6 text-sm text-slate-500">
              Modify the reservation period and car model. These changes take
              effect immediately upon saving.
            </p>

            <div className="flexrow justifyaround flex gap-4">
              <div>
                <label className="mb-1 block text-sm font-medium text-slate-700">
                  Start Date
                </label>
                <DatePicker
                  selected={
                    new Date(
                      updatedReservation.reservationTimeInterval.startDate,
                    )
                  }
                  onChange={(date) =>
                    setUpdatedReservation({
                      ...updatedReservation,
                      reservationTimeInterval: {
                        ...updatedReservation.reservationTimeInterval,
                        startDate: date?.toISOString() || "",
                      },
                    })
                  }
                  dateFormat="yyyy-MM-dd"
                  className="input"
                />
              </div>

              <div>
                <label className="mb-1 block text-sm font-medium text-slate-700">
                  End Date
                </label>
                <DatePicker
                  selected={
                    new Date(updatedReservation.reservationTimeInterval.endDate)
                  }
                  onChange={(date) =>
                    setUpdatedReservation({
                      ...updatedReservation,
                      reservationTimeInterval: {
                        ...updatedReservation.reservationTimeInterval,
                        endDate: date?.toISOString() || "",
                      },
                    })
                  }
                  dateFormat="yyyy-MM-dd"
                  className="input"
                />
              </div>

              <div>
                <label className="mb-1 block text-sm font-medium text-slate-700">
                  Car Model ID
                </label>
                <input
                  type="number"
                  value={updatedReservation.carModelId}
                  onChange={(e) =>
                    setUpdatedReservation({
                      ...updatedReservation,
                      carModelId: Number(e.target.value),
                    })
                  }
                  className="input"
                />
              </div>
            </div>

            <div className="mt-8 flex justify-end gap-4">
              <button
                onClick={handleEditReservation}
                className="button bg-gradient-to-r from-green-500 to-emerald-600"
              >
                Save Changes
              </button>
              <button
                onClick={() => setActiveForm(null)}
                className="button bg-gradient-to-r from-slate-400 to-slate-500"
              >
                Cancel
              </button>
            </div>
          </div>
        )}
        <div className="my-4">
          <button
            onClick={handleDeleteReservation}
            className="button bg-gradient-to-r from-red-600 to-red-700"
          >
            Delete Reservation
          </button>
        </div>

        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
          <TransferSection
            type="PICKUP"
            reservation={reservation}
            newTransfer={newTransfer}
            setNewTransfer={setNewTransfer}
            activeForm={activeForm}
            setActiveForm={setActiveForm}
            handleAddTransfer={handleAddTransfer}
            handleEditTransfer={handleEditTransfer}
            handleDeleteTransfer={handleDeleteTransfer}
          />
          <TransferSection
            type="DROPOFF"
            reservation={reservation}
            newTransfer={newTransfer}
            setNewTransfer={setNewTransfer}
            activeForm={activeForm}
            setActiveForm={setActiveForm}
            handleAddTransfer={handleAddTransfer}
            handleEditTransfer={handleEditTransfer}
            handleDeleteTransfer={handleDeleteTransfer}
          />
        </div>
      </div>
    </div>
  );
};

interface TransferSectionProps {
  type: TransferType;
  reservation: Reservation;
  newTransfer: Transfer;
  setNewTransfer: (transfer: Transfer) => void;
  activeForm:
    | "EDIT_RESERVATION"
    | "ADD_PICKUP"
    | "EDIT_PICKUP"
    | "ADD_DROPOFF"
    | "EDIT_DROPOFF"
    | null;
  setActiveForm: (
    form:
      | "EDIT_RESERVATION"
      | "ADD_PICKUP"
      | "EDIT_PICKUP"
      | "ADD_DROPOFF"
      | "EDIT_DROPOFF"
      | null,
  ) => void;
  handleAddTransfer: (type: TransferType) => Promise<void>;
  handleEditTransfer: (type: TransferType) => Promise<void>;
  handleDeleteTransfer: (type: TransferType) => Promise<void>;
}
const TransferSection = ({
  type,
  reservation,
  newTransfer,
  setNewTransfer,
  activeForm,
  setActiveForm,
  handleAddTransfer,
  handleEditTransfer,
  handleDeleteTransfer,
}: TransferSectionProps) => {
  const transfer: Transfer = (reservation as never)[type.toLowerCase()];
  const editing = activeForm === `EDIT_${type}`;
  const adding = activeForm === `ADD_${type}`;
  return (
    <div className="rounded-2xl border border-slate-200 bg-gradient-to-br from-white to-slate-50 p-3 shadow-lg transition-all hover:shadow-xl">
      <h3 className="mb-2 text-lg font-semibold text-indigo-600">
        {type.charAt(0) + type.slice(1).toLowerCase()}
      </h3>
      {transfer && !editing ? (
        <>
          <p>
            <strong>Date:</strong>{" "}
            {dayjs(transfer.transferTime).format("MMM D, YYYY HH:mm")}
          </p>
          <p>
            <strong>Location:</strong> {transfer.location}
          </p>
          <p>
            <strong>Staff ID:</strong> {transfer.handledByStaffId}
          </p>
          {reservation.reservationStatus === "CONFIRMED" && (
            <div className="mt-3 flex gap-3">
              <button
                className="button bg-gradient-to-br from-blue-500 to-blue-600"
                onClick={() => {
                  setNewTransfer(transfer);
                  setActiveForm(`EDIT_${type}`);
                }}
              >
                Edit
              </button>
              <button
                className="button bg-gradient-to-br from-red-500 to-red-600"
                onClick={() => handleDeleteTransfer(type)}
              >
                Delete
              </button>
            </div>
          )}
        </>
      ) : !transfer &&
        reservation.reservationStatus === "CONFIRMED" &&
        !adding ? (
        <button
          className="button bg-gradient-to-br from-green-500 to-green-600"
          onClick={() => {
            setNewTransfer({
              transferTime: "",
              location: "",
              handledByStaffId: undefined,
            });
            setActiveForm(`ADD_${type}`);
          }}
        >
          Add {type.charAt(0) + type.slice(1).toLowerCase()}
        </button>
      ) : (
        (editing || adding) && (
          <TransferForm
            transfer={newTransfer}
            onSubmit={() =>
              editing ? handleEditTransfer(type) : handleAddTransfer(type)
            }
            onCancel={() => setActiveForm(null)}
            onTransferChange={setNewTransfer}
          />
        )
      )}
    </div>
  );
};

interface TransferFormProps {
  transfer: Transfer;
  onSubmit: () => void;
  onCancel: () => void;
  onTransferChange: (transfer: Transfer) => void;
}

const TransferForm = ({
  transfer,
  onSubmit,
  onCancel,
  onTransferChange,
}: TransferFormProps) => {
  return (
    <div className="mt-3 space-y-3 rounded-xl bg-slate-50 shadow-sm">
      <DatePicker
        placeholderText={"Transfer Date"}
        selected={
          transfer.transferTime ? new Date(transfer.transferTime) : null
        }
        onChange={(date) =>
          onTransferChange({
            ...transfer,
            transferTime: date ? date.toISOString() : "",
          })
        }
        showTimeSelect
        dateFormat="yyyy-MM-dd HH:mm"
        className="input"
      />
      <input
        type="text"
        placeholder="Location"
        className="input"
        value={transfer.location || ""}
        onChange={(e) =>
          onTransferChange({
            ...transfer,
            location: e.target.value,
          })
        }
      />
      <input
        type="number"
        placeholder="Staff ID"
        className="input"
        value={transfer.handledByStaffId || ""}
        onChange={(e) =>
          onTransferChange({
            ...transfer,
            handledByStaffId: Number(e.target.value),
          })
        }
      />
      <div className="flex justify-end gap-3">
        <button onClick={onSubmit} className="button bg-blue-600">
          Save
        </button>
        <button onClick={onCancel} className="button bg-gray-400">
          Cancel
        </button>
      </div>
    </div>
  );
};
