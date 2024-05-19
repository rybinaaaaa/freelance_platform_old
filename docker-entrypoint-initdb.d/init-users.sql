CREATE ROLE readonly;

CREATE ROLE readwrite;

CREATE USER app_user WITH PASSWORD 'password';
GRANT readwrite TO app_user;

CREATE USER notification_user WITH PASSWORD 'password';
GRANT readonly TO notification_user;

GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly;
GRANT CREATE, SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO readwrite;