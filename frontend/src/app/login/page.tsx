"use client";
import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { login, register } from "@/lib/api";
import { ShieldCheck, Loader2 } from "lucide-react";

export default function LoginPage(){
 const router=useRouter(); const [mode,setMode]=useState<"login"|"register">("login"); const [email,setEmail]=useState(""); const [password,setPassword]=useState(""); const [error,setError]=useState(""); const [loading,setLoading]=useState(false);
 async function submit(e:FormEvent){e.preventDefault();setError("");setLoading(true);try{mode==="login"?await login(email,password):await register(email,password);router.replace("/dashboard");}catch(err){setError(err instanceof Error?err.message:"Authentication failed");}finally{setLoading(false);}}
 return <main className="min-h-screen grid place-items-center px-6 bg-grid"><div className="w-full max-w-md panel p-8">
   <div className="flex items-center gap-3 mb-8"><div className="h-11 w-11 rounded-xl bg-primary/20 grid place-items-center"><ShieldCheck className="text-primary"/></div><div><div className="text-xl font-bold">SentinelAI</div><div className="text-xs text-muted">Security Operations Console</div></div></div>
   <h1 className="text-2xl font-semibold">{mode==="login"?"Sign in":"Create analyst account"}</h1><p className="text-sm text-muted mt-1 mb-6">{mode==="login"?"Access your security monitoring workspace.":"New accounts are created with the VIEWER role."}</p>
   {error&&<div className="mb-4 rounded-lg border border-danger/40 bg-danger/10 px-4 py-3 text-sm text-red-300">{error}</div>}
   <form onSubmit={submit} className="space-y-4"><label className="block text-sm">Email<input className="field mt-2" type="email" required value={email} onChange={e=>setEmail(e.target.value)} placeholder="analyst@company.local"/></label><label className="block text-sm">Password<input className="field mt-2" type="password" required minLength={8} value={password} onChange={e=>setPassword(e.target.value)} placeholder="••••••••"/></label><button disabled={loading} className="btn-primary w-full">{loading?<Loader2 className="animate-spin mx-auto" size={18}/>:mode==="login"?"Sign in":"Create account"}</button></form>
   <button className="mt-5 text-sm text-muted hover:text-text w-full" onClick={()=>{setMode(mode==="login"?"register":"login");setError("")}}>{mode==="login"?"Need an account? Create one":"Already registered? Sign in"}</button>
 </div></main>
}
