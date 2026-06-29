import { useSyncExternalStore } from "react";

const TOKEN_KEY = "pp_token";
const USERNAME_KEY = "pp_username";
const ROLE_KEY = "pp_role";

export type Session = {
  token: string;
  username: string;
  role: string;
};

type Listener = () => void;
const listeners = new Set<Listener>();

function emit() {
  listeners.forEach((listener) => listener());
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function getUsername(): string | null {
  return localStorage.getItem(USERNAME_KEY);
}

export function getRole(): string | null {
  return localStorage.getItem(ROLE_KEY);
}

export function isAuthenticated(): boolean {
  return Boolean(getToken());
}

export function isAdmin(): boolean {
  return (getRole() ?? "").toUpperCase() === "ADMIN";
}

export function setSession(session: Session) {
  localStorage.setItem(TOKEN_KEY, session.token);
  localStorage.setItem(USERNAME_KEY, session.username ?? "");
  localStorage.setItem(ROLE_KEY, session.role ?? "");
  emit();
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USERNAME_KEY);
  localStorage.removeItem(ROLE_KEY);
  emit();
}

function subscribe(listener: Listener) {
  listeners.add(listener);
  window.addEventListener("storage", listener);
  return () => {
    listeners.delete(listener);
    window.removeEventListener("storage", listener);
  };
}

function getSnapshot(): string {
  return `${getToken() ?? ""}|${getRole() ?? ""}|${getUsername() ?? ""}`;
}

/**
 * React hook that re-renders when the auth session changes.
 */
export function useAuth() {
  useSyncExternalStore(subscribe, getSnapshot, () => "");
  return {
    isAuthenticated: isAuthenticated(),
    isAdmin: isAdmin(),
    username: getUsername(),
    role: getRole(),
  };
}
