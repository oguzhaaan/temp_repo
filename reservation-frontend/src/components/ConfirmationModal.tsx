export const ConfirmationModal = ({
  isOpen,
  title,
  message,
  cancelText,
  confirmText,
  onClose,
  onConfirm,
}: {
  isOpen: boolean;
  title?: string;
  message?: string;
  cancelText?: string;
  confirmText?: string;
  onClose: () => void;
  onConfirm: () => void;
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-gray-800 bg-opacity-50">
      <div className="relative h-min w-96 max-w-lg overflow-y-auto rounded-2xl bg-white p-3 shadow-2xl">
        {/* Close Button */}
        <i
          onClick={onClose}
          className="bi bi-x-circle-fill absolute right-2 top-1 cursor-pointer text-red-600 transition-colors duration-200 hover:text-red-800"
        />

        {/* Modal Title */}
        <h2 className="mb-4 text-center text-2xl font-bold text-gray-800">
          {title || "Confirmation"}
        </h2>

        {/* Modal Message */}
        <p className="mb-6 text-center text-gray-600">
          {message || "Are you sure you want to proceed?"}
        </p>

        {/* Buttons */}
        <div className="mt-6 flex justify-end space-x-4">
          {/* Cancel Button */}
          <button
            onClick={onClose}
            className="button bg-gray-300 text-gray-700"
          >
            {cancelText || "Cancel"}
          </button>

          {/* Confirm Button */}
          <button
            onClick={onConfirm}
            className="button bg-gradient-to-r from-blue-500 to-indigo-600 text-white"
          >
            {confirmText || "Confirm"}
          </button>
        </div>
      </div>
    </div>
  );
};
