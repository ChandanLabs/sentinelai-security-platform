"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { ArrowRight, Shield } from "lucide-react";
import { api } from "@/lib/api";

export default function LoginPage() {
  const router = useRouter();
  const [mode, setMode] = useState<"login" | "register">("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setBusy(true);
    setError("");
    try {
      if (mode === "register") await api.register(username, password);
      else await api.login(username, password);
      router.push("/dashboard/workspace");
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Could not sign in.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="min-h-screen flex items-center justify-center px-4 py-10"
      style={{ background: "var(--bg)" }}>
      <section className="card w-full max-w-lg"
        style={{ border: "1px solid rgba(99,102,241,0.3)", boxShadow: "0 0 60px rgba(99,102,241,0.1)" }}>
        <div className="flex flex-col items-center gap-3 mb-7">
          <div className="w-14 h-14 rounded-2xl flex items-center justify-center"
            style={{ background: "linear-gradient(135deg, #6366f1, #8b5cf6)" }}>
            <Shield className="w-7 h-7 text-white" />
          </div>
          <div className="text-center">
            <h1 className="text-2xl font-bold" style={{ color: "var(--text)" }}>
              Sentinel<span style={{ color: "var(--primary)" }}>AI</span>
            </h1>
            <p className="text-sm mt-1" style={{ color: "var(--muted)" }}>
              Application security activity demo
            </p>
          </div>
        </div>

        <p className="text-sm leading-relaxed mb-6" style={{ color: "var(--muted)" }}>
          Actions you perform in this sample workspace are recorded by the backend and shown in your
          account-scoped security dashboard. This does not monitor your computer or other websites.
        </p>

        <div className="flex gap-2 mb-5" role="tablist" aria-label="Account action">
          {(["login", "register"] as const).map(value => (
            <button key={value} type="button" role="tab" aria-selected={mode === value}
              onClick={() => { setMode(value); setError(""); }}
              className={mode === value ? "btn-primary flex-1 justify-center" : "btn-ghost flex-1 justify-center"}>
              {value === "login" ? "Sign in" : "Create account"}
            </button>
          ))}
        </div>

        <form onSubmit={submit} className="flex flex-col gap-4">
          <label className="text-sm" style={{ color: "var(--muted)" }}>
            Username
            <input className="input mt-1" autoComplete="username" required minLength={3} maxLength={32}
              value={username} onChange={event => setUsername(event.target.value)} />
          </label>
          <label className="text-sm" style={{ color: "var(--muted)" }}>
            Password
            <input className="input mt-1" type="password"
              autoComplete={mode === "login" ? "current-password" : "new-password"}
              required minLength={12} maxLength={72} value={password}
              onChange={event => setPassword(event.target.value)} />
            <span className="block text-xs mt-1">Use 12–72 printable characters without spaces. Passwords are hashed and never added to activity events.</span>
          </label>
          {error && <p role="alert" className="text-sm text-red-300">{error}</p>}
          <button disabled={busy} type="submit" className="btn-primary justify-center py-3">
            {busy ? "Please wait…" : mode === "login" ? "Sign in to workspace" : "Create isolated workspace"}
            {!busy && <ArrowRight className="w-4 h-4" />}
          </button>
        </form>

        <p className="text-xs mt-6 leading-relaxed" style={{ color: "var(--muted)" }}>
          For demonstration only. Use a unique test password and synthetic content; this is not a
          production identity provider. Each account has a separate document workspace and activity history.
        </p>
      </section>
    </main>
  );
}
