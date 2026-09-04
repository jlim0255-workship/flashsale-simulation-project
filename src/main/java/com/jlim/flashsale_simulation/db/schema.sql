CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    capacity INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE inventory (
    event_id BIGINT PRIMARY KEY REFERENCES events(id),
    available INT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id),
    user_id TEXT NOT NULL,
    idempotency_key TEXT,
    status TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_event ON orders(event_id);

INSERT INTO events (id, name, capacity) VALUES (1, 'Milestone One', 100);
INSERT INTO inventory (event_id, available) VALUES (1, 100);

SELECT setval('events_id_seq', 1);