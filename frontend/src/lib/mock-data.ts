// Mock data used when the backend is unreachable (demo mode)
import type { DashboardStats, SecurityEvent, Alert, Incident } from "./api";

export const MOCK_STATS: DashboardStats = {
  totalEvents: 31,
  openAlerts: 6,
  activeIncidents: 2,
  criticalEvents: 4,
  riskTrend: [
    { date: "Mon", score: 22 },
    { date: "Tue", score: 38 },
    { date: "Wed", score: 55 },
    { date: "Thu", score: 71 },
    { date: "Fri", score: 95 },
    { date: "Sat", score: 62 },
    { date: "Sun", score: 47 },
  ],
  severityBreakdown: [
    { severity: "INFO", count: 14 },
    { severity: "LOW", count: 5 },
    { severity: "MEDIUM", count: 6 },
    { severity: "HIGH", count: 3 },
    { severity: "CRITICAL", count: 3 },
  ],
};

export const MOCK_EVENTS: SecurityEvent[] = [
  { id: "fixture-1", eventType: "login_success", severity: "INFO", timestamp: "2026-10-14T08:01:00Z", sourceIp: "192.168.1.10", username: "alice@sentinel.local", message: "Successful authentication", riskScore: 5 },
  { id: "fixture-2", eventType: "login_failed", severity: "MEDIUM", timestamp: "2026-10-14T08:12:00Z", sourceIp: "203.0.113.77", username: "unknown", message: "Invalid password attempt", riskScore: 45 },
  { id: "fixture-3", eventType: "login_failed", severity: "MEDIUM", timestamp: "2026-10-14T08:12:30Z", sourceIp: "203.0.113.77", username: "unknown", message: "Invalid password attempt", riskScore: 45 },
  { id: "fixture-4", eventType: "login_failed", severity: "HIGH", timestamp: "2026-10-14T08:13:00Z", sourceIp: "203.0.113.77", username: "admin", message: "AUTH_BRUTE_FORCE: 5 failed attempts in 5 min", riskScore: 78 },
  { id: "fixture-5", eventType: "login_failed", severity: "HIGH", timestamp: "2026-10-14T08:13:15Z", sourceIp: "203.0.113.77", username: "admin", message: "Brute force pattern continues", riskScore: 82 },
  { id: "fixture-6", eventType: "login_failed", severity: "HIGH", timestamp: "2026-10-14T08:13:30Z", sourceIp: "203.0.113.77", username: "admin", message: "Brute force pattern continues", riskScore: 85 },
  { id: "fixture-7", eventType: "privilege_escalation", severity: "CRITICAL", timestamp: "2026-10-14T09:05:00Z", sourceIp: "10.0.0.44", username: "bob@sentinel.local", message: "PRIVILEGE_ESCALATION detected — sudo -i executed", riskScore: 95 },
  { id: "fixture-8", eventType: "suspicious_command", severity: "HIGH", timestamp: "2026-10-14T09:10:00Z", sourceIp: "10.0.0.44", username: "bob@sentinel.local", message: "SUSPICIOUS_COMMAND: wget from external host", riskScore: 77 },
  { id: "fixture-9", eventType: "login_success", severity: "INFO", timestamp: "2026-10-14T10:00:00Z", sourceIp: "192.168.1.22", username: "carol@sentinel.local", message: "Normal login", riskScore: 5 },
  { id: "fixture-10", eventType: "login_success", severity: "INFO", timestamp: "2026-10-14T10:15:00Z", sourceIp: "192.168.1.23", username: "dave@sentinel.local", message: "Normal login", riskScore: 5 },
  { id: "fixture-11", eventType: "login_success", severity: "INFO", timestamp: "2026-10-14T11:00:00Z", sourceIp: "192.168.1.24", username: "eve@sentinel.local", message: "Normal login", riskScore: 5 },
  { id: "fixture-12", eventType: "critical_event_burst", severity: "CRITICAL", timestamp: "2026-10-14T12:00:00Z", sourceIp: "203.0.113.77", username: "unknown", message: "CRITICAL_EVENT_BURST: 3 critical events in 10 min", riskScore: 97 },
];

export const MOCK_ALERTS: Alert[] = [
  { id: "fixture-alert-1", title: "Brute Force Attack from 203.0.113.77", severity: "HIGH", status: "OPEN", createdAt: "2026-10-14T08:14:00Z", sourceIp: "203.0.113.77", eventCount: 5 },
  { id: "fixture-alert-2", title: "Privilege Escalation — bob@sentinel.local", severity: "CRITICAL", status: "OPEN", createdAt: "2026-10-14T09:05:10Z", sourceIp: "10.0.0.44", eventCount: 2 },
  { id: "fixture-alert-3", title: "Suspicious Command Execution", severity: "HIGH", status: "ACKNOWLEDGED", createdAt: "2026-10-14T09:10:05Z", sourceIp: "10.0.0.44", eventCount: 1 },
  { id: "fixture-alert-4", title: "Critical Event Burst 203.0.113.77", severity: "CRITICAL", status: "OPEN", createdAt: "2026-10-14T12:00:10Z", sourceIp: "203.0.113.77", eventCount: 3 },
  { id: "fixture-alert-5", title: "High Risk Anomaly Detected (AI)", severity: "HIGH", status: "OPEN", createdAt: "2026-10-14T13:30:00Z", sourceIp: "172.16.0.5", eventCount: 4 },
  { id: "fixture-alert-6", title: "Repeated Auth Failure — admin account", severity: "HIGH", status: "OPEN", createdAt: "2026-10-14T14:00:00Z", sourceIp: "203.0.113.77", eventCount: 6 },
];

export const MOCK_INCIDENTS: Incident[] = [
  { id: "fixture-incident-1", title: "Active Brute Force + Privilege Escalation Campaign", severity: "CRITICAL", status: "INVESTIGATING", createdAt: "2026-10-14T09:06:00Z", updatedAt: "2026-10-14T10:00:00Z", alertCount: 3 },
  { id: "fixture-incident-2", title: "Critical Event Burst — Possible Intrusion from 203.0.113.77", severity: "CRITICAL", status: "OPEN", createdAt: "2026-10-14T12:01:00Z", updatedAt: "2026-10-14T12:01:00Z", alertCount: 2 },
];
