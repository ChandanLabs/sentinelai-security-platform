import { NextRequest, NextResponse } from "next/server";

export const dynamic = "force-dynamic";
export const runtime = "nodejs";

type RouteContext = { params: { path: string[] } };

async function proxy(request: NextRequest, { params }: RouteContext) {
  const backendUrl = process.env.BACKEND_URL;
  if (!backendUrl) {
    return NextResponse.json(
      { message: "Backend proxy is not configured." },
      { status: 503 },
    );
  }

  const path = params.path.map(encodeURIComponent).join("/");
  const target = new URL(`/api/${path}`, backendUrl);
  target.search = request.nextUrl.search;
  const headers = new Headers();
  for (const name of ["content-type", "accept", "x-csrf-token"]) {
    const value = request.headers.get(name);
    if (value) headers.set(name, value);
  }
  const sessionCookie = request.cookies.get("JSESSIONID");
  if (sessionCookie) headers.set("cookie", `JSESSIONID=${sessionCookie.value}`);

  const hasBody = request.method !== "GET" && request.method !== "HEAD";
  let response: Response;
  try {
    response = await fetch(target, {
      method: request.method,
      headers,
      body: hasBody ? await request.arrayBuffer() : undefined,
      cache: "no-store",
    });
  } catch {
    return NextResponse.json(
      { message: "Backend service is unavailable." },
      { status: 502 },
    );
  }

  const responseHeaders = new Headers();
  const contentType = response.headers.get("content-type");
  if (contentType) responseHeaders.set("content-type", contentType);
  const contentDisposition = response.headers.get("content-disposition");
  if (contentDisposition) responseHeaders.set("content-disposition", contentDisposition);
  const setCookie = response.headers.get("set-cookie");
  if (setCookie) responseHeaders.set("set-cookie", setCookie);
  return new NextResponse(response.body, {
    status: response.status,
    headers: responseHeaders,
  });
}

export const GET = proxy;
export const POST = proxy;
export const PATCH = proxy;
export const DELETE = proxy;
