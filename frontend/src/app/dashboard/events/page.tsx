"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Zap, Search, RefreshCw } from "lucide-react";
import { api, type SecurityEvent } from "@/lib/api";

const SEV_CLASS: Record<string, string> = {
  INFO: "badge-info", LOW: "badge-low", MEDIUM: "badge-medium",
  HIGH: "badge-high", CRITICAL: "badge-critical",
};

function RiskBar({ score }: { score: number }) {
  const color = score >= 75 ? "#f87171" : score >= 50 ? "#fb923c" : score >= 25 ? "#facc15" : "#4ade80";
  return (
    <div className="flex items-center gap-2 min-w-[90px]">
      <div className="risk-bar flex-1">
        <div className="risk-fill" style={{ width: `${score}%`, background: color }} />
      </div>
      <span className="text-xs font-mono font-bold w-6 text-right" style={{ color }}>{score}</span>
    </div>
  );
}

export default function EventsPage() {
  const [events, setEvents] = useState<SecurityEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("");
  const [sevFilter, setSevFilter] = useState("ALL");
  const [error, setError] = useState("");

  async function load(showLoading = true) {
    if (showLoading) setLoading(true);
    try {
      const data = await api.getEvents(0, 50);
      setEvents(data.content);
      setError("");
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Could not load events.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
    const interval = window.setInterval(() => void load(false), 5000);
    return () => {
      window.clearInterval(interval);
    };
  }, []);

  const filtered = events.filter(e => {
    const matchSev = sevFilter === "ALL" || e.severity === sevFilter;
    const q = filter.toLowerCase();
    const matchQ = !q || e.eventType.toLowerCase().includes(q) || e.sourceIp.includes(q)
      || e.username.toLowerCase().includes(q) || e.message.toLowerCase().includes(q);
    return matchSev && matchQ;
  });

  function fmt(ts: string) {
    return new Date(ts).toLocaleString("en-IN", { hour12: false, timeZone: "Asia/Kolkata" });
  }

  return (
    <div className="p-6 flex flex-col gap-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold flex items-center gap-2" style={{ color: "var(--text)" }}>
            <Zap className="w-6 h-6" style={{ color: "#60a5fa" }} />
            Security Events
          </h1>
          <p className="text-sm mt-0.5" style={{ color: "var(--muted)" }}>
            {filtered.length} events{filter || sevFilter !== "ALL" ? " (filtered)" : ""}
          </p>
        </div>
        <button onClick={() => void load()} className="btn-ghost" disabled={loading}>
          <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
        </button>
      </div>

      <p className="text-xs" style={{ color: "var(--muted)" }}>
        Events below are recorded from actions processed by the backend for your account. The browser does not generate or submit trusted detection events.
      </p>
      {error && (
        <div role="alert" className="card flex items-center justify-between gap-4 text-sm"
          style={{ borderColor: "rgba(248,113,113,0.4)", color: "#fca5a5" }}>
          <span>Could not load persisted backend events: {error}</span>
          <button onClick={() => void load()} className="btn-ghost shrink-0">Retry</button>
        </div>
      )}

      {/* Filters */}
      <div className="flex flex-wrap gap-3">
        <div className="relative flex-1 min-w-[200px]">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4" style={{ color: "var(--muted)" }} />
          <input className="input pl-9" placeholder="Search events, IPs, users…"
            value={filter} onChange={e => setFilter(e.target.value)} />
        </div>
        <div className="flex gap-2 flex-wrap">
          {["ALL", "INFO", "LOW", "MEDIUM", "HIGH", "CRITICAL"].map(s => (
            <button key={s} onClick={() => setSevFilter(s)}
              className={`px-3 py-2 rounded-lg text-xs font-semibold uppercase tracking-wide transition-all ${sevFilter === s ? "bg-indigo-600 text-white" : "text-slate-400 hover:text-white hover:bg-white/5"}`}>
              {s}
            </button>
          ))}
        </div>
      </div>

      {/* Table */}
      <div className="card" style={{ padding: 0, overflow: "hidden" }}>
        {loading ? (
          <div className="flex items-center justify-center h-40" style={{ color: "var(--muted)" }}>
            <RefreshCw className="w-5 h-5 animate-spin mr-2" /> Loading events…
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th><th>Activity</th><th>Target</th><th>Outcome</th><th>Severity</th>
                  <th>Risk</th><th>Detection rule and evidence</th><th>Actor</th><th>AI analysis</th><th>Timestamp</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(ev => (
                  <tr key={ev.id}>
                    <td className="font-mono text-xs">
                      <Link href={`/dashboard/events/${ev.id}`} className="text-indigo-300 hover:text-indigo-200">
                        {ev.id.slice(0, 8)}
                      </Link>
                    </td>
                    <td className="font-mono text-xs font-medium" style={{ color: "#93c5fd" }}>{ev.eventType}</td>
                    <td className="font-mono text-xs" style={{ color: "var(--muted)" }}>{ev.targetResource || "—"}</td>
                    <td><span className={`badge ${ev.outcome === "DENIED" ? "badge-critical" : "badge-low"}`}>{ev.outcome || "—"}</span></td>
                    <td><span className={`badge ${SEV_CLASS[ev.severity]}`}>{ev.severity}</span></td>
                    <td><RiskBar score={ev.riskScore} /></td>
                    <td className="font-mono text-xs" style={{ color: ev.detectionRule ? "#fb923c" : "var(--muted)" }}>
                      <span>{ev.detectionRule || "none"}</span>
                      {ev.detectionEvidence && <span className="block font-sans mt-1">{ev.detectionEvidence}</span>}
                    </td>
                    <td className="font-mono text-xs" style={{ color: "var(--text)" }}>{ev.actorId || ev.username}</td>
                    <td className="text-xs" style={{ color: "var(--muted)" }}>{ev.aiAnalysisStatus}</td>
                    <td className="text-xs font-mono whitespace-nowrap" style={{ color: "var(--muted-2)" }}>{fmt(ev.timestamp)}</td>
                  </tr>
                ))}
                {filtered.length === 0 && (
                  <tr><td colSpan={10} className="text-center py-12" style={{ color: "var(--muted)" }}>
                    {error ? "Backend history could not be retrieved." : "No persisted events match your filter yet."}
                  </td></tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
