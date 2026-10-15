// Browser requests are proxied by Next.js using a runtime-only backend URL.
const BASE_URL = "";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options?.headers,
    },
    ...options,
    cache: "no-store",
  });
  if (!res.ok) {
    const details = await res.json().catch(() => null);
    throw new Error(details?.message || `${res.status} ${res.statusText}`);
  }
  return res.json();
}

export const api = {
  health: () =>
    request<{ status: string; aiService: string; aiInference: string }>("/api/v1/health"),

  getStats: () => request<DashboardStats>("/api/v1/dashboard/stats"),

  getEvents: (page = 0, size = 20) =>
    request<Paginated<SecurityEvent>>(`/api/v1/events?page=${page}&size=${size}`),

  getEvent: (id: string) => request<SecurityEvent>(`/api/v1/events/${id}`),

  getAlerts: (page = 0, size = 20) =>
    request<Paginated<Alert>>(`/api/v1/alerts?page=${page}&size=${size}`),

  getIncidents: (page = 0, size = 20) =>
    request<Paginated<Incident>>(`/api/v1/incidents?page=${page}&size=${size}`),
};

export interface DashboardStats {
  totalEvents: number;
  openAlerts: number;
  activeIncidents: number;
  criticalEvents: number;
  riskTrend: { date: string; score: number }[];
  severityBreakdown: { severity: string; count: number }[];
}

export interface SecurityEvent {
  id: string;
  eventType: string;
  severity: "INFO" | "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
  timestamp: string;
  sourceIp: string;
  username: string;
  message: string;
  riskScore: number;
  detectionRule?: string;
  aiAnalysisStatus?: string;
  alertId?: string | null;
  incidentId?: string | null;
}

export interface Alert {
  id: string;
  title: string;
  severity: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
  status: "OPEN" | "ACKNOWLEDGED" | "RESOLVED";
  createdAt: string;
  sourceIp: string;
  eventCount: number;
  eventId?: string;
  detectionRule?: string;
}

export interface Incident {
  id: string;
  title: string;
  severity: "HIGH" | "CRITICAL";
  status: "OPEN" | "INVESTIGATING" | "RESOLVED" | "CLOSED";
  createdAt: string;
  updatedAt: string;
  alertCount: number;
  alertId?: string;
}

export interface Paginated<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
}
