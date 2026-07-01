import { useEffect, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { Loader2, TrendingUp } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { updateStudentCompanyPositions, type PositionInput } from "@/lib/api";
import type { Company } from "@/data/mockStudents";
import { resolvePositions } from "@/components/PositionTimeline";

type ProgressEngagementDialogProps = {
  company: Company;
  studentId?: string;
  trigger?: React.ReactNode;
};

/** The next stage a student can add on top of an ongoing internship. */
const PHASE_OPTIONS = [
  { value: "2M", label: "2-Month Internship", type: "internship" as const },
  { value: "6M", label: "6-Month Internship", type: "internship" as const },
  { value: "PPO", label: "Full-time — PPO", type: "full-time" as const },
  { value: "FTE", label: "Full-time — FTE", type: "full-time" as const },
];

/**
 * Appends the next phase of an engagement (e.g. a 6-month internship, then an
 * FTE) to a company's role timeline as a distinct entry, so a journey like
 * 2M → 6M → FTE is captured as three separate phases on the same company.
 */
const ProgressEngagementDialog = ({ company, studentId, trigger }: ProgressEngagementDialogProps) => {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [phase, setPhase] = useState("6M");
  const [title, setTitle] = useState(company.role ?? "");
  const [start, setStart] = useState("");
  const [end, setEnd] = useState("");
  const [pay, setPay] = useState("");

  const selectedPhase = PHASE_OPTIONS.find((p) => p.value === phase) ?? PHASE_OPTIONS[1];
  const isInternshipPhase = selectedPhase.type === "internship";

  const reset = () => {
    setPhase("6M");
    setTitle(company.role ?? "");
    setStart("");
    setEnd("");
    setPay("");
  };

  useEffect(() => {
    if (open) {
      reset();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  const mutation = useMutation({
    mutationFn: () => {
      if (!company.id) {
        throw new Error("Missing company record id");
      }

      // Preserve the phases already recorded, closing the last open-ended one
      // at the point the new phase begins so the timeline stays continuous.
      const existing: PositionInput[] = resolvePositions(company).map((p) => ({
        id: p.id,
        title: p.title ?? "",
        type: p.type ?? "full-time",
        startDate: p.startDate?.trim() || undefined,
        endDate: p.endDate?.trim() || undefined,
        stipend: p.stipend?.trim() || undefined,
        ctc: p.ctc?.trim() || undefined,
      }));

      const startDate = start.trim() || undefined;
      if (existing.length > 0) {
        const last = existing[existing.length - 1];
        if (!last.endDate) {
          last.endDate = startDate;
        }
      }

      const nextPhase: PositionInput = {
        title: title.trim() || company.role || selectedPhase.label,
        type: selectedPhase.type,
        startDate,
        endDate: end.trim() || undefined,
        stipend: isInternshipPhase ? pay.trim() || undefined : undefined,
        ctc: isInternshipPhase ? undefined : pay.trim() || undefined,
      };

      return updateStudentCompanyPositions(studentId!, company.id, [...existing, nextPhase]);
    },
    onSuccess: () => {
      toast.success("Phase added to timeline");
      queryClient.invalidateQueries({ queryKey: ["students"] });
      setOpen(false);
    },
    onError: (err) => toast.error(err instanceof Error ? err.message : "Failed to add phase"),
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!studentId) {
      toast.error("Missing student id");
      return;
    }
    if (!start.trim()) {
      toast.error("Enter when this phase started");
      return;
    }
    if (!pay.trim()) {
      toast.error(isInternshipPhase ? "Enter the stipend" : "Enter the CTC");
      return;
    }
    mutation.mutate();
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {trigger ?? (
          <Button size="sm" variant="outline">
            <TrendingUp className="mr-2 h-4 w-4" />
            Add next phase
          </Button>
        )}
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Add next phase at {company.name}</DialogTitle>
          <DialogDescription>
            Record the next stage of this engagement — e.g. extend into a 6-month internship, then
            convert to a full-time (PPO/FTE) offer. Each stage is added as its own timeline entry.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label>Next phase</Label>
            <Select value={phase} onValueChange={setPhase}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {PHASE_OPTIONS.map((opt) => (
                  <SelectItem key={opt.value} value={opt.value}>
                    {opt.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="prog-title">Role title</Label>
            <Input
              id="prog-title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder={isInternshipPhase ? "e.g. Software Intern" : "e.g. SWE"}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="prog-start">Start</Label>
              <Input
                id="prog-start"
                value={start}
                onChange={(e) => setStart(e.target.value)}
                placeholder="e.g. Jan 2027"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="prog-end">End</Label>
              <Input
                id="prog-end"
                value={end}
                onChange={(e) => setEnd(e.target.value)}
                placeholder="Leave blank if current"
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="prog-pay">{isInternshipPhase ? "Stipend" : "CTC (LPA)"}</Label>
            <Input
              id="prog-pay"
              value={pay}
              onChange={(e) => setPay(e.target.value)}
              placeholder={isInternshipPhase ? "e.g. 45K/month" : "e.g. 18 LPA"}
            />
          </div>

          <DialogFooter>
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              Add phase
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default ProgressEngagementDialog;
