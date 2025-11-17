-- Create Database
-- CREATE DATABASE lookups WITH ENCODING 'UTF8';


-- Create Schema
CREATE SCHEMA IF NOT EXISTS gepg;


CREATE TABLE gepg.currencies(
     currency_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
     currency_name VARCHAR(50) UNIQUE NOT NULL,
     currency_code varchar(10) UNIQUE NOT NULL,
     is_active BOOLEAN DEFAULT true NOT NULL,
     created_by VARCHAR(50),
     created_date TIMESTAMP DEFAULT NOW(),
     last_modified_by VARCHAR(50),
     last_modified_date TIMESTAMP
);


CREATE TABLE gepg.gfs_codes_level_one(
    gfs_code_level_one_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    gfs_code VARCHAR(50) UNIQUE NOT NULL,
    gfs_code_description TEXT,
    is_active BOOLEAN DEFAULT true NOT NULL,
    created_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT NOW(),
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP
);

CREATE TABLE gepg.gfs_codes_level_two(
     gfs_code_level_two_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
     gfs_code VARCHAR(50) UNIQUE NOT NULL,
     gfs_code_description TEXT,
     gfs_code_level_one_id UUID NOT NULL REFERENCES gepg.gfs_codes_level_one(gfs_code_level_one_id) ON DELETE CASCADE,
     is_active BOOLEAN DEFAULT true NOT NULL,
     created_by VARCHAR(50),
     created_date TIMESTAMP DEFAULT NOW(),
     last_modified_by VARCHAR(50),
     last_modified_date TIMESTAMP
);

CREATE TABLE gepg.gfs_codes_level_three(
     gfs_code_level_three_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
     gfs_code VARCHAR(50) UNIQUE NOT NULL,
     gfs_code_description TEXT,
     gfs_code_level_two_id UUID NOT NULL REFERENCES gepg.gfs_codes_level_two(gfs_code_level_two_id) ON DELETE CASCADE,
     is_active BOOLEAN DEFAULT true NOT NULL,
     created_by VARCHAR(50),
     created_date TIMESTAMP DEFAULT NOW(),
     last_modified_by VARCHAR(50),
     last_modified_date TIMESTAMP
);


CREATE TABLE gepg.regions(
    region_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    region_name VARCHAR(255) UNIQUE NOT NULL,
    created_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT NOW(),
    is_active BOOLEAN DEFAULT true NOT NULL,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP
);

CREATE TABLE gepg.districts(
     district_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
     district_name VARCHAR(255) UNIQUE NOT NULL,
     region_id UUID NOT NULL REFERENCES gepg.regions(region_id) ON DELETE CASCADE,
     is_active BOOLEAN DEFAULT true NOT NULL,
     created_by VARCHAR(50),
     created_date TIMESTAMP DEFAULT NOW(),
     last_modified_by VARCHAR(50),
     last_modified_date TIMESTAMP
);


CREATE TABLE gepg.sp_category(
     sp_category_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
     sp_category_name VARCHAR(50) UNIQUE NOT NULL,
     is_active BOOLEAN DEFAULT true NOT NULL,
     created_by VARCHAR(50),
     created_date TIMESTAMP DEFAULT NOW(),
     last_modified_by VARCHAR(50),
     last_modified_date TIMESTAMP
);

CREATE TABLE gepg.psp_category(
     psp_category_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
     psp_category_name VARCHAR(50) UNIQUE NOT NULL,
     is_active BOOLEAN DEFAULT true NOT NULL,
     created_by VARCHAR(50),
     created_date TIMESTAMP DEFAULT NOW(),
     last_modified_by VARCHAR(50),
     last_modified_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS gepg.outbox_message (
  id UUID PRIMARY KEY,
  aggregate_type VARCHAR(100),
  aggregate_id UUID,
  event_type VARCHAR(100),
  payload JSONB,
  published BOOLEAN DEFAULT FALSE NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  published_at TIMESTAMP WITH TIME ZONE
);


CREATE INDEX IF NOT EXISTS idx_outbox_published ON gepg.outbox_message(published, created_at);