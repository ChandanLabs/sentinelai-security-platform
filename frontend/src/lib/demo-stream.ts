import type { SecurityEvent } from "@/lib/api";

const STORAGE_KEY = "sentinel_simulated_events";
const UPDATED_EVENT = "sentinel:simulated-events-updated";

const EVENT_SCENARIOS: Omit<SecurityEvent, "id" | "timestamp">[] = [
  {
    eventType: "login_success",
    severity: "INFO",
    sourceIp: "192.0.2.24",
    username: "analyst-demo",
    message: "SIMULATED DEMO EVENT — Normal sign-in from a known workstation",
    riskScore: 8,
    detectionRule: "",
    aiAnalysisStatus: "NOT_RUN",
  },
  {
    eventType: "login_failed",
    severity: "MEDIUM",
    sourceIp: "198.51.100.42",
    username: "admin",
    message: "SIMULATED DEMO EVENT — First failed sign-in in the sequence",
    riskScore: 45,
    detectionRule: "",
    aiAnalysisStatus: "NOT_RUN",
  },
  {
    eventType: "login_failed",
    severity: "MEDIUM",
    sourceIp: "198.51.100.42",
    username: "admin",
    message: "SIMULATED DEMO EVENT — Repeated failed sign-in from the same source",
    riskScore: 58,
    detectionRule: "",
    aiAnalysisStatus: "NOT_RUN",
  },
  {
    eventType: "login_failed",
    severity: "HIGH",
    sourceIp: "198.51.100.42",
    username: "admin",
    message: "SIMULATED DEMO EVENT — Brute-force threshold example; review required",
    riskScore: 82,
    detectionRule: "SIMULATED_ONLY",
    aiAnalysisStatus: "NOT_RUN",
  },
  {
    eventType: "suspicious_command",
    severity: "CRITICAL",
    sourceIp: "203.0.113.19",
    username: "operator-demo",
    message: "SIMULATED DEMO EVENT — Example suspicious command requires investigation",
    riskScore: 96,
    detectionRule: "SIMULATED_ONLY",
    aiAnalysisStatus: "NOT_RUN",
  },
];

export function getSimulatedEvents(): SecurityEvent[] {
  if (typeof window === "undefined") return [];

  const saved = window.localStorage.getItem(STORAGE_KEY);
  if (!saved) return [];

  const events: unknown = JSON.parse(saved);
  if (!Array.isArray(events)) {
    throw new Error("Stored simulated event data is invalid.");
  }
  return events as SecurityEvent[];
}

export function addSimulatedEvent(sequence: number): SecurityEvent {
  const scenario = EVENT_SCENARIOS[sequence % EVENT_SCENARIOS.length];
  const event: SecurityEvent = {
    ...scenario,
    id: String(Date.now()),
    timestamp: new Date().toISOString(),
  };
  const events = [...getSimulatedEvents(), event].slice(-100);
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(events));
  window.dispatchEvent(new Event(UPDATED_EVENT));
  return event;
}

export function clearSimulatedEvents() {
  window.localStorage.removeItem(STORAGE_KEY);
  window.dispatchEvent(new Event(UPDATED_EVENT));
}

export function onSimulatedEventsUpdated(callback: () => void) {
  window.addEventListener(UPDATED_EVENT, callback);
  window.addEventListener("storage", callback);
  return () => {
    window.removeEventListener(UPDATED_EVENT, callback);
    window.removeEventListener("storage", callback);
  };
}
