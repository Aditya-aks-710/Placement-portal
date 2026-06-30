import { Briefcase, GraduationCap } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import type { Company, Position } from "@/data/mockStudents";

/**
 * Resolve the role timeline for a company. Prefers the explicit `positions`
 * array; otherwise synthesizes a sensible timeline from the flat company fields
 * so older records still render as a progression.
 */
export function resolvePositions(company: Company): Position[] {
  if (company.positions && company.positions.length > 0) {
    return company.positions;
  }

  const isInternship = company.type === "internship";
  const converted = !!company.converted || !!company.fullTimePackage;

  if (converted) {
    return [
      {
        title: company.role,
        type: "internship",
        startDate: company.joinDate,
        endDate: company.conversionDate,
        stipend: company.stipend,
      },
      {
        title: company.role,
        type: "full-time",
        startDate: company.conversionDate,
        endDate: company.endDate,
        ctc: company.fullTimePackage,
      },
    ];
  }

  return [
    {
      title: company.role,
      type: isInternship ? "internship" : "full-time",
      startDate: company.joinDate,
      endDate: company.endDate,
      stipend: isInternship ? company.stipend : undefined,
      ctc: isInternship ? undefined : company.fullTimePackage || company.package,
    },
  ];
}

type PositionTimelineProps = {
  company: Company;
};

/** Renders a company's roles as a vertical, reverse-chronological timeline. */
const PositionTimeline = ({ company }: PositionTimelineProps) => {
  // Newest role first (LinkedIn style).
  const positions = [...resolvePositions(company)].reverse();

  return (
    <ol className="relative space-y-4">
      {positions.map((pos, i) => {
        const isCurrent = !pos.endDate;
        const isIntern = pos.type === "internship";
        return (
          <li key={pos.id ?? i} className="relative pl-9">
            {i < positions.length - 1 && (
              <span className="absolute left-[13px] top-1/2 h-[calc(100%+1rem)] w-px bg-border" />
            )}
            <span
              className={`absolute left-0 top-1/2 flex h-7 w-7 -translate-y-1/2 items-center justify-center rounded-full ring-2 ${
                isIntern
                  ? "bg-internship/15 text-internship ring-internship/20"
                  : "bg-accent/15 text-accent ring-accent/20"
              }`}
            >
              {isIntern ? <GraduationCap className="h-4 w-4" /> : <Briefcase className="h-4 w-4" />}
            </span>

            <div className="flex items-start justify-between gap-3">
              <div className="min-w-0">
                <div className="flex flex-wrap items-center gap-2">
                  <p className="font-semibold text-foreground">{pos.title || "—"}</p>
                  {isCurrent && (
                    <Badge className="bg-placed/15 px-2 py-0 text-[10px] font-semibold text-placed hover:bg-placed/15">
                      Current
                    </Badge>
                  )}
                </div>
                <p className="text-xs capitalize text-muted-foreground">
                  {pos.type.replace("-", " ")}
                  {(pos.startDate || pos.endDate) && " · "}
                  {pos.startDate || ""}
                  {pos.startDate || pos.endDate ? " – " : ""}
                  {pos.endDate || (pos.startDate ? "Present" : "")}
                </p>
              </div>
            </div>
          </li>
        );
      })}
    </ol>
  );
};

export default PositionTimeline;
