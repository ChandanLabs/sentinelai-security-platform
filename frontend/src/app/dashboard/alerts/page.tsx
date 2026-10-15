"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Bell, RefreshCw, Search } from "lucide-react";
import { api, type Alert } from "@/lib/api";

const SEV_CLASS: Record<string, string> = {
  LOW: "badge-low", MEDIUM: "badge-medium", HIGH: "badge-high", CRITICAL: "badge-critical",
};
const STATUS_CLASS: Record<string, string> = {
  OPEN: "badge-open", ACKNOWLEDGED: "badge-ack", RESOLVED: "badge-resolved",
};

function timeAgo(ts: string) {
  const diff = Date.now() - new Date(ts).getTime();
  const m = Math.floor(diff / 60000);
  if (m < 60) return `${m}m ago`;
  const h = Math.floor(m / 60);
  if (h < 24) return `${h}h ago`;
  return `${Math.floor(h / 24)}d ago`;
}

export default function AlertsPage() {
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [error, setError] = useState("");

  async function load(showLoading = true) {
    if (showLoading) setLoading(true);
    try {
      const data = await api.getAlerts(0, 50);
      setAlerts(data.content);
      setError("");
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Could not load alerts.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
    const interval = window.setInterval(() => void load(false), 5000);
    return () => window.clearInterval(interval);
  }, []);

  const filtered = alerts.filter(a => {
    const matchStatus = statusFilter === "ALL" || a.status === statusFilter;
    const q = filter.toLowerCase();
    const matchQ = !q || a.title.toLowerCase().includes(q) || a.sourceIp.includes(q);
    return matchStatus && matchQ;
  });

  const openCount = alerts.filter(a => a.status === "OPEN").length;
  const critCount = alerts.filter(a => a.severity === "CRITICAL").length;

  return (
    <div className="p-6 flex flex-col gap-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold flex items-center gap-2" style={{ color: "var(--text)" }}>
            <Bell className="w-6 h-6 text-orange-400" />
            Alerts
          </h1>
          <p className="text-sm mt-0.5" style={{ color: "var(--muted)" }}>
            {openCount} open · {critCount} critical
          </p>
        </div>
        <button onClick={() => void load()} className="btn-ghost" disabled={loading}>
          <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
        </button>
      </div>
      {error && (
        <div role="alert" className="card flex items-center justify-between gap-4 text-sm"
          style={{ borderColor: "rgba(248,113,113,0.4)", color: "#fca5a5" }}>
          <span>Could not load persisted alerts: {error}</span>
          <button onClick={() => void load()} className="btn-ghost shrink-0">Retry</button>
        </div>
      )}

      {/* Summary chips */}
      <div className="flex gap-3 flex-wrap">
        {[
          { label: "Total", value: alerts.length, color: "var(--primary)" },
          { label: "Open", value: openCount, color: "#f87171" },
          { label: "Critical", value: critCount, color: "#f43f5e" },
          { label: "Acknowledged", value: alerts.filter(a => a.status === "ACKNOWLEDGED").length, color: "#facc15" },
        ].map(c => (
          <div key={c.label} className="card flex items-center gap-2 py-2 px-4" style={{ padding: "0.5rem 1rem" }}>
            <span className="text-xl font-bold" style={{ color: c.color }}>{c.value}</span>
            <span className="text-xs" style={{ color: "var(--muted)" }}>{c.label}</span>
          </div>
        ))}
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-3">
        <div className="relative flex-1 min-w-[200px]">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4" style={{ color: "var(--muted)" }} />
          <input className="input pl-9" placeholder="Search alerts…"
            value={filter} onChange={e => setFilter(e.target.value)} />
        </div>
        <div className="flex gap-2">
          {["ALL", "OPEN", "ACKNOWLEDGED", "RESOLVED"].map(s => (
            <button key={s} onClick={() => setStatusFilter(s)}
              className={`px-3 py-2 rounded-lg text-xs font-semibold uppercase tracking-wide transition-all ${statusFilter === s ? "bg-indigo-600 text-white" : "text-slate-400 hover:text-white hover:bg-white/5"}`}>
              {s}
            </button>
          ))}
        </div>
      </div>

      {/* Alert cards */}
      {loading ? (
        <div className="flex items-center justify-center h-40" style={{ color: "var(--muted)" }}>
          <RefreshCw className="w-5 h-5 animate-spin mr-2" /> Loading alerts…
        </div>
      ) : (
        <div className="flex flex-col gap-3">
          {filtered.map(a => (
            <div key={a.id} id={a.id} className="card flex items-center gap-4 hover:border-indigo-500/30 transition-colors"
              style={{ borderLeft: `3px solid ${a.severity === "CRITICAL" ? "#f87171" : a.severity === "HIGH" ? "#fb923c" : "#facc15"}` }}>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap mb-1">
                  <span className={`badge ${SEV_CLASS[a.severity]}`}>{a.severity}</span>
                  <span className={`badge ${STATUS_CLASS[a.status]}`}>{a.status}</span>
                  <span className="text-xs font-mono ml-1" style={{ color: "var(--muted)" }}>#{a.id}</span>
                  <span className="text-xs font-mono" style={{ color: "var(--muted)" }}>{a.detectionRule}</span>
                  {a.eventId && (
                    <Link href={`/dashboard/events/${a.eventId}`} className="text-xs text-indigo-300 hover:text-indigo-200">
                      View event
                    </Link>
                  )}
                </div>
                <p className="text-sm font-semibold" style={{ color: "var(--text)" }}>{a.title}</p>
                <div className="flex items-center gap-4 mt-1 flex-wrap">
                  <span className="text-xs font-mono" style={{ color: "var(--muted)" }}>{a.sourceIp}</span>
                  <span className="text-xs" style={{ color: "var(--muted)" }}>{a.eventCount} event{a.eventCount !== 1 ? "s" : ""}</span>
                  <span className="text-xs" style={{ color: "var(--muted-2)" }}>{timeAgo(a.createdAt)}</span>
                </div>
              </div>
            </div>
          ))}
          {filtered.length === 0 && (
            <div className="card text-center py-12" style={{ color: "var(--muted)" }}>
              {error ? "Alert history could not be retrieved." : "No persisted alerts match your filter."}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
