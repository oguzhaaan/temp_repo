import { useNavigate } from "react-router-dom";
import {
  FaCar,
  FaListAlt,
  FaBookOpen,
  FaUserShield,
  FaPlusCircle,
} from "react-icons/fa";

export const HomePage = () => {
  const navigate = useNavigate();

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-br from-blue-600 via-indigo-600 to-purple-700 px-6 py-12 text-white">
      <div className="text-center">
        <h1 className="animate-fade-in mb-4 text-5xl font-extrabold tracking-tight drop-shadow-xl md:text-7xl">
          Car Rental App
        </h1>
        <p className="mb-10 text-lg text-white/80 md:text-2xl">
          Your journey starts here. Find and reserve your perfect ride.
        </p>
      </div>

      <div className="animate-slide-up w-full max-w-6xl rounded-3xl border border-white/20 bg-white/10 p-10 shadow-2xl backdrop-blur-xl md:p-16">
        <h2 className="mb-6 text-center text-4xl font-bold md:text-5xl">
          Welcome!
        </h2>
        <p className="mb-12 text-center text-lg text-white/80 md:text-xl">
          Explore a wide range of car models and reserve your dream ride today.
        </p>

        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
          {/* View Car Models */}
          <button
            onClick={() => navigate("/models")}
            className="homeButton bg-gradient-to-r from-blue-500 to-indigo-500 hover:from-indigo-600 hover:to-purple-600"
          >
            <span className="text-2xl">
              <FaCar />
            </span>
            View Car Models
          </button>

          {/* View Vehicles */}
          <button
            onClick={() => navigate("/vehicles")}
            className="homeButton bg-gradient-to-r from-green-500 to-teal-500 hover:from-teal-600 hover:to-emerald-600"
          >
            <span className="text-2xl">
              <FaListAlt />
            </span>
            View Vehicles
          </button>

          {/* Create Reservation */}
          <button
            onClick={() => navigate("/create-reservation")}
            className="homeButton bg-gradient-to-r from-cyan-500 to-sky-500 hover:from-sky-600 hover:to-cyan-600"
          >
            <span className="text-2xl">
              <FaPlusCircle />
            </span>
            Create Reservation
          </button>

          {/* Reservation Service API */}
          <button
            onClick={() =>
              window.open(
                "http://localhost:8081/openapi/swagger-ui/index.html",
                "_blank",
                "noopener,noreferrer",
              )
            }
            className="homeButton bg-gradient-to-r from-yellow-400 to-orange-400 hover:from-orange-500 hover:to-red-500"
          >
            <span className="text-2xl">
              <FaBookOpen />
            </span>
            Reservation Service API
          </button>

          {/* User Service API */}
          <button
            onClick={() =>
              window.open(
                "http://localhost:8082/openapi/swagger-ui/index.html",
                "_blank",
                "noopener,noreferrer",
              )
            }
            className="homeButton bg-gradient-to-r from-pink-500 to-rose-500 hover:from-rose-600 hover:to-pink-600"
          >
            <span className="text-2xl">
              <FaUserShield />
            </span>
            User Service API
          </button>

          {/* Manage Reservations */}
          <button
            onClick={() => navigate("/reservation-management")}
            className="homeButton bg-gradient-to-r from-purple-500 to-violet-500 hover:from-violet-600 hover:to-purple-600"
          >
            <span className="text-2xl">
              <FaListAlt />
            </span>
            Manage Reservations
          </button>

          {/* User Dashboard */}
          <button
            onClick={() => navigate("/user-dashboard")}
            className="homeButton bg-gradient-to-r from-purple-500 to-indigo-500 hover:from-indigo-600 hover:to-purple-600"
          >
            <span className="text-2xl">
              <FaUserShield />
            </span>
            User Dashboard
          </button>
        </div>
      </div>
    </div>
  );
};
