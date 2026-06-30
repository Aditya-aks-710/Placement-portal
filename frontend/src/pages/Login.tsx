import { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import Navbar from "@/components/Navbar";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Lock, User, ArrowRight, Loader2 } from "lucide-react";
import { login as loginApi } from "@/lib/api";
import { setSession, isAdmin } from "@/lib/auth";
import { toast } from "sonner";

const Login = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const expired = searchParams.get("expired") === "1";
  const redirectTo = searchParams.get("redirect");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(expired ? "Your session expired. Please sign in again." : "");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!username.trim() || !password) {
      setError("Please enter both username and password.");
      return;
    }

    setLoading(true);
    try {
      const res = await loginApi(username.trim(), password);
      setSession({ token: res.token, username: res.username, role: res.role });
      toast.success(`Welcome back, ${res.username}`);
      // Honor an explicit redirect target, but never drop the user straight onto
      // a profile page — always land on the dashboard (or admin) instead.
      if (redirectTo && redirectTo.startsWith("/") && !redirectTo.startsWith("/student/")) {
        navigate(redirectTo);
      } else {
        navigate(isAdmin() ? "/admin" : "/");
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : "Login failed. Please try again.";
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
                <Lock className="h-7 w-7 text-accent-foreground" />
              </div>
              <h1 className="font-display text-2xl font-bold text-foreground">Welcome Back</h1>
              <p className="mt-1.5 text-sm text-muted-foreground">
                Sign in with your registration number
              </p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-5">
              <div className="space-y-2">
                <Label htmlFor="username" className="text-sm font-medium">Username / Reg. No.</Label>
                <div className="relative">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                  <Input
                    id="username"
                    type="text"
                    autoComplete="username"
                    placeholder="e.g. 2024CS001 or admin"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="pl-10"
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="password" className="text-sm font-medium">Password</Label>
                <div className="relative">
                  <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                  <Input
                    id="password"
                    type="password"
                    autoComplete="current-password"
                    placeholder="••••••••"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="pl-10"
                  />
                </div>
              </div>

              {error && (
                <p className="text-sm text-destructive">{error}</p>
              )}

              <Button
                type="submit"
                disabled={loading}
                className="w-full bg-accent text-accent-foreground hover:bg-accent/90"
              >
                {loading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Signing in...
                  </>
                ) : (
                  <>
                    Sign In
                    <ArrowRight className="ml-2 h-4 w-4" />
                  </>
                )}
              </Button>
            </form>

            <p className="mt-6 text-center text-sm text-muted-foreground">
              New here?{" "}
              <Link to="/register" className="font-medium text-accent hover:underline">
                Activate your account
              </Link>
            </p>
            <p className="mt-2 text-center text-xs text-muted-foreground">
              Admin? Use your admin credentials to manage all profiles.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
