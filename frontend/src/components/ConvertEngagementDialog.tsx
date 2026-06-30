import { useState } from "react";
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
import { submitPlacementRequest } from "@/lib/api";
import type { Company } from "@/data/mockStudents";

type ConvertEngagementDialogProps = {
  company: Company;
  studentId?: string;
  trigger?: React.ReactNode;
};

const currentYear = new Date().getFullYear();

const ConvertEngagementDialog = ({ company, studentId, trigger }: ConvertEngagementDialogProps) => {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [conversionType, setConversionType] = useState("PPO");
  const [ctc, setCtc] = useState("");
  const [placementYear, setPlacementYear] = useState(String(currentYear));

  const reset = () => {
    setConversionType("PPO");
    setCtc("");
    setPlacementYear(String(currentYear));
  };

  const mutation = useMutation({
    mutationFn: () =>
      submitPlacementRequest({
        studentId,
        companyName: company.name,
        role: company.role,
        engagementType: conversionType === "PPO" ? "6M+PPO" : "6M+FTE",
        requestType: "CONVERSION",
        targetCompanyRecordId: company.id,
        ctc: Number(ctc),
        placementYear: Number(placementYear),
        placementNature: "Full-Time",
      }),
    onSuccess: () => {
      toast.success("Conversion submitted for admin approval");
      queryClient.invalidateQueries({ queryKey: ["students"] });
      reset();
      setOpen(false);
    },
    onError: (err) => toast.error(err instanceof Error ? err.message : "Failed to submit"),
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!ctc || Number.isNaN(Number(ctc)) || Number(ctc) <= 0) {
      toast.error("Enter a valid CTC (LPA)");
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
            Convert to PPO / FTE
          </Button>
        )}
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Convert Internship</DialogTitle>
          <DialogDescription>
            Convert your internship at {company.name} into a full-time offer (PPO or FTE).
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label>Conversion Type</Label>
            <Select value={conversionType} onValueChange={setConversionType}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="PPO">PPO (Pre-Placement Offer)</SelectItem>
                <SelectItem value="FTE">FTE (Full-Time Employment)</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="conv-ctc">CTC (LPA)</Label>
              <Input
                id="conv-ctc"
                type="number"
                min="0"
                step="0.1"
                value={ctc}
                onChange={(e) => setCtc(e.target.value)}
                placeholder="e.g. 18"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="conv-year">Year</Label>
              <Input
                id="conv-year"
                type="number"
                value={placementYear}
                onChange={(e) => setPlacementYear(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              Submit for approval
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default ConvertEngagementDialog;
