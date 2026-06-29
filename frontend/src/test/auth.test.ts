import { describe, it, expect, beforeEach } from "vitest";
import {
  setSession,
  clearSession,
  getToken,
  getRole,
  getUsername,
  isAuthenticated,
  isAdmin,
} from "@/lib/auth";

describe("auth session", () => {
  beforeEach(() => {
    clearSession();
  });

  it("starts unauthenticated", () => {
    expect(isAuthenticated()).toBe(false);
    expect(getToken()).toBeNull();
    expect(isAdmin()).toBe(false);
  });

  it("stores and reads a session", () => {
    setSession({ token: "abc.def.ghi", username: "2024CS001", role: "STUDENT" });

    expect(getToken()).toBe("abc.def.ghi");
    expect(getUsername()).toBe("2024CS001");
    expect(getRole()).toBe("STUDENT");
    expect(isAuthenticated()).toBe(true);
    expect(isAdmin()).toBe(false);
  });

  it("detects admin role case-insensitively", () => {
    setSession({ token: "t", username: "admin", role: "admin" });
    expect(isAdmin()).toBe(true);
  });

  it("clears a session", () => {
    setSession({ token: "t", username: "u", role: "ADMIN" });
    clearSession();

    expect(getToken()).toBeNull();
    expect(isAuthenticated()).toBe(false);
    expect(isAdmin()).toBe(false);
  });
});
