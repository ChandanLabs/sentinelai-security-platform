"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { ArrowLeft, Bell, RefreshCw, ShieldAlert } from "lucide-react";
import { api, type SecurityEvent } from "@/lib/api";

export default function EventDetailPage({ params }: { params: { id: string } }) {
  const [event, setEvent] = useState<SecurityEvent | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    try {
      setEvent(await api.getEvent(params.id));
      setError("");
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Could not load event details.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
  }, [params.id]);

  return (
    <div className="p-6 flex flex-col gap-6">
      <Link href="/dashboard/events" className="btn-ghost w-fit">
        <ArrowLeft className="w-4 h-4" /> Back to events
      </Link>
      <div>
        <h1 className="text-2xl font-bold" style={{ color: "var(--text)" }}>Event details</h1>
        <p className="text-sm mt-1 font-mono break-all" style={{ color: "var(--muted)" }}>{params.id}</p>
      </div>
      {loading && (
        <div className="card flex items-center gap-2" style={{ color: "var(--muted)" }}>
          <RefreshCw className="w-4 h-4 animate-spin" /> Loading event…
        </div>
      )}
      {error && (
        <div role="alert" className="card flex items-center justify-between gap-4"
          style={{ borderColor: "rgba(248,113,113,0.4)", color: "#fca5a5" }}>
          <span>Could not load persisted event: {error}</span>
          <button onClick={() => void load()} className="btn-ghost shrink-0">Retry</button>
        </div>
      )}
      {event && (
        <section className="card grid sm:grid-cols-2 gap-5">
          {[
            ["Event type", event.eventType],
            ["Severity", event.severity],
            ["Server timestamp", new Date(event.timestamp).toLocaleString()],
            ["Outcome", event.outcome || "Not available"],
            ["Actor (pseudonymous)", event.actorId || event.username],
            ["Session tag", event.sessionId || "Not available for this action"],
            ["Target resource", event.targetResource || "Not applicable"],
            ["Risk score", String(event.riskScore)],
            ["Detection rule", event.detectionRule || "No deterministic rule matched"],
            ["Rule evidence", event.detectionEvidence || "No detection rule matched."],
            ["AI analysis", event.aiAnalysisStatus || "Status not returned"],
          ].map(([label, value]) => (
            <div key={label}>
              <p className="text-xs uppercase tracking-wider" style={{ color: "var(--muted)" }}>{label}</p>
              <p className="mt-1 font-mono text-sm break-all" style={{ color: "var(--text)" }}>{value}</p>
            </div>
          ))}
          {event.alertId && (
            <div>
              <p className="text-xs uppercase tracking-wider" style={{ color: "var(--muted)" }}>Associated alert</p>
              <Link href={`/dashboard/alerts#${event.alertId}`} className="mt-1 inline-flex items-center gap-2 font-mono text-sm text-indigo-300 hover:text-indigo-200">
                <Bell className="w-4 h-4" /> {event.alertId}
              </Link>
            </div>
          )}
          {event.incidentId && (
            <div>
              <p className="text-xs uppercase tracking-wider" style={{ color: "var(--muted)" }}>Associated incident</p>
              <Link href={`/dashboard/incidents#${event.incidentId}`} className="mt-1 inline-flex items-center gap-2 font-mono text-sm text-indigo-300 hover:text-indigo-200">
                <ShieldAlert className="w-4 h-4" /> {event.incidentId}
              </Link>
            </div>
          )}
          <div className="sm:col-span-2">
            <p className="text-xs uppercase tracking-wider" style={{ color: "var(--muted)" }}>Event message</p>
            <p className="mt-1 text-sm break-words" style={{ color: "var(--text)" }}>{event.message}</p>
          </div>
        </section>
      )}
    </div>
  );
}
