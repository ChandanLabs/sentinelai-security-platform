// Browser requests are proxied by Next.js using a runtime-only backend URL.
const BASE_URL = "";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const headers = new Headers({ "Content-Type": "application/json" });
  new Headers(options?.headers).forEach((value, name) => headers.set(name, value));
  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
    cache: "no-store",
  });
  if (!res.ok) {
    const raw = await res.text();
    let message = raw;
    try {
      const details = JSON.parse(raw);
      message = details.message || details.detail || raw;
    } catch {
      // Non-JSON error bodies are surfaced as text.
    }
    throw new Error(message || `${res.status} ${res.statusText}`);
  }
  if (res.status === 204) return undefined as T;
  return res.json();
}

async function csrfToken(): Promise<string> {
  const result = await request<{ token: string }>("/api/v1/auth/csrf");
  return result.token;
}

async function protectedRequest<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = await csrfToken();
  return request<T>(path, {
    ...options,
    headers: { ...options.headers, "X-CSRF-Token": token },
  });
}

export const api = {
  health: () =>
    request<{ status: string; aiService: string; aiInference: string }>("/api/v1/health"),

  register: (username: string, password: string) =>
    protectedRequest<AuthResponse>("/api/v1/auth/register", {
      method: "POST", body: JSON.stringify({ username, password }),
    }),

  login: (username: string, password: string) =>
    protectedRequest<AuthResponse>("/api/v1/auth/login", {
      method: "POST", body: JSON.stringify({ username, password }),
    }),

  me: () => request<AuthResponse>("/api/v1/auth/me"),

  logout: () => protectedRequest<void>("/api/v1/auth/logout", { method: "POST" }),

  deleteAccount: () =>
    protectedRequest<void>("/api/v1/auth/account", { method: "DELETE" }),

  getStats: () => request<DashboardStats>("/api/v1/dashboard/stats"),

  getEvents: (page = 0, size = 20) =>
    request<Paginated<SecurityEvent>>(`/api/v1/events?page=${page}&size=${size}`),

  getEvent: (id: string) => request<SecurityEvent>(`/api/v1/events/${id}`),

  getAlerts: (page = 0, size = 20) =>
    request<Paginated<Alert>>(`/api/v1/alerts?page=${page}&size=${size}`),

  getIncidents: (page = 0, size = 20) =>
    request<Paginated<Incident>>(`/api/v1/incidents?page=${page}&size=${size}`),

  getDocuments: () => request<WorkspaceDocument[]>("/api/v1/workspace/documents"),

  getDocument: (id: string) =>
    request<WorkspaceDocument>(`/api/v1/workspace/documents/${encodeURIComponent(id)}`),

  tryPrivilegedAction: () =>
    protectedRequest<void>("/api/v1/workspace/admin", { method: "POST" }),

  updateIncidentStatus: (id: string, status: Incident["status"]) =>
    protectedRequest<Incident>(`/api/v1/incidents/${encodeURIComponent(id)}/status`, {
      method: "PATCH", body: JSON.stringify({ status }),
    }),

  exportDocuments: async () => {
    const token = await csrfToken();
    const response = await fetch("/api/v1/workspace/export", {
      method: "POST",
      headers: { "X-CSRF-Token": token },
      cache: "no-store",
    });
    if (!response.ok) {
      const text = await response.text();
      throw new Error(text || `${response.status} ${response.statusText}`);
    }
    return response.blob();
  },
};

export interface AuthResponse {
  userId: string;
  username: string;
}

export interface WorkspaceDocument {
  id: string;
  title: string;
  body: string;
}

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
  actorId?: string;
  sessionId?: string | null;
  targetResource?: string | null;
  outcome?: string | null;
  detectionEvidence?: string | null;
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
