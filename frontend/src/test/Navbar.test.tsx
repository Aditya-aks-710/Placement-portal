import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { render, screen, cleanup } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Navbar from "@/components/Navbar";
import { setSession, clearSession } from "@/lib/auth";

function renderNavbar() {
  return render(
    <MemoryRouter>
      <Navbar />
    </MemoryRouter>,
  );
}

describe("Navbar", () => {
  beforeEach(() => clearSession());
  afterEach(() => {
    cleanup();
    clearSession();
  });

  it("shows Login and hides Admin when logged out", () => {
    renderNavbar();
    expect(screen.getByText("Login")).toBeInTheDocument();
    expect(screen.queryByText("Admin")).not.toBeInTheDocument();
    expect(screen.queryByText("Logout")).not.toBeInTheDocument();
  });

  it("shows Logout and Admin for an admin session", () => {
    setSession({ token: "t", username: "admin", role: "ADMIN" });
    renderNavbar();
    expect(screen.getByText("Logout")).toBeInTheDocument();
    expect(screen.getByText("Admin")).toBeInTheDocument();
    expect(screen.queryByText("Login")).not.toBeInTheDocument();
  });

  it("hides Admin for a student session", () => {
    setSession({ token: "t", username: "2024CS001", role: "STUDENT" });
    renderNavbar();
    expect(screen.getByText("Logout")).toBeInTheDocument();
    expect(screen.queryByText("Admin")).not.toBeInTheDocument();
  });
});
