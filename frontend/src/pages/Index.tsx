import { useState, useMemo } from "react";
import { mockStudents } from "@/data/mockStudents";
import StudentCard from "@/components/StudentCard";
import FilterBar from "@/components/FilterBar";
import Navbar from "@/components/Navbar";
import { Users, CheckCircle, Clock, Briefcase } from "lucide-react";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const Index = () => {
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [companyFilter, setCompanyFilter] = useState("all");
  const [batchFilter, setBatchFilter] = useState("all");
  const [heroBranch, setHeroBranch] = useState("all");

  const companies = useMemo(() => {
    const set = new Set<string>();
    mockStudents.forEach((s) => {
      if (s.currentCompany) set.add(s.currentCompany.name);
    });
    return Array.from(set).sort();
  }, []);

  const branches = useMemo(() => Array.from(new Set(mockStudents.map((s) => s.branch))).sort(), []);
  const batches = useMemo(() => Array.from(new Set(mockStudents.map((s) => s.batch))).sort(), []);

  const activeFilters = [statusFilter, companyFilter, batchFilter].filter(
    (f) => f !== "all"
  ).length;

  const filtered = useMemo(() => {
    return mockStudents.filter((s) => {
      const matchSearch =
        !search ||
        s.name.toLowerCase().includes(search.toLowerCase()) ||
        s.currentCompany?.name.toLowerCase().includes(search.toLowerCase()) ||
        s.skills.some((sk) => sk.toLowerCase().includes(search.toLowerCase()));

      const matchStatus = statusFilter === "all" || s.status === statusFilter;
      const matchCompany = companyFilter === "all" || s.currentCompany?.name === companyFilter;
      const matchBranch = heroBranch === "all" || s.branch === heroBranch;
      const matchBatch = batchFilter === "all" || s.batch === batchFilter;

      return matchSearch && matchStatus && matchCompany && matchBranch && matchBatch;
    });
  }, [search, statusFilter, companyFilter, heroBranch, batchFilter]);

  const clearFilters = () => {
    setSearch("");
    setStatusFilter("all");
    setCompanyFilter("all");
    setBatchFilter("all");
  };


  const branchStudents = useMemo(() => {
    if (heroBranch === "all") return mockStudents;
    return mockStudents.filter((s) => s.branch === heroBranch);
  }, [heroBranch]);

  const placedCount = branchStudents.filter((s) => s.status === "placed").length;
  const unplacedCount = branchStudents.filter((s) => s.status === "unplaced").length;
  const internCount = branchStudents.filter((s) => s.status === "internship").length;

  return (
    <div className="min-h-screen bg-background">
      <Navbar />

      {/* Hero */}
      <section className="gradient-hero py-12 px-4">
        <div className="container">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h1 className="font-display text-3xl font-bold text-primary-foreground md:text-4xl">
                Placement Portal
              </h1>
              <p className="mt-2 text-sm text-primary-foreground/70 max-w-lg">
                Explore placement records, interview experiences, and career insights from our students.
              </p>
            </div>
            <Select value={heroBranch} onValueChange={setHeroBranch}>
              <SelectTrigger className="h-10 w-[200px] text-sm bg-white/10 border-white/20 text-primary-foreground">
                <SelectValue placeholder="Select Branch" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Branches</SelectItem>
                {branches.map((b) => (
                  <SelectItem key={b} value={b}>{b}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="mt-6 grid grid-cols-2 gap-3 sm:grid-cols-4">
            {[
              { icon: Users, label: "Total Students", value: branchStudents.length, color: "bg-white/10" },
              { icon: CheckCircle, label: "Placed", value: placedCount, color: "bg-placed/20" },
              { icon: Clock, label: "Unplaced", value: unplacedCount, color: "bg-white/10" },
              { icon: Briefcase, label: "Internships", value: internCount, color: "bg-internship/20" },
            ].map(({ icon: Icon, label, value, color }) => (
              <div key={label} className={`rounded-xl ${color} backdrop-blur-sm p-4 border border-white/10`}>
                <Icon className="h-5 w-5 text-primary-foreground/70" />
                <div className="mt-2 text-2xl font-bold text-primary-foreground">{value}</div>
                <div className="text-xs text-primary-foreground/60">{label}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Main Content */}
      <main className="container py-8">
        <FilterBar
          search={search}
          onSearchChange={setSearch}
          statusFilter={statusFilter}
          onStatusChange={setStatusFilter}
          companyFilter={companyFilter}
          onCompanyChange={setCompanyFilter}
          batchFilter={batchFilter}
          onBatchChange={setBatchFilter}
          companies={companies}
          batches={batches}
          activeFilters={activeFilters}
          onClearFilters={clearFilters}
        />

        <div className="mt-2 mb-4 text-sm text-muted-foreground">
          Showing {filtered.length} of {mockStudents.length} students
        </div>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {filtered.map((student) => (
            <StudentCard key={student.id} student={student} />
          ))}
        </div>

        {filtered.length === 0 && (
          <div className="flex flex-col items-center justify-center py-20 text-center">
            <Users className="h-12 w-12 text-muted-foreground/30" />
            <h3 className="mt-4 font-display text-lg font-semibold text-foreground">No students found</h3>
            <p className="mt-1 text-sm text-muted-foreground">Try adjusting your filters</p>
          </div>
        )}
      </main>
    </div>
  );
};

export default Index;
