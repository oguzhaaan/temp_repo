import { useNavigate, useLocation } from "react-router-dom";
import { FaHome, FaCarSide, FaBars, FaTimes } from "react-icons/fa";
import { MdDirectionsCarFilled } from "react-icons/md";
import { IoCalendarOutline } from "react-icons/io5";
import { HiOutlineClipboardList } from "react-icons/hi";
import { LuLayoutDashboard } from "react-icons/lu";
import { useState } from "react";

export function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);

  const navItems = [
    {
      name: "Home",
      path: "/",
      icon: <FaHome className="text-xl text-blue-600" />,
    },
    {
      name: "Models",
      path: "/models",
      icon: <MdDirectionsCarFilled className="text-xl text-emerald-600" />,
    },
    {
      name: "Vehicles",
      path: "/vehicles",
      icon: <FaCarSide className="text-xl text-indigo-600" />,
    },
    {
      name: "Reserve",
      path: "/create-reservation",
      icon: <IoCalendarOutline className="text-xl text-orange-500" />,
    },
    {
      name: "Dashboard",
      path: "/user-dashboard",
      icon: <LuLayoutDashboard className="text-xl text-purple-600" />,
    },
    {
      name: "Manage Reservations",
      path: "/reservation-management",
      icon: <HiOutlineClipboardList className="text-xl text-rose-600" />,
    },
  ];

  const handleNavigate = (path: string) => {
    navigate(path);
    setMenuOpen(false); // close menu on mobile after navigation
  };

  return (
    <nav className="sticky top-0 z-50 bg-white shadow-md">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
        <div
          onClick={() => handleNavigate("/")}
          className="cursor-pointer text-2xl font-bold text-blue-600"
        >
          CarRental
        </div>

        {/* Desktop menu */}
        <ul className="hidden space-x-8 md:flex">
          {navItems.map((item) => (
            <li
              key={item.path}
              onClick={() => handleNavigate(item.path)}
              className={`flex cursor-pointer items-center gap-2 font-medium text-gray-700 transition hover:text-blue-600 ${
                location.pathname === item.path
                  ? "font-semibold text-blue-600"
                  : ""
              }`}
            >
              {item.icon}
              {item.name}
            </li>
          ))}
        </ul>

        {/* Mobile toggle button */}
        <button
          className="text-xl text-gray-700 md:hidden"
          onClick={() => setMenuOpen(!menuOpen)}
        >
          {menuOpen ? <FaTimes /> : <FaBars />}
        </button>
      </div>

      {/* Mobile menu */}
      {menuOpen && (
        <ul className="space-y-3 bg-white px-6 pb-4 md:hidden">
          {navItems.map((item) => (
            <li
              key={item.path}
              onClick={() => handleNavigate(item.path)}
              className={`flex cursor-pointer items-center gap-2 rounded-md px-2 py-2 text-gray-700 transition hover:bg-gray-100 hover:text-blue-600 ${
                location.pathname === item.path
                  ? "font-semibold text-blue-600"
                  : ""
              }`}
            >
              {item.icon}
              {item.name}
            </li>
          ))}
        </ul>
      )}
    </nav>
  );
}
