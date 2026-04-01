import type { Student } from "@/data/mockStudents";

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "";

type BackendStudent = Record<string, any>;
type StudentPageResponse = {
  students?: BackendStudent[];
  total?: number;
  page?: number;
  size?: number;
  hasMore?: boolean;
  placedCount?: number;
  unplacedCount?: number;
  internshipCount?: number;
  pendingCount?: number;
};

export type StudentFilters = {
  search?: string;
  status?: string;
  company?: string;
  branch?: string;
  batch?: string;
};

export type StudentPage = {
  students: Student[];
  total: number;
  page: number;
  size: number;
  hasMore: boolean;
  placedCount: number;
  unplacedCount: number;
  internshipCount: number;
  pendingCount: number;
};

export type StudentFilterOptions = {
  companies: string[];
  branches: string[];
  batches: string[];
};

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
  const status = normalizeStatus(b.status);
  const companyName: string | undefined = b.company ?? b.currentCompanyName ?? undefined;
  const companyType = status === "internship" ? "internship" : "full-time";

  return {
    id: b.id ?? b._id ?? b.regno ?? b.regNo ?? "",
    name,
    email: b.email ?? "",
    phone: b.phone ?? "",
    avatar: b.profilePic ?? b.avatar ?? "",
    branch: b.branch ?? "Unknown",
    batch: (function(){
      if (b.batch) return b.batch;
      const reg = b.regno ?? b.regNo ?? b.regno;
      if (typeof reg === 'string'){
        const m = reg.match(/^(\d{4})/);
        if (m) return m[1];
      }
      return "Unknown";
    })(),
    status,
    currentCompany: companyName
      ? {
          name: companyName,
          role: b.role ?? "",
          package: b.packageValue ?? b.package ?? "",
          joinDate: b.joinDate ?? "",
          type: b.type ?? companyType,
          duration: b.duration ?? undefined,
          endDate: b.endDate ?? undefined,
          logo: b.companyLogo ?? undefined,
        }
      : undefined,
    pastCompanies: b.pastCompanies ?? [],
    interviewExperiences: b.interviewExperiences ?? [],
    education: b.education ?? [],
    skills: b.skills ?? [],
    linkedin: b.linkedin ?? undefined,
    github: b.github ?? undefined,
    bio: b.bio ?? "",
  } as Student;
}

function appendIfPresent(params: URLSearchParams, key: string, value?: string) {
  if (value && value !== "all") {
    params.append(key, value);
  }
}

async function readJsonOrThrow<T>(url: string): Promise<T> {
  const res = await fetch(url, {
    credentials: "include",
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `Failed to fetch students: ${res.status}`);
  }
  return res.json();
}

export async function getStudents(status?: string): Promise<Student[]> {
  const params = new URLSearchParams();
  appendIfPresent(params, "status", status);
  
  const qs = params.toString() ? `?${params.toString()}` : "";
  const data = await readJsonOrThrow<BackendStudent[]>(`${API_BASE}/api/public/students${qs}`);
  
  if (Array.isArray(data)) {
    return data.map(mapBackendToStudent);
  }
  
  return [];
}

export async function getStudentsPage(
  filters: StudentFilters & { page?: number; size?: number } = {},
): Promise<StudentPage> {
  const params = new URLSearchParams();
  params.append("page", String(filters.page ?? 0));
  params.append("size", String(filters.size ?? 12));
  appendIfPresent(params, "search", filters.search);
  appendIfPresent(params, "status", filters.status);
  appendIfPresent(params, "company", filters.company);
  appendIfPresent(params, "branch", filters.branch);
  appendIfPresent(params, "batch", filters.batch);

  const data = await readJsonOrThrow<StudentPageResponse>(
    `${API_BASE}/api/public/students/page?${params.toString()}`,
  );

  return {
    students: Array.isArray(data.students) ? data.students.map(mapBackendToStudent) : [],
    total: data.total ?? 0,
    page: data.page ?? 0,
    size: data.size ?? filters.size ?? 12,
    hasMore: Boolean(data.hasMore),
    placedCount: data.placedCount ?? 0,
    unplacedCount: data.unplacedCount ?? 0,
    internshipCount: data.internshipCount ?? 0,
    pendingCount: data.pendingCount ?? 0,
  };
}

export async function getStudentFilterOptions(): Promise<StudentFilterOptions> {
  const data = await readJsonOrThrow<Partial<StudentFilterOptions>>(`${API_BASE}/api/public/students/filters`);
  return {
    companies: Array.isArray(data.companies) ? data.companies : [],
    branches: Array.isArray(data.branches) ? data.branches : [],
    batches: Array.isArray(data.batches) ? data.batches : [],
  };
}

export default {
  getStudents,
  getStudentsPage,
  getStudentFilterOptions,
};
