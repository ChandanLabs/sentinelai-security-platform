-- DEMO-ONLY helper.
-- Register the account through POST /api/v1/auth/register first.
-- Then run this statement against the SentinelAI database to promote that
-- synthetic demo account to ADMIN. Never use this mechanism for production.
-- Replace the email with the synthetic demo account you created.

UPDATE users
SET role = 'ADMIN', enabled = TRUE
WHERE lower(email) = lower('demo-admin@example.com');
