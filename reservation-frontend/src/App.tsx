import "./index.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import { CarModelsPage } from "./pages/carModelsPage/CarModelsPage.tsx";
import { Route, Routes } from "react-router-dom";
import { HomePage } from "./pages/homePage/HomePage.tsx";
import { VehiclesPage } from "./pages/vehiclesPage/VehiclesPage.tsx";
import { Toaster } from "react-hot-toast";
import { CreateReservationPage } from "./pages/createReservationPage/CreateReservationPage.tsx";
import { ReservationManagementPage } from "./pages/reservationManagementPage/ReservationManagementPage.tsx";
import { NotFoundPage } from "./pages/notFoundPage/NotFoundPage.tsx";
import { UserDashboardPage } from "./pages/userDashboardPage/UserDashboardPage.tsx";
import { PaymentResultPage } from "./pages/paymentResultPage/PaymentResultPage.tsx";
import { PaymentCanceledPage } from "./pages/paymentResultPage/PaymentCanceledPage.tsx";
import { Layout } from "./components/Layout.tsx";

function App() {
  return (
    <>
      <Toaster position="top-right" reverseOrder={false} />
      <Routes>
        {/* Layout wrapper route */}
        <Route path="/" element={<Layout />}>
          <Route index element={<HomePage />} />
          <Route path="models" element={<CarModelsPage />} />
          <Route path="vehicles" element={<VehiclesPage />} />
          <Route
            path="create-reservation"
            element={<CreateReservationPage />}
          />
          <Route
            path="reservation-management"
            element={<ReservationManagementPage />}
          />
          <Route path="user-dashboard" element={<UserDashboardPage />} />
          <Route path="payment-result" element={<PaymentResultPage />} />
          <Route path="payment-cancelled" element={<PaymentCanceledPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </>
  );
}

export default App;
