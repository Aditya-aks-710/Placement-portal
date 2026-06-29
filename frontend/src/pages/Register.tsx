import { useEffect, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import Navbar from "@/components/Navbar";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Hash, Lock, KeyRound, ArrowRight, Loader2, CheckCircle2 } from "lucide-react";
import { initiateRegistration, completeRegistration } from "@/lib/api";
import { toast } from "sonner";

type Step = "initiate" | "complete";

const Register = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const tokenFromUrl = searchParams.get("token") ?? "";
  const [step, setStep] = useState<Step>(tokenFromUrl ? "complete" : "initiate");

  const [regno, setRegno] = useState("");
  const [token, setToken] = useState(tokenFromUrl);
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (tokenFromUrl) {
      setToken(tokenFromUrl);
      setStep("complete");
    }
  }, [tokenFromUrl]);

  const handleInitiate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!regno.trim()) {
      setError("Please enter your registration number.");
      return;
    }

    setLoading(true);
    try {
      const res = await initiateRegistration(regno.trim());
      if (res.token) {
        setToken(res.token);
        toast.success("Account found! Choose a password to finish.");
      } else {
        toast.success("Activation link generated. Enter the activation token to set your password.");
      }
      setStep("complete");
    } catch (err) {
      const message = err instanceof Error ? err.message : "Could not start registration. Please try again.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  const handleComplete = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!token.trim()) {
      setError("Please enter your activation token.");
      return;
    }
    if (password.length < 6) {
      setError("Password must be at least 6 characters long.");
      return;
    }
    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);
    try {
      await completeRegistration(token.trim(), password);
      toast.success("Registration complete! You can now sign in.");
      navigate("/login");
    } catch (err) {
      const message = err instanceof Error ? err.message : "Could not complete registration. Please try again.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <Navbar />

      <div className="flex items-center justify-center px-4 py-20">
        <div className="w-full max-w-md">
          <div className="elevated-card rounded-2xl p-8 animate-scale-in">
            <div className="text-center mb-8">
              <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-xl gradient-accent">
                {step === "initiate" ? (
                  <Hash className="h-7 w-7 text-accent-foreground" />
                ) : (
                  <KeyRound className="h-7 w-7 text-accent-foreground" />
                )}
              </div>
              <h1 className="font-display text-2xl font-bold text-foreground">
                {step === "initiate" ? "Activate Your Account" : "Set Your Password"}
              </h1>
              <p className="mt-1.5 text-sm text-muted-foreground">
                {step === "initiate"
                  ? "Enter your registration number to begin"
                  : "Enter your activation token and choose a password"}
              </p>
            </div>

            {/* Step indicator */}
            <div className="mb-6 flex items-center justify-center gap-2">
              <span
                className={`flex h-6 w-6 items-center justify-center rounded-full text-xs font-semibold ${
                  step === "initiate"
                    ? "bg-accent text-accent-foreground"
                    : "bg-accent/20 text-accent"
                }`}
              >
                {step === "complete" ? <CheckCircle2 className="h-4 w-4" /> : "1"}
              </span>
              <span className="h-px w-8 bg-border" />
              <span
                className={`flex h-6 w-6 items-center justify-center rounded-full text-xs font-semibold ${
                  step === "complete"
                    ? "bg-accent text-accent-foreground"
                    : "bg-muted text-muted-foreground"
                }`}
              >
                2
              </span>
            </div>

            {step === "initiate" ? (
              <form onSubmit={handleInitiate} className="space-y-5">
                <div className="space-y-2">
                  <Label htmlFor="regno" className="text-sm font-medium">Registration Number</Label>
                  <div className="relative">
                    <Hash className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                    <Input
                      id="regno"
                      type="text"
                      autoComplete="username"
                      placeholder="e.g. 2024CS001"
                      value={regno}
                      onChange={(e) => setRegno(e.target.value)}
                      className="pl-10"
                    />
                  </div>
                </div>

                {error && <p className="text-sm text-destructive">{error}</p>}

                <Button
                  type="submit"
                  disabled={loading}
                  className="w-full bg-accent text-accent-foreground hover:bg-accent/90"
                >
                  {loading ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Generating link...
                    </>
                  ) : (
                    <>
                      Continue
                      <ArrowRight className="ml-2 h-4 w-4" />
                    </>
                  )}
                </Button>
              </form>
            ) : (
              <form onSubmit={handleComplete} className="space-y-5">
                <div className="space-y-2">
                  <Label htmlFor="token" className="text-sm font-medium">Activation Token</Label>
                  <div className="relative">
                    <KeyRound className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                    <Input
                      id="token"
                      type="text"
                      placeholder="Paste your activation token"
                      value={token}
                      onChange={(e) => setToken(e.target.value)}
                      className="pl-10"
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="password" className="text-sm font-medium">New Password</Label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                    <Input
                      id="password"
                      type="password"
                      autoComplete="new-password"
                      placeholder="At least 6 characters"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className="pl-10"
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="confirmPassword" className="text-sm font-medium">Confirm Password</Label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                    <Input
                      id="confirmPassword"
                      type="password"
                      autoComplete="new-password"
                      placeholder="Re-enter your password"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      className="pl-10"
                    />
                  </div>
                </div>

                {error && <p className="text-sm text-destructive">{error}</p>}

                <Button
                  type="submit"
                  disabled={loading}
                  className="w-full bg-accent text-accent-foreground hover:bg-accent/90"
                >
                  {loading ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Completing...
                    </>
                  ) : (
                    <>
                      Complete Registration
                      <ArrowRight className="ml-2 h-4 w-4" />
                    </>
                  )}
                </Button>

                {!tokenFromUrl && (
                  <button
                    type="button"
                    onClick={() => {
                      setStep("initiate");
                      setError("");
                    }}
                    className="w-full text-center text-xs text-muted-foreground hover:text-foreground"
                  >
                    ← Back to registration number
                  </button>
                )}
              </form>
            )}

            <p className="mt-6 text-center text-xs text-muted-foreground">
              Already have an account?{" "}
              <Link to="/login" className="font-medium text-accent hover:underline">
                Sign in
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;
