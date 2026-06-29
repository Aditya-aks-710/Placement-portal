import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import {
  login,
  getStudentsPage,
  approvePlacementRequest,
} from "@/lib/api";
import { setSession, clearSession } from "@/lib/auth";

function jsonResponse(body: unknown, init: Partial<Response> & { ok?: boolean; status?: number } = {}) {
  return {
    ok: init.ok ?? true,
    status: init.status ?? 200,
    headers: { get: () => "application/json" },
    json: async () => body,
    text: async () => JSON.stringify(body),
  } as unknown as Response;
}

describe("api client", () => {
  beforeEach(() => {
    clearSession();
    vi.restoreAllMocks();
  });

  afterEach(() => {
    clearSession();
  });

  it("login posts credentials and returns the session payload", async () => {
    const fetchMock = vi
      .spyOn(globalThis, "fetch")
      .mockResolvedValue(jsonResponse({ token: "jwt-token", username: "admin", role: "ADMIN" }));

    const res = await login("admin", "secret");

    expect(res.token).toBe("jwt-token");
    expect(res.role).toBe("ADMIN");

    const [, init] = fetchMock.mock.calls[0];
    expect(init?.method).toBe("POST");
    expect(JSON.parse(init?.body as string)).toEqual({ username: "admin", password: "secret" });
  });

  it("throws the backend error message on failure", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(
      jsonResponse({ message: "Invalid username or password", status: 401 }, { ok: false, status: 401 }),
    );

    await expect(login("admin", "wrong")).rejects.toThrow("Invalid username or password");
  });

  it("attaches the bearer token when authenticated", async () => {
    setSession({ token: "my-token", username: "admin", role: "ADMIN" });
    const fetchMock = vi
      .spyOn(globalThis, "fetch")
      .mockResolvedValue(jsonResponse({ id: "1", status: "APPROVED" }));

    await approvePlacementRequest("1");

    const [, init] = fetchMock.mock.calls[0];
    const headers = init?.headers as Record<string, string>;
    expect(headers.Authorization).toBe("Bearer my-token");
  });

  it("maps backend students into the frontend shape", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(
      jsonResponse({
        students: [
          {
            id: "s1",
            name: "Arjun",
            status: "PLACED",
            company: "Google",
            role: "SDE",
            branch: "CSE",
            regNo: "2024CS001",
          },
        ],
        total: 1,
        page: 0,
        size: 12,
        hasMore: false,
        placedCount: 1,
        unplacedCount: 0,
        internshipCount: 0,
        pendingCount: 0,
      }),
    );

    const page = await getStudentsPage({ page: 0, size: 12 });

    expect(page.total).toBe(1);
    expect(page.students).toHaveLength(1);
    expect(page.students[0].name).toBe("Arjun");
    expect(page.students[0].status).toBe("placed");
    expect(page.students[0].currentCompany?.name).toBe("Google");
    expect(page.students[0].batch).toBe("2024");
  });
});
