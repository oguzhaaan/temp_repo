import { Note } from "../../models/Note.ts";
import { useEffect, useState } from "react";
import API from "../../api";
import toast from "react-hot-toast";
import dayjs from "dayjs";

interface VehicleNotesProps {
  vehicleId: number;
  onClose: () => void;
}

export const VehicleNotes = ({ vehicleId, onClose }: VehicleNotesProps) => {
  const [notes, setNotes] = useState<Note[]>([]);

  useEffect(() => {
    const loadNotes = async () => {
      try {
        const data = await API.getAllNotesByVehicleId(vehicleId);
        setNotes(data);
      } catch (error) {
        toast.error((error as Error).message);
      }
    };
    loadNotes();
  }, [vehicleId]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[80vh] overflow-y-auto p-6 animate-fade-in">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-red-500 hover:text-red-700 text-lg"
        >
          <i className="bi bi-x-circle-fill"></i>
        </button>

        <h1 className="text-2xl font-bold text-center mb-6">Vehicle Notes</h1>

        <div className="overflow-x-auto">
          <table className="min-w-full border-collapse border border-gray-300">
            <thead className="bg-gray-100">
              <tr>
                <th className="border border-gray-300 px-4 py-2 text-left">
                  ID
                </th>
                <th className="border border-gray-300 px-4 py-2 text-left">
                  Note
                </th>
                <th className="border border-gray-300 px-4 py-2 text-left">
                  Date
                </th>
              </tr>
            </thead>
            <tbody>
              {notes.map((note) => (
                <tr key={note.id} className="hover:bg-gray-50">
                  <td className="border border-gray-300 px-4 py-2">
                    {note.id}
                  </td>
                  <td className="border border-gray-300 px-4 py-2">
                    {note.note}
                  </td>
                  <td className="border border-gray-300 px-4 py-2 whitespace-nowrap">
                    {dayjs(note.date).format("MMM D, YYYY h:mm A")}
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
