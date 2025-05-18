import { FaHome } from "react-icons/fa";
import { MdErrorOutline } from "react-icons/md";

export const NotFoundPage = () => {
  return (
    <div className="flex h-screen flex-col items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 px-4 text-center">
      {/* Updated SVG Illustration */}
      <img
        src="/images/404-car-journey.svg"
        alt="Page not found"
        className="mb-8 w-72 max-w-full drop-shadow-md"
      />

      {/* Icon + Title */}
      <div className="flex items-center gap-3 text-red-500">
        <MdErrorOutline className="text-5xl" />
        <h1 className="text-4xl font-extrabold sm:text-5xl">
          404 - Road Closed!
        </h1>
      </div>

      {/* Message */}
      <p className="mt-5 max-w-xl text-lg text-gray-700">
        Oops! You've driven off the map. This page doesn't exist or might have
        been towed away. 🛻
      </p>

      {/* Call to Action */}
      <a
        href="/"
        className="mt-8 inline-flex items-center gap-2 rounded-full bg-blue-600 px-6 py-3 text-lg text-white shadow-lg transition hover:bg-blue-700"
      >
        <FaHome className="text-xl" />
        Take Me Home
      </a>
    </div>
  );
};
