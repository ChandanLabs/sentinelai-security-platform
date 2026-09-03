# Database Domain Model

*Note: This phase documents the planned domain model. Full database business logic and schemas are NOT implemented yet.*

## Planned Entities

- **User**: Represents individuals accessing the system.
- **Organization**: Logical grouping if multi-tenancy is implemented.
- **Role**: Defines user privileges (e.g., Admin, Analyst).
- **Permission**: Granular access rights.
- **Asset**: Protected devices, servers, or applications being monitored.
- **Application**: Software linked to an asset generating logs.
- **Event**: A normalized log entry or security event ingested by the system.
- **EventSource**: The origin of the event data.
- **Detection**: A rule or signature that flagged an event.
- **Anomaly**: An AI-detected outlier with its associated confidence score.
- **RiskScore**: Calculated score for an event or asset based on detections and anomalies.
- **Alert**: A notification triggered when risk scores exceed a threshold.
- **Incident**: A grouped set of alerts investigated by analysts.
- **AuditLog**: Internal system logs tracking user actions within the platform.
- **Notification**: Delivery mechanism details for alerts (e.g., email, webhook).
