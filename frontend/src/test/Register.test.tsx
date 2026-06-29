import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { render, screen, cleanup, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Register from "@/pages/Register";

const mockNavigate = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<typeof import("react-router-dom")>("react-router-dom");
  return { ...actual, useNavigate: () => mockNavigate };
});

const initiateMock = vi.fn();
const completeMock = vi.fn();

vi.mock("@/lib/api", () => ({
  initiateRegistration: (...args: unknown[]) => initiateMock(...args),
  completeRegistration: (...args: unknown[]) => completeMock(...args),
}));

vi.mock("sonner", () => ({
  toast: { success: vi.fn(), error: vi.fn() },
}));

function renderAt(path = "/register") {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Register />
    </MemoryRouter>,
  );
}

describe("Register", () => {
  beforeEach(() => {
    initiateMock.mockReset();
    completeMock.mockReset();
    mockNavigate.mockReset();
  });
  afterEach(() => cleanup());

  it("starts on the activation-number step", () => {
    renderAt();
    expect(screen.getByText("Activate Your Account")).toBeInTheDocument();
    expect(screen.getByLabelText("Registration Number")).toBeInTheDocument();
  });

  it("advances to the password step after initiating", async () => {
    initiateMock.mockResolvedValue({ message: "Activation link generated" });
    renderAt();

    fireEvent.change(screen.getByLabelText("Registration Number"), {
      target: { value: "2024CS001" },
    });
    fireEvent.click(screen.getByText("Continue"));

    await waitFor(() => {
      expect(initiateMock).toHaveBeenCalledWith("2024CS001");
      expect(screen.getByText("Set Your Password")).toBeInTheDocument();
    });
  });

  it("auto-fills the token when the backend returns it in dev mode", async () => {
    initiateMock.mockResolvedValue({ message: "Activation link generated", token: "dev-token-9" });
    renderAt();

    fireEvent.change(screen.getByLabelText("Registration Number"), {
      target: { value: "2024CS001" },
    });
    fireEvent.click(screen.getByText("Continue"));

    await waitFor(() => {
      expect(screen.getByText("Set Your Password")).toBeInTheDocument();
      expect(screen.getByLabelText("Activation Token")).toHaveValue("dev-token-9");
    });
  });

  it("jumps straight to the password step when a token is in the URL", () => {
    renderAt("/register?token=abc-123");
    expect(screen.getByText("Set Your Password")).toBeInTheDocument();
    expect(screen.getByLabelText("Activation Token")).toHaveValue("abc-123");
  });

  it("rejects mismatched passwords without calling the API", async () => {
    renderAt("/register?token=abc-123");

    fireEvent.change(screen.getByLabelText("New Password"), {
      target: { value: "secret1" },
    });
    fireEvent.change(screen.getByLabelText("Confirm Password"), {
      target: { value: "secret2" },
    });
    fireEvent.click(screen.getByText("Complete Registration"));

    await waitFor(() => {
      expect(screen.getByText("Passwords do not match.")).toBeInTheDocument();
    });
    expect(completeMock).not.toHaveBeenCalled();
  });

  it("completes registration and navigates to login", async () => {
    completeMock.mockResolvedValue("Registration completed Successfully");
    renderAt("/register?token=abc-123");

    fireEvent.change(screen.getByLabelText("New Password"), {
      target: { value: "secret1" },
    });
    fireEvent.change(screen.getByLabelText("Confirm Password"), {
      target: { value: "secret1" },
    });
    fireEvent.click(screen.getByText("Complete Registration"));

    await waitFor(() => {
      expect(completeMock).toHaveBeenCalledWith("abc-123", "secret1");
      expect(mockNavigate).toHaveBeenCalledWith("/login");
    });
  });
});
