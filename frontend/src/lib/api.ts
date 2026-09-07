export const API_BASE = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

type AuthResponse = { accessToken: string; refreshToken: string; expiresInSeconds: number; user: User };
export type User = { id: string; email: string; role: "ADMIN" | "ANALYST" | "VIEWER"; enabled: boolean };
export type Page<T> = { content: T[]; totalElements: number; totalPages: number; number: number; size: number };
export type Event = { id:string; eventSourceId:string; eventSourceName:string; eventType:string; severity:string; timestamp:string; sourceIp?:string; username?:string; message:string; metadata?:string; ingestedAt:string };
export type Risk = { id:string; eventId:string; score:number; level:string; ruleComponent:number; aiComponent:number; ruleCount:number; aiAvailable:boolean; explanation:string; calculatedAt:string };
export type Alert = { id:string; eventId:string; incidentId?:string; title:string; description:string; riskScore:number; riskLevel:string; status:string; createdAt:string; acknowledgedAt?:string; resolvedAt?:string };
export type Incident = { id:string; title:string; summary:string; severity:string; status:string; createdBy:string; createdAt:string; updatedAt:string; resolvedAt?:string };

const accessKey = "sentinelai.accessToken";
const refreshKey = "sentinelai.refreshToken";

export const tokenStore = {
  get access() { return typeof window === "undefined" ? null : localStorage.getItem(accessKey); },
  get refresh() { return typeof window === "undefined" ? null : localStorage.getItem(refreshKey); },
  save(a:string,r:string){ localStorage.setItem(accessKey,a); localStorage.setItem(refreshKey,r); },
  clear(){ localStorage.removeItem(accessKey); localStorage.removeItem(refreshKey); }
};

async function refreshAccessToken() {
  const refreshToken = tokenStore.refresh;
  if (!refreshToken) return false;
  const response = await fetch(`${API_BASE}/auth/refresh`, { method:"POST", headers:{"Content-Type":"application/json"}, body:JSON.stringify({refreshToken}) });
  if (!response.ok) { tokenStore.clear(); return false; }
  const data:AuthResponse = await response.json();
  tokenStore.save(data.accessToken, data.refreshToken);
  return true;
}

export async function api<T>(path:string, options:RequestInit = {}, retry = true):Promise<T> {
  const headers = new Headers(options.headers);
  headers.set("Content-Type", "application/json");
  const access = tokenStore.access;
  if (access) headers.set("Authorization", `Bearer ${access}`);
  const response = await fetch(`${API_BASE}${path}`, {...options, headers});
  if (response.status === 401 && retry && await refreshAccessToken()) return api<T>(path, options, false);
  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try { const body = await response.json(); message = body.message || body.error || message; } catch {}
    throw new Error(message);
  }
  if (response.status === 204) return undefined as T;
  return response.json();
}

export async function login(email:string,password:string){
  const data = await api<AuthResponse>("/auth/login", {method:"POST", body:JSON.stringify({email,password})}, false);
  tokenStore.save(data.accessToken,data.refreshToken); return data.user;
}
export async function register(email:string,password:string){
  await api<User>("/auth/register", {method:"POST", body:JSON.stringify({email,password})}, false);
  return login(email,password);
}
export async function logout(){
  const refreshToken=tokenStore.refresh;
  try { if(refreshToken) await api<void>("/auth/logout",{method:"POST",body:JSON.stringify({refreshToken})},false); } finally { tokenStore.clear(); }
}
