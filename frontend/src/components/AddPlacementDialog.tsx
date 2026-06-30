import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
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
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import { Check, ChevronsUpDown, Loader2, Plus } from "lucide-react";
import { cn } from "@/lib/utils";
import { getCompanies, submitPlacementRequest } from "@/lib/api";
import { toast } from "sonner";

type AddPlacementDialogProps = {
  trigger: React.ReactNode;
  studentId?: string;
};

const currentYear = new Date().getFullYear();

const ENGAGEMENT_OPTIONS = [
  { value: "2M", label: "2 Month Internship", needsStipend: true, needsCtc: false },
  { value: "6M", label: "6 Month Internship", needsStipend: true, needsCtc: false },
  { value: "6M+PPO", label: "6 Month + PPO", needsStipend: true, needsCtc: true },
  { value: "6M+FTE", label: "6 Month + FTE", needsStipend: true, needsCtc: true },
  { value: "FTE", label: "Full-Time (FTE)", needsStipend: false, needsCtc: true },
] as const;

const MONTHS = [
  "Jan", "Feb", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
];

const AddPlacementDialog = ({ trigger, studentId }: AddPlacementDialogProps) => {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [companyName, setCompanyName] = useState("");
  const [companyPickerOpen, setCompanyPickerOpen] = useState(false);
  const [companySearch, setCompanySearch] = useState("");
  const [role, setRole] = useState("");
  const [engagementType, setEngagementType] = useState("FTE");
  const [stipend, setStipend] = useState("");
  const [ctc, setCtc] = useState("");
  const [placementYear, setPlacementYear] = useState(String(currentYear));
  const [startMonth, setStartMonth] = useState("");
  const [campusMode, setCampusMode] = useState("On-Campus");

  const engagement = ENGAGEMENT_OPTIONS.find((e) => e.value === engagementType) ?? ENGAGEMENT_OPTIONS[4];
  const placementNature = engagementType === "FTE" ? "Full-Time" : "Internship";

  const { data: companies = [], isLoading: companiesLoading } = useQuery({
    queryKey: ["companies"],
    queryFn: getCompanies,
    enabled: open,
  });

  const trimmedSearch = companySearch.trim();
  const filteredCompanies = companies.filter((c) =>
    c.name.toLowerCase().includes(trimmedSearch.toLowerCase())
  );
  const exactMatch = companies.some(
    (c) => c.name.toLowerCase() === trimmedSearch.toLowerCase()
  );

  const reset = () => {
    setCompanyName("");
    setCompanySearch("");
    setRole("");
    setEngagementType("FTE");
    setStipend("");
    setCtc("");
    setPlacementYear(String(currentYear));
    setStartMonth("");
    setCampusMode("On-Campus");
  };

  const mutation = useMutation({
    mutationFn: () =>
      submitPlacementRequest({
        studentId,
        companyName: companyName.trim(),
        role: role.trim(),
        engagementType,
        requestType: "PLACEMENT",
        stipend: engagement.needsStipend ? Number(stipend) : undefined,
        ctc: engagement.needsCtc ? Number(ctc) : undefined,
        placementYear: Number(placementYear),
        startMonth: startMonth || undefined,
        campusMode,
        placementNature,
      }),
    onSuccess: () => {
      toast.success("Placement submitted for admin approval");
      queryClient.invalidateQueries({ queryKey: ["students"] });
      queryClient.invalidateQueries({ queryKey: ["companies"] });
      reset();
      setOpen(false);
    },
    onError: (err) => toast.error(err instanceof Error ? err.message : "Failed to submit"),
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!companyName.trim()) {
      toast.error("Please select or add a company");
      return;
    }
    if (!role.trim()) {
      toast.error("Role is required");
      return;
    }
    if (engagement.needsStipend && (!stipend || Number.isNaN(Number(stipend)) || Number(stipend) <= 0)) {
      toast.error("Enter a valid monthly stipend (in ₹K)");
      return;
    }
    if (engagement.needsCtc && (!ctc || Number.isNaN(Number(ctc)) || Number(ctc) <= 0)) {
      toast.error("Enter a valid CTC (LPA)");
      return;
    }
    mutation.mutate();
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>{trigger}</DialogTrigger>
      <DialogContent className="max-h-[85vh] overflow-y-auto sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Submit a placement</DialogTitle>
          <DialogDescription>
            Record a new offer. It will appear once an admin approves it.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label>Company</Label>
            <Popover open={companyPickerOpen} onOpenChange={setCompanyPickerOpen}>
              <PopoverTrigger asChild>
                <Button
                  type="button"
                  variant="outline"
                  role="combobox"
                  aria-expanded={companyPickerOpen}
                  className="w-full justify-between font-normal"
                  disabled={companiesLoading}
                >
                  <span className={cn(!companyName && "text-muted-foreground")}>
                    {companiesLoading
                      ? "Loading companies..."
                      : companyName || "Select or add a company"}
                  </span>
                  <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-[--radix-popover-trigger-width] p-0" align="start">
                <Command shouldFilter={false}>
                  <CommandInput
                    placeholder="Search or type a new company..."
                    value={companySearch}
                    onValueChange={setCompanySearch}
                  />
                  <CommandList>
                    {filteredCompanies.length === 0 && !trimmedSearch && (
                      <CommandEmpty>No companies yet. Type to add one.</CommandEmpty>
                    )}
                    {filteredCompanies.length > 0 && (
                      <CommandGroup heading="Companies">
                        {filteredCompanies.map((c) => (
                          <CommandItem
                            key={c.id}
                            value={c.name}
                            onSelect={() => {
                              setCompanyName(c.name);
                              setCompanyPickerOpen(false);
                            }}
                          >
                            <Check
                              className={cn(
                                "mr-2 h-4 w-4",
                                companyName.toLowerCase() === c.name.toLowerCase()
                                  ? "opacity-100"
                                  : "opacity-0"
                              )}
                            />
                            {c.name}
                          </CommandItem>
                        ))}
                      </CommandGroup>
                    )}
                    {trimmedSearch && !exactMatch && (
                      <CommandGroup heading="Add new">
                        <CommandItem
                          value={`__add__${trimmedSearch}`}
                          onSelect={() => {
                            setCompanyName(trimmedSearch);
                            setCompanyPickerOpen(false);
                          }}
                        >
                          <Plus className="mr-2 h-4 w-4" />
                          Add "{trimmedSearch}"
                        </CommandItem>
                      </CommandGroup>
                    )}
                  </CommandList>
                </Command>
              </PopoverContent>
            </Popover>
          </div>

          <div className="space-y-2">
            <Label htmlFor="pl-role">Role</Label>
            <Input
              id="pl-role"
              value={role}
              onChange={(e) => setRole(e.target.value)}
              placeholder="e.g. Software Engineer"
            />
          </div>

          <div className="space-y-2">
            <Label>Engagement Type</Label>
            <Select value={engagementType} onValueChange={setEngagementType}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {ENGAGEMENT_OPTIONS.map((opt) => (
                  <SelectItem key={opt.value} value={opt.value}>
                    {opt.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            {engagement.needsStipend && (
              <div className="space-y-2">
                <Label htmlFor="pl-stipend">Stipend (₹K / month)</Label>
                <Input
                  id="pl-stipend"
                  type="number"
                  min="0"
                  step="1"
                  value={stipend}
                  onChange={(e) => setStipend(e.target.value)}
                  placeholder="e.g. 45"
                />
              </div>
            )}
            {engagement.needsCtc && (
              <div className="space-y-2">
                <Label htmlFor="pl-ctc">CTC (LPA)</Label>
                <Input
                  id="pl-ctc"
                  type="number"
                  min="0"
                  step="0.1"
                  value={ctc}
                  onChange={(e) => setCtc(e.target.value)}
                  placeholder="e.g. 12"
                />
              </div>
            )}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Start Month (optional)</Label>
              <Select value={startMonth} onValueChange={setStartMonth}>
                <SelectTrigger>
                  <SelectValue placeholder="Select month" />
                </SelectTrigger>
                <SelectContent>
                  {MONTHS.map((m) => (
                    <SelectItem key={m} value={m}>
                      {m}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="pl-year">{placementNature === "Internship" ? "Internship Year" : "Placement Year"}</Label>
              <Input
                id="pl-year"
                type="number"
                value={placementYear}
                onChange={(e) => setPlacementYear(e.target.value)}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label>Campus Mode</Label>
            <Select value={campusMode} onValueChange={setCampusMode}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="On-Campus">On-Campus</SelectItem>
                <SelectItem value="Off-Campus">Off-Campus</SelectItem>
              </SelectContent>
            </Select>
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

export default AddPlacementDialog;
