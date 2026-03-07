import Navbar from "@/components/Navbar";
import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, BarChart, Bar, XAxis, YAxis, CartesianGrid } from "recharts";
import { Users, CheckCircle, Clock, Briefcase, TrendingUp } from "lucide-react";
import { getStudents } from "@/lib/api";

const COLORS = [
  "hsl(174, 62%, 42%)",
  "hsl(220, 70%, 18%)",
  "hsl(38, 92%, 50%)",
  "hsl(152, 60%, 42%)",
  "hsl(0, 84%, 60%)",
  "hsl(280, 60%, 50%)",
  "hsl(200, 70%, 50%)",
  "hsl(340, 70%, 50%)",
];

const Stats = () => {
  const { data: students = [], isLoading, isError, error } = useQuery({
    queryKey: ["students"],
    queryFn: () => getStudents(),
  });
  const errorMessage = error instanceof Error ? error.message : "Unable to load stats from backend.";

  const placedCount = students.filter((s) => s.status === "placed").length;
  const unplacedCount = students.filter((s) => s.status === "unplaced").length;
  const internCount = students.filter((s) => s.status === "internship").length;

  const statusData = [
    { name: "Placed", value: placedCount },
    { name: "Unplaced", value: unplacedCount },
    { name: "Internship", value: internCount },
  ];

  const companyData = useMemo(() => {
    const map: Record<string, number> = {};
    students.forEach((s) => {
      if (s.currentCompany) {
        map[s.currentCompany.name] = (map[s.currentCompany.name] || 0) + 1;
      }
    });
    return Object.entries(map)
      .map(([name, value]) => ({ name, value }))
      .sort((a, b) => b.value - a.value);
  }, [students]);

  const branchData = useMemo(() => {
    const map: Record<string, { placed: number; total: number }> = {};
    students.forEach((s) => {
      if (!map[s.branch]) map[s.branch] = { placed: 0, total: 0 };
      map[s.branch].total++;
      if (s.status === "placed") map[s.branch].placed++;
    });
    return Object.entries(map).map(([name, { placed, total }]) => ({
      name,
      placed,
      total,
      rate: Math.round((placed / total) * 100),
    }));
  }, [students]);

  const placementRate = students.length > 0 ? Math.round((placedCount / students.length) * 100) : 0;

  return (
    <div className="min-h-screen bg-background">
      <Navbar />

      <section className="gradient-hero py-10 px-4">
        <div className="container">
          <h1 className="font-display text-3xl font-bold text-primary-foreground">
            Placement Statistics
          </h1>
          <p className="mt-2 text-sm text-primary-foreground/70">
            Analytics and insights from our placement records.
          </p>
        </div>
      </section>

      <main className="container py-8 space-y-8">
        {isLoading && (
          <div className="rounded-lg border border-border bg-muted/40 p-3 text-sm text-muted-foreground">
            Loading stats from backend...
          </div>
        )}

        {isError && (
          <div className="rounded-lg border border-destructive/40 bg-destructive/10 p-3 text-sm text-destructive">
            {errorMessage}
          </div>
        )}

        {/* Stat Cards */}
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {[
            { icon: Users, label: "Total Students", value: students.length, sub: "All batches" },
            { icon: CheckCircle, label: "Placed", value: placedCount, sub: `${placementRate}% placement rate` },
            { icon: Clock, label: "Unplaced", value: unplacedCount, sub: "Seeking opportunities" },
            { icon: Briefcase, label: "Companies", value: companyData.length, sub: "Unique recruiters" },
          ].map(({ icon: Icon, label, value, sub }) => (
            <div key={label} className="elevated-card rounded-xl p-5">
              <div className="flex items-center justify-between">
                <Icon className="h-5 w-5 text-accent" />
                <TrendingUp className="h-4 w-4 text-placed" />
              </div>
              <div className="mt-3 text-3xl font-bold font-display text-foreground">{value}</div>
              <div className="text-sm font-medium text-foreground">{label}</div>
              <div className="text-xs text-muted-foreground">{sub}</div>
            </div>
          ))}
        </div>

        {/* Charts */}
        <div className="grid gap-6 lg:grid-cols-2">
          {/* Placement Status Pie */}
          <div className="elevated-card rounded-xl p-6">
            <h2 className="font-display text-lg font-semibold text-foreground">Placement Status</h2>
            <p className="text-xs text-muted-foreground mb-4">Distribution of placement outcomes</p>
            <ResponsiveContainer width="100%" height={280}>
              <PieChart>
                <Pie
                  data={statusData}
                  cx="50%"
                  cy="50%"
                  innerRadius={65}
                  outerRadius={100}
                  paddingAngle={4}
                  dataKey="value"
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                >
                  {statusData.map((_, index) => (
                    <Cell key={index} fill={COLORS[index]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Company Distribution Pie */}
          <div className="elevated-card rounded-xl p-6">
            <h2 className="font-display text-lg font-semibold text-foreground">Students per Company</h2>
            <p className="text-xs text-muted-foreground mb-4">Number of students placed at each company</p>
            <ResponsiveContainer width="100%" height={280}>
              <PieChart>
                <Pie
                  data={companyData}
                  cx="50%"
                  cy="50%"
                  outerRadius={100}
                  paddingAngle={3}
                  dataKey="value"
                  label={({ name, value }) => `${name} (${value})`}
                >
                  {companyData.map((_, index) => (
                    <Cell key={index} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Branch-wise Bar Chart */}
          <div className="lg:col-span-2 elevated-card rounded-xl p-6">
            <h2 className="font-display text-lg font-semibold text-foreground">Branch-wise Placement</h2>
            <p className="text-xs text-muted-foreground mb-4">Placed vs total students by branch</p>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={branchData}>
                <CartesianGrid strokeDasharray="3 3" stroke="hsl(220, 13%, 88%)" />
                <XAxis dataKey="name" fontSize={12} tick={{ fill: "hsl(220, 10%, 46%)" }} />
                <YAxis fontSize={12} tick={{ fill: "hsl(220, 10%, 46%)" }} />
                <Tooltip />
                <Bar dataKey="total" fill="hsl(220, 70%, 18%)" radius={[6, 6, 0, 0]} name="Total" />
                <Bar dataKey="placed" fill="hsl(174, 62%, 42%)" radius={[6, 6, 0, 0]} name="Placed" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Stats;
