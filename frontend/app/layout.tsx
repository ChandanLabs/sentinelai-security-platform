import type { Metadata } from "next";
import "./globals.css";
import { CrossGuardProvider } from "./state";
export const metadata: Metadata = { title: "CrossGuard — Security Activity Monitor", description: "Interactive cross-application security monitoring and investigation demo" };
export default function RootLayout({children}:{children:React.ReactNode}) { return <html lang="en"><body><CrossGuardProvider>{children}</CrossGuardProvider></body></html>; }
