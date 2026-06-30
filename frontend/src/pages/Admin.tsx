import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import Navbar from "@/components/Navbar";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Building2,
  CheckCircle2,
  XCircle,
  Loader2,
  Clock,
  IndianRupee,
  CalendarDays,
  MapPin,
  Briefcase,
} from "lucide-react";
import {
  getPlacementRequests,
  approvePlacementRequest,
  rejectPlacementRequest,
  type PlacementRequest,
} from "@/lib/api";
import { toast } from "sonner";

const STATUS_TABS = ["PENDING", "APPROVED", "REJECTED"] as const;
type StatusTab = (typeof STATUS_TABS)[number];

const statusBadge: Record<string, string> = {
  PENDING: "bg-muted text-foreground",
  APPROVED: "bg-placed text-placed-foreground",
  REJECTED: "bg-destructive text-destructive-foreground",
};

const Admin = () => {
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState<StatusTab>("PENDING");

  const { data: requests = [], isLoading, isError, error } = useQuery({
    queryKey: ["admin", "placement-requests", activeTab],
    queryFn: () => getPlacementRequests(activeTab),
  });

  const errorMessage = error instanceof Error ? error.message : "Unable to load placement requests.";

  const invalidate = () => {
    queryClient.invalidateQueries({ queryKey: ["admin", "placement-requests"] });
    queryClient.invalidateQueries({ queryKey: ["students"] });
  };

  const approveMutation = useMutation({
    mutationFn: (id: string) => approvePlacementRequest(id),
    onSuccess: () => {
      toast.success("Request approved");
      invalidate();
    },
    onError: (err) => toast.error(err instanceof Error ? err.message : "Failed to approve"),
  });

  const rejectMutation = useMutation({
    mutationFn: (id: string) => rejectPlacementRequest(id),
    onSuccess: () => {
      toast.success("Request rejected");
      invalidate();
    },
    onError: (err) => toast.error(err instanceof Error ? err.message : "Failed to reject"),
  });

  const pendingActionId = approveMutation.isPending
    ? approveMutation.variables
    : rejectMutation.isPending
      ? rejectMutation.variables
      : undefined;

  const heading = useMemo(() => {
    switch (activeTab) {
      case "APPROVED":
        return "Approved placements";
      case "REJECTED":
        return "Rejected requests";
      default:
        return "Pending approval";
    }
  }, [activeTab]);

  return (
    <div className="min-h-screen bg-background">
      <Navbar />

      <section className="gradient-hero py-10 px-4">
        <div className="container">
          <h1 className="font-display text-3xl font-bold text-primary-foreground">Admin Dashboard</h1>
          <p className="mt-2 text-sm text-primary-foreground/70">
            Review and manage student placement requests.
          </p>
        </div>
      </section>

      <main className="container py-8">
        <div className="mb-6 inline-flex rounded-lg border border-border bg-card p-1">
          {STATUS_TABS.map((tab) => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`rounded-md px-4 py-2 text-sm font-medium capitalize transition-colors ${
                activeTab === tab
                  ? "bg-primary text-primary-foreground"
                  : "text-muted-foreground hover:text-foreground"
              }`}
            >
              {tab.toLowerCase()}
            </button>
          ))}
        </div>

        <h2 className="mb-4 font-display text-lg font-semibold text-foreground">{heading}</h2>

        {isLoading && (
          <div className="flex items-center gap-2 rounded-lg border border-border bg-muted/40 p-3 text-sm text-muted-foreground">
            <Loader2 className="h-4 w-4 animate-spin" /> Loading requests...
          </div>
        )}

        {isError && (
          <div className="rounded-lg border border-destructive/40 bg-destructive/10 p-3 text-sm text-destructive">
            {errorMessage}
          </div>
        )}

        {!isLoading && !isError && requests.length === 0 && (
          <div className="flex flex-col items-center justify-center py-20 text-center">
            <Clock className="h-12 w-12 text-muted-foreground/30" />
            <h3 className="mt-4 font-display text-lg font-semibold text-foreground">No requests here</h3>
            <p className="mt-1 text-sm text-muted-foreground">
              {activeTab === "PENDING" ? "All caught up — nothing to review." : "Nothing to show yet."}
            </p>
          </div>
        )}

        <div className="grid gap-4">
          {requests.map((request: PlacementRequest) => (
            <div
              key={request.id}
              className="elevated-card group flex flex-col gap-5 rounded-2xl border border-border/60 p-5 transition-all duration-300 hover:-translate-y-0.5 hover:border-accent/40 hover:shadow-lg sm:flex-row sm:items-center sm:justify-between"
            >
              <div className="flex min-w-0 items-start gap-4">
                <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-xl bg-gradient-to-br from-accent/15 to-primary/10 ring-1 ring-border/50">
                  {request.companyLogo ? (
                    <img
                      src={request.companyLogo}
                      alt={request.company}
                      className="h-9 w-9 rounded-md object-contain"
                    />
                  ) : (
                    <Building2 className="h-7 w-7 text-accent" />
                  )}
                </div>
                <div className="min-w-0">
                  <div className="flex flex-wrap items-center gap-2">
                    <h3 className="font-display text-base font-semibold text-foreground">{request.company}</h3>
                    {request.engagementType && (
                      <Badge variant="secondary" className="rounded-full px-2.5 py-0.5 text-[10px] font-semibold">
                        {request.engagementType}
                      </Badge>
                    )}
                    {request.requestType === "CONVERSION" && (
                      <Badge className="rounded-full bg-placed/15 px-2.5 py-0.5 text-[10px] font-semibold text-placed hover:bg-placed/15">
                        Conversion
                      </Badge>
                    )}
                    {request.status !== "PENDING" && (
                      <Badge className={`rounded-full px-2.5 py-0.5 text-[10px] font-semibold ${statusBadge[request.status] ?? "bg-muted"}`}>
                        {request.status}
                      </Badge>
                    )}
                  </div>
                  <p className="mt-0.5 truncate text-sm text-muted-foreground">{request.role}</p>
                  <div className="mt-3 flex flex-wrap items-center gap-2">
                    {request.ctc > 0 && (
                      <span className="inline-flex items-center gap-1 rounded-full bg-accent/10 px-2.5 py-1 text-xs font-semibold text-accent">
                        <IndianRupee className="h-3.5 w-3.5" /> {request.ctc} LPA
                      </span>
                    )}
                    {request.stipend ? (
                      <span className="inline-flex items-center gap-1 rounded-full bg-internship/15 px-2.5 py-1 text-xs font-semibold text-foreground/80">
                        <IndianRupee className="h-3.5 w-3.5" /> {request.stipend}K/mo
                      </span>
                    ) : null}
                    <span className="inline-flex items-center gap-1 rounded-full bg-muted px-2.5 py-1 text-xs font-medium text-foreground/70">
                      <CalendarDays className="h-3.5 w-3.5 text-muted-foreground" /> {request.placementYear}
                    </span>
                    {request.campusMode && (
                      <span className="inline-flex items-center gap-1 rounded-full bg-muted px-2.5 py-1 text-xs font-medium text-foreground/70">
                        <MapPin className="h-3.5 w-3.5 text-muted-foreground" /> {request.campusMode}
                      </span>
                    )}
                    {request.placementNature && (
                      <span className="inline-flex items-center gap-1 rounded-full bg-muted px-2.5 py-1 text-xs font-medium text-foreground/70">
                        <Briefcase className="h-3.5 w-3.5 text-muted-foreground" /> {request.placementNature}
                      </span>
                    )}
                  </div>
                </div>
              </div>

              {request.status === "PENDING" && (
                <div className="flex shrink-0 gap-2 sm:flex-col sm:items-stretch lg:flex-row">
                  <Button
                    size="sm"
                    onClick={() => approveMutation.mutate(request.id)}
                    disabled={pendingActionId === request.id}
                    className="bg-placed text-placed-foreground hover:bg-placed/90"
                  >
                    {pendingActionId === request.id && approveMutation.isPending ? (
                      <Loader2 className="mr-1 h-4 w-4 animate-spin" />
                    ) : (
                      <CheckCircle2 className="mr-1 h-4 w-4" />
                    )}
                    Approve
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => rejectMutation.mutate(request.id)}
                    disabled={pendingActionId === request.id}
                    className="border-destructive/40 text-destructive hover:bg-destructive/10"
                  >
                    {pendingActionId === request.id && rejectMutation.isPending ? (
                      <Loader2 className="mr-1 h-4 w-4 animate-spin" />
                    ) : (
                      <XCircle className="mr-1 h-4 w-4" />
                    )}
                    Reject
                  </Button>
                </div>
              )}
            </div>
          ))}
        </div>
      </main>
    </div>
  );
};

export default Admin;
