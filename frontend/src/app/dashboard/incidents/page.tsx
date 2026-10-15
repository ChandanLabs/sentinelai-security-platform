"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { AlertTriangle, RefreshCw, ChevronRight } from "lucide-react";
import { api, type Incident } from "@/lib/api";

const STATUS_CLASS: Record<string, string> = {
  OPEN: "badge-open", INVESTIGATING: "badge-investigating",
  RESOLVED: "badge-resolved", CLOSED: "badge-closed",
};

const LIFECYCLE: Incident["status"][] = ["OPEN", "INVESTIGATING", "RESOLVED", "CLOSED"];

function timeAgo(ts: string) {
  const diff = Date.now() - new Date(ts).getTime();
  const m = Math.floor(diff / 60000);
  if (m < 60) return `${m}m ago`;
  const h = Math.floor(m / 60);
  if (h < 24) return `${h}h ago`;
  return `${Math.floor(h / 24)}d ago`;
}

export default function IncidentsPage() {
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [updatingId, setUpdatingId] = useState("");

  async function load(showLoading = true) {
    if (showLoading) setLoading(true);
    try {
      const data = await api.getIncidents(0, 20);
      setIncidents(data.content);
      setError("");
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Could not load incidents.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
    const interval = window.setInterval(() => void load(false), 5000);
    return () => window.clearInterval(interval);
  }, []);

  async function advanceIncident(incident: Incident) {
    const nextStatus = LIFECYCLE[LIFECYCLE.indexOf(incident.status) + 1];
    if (!nextStatus) return;
    setUpdatingId(incident.id);
    setError("");
    try {
      await api.updateIncidentStatus(incident.id, nextStatus);
      await load(false);
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Could not update incident.");
    } finally {
      setUpdatingId("");
    }
  }

  const openCount = incidents.filter(i => i.status === "OPEN").length;
  const investigatingCount = incidents.filter(i => i.status === "INVESTIGATING").length;

  return (
    <div className="p-6 flex flex-col gap-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold flex items-center gap-2" style={{ color: "var(--text)" }}>
            <AlertTriangle className="w-6 h-6 text-red-400" />
            Incidents
          </h1>
          <p className="text-sm mt-0.5" style={{ color: "var(--muted)" }}>
            {openCount} open · {investigatingCount} investigating
          </p>
        </div>
        <button onClick={() => void load()} className="btn-ghost" disabled={loading}>
          <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
        </button>
      </div>
      {error && (
        <div role="alert" className="card flex items-center justify-between gap-4 text-sm"
          style={{ borderColor: "rgba(248,113,113,0.4)", color: "#fca5a5" }}>
          <span>Could not load persisted incidents: {error}</span>
          <button onClick={() => void load()} className="btn-ghost shrink-0">Retry</button>
        </div>
      )}

      {/* Lifecycle legend */}
      <div className="card flex items-center gap-2 flex-wrap py-3" style={{ padding: "0.75rem 1.25rem" }}>
        <span className="text-xs font-semibold uppercase tracking-widest mr-2" style={{ color: "var(--muted)" }}>Lifecycle:</span>
        {LIFECYCLE.map((s, i) => (
          <div key={s} className="flex items-center gap-1">
            <span className={`badge ${STATUS_CLASS[s]}`}>{s}</span>
            {i < LIFECYCLE.length - 1 && <ChevronRight className="w-3 h-3" style={{ color: "var(--muted-2)" }} />}
          </div>
        ))}
      </div>

      {/* Incidents */}
      {loading ? (
        <div className="flex items-center justify-center h-40" style={{ color: "var(--muted)" }}>
          <RefreshCw className="w-5 h-5 animate-spin mr-2" /> Loading incidents…
        </div>
      ) : (
        <div className="flex flex-col gap-4">
          {incidents.map(inc => {
            const idx = LIFECYCLE.indexOf(inc.status);
            return (
              <div key={inc.id} id={inc.id} className="card flex flex-col gap-3"
                style={{
                  borderLeft: `3px solid ${inc.severity === "CRITICAL" ? "#f87171" : "#fb923c"}`,
                  boxShadow: inc.status === "OPEN" || inc.status === "INVESTIGATING"
                    ? "0 0 20px rgba(239,68,68,0.08)" : "none"
                }}>
                {/* Top row */}
                <div className="flex items-start gap-3">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 flex-wrap mb-1">
                      <span className="badge badge-critical">{inc.severity}</span>
                      <span className={`badge ${STATUS_CLASS[inc.status]}`}>{inc.status}</span>
                      <span className="text-xs font-mono" style={{ color: "var(--muted)" }}>INC-{String(inc.id).padStart(4, "0")}</span>
                    </div>
                    <h2 className="text-sm font-semibold leading-snug" style={{ color: "var(--text)" }}>{inc.title}</h2>
                  </div>
                </div>

                {/* Meta */}
                <div className="flex items-center gap-4 flex-wrap text-xs" style={{ color: "var(--muted)" }}>
                  <span>📅 Created {timeAgo(inc.createdAt)}</span>
                  <span>🔔 {inc.alertCount} alert{inc.alertCount !== 1 ? "s" : ""}</span>
                  {inc.alertId && (
                    <Link href={`/dashboard/alerts#${inc.alertId}`} className="font-mono text-indigo-300 hover:text-indigo-200">
                      Alert {inc.alertId}
                    </Link>
                  )}
                  <span>🔄 Updated {timeAgo(inc.updatedAt)}</span>
                </div>

                {/* Progress bar */}
                <div className="flex items-center gap-2">
                  {LIFECYCLE.map((s, i) => (
                    <div key={s} className="flex items-center gap-2 flex-1">
                      <div className="flex-1 h-1 rounded-full transition-all duration-500"
                        style={{ background: i <= idx ? (inc.severity === "CRITICAL" ? "#f87171" : "#fb923c") : "var(--border)" }} />
                      {i < LIFECYCLE.length - 1 && null}
                    </div>
                  ))}
                </div>
                <div className="flex gap-1">
                  {LIFECYCLE.map((s, i) => (
                    <span key={s} className="text-xs flex-1 text-center"
                      style={{ color: i <= idx ? "var(--text)" : "var(--muted-2)", fontWeight: i === idx ? 600 : 400 }}>
                      {s}
                    </span>
                  ))}
                </div>

                <div className="flex items-center justify-between gap-3 pt-2"
                  style={{ borderTop: "1px solid var(--border)" }}>
                  <span className="text-xs" style={{ color: "var(--muted)" }}>
                    Status transitions are recorded for your account.
                  </span>
                  {idx < LIFECYCLE.length - 1 && (
                    <button className="btn-ghost text-xs" disabled={updatingId === inc.id}
                      onClick={() => void advanceIncident(inc)}>
                      {updatingId === inc.id ? "Updating…" : `Advance to ${LIFECYCLE[idx + 1]}`}
                    </button>
                  )}
                </div>
              </div>
            );
          })}

          {incidents.length === 0 && (
            <div className="card text-center py-16 flex flex-col items-center gap-3">
              <AlertTriangle className="w-10 h-10" style={{ color: "var(--muted)" }} />
              <p style={{ color: "var(--muted)" }}>
                {error ? "Incident history could not be retrieved." : "No persisted incidents yet."}
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
