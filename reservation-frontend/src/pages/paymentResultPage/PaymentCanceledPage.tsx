import { FaBan, FaArrowLeft } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

export const PaymentCanceledPage = () => {
  const navigate = useNavigate();

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-br from-yellow-100 to-orange-200 px-4 py-12">
      <div className="w-full max-w-lg rounded-2xl bg-white p-10 text-center shadow-2xl ring-1 ring-gray-200">
        <div className="animate-fadeIn mb-8 text-yellow-500">
          <FaBan className="mx-auto mb-4 text-5xl" />
          <h2 className="text-3xl font-bold">Payment Canceled</h2>
          <p className="mt-2 text-lg text-gray-700">
            Your payment was canceled.
          </p>
        </div>

        <button
          onClick={() => navigate("/user-dashboard")}
          className="inline-flex items-center gap-2 rounded-full bg-yellow-500 px-6 py-3 text-white shadow-lg transition duration-200 hover:scale-105 hover:bg-yellow-600"
        >
          <FaArrowLeft />
          Return to Dashboard
        </button>
      </div>
    </div>
  );
};
