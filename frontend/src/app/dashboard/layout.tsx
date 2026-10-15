"use client";

import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import Link from "next/link";
import {
  Shield, LayoutDashboard, Zap, Bell, AlertTriangle, BriefcaseBusiness, LogOut,
  ChevronRight, Wifi, WifiOff, Menu, X
} from "lucide-react";
import { api } from "@/lib/api";

const NAV = [
  { href: "/dashboard", label: "Overview", icon: LayoutDashboard },
  { href: "/dashboard/workspace", label: "Workspace", icon: BriefcaseBusiness },
  { href: "/dashboard/events", label: "Events", icon: Zap },
  { href: "/dashboard/alerts", label: "Alerts", icon: Bell },
  { href: "/dashboard/incidents", label: "Incidents", icon: AlertTriangle },
];

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [online, setOnline] = useState<boolean | null>(null);
  const [aiStatus, setAiStatus] = useState("Checking AI service");
  const [sidebarOpen, setSidebarOpen] = useState(false);

  useEffect(() => {
    let active = true;
    const checkHealth = async () => {
      try {
        const session = await api.me();
        if (!active) return;
        setUsername(session.username);
      } catch {
        if (active) router.replace("/");
        return;
      }
      try {
        const health = await api.health();
        if (!active) return;
        setOnline(health.status === "UP");
        setAiStatus(
          health.aiService === "AVAILABLE"
            ? "AI service up · inference not configured"
            : "AI service unavailable · inference not configured",
        );
      } catch {
        if (!active) return;
        setOnline(false);
        setAiStatus("AI service status unavailable");
      }
    };
    void checkHealth();
    const interval = window.setInterval(checkHealth, 15000);
    return () => {
      active = false;
      window.clearInterval(interval);
    };
  }, [router]);

  async function signOut() {
    try {
      await api.logout();
    } finally {
      router.replace("/");
    }
  }

  const Sidebar = () => (
    <aside
      className="flex flex-col h-full"
      style={{ background: "var(--surface)", borderRight: "1px solid var(--border)" }}>
      {/* Logo */}
      <div className="flex items-center gap-3 px-5 py-5" style={{ borderBottom: "1px solid var(--border)" }}>
        <div className="w-9 h-9 rounded-xl flex items-center justify-center shrink-0"
          style={{ background: "linear-gradient(135deg, #6366f1, #8b5cf6)", boxShadow: "0 0 20px rgba(99,102,241,0.3)" }}>
          <Shield className="w-5 h-5 text-white" />
        </div>
        <div>
          <span className="font-bold text-sm" style={{ color: "var(--text)" }}>
            Sentinel<span style={{ color: "var(--primary)" }}>AI</span>
          </span>
          <div className="flex items-center gap-1.5 mt-0.5">
            {online === null ? null : online ? (
              <><Wifi className="w-3 h-3" style={{ color: "var(--success)" }} /><span className="text-xs" style={{ color: "var(--success)" }}>API online</span></>
            ) : (
              <><WifiOff className="w-3 h-3 text-yellow-500" /><span className="text-xs text-yellow-500">API offline</span></>
            )}
          </div>
          <p className="text-[10px] mt-1 leading-tight" style={{ color: "var(--muted)" }}>{aiStatus}</p>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-3 py-4 flex flex-col gap-1">
        <p className="text-xs font-semibold uppercase tracking-widest px-3 mb-2" style={{ color: "var(--muted-2)" }}>
          Account: {username || "checking…"}
        </p>
        {NAV.map(({ href, label, icon: Icon }) => {
          const active = pathname === href;
          return (
            <Link key={href} href={href}
              onClick={() => setSidebarOpen(false)}
              className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-200 group"
              style={{
                background: active ? "rgba(99,102,241,0.15)" : "transparent",
                color: active ? "#a5b4fc" : "var(--muted)",
                border: active ? "1px solid rgba(99,102,241,0.3)" : "1px solid transparent",
              }}>
              <Icon className="w-4 h-4 shrink-0" />
              {label}
              {active && <ChevronRight className="w-3 h-3 ml-auto opacity-60" />}
            </Link>
          );
        })}
      </nav>

      <div className="px-5 py-4 text-xs leading-relaxed"
        style={{ borderTop: "1px solid var(--border)", color: "var(--muted)" }}>
        Account-scoped sample workspace · server-recorded actions
      </div>
      <button onClick={() => void signOut()} className="btn-ghost mx-4 mb-4 justify-center">
        <LogOut className="w-4 h-4" /> Sign out
      </button>
    </aside>
  );

  return (
    <div className="min-h-screen" style={{ background: "var(--bg)" }}>
      {/* Mobile header */}
      <div className="lg:hidden flex items-center justify-between px-4 py-3"
        style={{ background: "var(--surface)", borderBottom: "1px solid var(--border)" }}>
        <div className="flex items-center gap-2">
          <Shield className="w-6 h-6" style={{ color: "var(--primary)" }} />
          <span className="font-bold text-sm">SentinelAI</span>
        </div>
        <button onClick={() => setSidebarOpen(!sidebarOpen)}
          className="p-2 rounded-lg" style={{ color: "var(--muted)" }}>
          {sidebarOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
        </button>
      </div>

      {/* Mobile sidebar overlay */}
      {sidebarOpen && (
        <div className="lg:hidden fixed inset-0 z-50 flex">
          <div className="w-64 h-full"><Sidebar /></div>
          <div className="flex-1 bg-black/50" onClick={() => setSidebarOpen(false)} />
        </div>
      )}

      {/* Desktop layout */}
      <div className="hidden lg:grid" style={{ gridTemplateColumns: "240px 1fr", minHeight: "100vh" }}>
        <div className="sticky top-0 h-screen"><Sidebar /></div>
        <main className="overflow-auto">{children}</main>
      </div>

      {/* Mobile main */}
      <main className="lg:hidden">{children}</main>
    </div>
  );
}
