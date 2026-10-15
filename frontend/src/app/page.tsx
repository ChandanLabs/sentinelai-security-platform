"use client";

import { useRouter } from "next/navigation";
import { Shield, ArrowRight } from "lucide-react";

export default function LoginPage() {
  const router = useRouter();

  return (
    <div className="min-h-screen flex items-center justify-center relative overflow-hidden"
      style={{ background: "var(--bg)" }}>

      {/* Animated background grid */}
      <div className="absolute inset-0 opacity-[0.03]"
        style={{ backgroundImage: "linear-gradient(var(--border) 1px, transparent 1px), linear-gradient(90deg, var(--border) 1px, transparent 1px)", backgroundSize: "40px 40px" }} />

      {/* Glow orbs */}
      <div className="absolute top-1/4 left-1/4 w-96 h-96 rounded-full blur-3xl opacity-10"
        style={{ background: "radial-gradient(circle, #6366f1, transparent)" }} />
      <div className="absolute bottom-1/4 right-1/4 w-64 h-64 rounded-full blur-3xl opacity-8"
        style={{ background: "radial-gradient(circle, #ef4444, transparent)" }} />

      {/* Login card */}
      <div className="relative w-full max-w-md mx-4">
        <div className="card" style={{ border: "1px solid rgba(99,102,241,0.3)", boxShadow: "0 0 60px rgba(99,102,241,0.1), 0 25px 50px rgba(0,0,0,0.5)" }}>

          {/* Logo */}
          <div className="flex flex-col items-center gap-3 mb-8">
            <div className="w-16 h-16 rounded-2xl flex items-center justify-center relative"
              style={{ background: "linear-gradient(135deg, #6366f1, #8b5cf6)", boxShadow: "0 0 30px rgba(99,102,241,0.4)" }}>
              <Shield className="w-8 h-8 text-white" />
              <div className="absolute -top-1 -right-1 w-3 h-3 rounded-full bg-green-400 border-2"
                style={{ borderColor: "var(--surface)", animation: "pulse 2s infinite" }} />
            </div>
            <div className="text-center">
              <h1 className="text-2xl font-bold tracking-tight" style={{ color: "var(--text)" }}>
                Sentinel<span style={{ color: "var(--primary)" }}>AI</span>
              </h1>
              <p className="text-xs mt-1" style={{ color: "var(--muted)" }}>
                Intelligent Security Monitoring Platform
              </p>
            </div>
          </div>

          <p className="text-sm text-center leading-relaxed mb-5" style={{ color: "var(--muted)" }}>
            Public interview demo. The console displays synthetic security activity;
            do not submit real credentials or sensitive data.
          </p>
          <div className="mt-6 p-3 rounded-lg text-center"
            style={{ background: "rgba(99,102,241,0.08)", border: "1px solid rgba(99,102,241,0.2)" }}>
            <p className="text-xs" style={{ color: "var(--muted)" }}>
              Dashboard data is public and read-only. Event submission and incident status
              changes require a server-side demo key that is never sent to this page.
            </p>
          </div>

          <button
            id="open-demo-btn"
            type="button"
            onClick={() => router.push("/dashboard")}
            className="btn-primary justify-center mt-5 py-3 w-full"
          >
            <span>Open public dashboard</span><ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
