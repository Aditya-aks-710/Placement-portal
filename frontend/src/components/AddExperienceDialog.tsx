import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
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
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Plus, Trash2, Loader2 } from "lucide-react";
import { submitInterviewExperience, type InterviewRoundInput } from "@/lib/api";
import { toast } from "sonner";

type RoundDraft = InterviewRoundInput;

const emptyRound = (): RoundDraft => ({ name: "", description: "", tips: "" });

type AddExperienceDialogProps = {
  trigger: React.ReactNode;
};

const AddExperienceDialog = ({ trigger }: AddExperienceDialogProps) => {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [company, setCompany] = useState("");
  const [difficulty, setDifficulty] = useState<"Easy" | "Medium" | "Hard">("Medium");
  const [rating, setRating] = useState(4);
  const [overallTips, setOverallTips] = useState("");
  const [rounds, setRounds] = useState<RoundDraft[]>([emptyRound()]);

  const reset = () => {
    setCompany("");
    setDifficulty("Medium");
    setRating(4);
    setOverallTips("");
    setRounds([emptyRound()]);
  };

  const mutation = useMutation({
    mutationFn: () =>
      submitInterviewExperience({
        company: company.trim(),
        difficulty,
        rating,
        overallTips: overallTips.trim(),
        rounds: rounds
          .filter((r) => r.name.trim())
          .map((r) => ({
            name: r.name.trim(),
            description: r.description.trim(),
            tips: r.tips.trim(),
          })),
      }),
    onSuccess: () => {
      toast.success("Interview experience shared");
      queryClient.invalidateQueries({ queryKey: ["students"] });
      reset();
      setOpen(false);
    },
    onError: (err) => toast.error(err instanceof Error ? err.message : "Failed to submit"),
  });

  const updateRound = (index: number, field: keyof RoundDraft, value: string) => {
    setRounds((prev) => prev.map((r, i) => (i === index ? { ...r, [field]: value } : r)));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!company.trim()) {
      toast.error("Company name is required");
      return;
    }
    if (!rounds.some((r) => r.name.trim())) {
      toast.error("Add at least one round");
      return;
    }
    mutation.mutate();
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>{trigger}</DialogTrigger>
      <DialogContent className="max-h-[85vh] overflow-y-auto sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Share an interview experience</DialogTitle>
          <DialogDescription>
            Help juniors prepare by sharing your interview rounds and tips.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="exp-company">Company</Label>
            <Input
              id="exp-company"
              value={company}
              onChange={(e) => setCompany(e.target.value)}
              placeholder="e.g. Google"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Difficulty</Label>
              <Select value={difficulty} onValueChange={(v) => setDifficulty(v as typeof difficulty)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Easy">Easy</SelectItem>
                  <SelectItem value="Medium">Medium</SelectItem>
                  <SelectItem value="Hard">Hard</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label>Rating (1-5)</Label>
              <Select value={String(rating)} onValueChange={(v) => setRating(Number(v))}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {[1, 2, 3, 4, 5].map((n) => (
                    <SelectItem key={n} value={String(n)}>
                      {n}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <Label>Rounds</Label>
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={() => setRounds((prev) => [...prev, emptyRound()])}
              >
                <Plus className="mr-1 h-4 w-4" /> Add round
              </Button>
            </div>

            {rounds.map((round, index) => (
              <div key={index} className="space-y-2 rounded-lg border border-border p-3">
                <div className="flex items-center gap-2">
                  <Input
                    value={round.name}
                    onChange={(e) => updateRound(index, "name", e.target.value)}
                    placeholder={`Round ${index + 1} name (e.g. Technical Round 1)`}
                  />
                  {rounds.length > 1 && (
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      onClick={() => setRounds((prev) => prev.filter((_, i) => i !== index))}
                    >
                      <Trash2 className="h-4 w-4 text-destructive" />
                    </Button>
                  )}
                </div>
                <Textarea
                  value={round.description}
                  onChange={(e) => updateRound(index, "description", e.target.value)}
                  placeholder="What happened in this round?"
                  rows={2}
                />
                <Input
                  value={round.tips}
                  onChange={(e) => updateRound(index, "tips", e.target.value)}
                  placeholder="Tip for this round"
                />
              </div>
            ))}
          </div>

          <div className="space-y-2">
            <Label htmlFor="exp-overall">Overall tips</Label>
            <Textarea
              id="exp-overall"
              value={overallTips}
              onChange={(e) => setOverallTips(e.target.value)}
              placeholder="Any overall advice for this company?"
              rows={3}
            />
          </div>

          <DialogFooter>
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              Submit
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default AddExperienceDialog;
