import { useParams, Link, useNavigate } from "react-router-dom";
import { mockStudents } from "@/data/mockStudents";
import Navbar from "@/components/Navbar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import {
  ArrowLeft,
  Briefcase,
  GraduationCap,
  Mail,
  Phone,
  Edit,
  Star,
  ChevronRight,
  ExternalLink,
  MessageSquare,
} from "lucide-react";
import { useState } from "react";

const statusConfig = {
  placed: { label: "Placed", className: "bg-placed text-placed-foreground" },
  unplaced: { label: "Unplaced", className: "bg-unplaced text-unplaced-foreground" },
  internship: { label: "Intern", className: "bg-internship text-internship-foreground" },
};

const difficultyColor = {
  Easy: "text-placed",
  Medium: "text-internship",
  Hard: "text-destructive",
};

const StudentDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const student = mockStudents.find((s) => s.id === id);
  const [activeTab, setActiveTab] = useState<"overview" | "interviews" | "education">("overview");

  if (!student) {
    return (
      <div className="min-h-screen bg-background">
        <Navbar />
        <div className="container flex flex-col items-center justify-center py-32">
          <h2 className="font-display text-2xl font-bold">Student not found</h2>
          <Link to="/" className="mt-4 text-accent underline">Go back</Link>
        </div>
      </div>
    );
  }

  const config = statusConfig[student.status];
  const initials = student.name.split(" ").map((n) => n[0]).join("").toUpperCase();
  const colors = [
    "from-blue-500 to-indigo-600",
    "from-emerald-500 to-teal-600",
    "from-orange-500 to-red-500",
    "from-purple-500 to-pink-500",
    "from-cyan-500 to-blue-500",
  ];
  const colorIndex = student.name.charCodeAt(0) % colors.length;

  const tabs = [
    { key: "overview" as const, label: "Overview" },
    { key: "interviews" as const, label: "Interview Experiences" },
    { key: "education" as const, label: "Education" },
  ];

  return (
    <div className="min-h-screen bg-background">
      <Navbar />

      {/* Header */}
      <section className="gradient-hero pb-24 pt-8 px-4">
        <div className="container">
          <button
            onClick={() => navigate(-1)}
            className="mb-6 flex items-center gap-1.5 text-sm text-primary-foreground/70 hover:text-primary-foreground transition-colors"
          >
            <ArrowLeft className="h-4 w-4" />
            Back
          </button>

          <div className="flex flex-col items-start gap-6 sm:flex-row sm:items-center">
            <div
              className={`flex h-20 w-20 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br ${colors[colorIndex]} text-2xl font-bold text-white shadow-xl`}
            >
              {initials}
            </div>

            <div className="flex-1">
              <div className="flex items-center gap-3 flex-wrap">
                <h1 className="font-display text-2xl font-bold text-primary-foreground md:text-3xl">
                  {student.name}
                </h1>
                <Badge className={`text-xs ${config.className}`}>{config.label}</Badge>
              </div>
              <p className="mt-1.5 text-sm text-primary-foreground/70">{student.bio}</p>
              <div className="mt-3 flex flex-wrap items-center gap-4 text-xs text-primary-foreground/60">
                <span className="flex items-center gap-1"><Mail className="h-3.5 w-3.5" /> {student.email}</span>
                <span className="flex items-center gap-1"><Phone className="h-3.5 w-3.5" /> {student.phone}</span>
                <span className="flex items-center gap-1"><GraduationCap className="h-3.5 w-3.5" /> {student.branch} • {student.batch}</span>
              </div>
            </div>

            <Button
              onClick={() => navigate("/login")}
              className="shrink-0 bg-accent text-accent-foreground hover:bg-accent/90"
            >
              <Edit className="mr-2 h-4 w-4" />
              Edit Profile
            </Button>
          </div>
        </div>
      </section>

      {/* Content */}
      <main className="container -mt-12">
        {/* Tabs */}
        <div className="elevated-card rounded-xl p-1.5 mb-6 inline-flex gap-1">
          {tabs.map((tab) => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`rounded-lg px-4 py-2 text-sm font-medium transition-all ${
                activeTab === tab.key
                  ? "bg-primary text-primary-foreground shadow-sm"
                  : "text-muted-foreground hover:text-foreground hover:bg-muted"
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {activeTab === "overview" && (
          <div className="grid gap-6 lg:grid-cols-3 animate-fade-in">
            {/* Current Company */}
            {student.currentCompany && (
              <div className="lg:col-span-2 elevated-card rounded-xl p-6">
                <h2 className="font-display text-lg font-semibold text-foreground flex items-center gap-2">
                  <Briefcase className="h-5 w-5 text-accent" />
                  Current Position
                </h2>
                <div className="mt-4 rounded-xl bg-muted/50 p-5 border border-border/30">
                  <div className="flex items-start justify-between">
                    <div>
                      <h3 className="text-xl font-bold text-foreground">{student.currentCompany.name}</h3>
                      <p className="text-sm text-muted-foreground mt-1">{student.currentCompany.role}</p>
                    </div>
                    <span className="text-lg font-bold text-accent">{student.currentCompany.package}</span>
                  </div>
                  <div className="mt-3 flex items-center gap-4 text-xs text-muted-foreground">
                    <span>Joined: {student.currentCompany.joinDate}</span>
                    <Badge variant="outline" className="text-[10px]">{student.currentCompany.type}</Badge>
                  </div>
                </div>
              </div>
            )}

            {/* Skills */}
            <div className="elevated-card rounded-xl p-6">
              <h2 className="font-display text-lg font-semibold text-foreground">Skills</h2>
              <div className="mt-4 flex flex-wrap gap-2">
                {student.skills.map((skill) => (
                  <Badge key={skill} variant="secondary" className="font-medium">
                    {skill}
                  </Badge>
                ))}
              </div>

              {(student.linkedin || student.github) && (
                <>
                  <Separator className="my-4" />
                  <h3 className="text-sm font-medium text-foreground mb-3">Links</h3>
                  <div className="space-y-2">
                    {student.linkedin && (
                      <a
                        href={`https://${student.linkedin}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="flex items-center gap-2 text-sm text-accent hover:underline"
                      >
                        <ExternalLink className="h-3.5 w-3.5" /> LinkedIn
                      </a>
                    )}
                    {student.github && (
                      <a
                        href={`https://${student.github}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="flex items-center gap-2 text-sm text-accent hover:underline"
                      >
                        <ExternalLink className="h-3.5 w-3.5" /> GitHub
                      </a>
                    )}
                  </div>
                </>
              )}
            </div>

            {/* Past Companies */}
            {student.pastCompanies.length > 0 && (
              <div className="lg:col-span-3 elevated-card rounded-xl p-6">
                <h2 className="font-display text-lg font-semibold text-foreground flex items-center gap-2">
                  <Briefcase className="h-5 w-5 text-muted-foreground" />
                  Past Experience
                </h2>
                <div className="mt-4 space-y-3">
                  {student.pastCompanies.map((company, i) => (
                    <div key={i} className="flex items-center justify-between rounded-lg bg-muted/50 p-4 border border-border/20">
                      <div>
                        <h3 className="font-semibold text-foreground">{company.name}</h3>
                        <p className="text-sm text-muted-foreground">{company.role}</p>
                        <p className="text-xs text-muted-foreground mt-1">
                          {company.joinDate} – {company.endDate || "Present"} {company.duration && `(${company.duration})`}
                        </p>
                      </div>
                      <div className="text-right">
                        <span className="font-medium text-foreground">{company.package}</span>
                        <Badge variant="outline" className="ml-2 text-[10px]">{company.type}</Badge>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {activeTab === "interviews" && (
          <div className="space-y-6 animate-fade-in">
            {student.interviewExperiences.length === 0 ? (
              <div className="elevated-card rounded-xl p-12 text-center">
                <MessageSquare className="mx-auto h-10 w-10 text-muted-foreground/30" />
                <p className="mt-3 text-muted-foreground">No interview experiences shared yet.</p>
              </div>
            ) : (
              student.interviewExperiences.map((exp, i) => (
                <div key={i} className="elevated-card rounded-xl p-6">
                  <div className="flex items-start justify-between">
                    <div>
                      <h2 className="font-display text-xl font-bold text-foreground">{exp.company}</h2>
                      <div className="mt-1 flex items-center gap-3 text-sm">
                        <span className={`font-medium ${difficultyColor[exp.difficulty]}`}>
                          {exp.difficulty}
                        </span>
                        <span className="flex items-center gap-1 text-internship">
                          {Array.from({ length: exp.rating }).map((_, j) => (
                            <Star key={j} className="h-3.5 w-3.5 fill-current" />
                          ))}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="mt-6 space-y-4">
                    {exp.rounds.map((round, j) => (
                      <div key={j} className="relative pl-6 border-l-2 border-accent/30">
                        <div className="absolute -left-[7px] top-1 h-3 w-3 rounded-full bg-accent" />
                        <h3 className="font-semibold text-foreground">{round.name}</h3>
                        <p className="mt-1 text-sm text-muted-foreground">{round.description}</p>
                        <p className="mt-1 text-xs text-accent">💡 {round.tips}</p>
                      </div>
                    ))}
                  </div>

                  <div className="mt-6 rounded-lg bg-accent/5 border border-accent/20 p-4">
                    <h3 className="text-sm font-semibold text-accent">Overall Tips</h3>
                    <p className="mt-1 text-sm text-muted-foreground">{exp.overallTips}</p>
                  </div>
                </div>
              ))
            )}
          </div>
        )}

        {activeTab === "education" && (
          <div className="space-y-4 animate-fade-in">
            {student.education.map((edu, i) => (
              <div key={i} className="elevated-card rounded-xl p-5 flex items-center gap-4">
                <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-muted">
                  <GraduationCap className="h-6 w-6 text-accent" />
                </div>
                <div className="flex-1">
                  <h3 className="font-semibold text-foreground">{edu.degree}</h3>
                  <p className="text-sm text-muted-foreground">{edu.institution}</p>
                  <p className="text-xs text-muted-foreground mt-0.5">{edu.year}</p>
                </div>
                <Badge variant="outline" className="font-semibold">{edu.grade}</Badge>
              </div>
            ))}
          </div>
        )}

        <div className="h-12" />
      </main>
    </div>
  );
};

export default StudentDetail;
