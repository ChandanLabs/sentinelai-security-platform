"use client";

import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Download, FileText, LockKeyhole, RefreshCw, ShieldAlert } from "lucide-react";
import { api, type WorkspaceDocument } from "@/lib/api";

export default function WorkspacePage() {
  const router = useRouter();
  const [documents, setDocuments] = useState<WorkspaceDocument[]>([]);
  const [selected, setSelected] = useState<WorkspaceDocument | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState("");

  const loadDocuments = useCallback(async () => {
    setBusy(true);
    setError("");
    try {
      setDocuments(await api.getDocuments());
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Could not load your workspace.");
    } finally {
      setBusy(false);
    }
  }, []);

  useEffect(() => {
    void loadDocuments();
  }, [loadDocuments]);

  async function openDocument(id: string) {
    setBusy(true);
    setError("");
    setResult("");
    try {
      setSelected(await api.getDocument(id));
      setResult("Document read was recorded by the backend.");
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Document could not be opened.");
    } finally {
      setBusy(false);
    }
  }

  async function attemptDeniedAccess() {
    setBusy(true);
    setError("");
    try {
      await api.getDocument("00000000-0000-4000-8000-000000000001");
    } catch {
      setResult("The backend denied the unavailable document request and recorded the outcome.");
    } finally {
      setBusy(false);
    }
  }

  async function attemptAdminAction() {
    setBusy(true);
    setError("");
    try {
      await api.tryPrivilegedAction();
      setResult("Unexpected: the administrative operation was accepted.");
    } catch {
      setResult("The backend denied the administrative operation and recorded it.");
    } finally {
      setBusy(false);
    }
  }

  async function downloadExport() {
    setBusy(true);
    setError("");
    try {
      const blob = await api.exportDocuments();
      const url = URL.createObjectURL(blob);
      const anchor = document.createElement("a");
      anchor.href = url;
      anchor.download = "workspace-documents.csv";
      anchor.click();
      URL.revokeObjectURL(url);
      setResult("Your account's document list was exported; the backend recorded the export.");
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Export failed.");
    } finally {
      setBusy(false);
    }
  }

  async function deleteAccount() {
    if (!window.confirm("Permanently delete this account, its documents, events, alerts, and incidents?")) return;
    setBusy(true);
    setError("");
    try {
      await api.deleteAccount();
      router.replace("/");
    } catch (reason) {
      setError(reason instanceof Error ? reason.message : "Could not delete this account.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="p-6 flex flex-col gap-6">
      <header>
        <h1 className="text-2xl font-bold" style={{ color: "var(--text)" }}>Connected sample workspace</h1>
        <p className="text-sm mt-1" style={{ color: "var(--muted)" }}>
          These buttons perform real authenticated API actions. Each outcome is timestamped, stored,
          and evaluated on the server; no browser timer fabricates events.
        </p>
      </header>

      <section className="card">
        <h2 className="text-sm font-semibold mb-2" style={{ color: "var(--text)" }}>Live detection walkthrough</h2>
        <ol className="list-decimal pl-5 text-sm space-y-1" style={{ color: "var(--muted)" }}>
          <li>Sign out, then make five unsuccessful sign-in attempts against this account within five minutes.</li>
          <li>Sign in with the correct password. The dashboard records the attempts and can alert on repeated failures and failed-then-success.</li>
          <li>Open a document, try an unavailable document, attempt the restricted admin action, or export your document list.</li>
          <li>Open Events, Alerts, and Incidents to review persisted evidence and rule decisions.</li>
        </ol>
        <p className="text-xs mt-3" style={{ color: "var(--muted)" }}>
          Login failures are visible only to the account they target. Unknown usernames are not stored as account activity.
        </p>
      </section>

      {error && <p role="alert" className="card text-sm text-red-300">{error}</p>}
      {result && <p role="status" className="card text-sm text-emerald-300">{result}</p>}

      <section className="card">
        <div className="flex items-center justify-between gap-3 mb-4">
          <h2 className="font-semibold" style={{ color: "var(--text)" }}>Your documents</h2>
          <div className="flex flex-wrap gap-2">
            <button className="btn-ghost text-xs" disabled={busy} onClick={() => void loadDocuments()}>
              <RefreshCw className={`w-4 h-4 ${busy ? "animate-spin" : ""}`} /> Refresh
            </button>
            <button className="btn-primary text-xs" disabled={busy} onClick={() => void downloadExport()}>
              <Download className="w-4 h-4" /> Export list
            </button>
          </div>
        </div>
        {documents.length === 0 && !busy ? (
          <p className="text-sm" style={{ color: "var(--muted)" }}>No documents are available for this account.</p>
        ) : (
          <div className="grid md:grid-cols-2 gap-3">
            {documents.map(document => (
              <article key={document.id} className="rounded-lg p-4"
                style={{ border: "1px solid var(--border)", background: "var(--bg)" }}>
                <div className="flex items-center gap-2">
                  <FileText className="w-4 h-4 text-indigo-300" />
                  <h3 className="text-sm font-medium" style={{ color: "var(--text)" }}>{document.title}</h3>
                </div>
                <button className="btn-ghost text-xs mt-3" disabled={busy}
                  onClick={() => void openDocument(document.id)}>
                  Open document
                </button>
              </article>
            ))}
          </div>
        )}
      </section>

      {selected && (
        <article className="card">
          <h2 className="font-semibold" style={{ color: "var(--text)" }}>{selected.title}</h2>
          <p className="text-sm mt-3 leading-relaxed" style={{ color: "var(--muted)" }}>{selected.body}</p>
        </article>
      )}

      <section className="card">
        <div className="flex items-center gap-2 mb-2">
          <ShieldAlert className="w-4 h-4 text-orange-300" />
          <h2 className="font-semibold" style={{ color: "var(--text)" }}>Controlled denied-action tests</h2>
        </div>
        <p className="text-xs mb-4" style={{ color: "var(--muted)" }}>
          These make actual authenticated requests. They do not execute operating-system commands or touch another account&apos;s data.
        </p>
        <div className="flex flex-wrap gap-2">
          <button className="btn-ghost text-xs" disabled={busy} onClick={() => void attemptDeniedAccess()}>
            <FileText className="w-4 h-4" /> Try unavailable document
          </button>
          <button className="btn-ghost text-xs" disabled={busy} onClick={() => void attemptAdminAction()}>
            <LockKeyhole className="w-4 h-4" /> Try restricted admin action
          </button>
        </div>
      </section>

      <section className="card flex flex-wrap items-center justify-between gap-3"
        style={{ borderColor: "rgba(248,113,113,0.35)" }}>
        <div>
          <h2 className="text-sm font-semibold text-red-200">Delete demo account</h2>
          <p className="text-xs mt-1" style={{ color: "var(--muted)" }}>
            Permanently removes this account and its documents, events, alerts, and incidents.
          </p>
        </div>
        <button className="btn-ghost text-xs text-red-200" disabled={busy}
          onClick={() => void deleteAccount()}>
          Delete account and activity
        </button>
      </section>
    </div>
  );
}
