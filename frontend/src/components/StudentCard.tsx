import { useNavigate } from "react-router-dom";
import { Briefcase, GraduationCap } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import type { Student } from "@/data/mockStudents";

const statusConfig = {
  placed: { label: "Placed", className: "bg-placed text-placed-foreground" },
  unplaced: { label: "Unplaced", className: "bg-unplaced text-unplaced-foreground" },
  internship: { label: "Intern", className: "bg-internship text-internship-foreground" },
  pending: { label: "Pending", className: "bg-muted text-foreground" },
};

const StudentCard = ({ student }: { student: Student }) => {
  const navigate = useNavigate();
  const config = statusConfig[student.status] ?? statusConfig.unplaced;
  const isClickable = student.status !== "unplaced";

  const initials = student.name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase();

  const colors = [
    "from-blue-500 to-indigo-600",
    "from-emerald-500 to-teal-600",
    "from-orange-500 to-red-500",
    "from-purple-500 to-pink-500",
    "from-cyan-500 to-blue-500",
  ];
  const colorIndex = student.name.charCodeAt(0) % colors.length;

  return (
    <div
      onClick={() => isClickable && navigate(`/student/${student.id}`)}
      className={`group elevated-card rounded-xl p-5 transition-all duration-300 ${
        isClickable
          ? "cursor-pointer hover:-translate-y-1 hover:shadow-lg hover:border-accent/30"
          : "opacity-80"
      } animate-fade-in`}
    >
      <div className="flex items-start gap-4">
        <div
          className={`flex h-14 w-14 shrink-0 items-center justify-center rounded-xl bg-gradient-to-br ${colors[colorIndex]} text-lg font-bold text-white shadow-md`}
        >
          {initials}
        </div>

        <div className="min-w-0 flex-1">
          <div className="flex items-start justify-between gap-2">
            <h3 className="font-display text-base font-semibold text-foreground truncate group-hover:text-accent transition-colors">
              {student.name}
            </h3>
            <Badge className={`shrink-0 text-[10px] font-semibold px-2 py-0.5 ${config.className}`}>
              {config.label}
            </Badge>
          </div>

          <div className="mt-1.5 flex items-center gap-1.5 text-xs text-muted-foreground">
            <GraduationCap className="h-3.5 w-3.5" />
            <span>{student.branch} - {student.batch}</span>
          </div>

          {student.currentCompany && (
            <div className="mt-2 flex items-center gap-1.5 text-xs">
              <Briefcase className="h-3.5 w-3.5 text-accent" />
              <span className="font-medium text-foreground">{student.currentCompany.role}</span>
              <span className="text-muted-foreground">at</span>
              <span className="font-semibold text-accent">{student.currentCompany.name}</span>
            </div>
          )}

          {student.currentCompany?.package && (
            <div className="mt-1 text-xs text-muted-foreground">Package: {student.currentCompany.package}</div>
          )}

          {student.status === "unplaced" && (
            <p className="mt-2 text-xs text-muted-foreground italic">Currently seeking opportunities</p>
          )}

          <div className="mt-3 flex flex-wrap gap-1">
            {student.skills.slice(0, 3).map((skill) => (
              <span
                key={skill}
                className="rounded-md bg-muted px-2 py-0.5 text-[10px] font-medium text-muted-foreground"
              >
                {skill}
              </span>
            ))}
            {student.skills.length > 3 && (
              <span className="rounded-md bg-muted px-2 py-0.5 text-[10px] text-muted-foreground">
                +{student.skills.length - 3}
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentCard;
