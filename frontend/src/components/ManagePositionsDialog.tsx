import { useEffect, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { Loader2, Plus, Trash2, Pencil } from "lucide-react";
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
import { updateStudentCompanyPositions, deleteStudentCompany, type PositionInput } from "@/lib/api";
import type { Company } from "@/data/mockStudents";
import { resolvePositions } from "@/components/PositionTimeline";

type ManagePositionsDialogProps = {
  company: Company;
  studentId: string;
  trigger?: React.ReactNode;
};

type EditablePosition = PositionInput & { _key: string };

let keyCounter = 0;
const nextKey = () => `pos-${Date.now()}-${keyCounter++}`;

function toEditable(company: Company): EditablePosition[] {
  return resolvePositions(company).map((p) => ({
    _key: nextKey(),
    id: p.id,
    title: p.title ?? "",
    type: p.type ?? "full-time",
    startDate: p.startDate ?? "",
    endDate: p.endDate ?? "",
    stipend: p.stipend ?? "",
    ctc: p.ctc ?? "",
  }));
}

/** Owner/admin editor to add, edit, or remove the roles held at a company. */
const ManagePositionsDialog = ({ company, studentId, trigger }: ManagePositionsDialogProps) => {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [rows, setRows] = useState<EditablePosition[]>(() => toEditable(company));

  // Re-seed the form whenever it is opened so it reflects the latest data.
  useEffect(() => {
    if (open) {
      setRows(toEditable(company));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  const updateRow = (key: string, patch: Partial<EditablePosition>) => {
    setRows((prev) => prev.map((r) => (r._key === key ? { ...r, ...patch } : r)));
  };

  const removeRow = (key: string) => {
    setRows((prev) => prev.filter((r) => r._key !== key));
  };

  const addRow = () => {
    setRows((prev) => [
      ...prev,
      { _key: nextKey(), title: "", type: "full-time", startDate: "", endDate: "", stipend: "", ctc: "" },
    ]);
  };

  const mutation = useMutation({
    mutationFn: () => {
      const payload: PositionInput[] = rows.map((r) => ({
        id: r.id,
        title: r.title.trim(),
        type: r.type,
        startDate: r.startDate?.trim() || undefined,
        endDate: r.endDate?.trim() || undefined,
        stipend: r.type === "internship" ? r.stipend?.trim() || undefined : undefined,
        ctc: r.type === "full-time" ? r.ctc?.trim() || undefined : undefined,
      }));
      if (!company.id) {
        throw new Error("Missing company record id");
      }
      return updateStudentCompanyPositions(studentId, company.id, payload);
    },
    onSuccess: () => {
      toast.success("Roles updated");
      queryClient.invalidateQueries({ queryKey: ["students"] });
      setOpen(false);
    },
    onError: (err) => toast.error(err instanceof Error ? err.message : "Failed to update roles"),
  });

  const deleteMutation = useMutation({
    mutationFn: () => {
      if (!company.id) {
        throw new Error("Missing company record id");
      }
      return deleteStudentCompany(studentId, company.id);
    },
    onSuccess: () => {
      toast.success(`Removed ${company.name}`);
      queryClient.invalidateQueries({ queryKey: ["students"] });
      setOpen(false);
    },
    onError: (err) => toast.error(err instanceof Error ? err.message : "Failed to remove company"),
  });

  const handleDelete = () => {
    if (
      window.confirm(
        `Remove ${company.name} and all of its roles from this profile? This cannot be undone.`,
      )
    ) {
      deleteMutation.mutate();
    }
  };

  const handleSave = () => {
    const cleaned = rows.filter((r) => r.title.trim().length > 0);
    if (cleaned.length === 0) {
      // No roles left means the whole company entry should be removed.
      handleDelete();
      return;
    }
    setRows(cleaned);
    mutation.mutate();
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {trigger ?? (
          <Button size="sm" variant="outline">
            <Pencil className="mr-2 h-4 w-4" />
            Manage roles
          </Button>
        )}
      </DialogTrigger>
      <DialogContent className="max-h-[85vh] overflow-y-auto sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Roles at {company.name}</DialogTitle>
          <DialogDescription>
            Add each role you held here as a separate entry — e.g. an "IT Trainee" internship
            that later converted into a "Developer" full-time role.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {rows.map((row, idx) => (
            <div key={row._key} className="rounded-lg border border-border/60 p-3">
              <div className="mb-2 flex items-center justify-between">
                <span className="text-xs font-semibold text-muted-foreground">Role {idx + 1}</span>
                <Button
                  type="button"
                  variant="ghost"
                  size="icon"
                  className="h-7 w-7 text-destructive hover:bg-destructive/10"
                  onClick={() => removeRow(row._key)}
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>

              <div className="space-y-3">
                <div className="space-y-1.5">
                  <Label className="text-xs">Title</Label>
                  <Input
                    value={row.title}
                    onChange={(e) => updateRow(row._key, { title: e.target.value })}
                    placeholder="e.g. IT Trainee"
                  />
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div className="space-y-1.5">
                    <Label className="text-xs">Type</Label>
                    <Select
                      value={row.type}
                      onValueChange={(value) =>
                        updateRow(row._key, { type: value as PositionInput["type"] })
                      }
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="internship">Internship</SelectItem>
                        <SelectItem value="full-time">Full-time</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-1.5">
                    <Label className="text-xs">
                      {row.type === "internship" ? "Stipend" : "CTC"}
                    </Label>
                    <Input
                      value={row.type === "internship" ? row.stipend : row.ctc}
                      onChange={(e) =>
                        updateRow(
                          row._key,
                          row.type === "internship"
                            ? { stipend: e.target.value }
                            : { ctc: e.target.value },
                        )
                      }
                      placeholder={row.type === "internship" ? "e.g. 45K/month" : "e.g. 17 LPA"}
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div className="space-y-1.5">
                    <Label className="text-xs">Start</Label>
                    <Input
                      value={row.startDate}
                      onChange={(e) => updateRow(row._key, { startDate: e.target.value })}
                      placeholder="e.g. Jan 2026"
                    />
                  </div>
                  <div className="space-y-1.5">
                    <Label className="text-xs">End</Label>
                    <Input
                      value={row.endDate}
                      onChange={(e) => updateRow(row._key, { endDate: e.target.value })}
                      placeholder="Leave blank if current"
                    />
                  </div>
                </div>
              </div>
            </div>
          ))}

          <Button type="button" variant="outline" size="sm" className="w-full" onClick={addRow}>
            <Plus className="mr-2 h-4 w-4" />
            Add role
          </Button>
        </div>

        <DialogFooter className="flex-col gap-2 sm:flex-row sm:justify-between">
          <Button
            type="button"
            variant="outline"
            className="text-destructive hover:bg-destructive/10 hover:text-destructive"
            onClick={handleDelete}
            disabled={deleteMutation.isPending || mutation.isPending}
          >
            {deleteMutation.isPending ? (
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            ) : (
              <Trash2 className="mr-2 h-4 w-4" />
            )}
            Delete company
          </Button>
          <Button onClick={handleSave} disabled={mutation.isPending || deleteMutation.isPending}>
            {mutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            Save roles
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default ManagePositionsDialog;
