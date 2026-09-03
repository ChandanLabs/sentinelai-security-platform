export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-24">
      <div className="z-10 w-full max-w-5xl items-center justify-between font-mono text-sm flex">
        <p className="fixed left-0 top-0 flex w-full justify-center border-b border-gray-800 bg-surface pb-6 pt-8 backdrop-blur-2xl lg:static lg:w-auto lg:rounded-xl lg:border lg:p-4">
          SentinelAI&nbsp;
          <code className="font-mono font-bold">Phase 1 Foundation</code>
        </p>
      </div>

      <div className="relative flex place-items-center my-32">
        <h1 className="text-4xl font-bold tracking-tight sm:text-6xl text-primary">
          Security Monitoring Console
        </h1>
      </div>

      <div className="mb-32 grid text-center lg:max-w-5xl lg:w-full lg:mb-0 lg:grid-cols-4 lg:text-left gap-4">
        <div className="group rounded-lg border border-transparent px-5 py-4 transition-colors hover:border-gray-800 hover:bg-surface">
          <h2 className="mb-3 text-2xl font-semibold text-success">
            Events{" "}
            <span className="inline-block transition-transform group-hover:translate-x-1 motion-reduce:transform-none">
              -&gt;
            </span>
          </h2>
          <p className="m-0 max-w-[30ch] text-sm text-muted">
            View ingested security and application events.
          </p>
        </div>
        
        <div className="group rounded-lg border border-transparent px-5 py-4 transition-colors hover:border-gray-800 hover:bg-surface">
          <h2 className="mb-3 text-2xl font-semibold text-warning">
            Alerts{" "}
            <span className="inline-block transition-transform group-hover:translate-x-1 motion-reduce:transform-none">
              -&gt;
            </span>
          </h2>
          <p className="m-0 max-w-[30ch] text-sm text-muted">
            Review alerts generated from anomalies.
          </p>
        </div>

        <div className="group rounded-lg border border-transparent px-5 py-4 transition-colors hover:border-gray-800 hover:bg-surface">
          <h2 className="mb-3 text-2xl font-semibold text-danger">
            Incidents{" "}
            <span className="inline-block transition-transform group-hover:translate-x-1 motion-reduce:transform-none">
              -&gt;
            </span>
          </h2>
          <p className="m-0 max-w-[30ch] text-sm text-muted">
            Investigate and manage security incidents.
          </p>
        </div>
      </div>
    </main>
  );
}
