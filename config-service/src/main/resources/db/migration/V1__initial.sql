-- lookup_def
CREATE TABLE lookup_def (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  code text NOT NULL UNIQUE,
  name text,
  description text,
  data_type text NOT NULL,
  default_scope text NOT NULL DEFAULT 'GLOBAL',
  schema_json jsonb,
  created_by text,
  created_at timestamptz DEFAULT now()
);

-- lookup_value (versioned)
CREATE TABLE lookup_value (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  lookup_id UUID NOT NULL REFERENCES lookup_def(id) ON DELETE CASCADE,
  tenant_id text,
  key text NOT NULL,
  value jsonb NOT NULL,
  metadata jsonb,
  effective_from timestamptz DEFAULT now(),
  effective_to timestamptz,
  version int NOT NULL DEFAULT 1,
  active boolean NOT NULL DEFAULT true,
  created_by text,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now(),
  CONSTRAINT ux_lookup_key_tenant UNIQUE (lookup_id, key, tenant_id, version)
);
CREATE INDEX idx_lookup_key ON lookup_value (lookup_id, key);
CREATE INDEX idx_lookup_tenant ON lookup_value (tenant_id);

-- outbox (for reliable event publishing)
CREATE TABLE outbox_message (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  aggregate_type text,
  aggregate_id UUID,
  event_type text,
  payload jsonb,
  published boolean NOT NULL DEFAULT false,
  created_at timestamptz DEFAULT now(),
  published_at timestamptz
);

-- ledger_events for auditing (optional)
CREATE TABLE ledger_event (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  aggregate_type text,
  aggregate_id UUID,
  event_type text,
  payload jsonb,
  created_at timestamptz DEFAULT now()
);
