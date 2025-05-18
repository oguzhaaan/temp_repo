import { Outlet } from "react-router-dom";
import { Navbar } from "./Navbar.tsx";

export function Layout() {
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1 overflow-auto">
        <Outlet />
      </main>
    </div>
  );
}
