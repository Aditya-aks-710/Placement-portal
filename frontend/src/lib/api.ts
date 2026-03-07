import type { Student } from "@/data/mockStudents";

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "";

type BackendStudent = Record<string, any>;

function normalizeStatus(s: string | null | undefined): "placed" | "unplaced" | "internship" | "pending" {
  if (!s) return "unplaced";
  const v = s.toLowerCase().trim();
  // explicit unplaced check before generic "place" check
  if (v === "pending" || v === "p") return "pending";
  if (v === "unplaced" || v === "not placed" || v === "none") return "unplaced";
  if (v === "placed" || v === "place") return "placed";
  if (v.includes("intern")) return "internship";
  if (v.includes("place")) return "placed"; // fallback catch-all
  return "unplaced";
}

function mapBackendToStudent(b: BackendStudent): Student {
  const name: string = b.name ?? b.fullName ?? "Unknown";
  const companyName: string | undefined = b.company ?? b.currentCompanyName ?? undefined;

  return {
    id: b.id ?? b._id ?? "",
    name,
    email: b.email ?? "",
    phone: b.phone ?? "",
    avatar: b.profilePic ?? b.avatar ?? "",
    branch: b.branch ?? "",
    batch: (function(){
      if (b.batch) return b.batch;
      const reg = b.regno ?? b.regNo ?? b.regno;
      if (typeof reg === 'string'){
        const m = reg.match(/^(\d{4})/);
        if (m) return m[1];
      }
      return "Unknown";
    })(),
    status: normalizeStatus(b.status),
    currentCompany: companyName ? { name: companyName, role: b.role ?? "", package: b.package ?? "", joinDate: b.joinDate ?? "" } : undefined,
    pastCompanies: b.pastCompanies ?? [],
    interviewExperiences: b.interviewExperiences ?? [],
    education: b.education ?? [],
    skills: b.skills ?? [],
    linkedin: b.linkedin ?? undefined,
    github: b.github ?? undefined,
    bio: b.bio ?? "",
  } as Student;
}

export async function getStudents(status?: string): Promise<Student[]> {
  const params = new URLSearchParams();
  if (status) params.append("status", status);
  
  const qs = params.toString() ? `?${params.toString()}` : "";
  const res = await fetch(`${API_BASE}/api/public/students${qs}`, {
    credentials: "include",
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `Failed to fetch students: ${res.status}`);
  }

  const data = await res.json();
  
  if (Array.isArray(data)) {
    return data.map(mapBackendToStudent);
  }
  
  return [];
}

export default {
  getStudents,
};
