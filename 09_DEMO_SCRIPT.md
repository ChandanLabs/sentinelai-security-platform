# SentinelAI workspace activity demo

This walkthrough uses real authenticated actions inside SentinelAI's sample web workspace. Those requests are processed by the backend and stored as account-scoped events; no browser timer fabricates events. It does **not** monitor your computer, run commands, or observe other websites. Use a unique demo password and synthetic content only.

## Prepare

1. Start the stack using [10_DEPLOYMENT.md](./10_DEPLOYMENT.md), or open the shared hosted demo.
2. Open the frontend URL and create a throwaway demo account.
3. Keep the **Connected sample workspace** open. Use a test username and never use a password from another service.

## Perform actions and inspect results

1. Open a sample document. The workspace confirms that the backend recorded the read.
2. Select **Try unavailable document** three times. Each request is denied and recorded; the third event should show `REPEATED_DENIED_ACCESS`.
3. Select **Try restricted admin action**. The backend returns `403` and records `PRIVILEGED_ACCESS_DENIED`.
4. Select **Export list**. The CSV is returned and the backend records the export. Repeat three times within ten minutes to trigger `REPEATED_DATA_EXPORT`.
5. Open **Events** to inspect timestamps, outcomes, severity, rule evidence, and AI status.
6. Open **Alerts** to see grouped detections and **Incidents** to see linked lifecycle records. Advance an incident one status at a time; each transition is recorded as account activity.

## Demonstrate sign-in detection

1. Sign out.
2. Enter the account's exact username and make five incorrect password attempts within five minutes. This records failures against that known account and triggers `AUTH_BRUTE_FORCE` at the threshold.
3. Sign in with the correct password within five minutes. The successful sign-in should also trigger `FAILED_THEN_SUCCESS`.
4. Return to Events, Alerts, and Incidents to inspect the backend-created records.

Do not deliberately try passwords against another person's account. The controlled walkthrough uses only the account you created for this demo. The in-memory throttle permits at most eight failures per username during a 15-minute window.

## What to explain

- Activity comes from requests actually handled by the workspace backend.
- Detection is deterministic rule logic, not AI. The risk score is a severity mapping, not a probability.
- Events are scoped to the account; source IP is intentionally not collected.
- The AI service has a health endpoint only; inference is not implemented.
- The application does not monitor host processes, PowerShell, or third-party applications.
- This is a demo, not a production security product. Do not enter real credentials or private data.

## Cleanup

Use **Delete account and activity** in the workspace to remove the demo account and its owned documents, events, alerts, and incidents. This is preferable to deleting the entire local database volume. The scheduled retention job also removes events and their linked alerts/incidents after the configured retention period, but does not remove accounts or documents.
