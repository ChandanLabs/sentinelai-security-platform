"use client";

import { useEffect, useState } from "react";
import { Zap, Bell, AlertTriangle, TrendingUp, Activity, Shield, RefreshCw } from "lucide-react";
import { AreaChart, Area, BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from "recharts";
import { api, type DashboardStats } from "@/lib/api";

const SEVERITY_COLORS: Record<string, string> = {
  INFO: "#60a5fa",
  LOW: "#4ade80",
  MEDIUM: "#facc15",
  HIGH: "#fb923c",
  CRITICAL: "#f87171",
};

function StatCard({ icon: Icon, label, value, color, sub }: {
  icon: typeof Zap; label: string; value: number | string; color: string; sub?: string;
}) {
  return (
    <div className="stat-card">
      <div className="flex items-center justify-between">
        <span className="text-xs font-semibold uppercase tracking-widest" style={{ color: "var(--muted)" }}>{label}</span>
        <div className="w-8 h-8 rounded-lg flex items-center justify-center"
          style={{ background: `${color}20` }}>
          <Icon className="w-4 h-4" style={{ color }} />
        </div>
      </div>
      <div className="text-3xl font-bold tracking-tight mt-1" style={{ color: "var(--text)" }}>{value}</div>
      {sub && <p className="text-xs" style={{ color: "var(--muted)" }}>{sub}</p>}
    </div>
  );
}

// Custom tooltip for recharts
const ChartTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="card py-2 px-3 text-xs" style={{ border: "1px solid rgba(99,102,241,0.3)" }}>
      <p style={{ color: "var(--muted)" }}>{label}</p>
      <p className="font-bold mt-0.5" style={{ color: "#a5b4fc" }}>
        Risk Score: {payload[0].value}
      </p>
    </div>
  );
};

export default function DashboardPage() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [lastUpdate, setLastUpdate] = useState<Date | null>(null);
  const [error, setError] = useState("");

  async function load(showLoading = true) {
    if (showLoading) setLoading(true);
    try {
      const data = await api.getStats();
      setStats(data);
      setError("");
      setLastUpdate(new Date());
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Could not load dashboard data.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
    const interval = window.setInterval(() => void load(false), 5000);
    return () => window.clearInterval(interval);
  }, []);

  const statCards = [
    { icon: Zap, label: "Total Events", value: stats?.totalEvents ?? "—", color: "#60a5fa", sub: "persisted in PostgreSQL" },
    { icon: Bell, label: "Open Alerts", value: stats?.openAlerts ?? "—", color: "#fb923c", sub: "created by implemented rules" },
    { icon: AlertTriangle, label: "Active Incidents", value: stats?.activeIncidents ?? "—", color: "#f87171", sub: "open or investigating" },
    { icon: TrendingUp, label: "Critical Events", value: stats?.criticalEvents ?? "—", color: "#f43f5e", sub: "persisted critical severity" },
  ];
  const displayedSeverityBreakdown = stats?.severityBreakdown ?? [];

  return (
    <div className="p-6 flex flex-col gap-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold" style={{ color: "var(--text)" }}>Security Overview</h1>
          <p className="text-sm mt-0.5" style={{ color: "var(--muted)" }}>
            {lastUpdate ? `Backend data updated ${lastUpdate.toLocaleTimeString()}` : "Waiting for backend data…"}
          </p>
          <p className="text-xs mt-1" style={{ color: "var(--muted)" }}>
            This dashboard shows only activity recorded for your account. Risk score maps severity to 0/25/50/75/100; it is not a probability.
          </p>
        </div>
        <button onClick={() => void load()} className="btn-ghost" disabled={loading}>
          <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
          Refresh
        </button>
      </div>
      {error && (
        <div role="alert" className="card flex items-center justify-between gap-4 text-sm"
          style={{ borderColor: "rgba(248,113,113,0.4)", color: "#fca5a5" }}>
          <span>Backend data unavailable: {error}</span>
          <button onClick={() => void load()} className="btn-ghost shrink-0">Retry</button>
        </div>
      )}

      {/* Stat cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {statCards.map(c => <StatCard key={c.label} {...c} />)}
      </div>

      {/* Charts row */}
      <div className="grid lg:grid-cols-5 gap-4">
        {/* Risk trend area chart */}
        <div className="card lg:col-span-3">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-sm font-semibold" style={{ color: "var(--text)" }}>Risk Score Trend</h2>
              <p className="text-xs" style={{ color: "var(--muted)" }}>7-day rolling average</p>
            </div>
            <Activity className="w-4 h-4" style={{ color: "var(--primary)" }} />
          </div>
          <ResponsiveContainer width="100%" height={180}>
            <AreaChart data={stats?.riskTrend ?? []} margin={{ top: 5, right: 5, bottom: 0, left: -20 }}>
              <defs>
                <linearGradient id="riskGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#6366f1" stopOpacity={0.3} />
                  <stop offset="95%" stopColor="#6366f1" stopOpacity={0} />
                </linearGradient>
              </defs>
              <XAxis dataKey="date" tick={{ fontSize: 11, fill: "#475569" }} axisLine={false} tickLine={false} />
              <YAxis domain={[0, 100]} tick={{ fontSize: 11, fill: "#475569" }} axisLine={false} tickLine={false} />
              <Tooltip content={<ChartTooltip />} />
              <Area type="monotone" dataKey="score" stroke="#6366f1" strokeWidth={2}
                fill="url(#riskGrad)" dot={{ fill: "#6366f1", r: 3 }} activeDot={{ r: 5 }} />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Severity breakdown bar chart */}
        <div className="card lg:col-span-2">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-sm font-semibold" style={{ color: "var(--text)" }}>Severity Breakdown</h2>
              <p className="text-xs" style={{ color: "var(--muted)" }}>Event distribution</p>
            </div>
            <Shield className="w-4 h-4" style={{ color: "var(--primary)" }} />
          </div>
          <ResponsiveContainer width="100%" height={180}>
            <BarChart data={displayedSeverityBreakdown} margin={{ top: 5, right: 5, bottom: 0, left: -25 }}>
              <XAxis dataKey="severity" tick={{ fontSize: 10, fill: "#475569" }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 11, fill: "#475569" }} axisLine={false} tickLine={false} />
              <Tooltip
                contentStyle={{ background: "var(--surface)", border: "1px solid rgba(99,102,241,0.3)", borderRadius: 8, fontSize: 12 }}
                labelStyle={{ color: "var(--muted)" }}
                itemStyle={{ color: "#a5b4fc" }}
              />
              <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                {displayedSeverityBreakdown.map((entry) => (
                  <Cell key={entry.severity} fill={SEVERITY_COLORS[entry.severity] || "#6366f1"} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Detection status */}
      <div className="grid lg:grid-cols-3 gap-4">
        {[
          { name: "AUTH_BRUTE_FORCE", status: "ACTIVE", desc: "5 failed logins / 5 min from same IP", color: "#fb923c" },
          { name: "PRIVILEGE_ESCALATION", status: "ACTIVE", desc: "Explicit privilege_escalation event type", color: "#f87171" },
          { name: "SUSPICIOUS_COMMAND", status: "ACTIVE", desc: "Explicit suspicious_command event type", color: "#fb923c" },
          { name: "AI Anomaly Analysis", status: "NOT CONFIGURED", desc: "The AI service currently provides health only; no inference is run.", color: "#facc15" },
          { name: "Rule-based Risk Score", status: "ACTIVE", desc: "Server-assigned 0–100 demo score from event type and failure count", color: "#4ade80" },
        ].map(r => (
          <div key={r.name} className="card flex items-start gap-3" style={{ padding: "0.875rem 1rem" }}>
            <div className="w-2 h-2 rounded-full mt-1.5 shrink-0" style={{ background: r.color, boxShadow: `0 0 8px ${r.color}` }} />
            <div className="min-w-0">
              <p className="text-xs font-bold font-mono" style={{ color: "var(--text)" }}>{r.name}</p>
              <p className="text-xs mt-0.5" style={{ color: "var(--muted)" }}>{r.desc}</p>
            </div>
            <span className="text-xs font-semibold ml-auto shrink-0" style={{ color: r.color }}>{r.status}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
