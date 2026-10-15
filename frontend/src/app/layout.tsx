import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "SentinelAI — Security Monitoring Platform",
  description: "Intelligent Security Monitoring & Anomaly Detection Platform by Chandan Kumar Sah Teli",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
