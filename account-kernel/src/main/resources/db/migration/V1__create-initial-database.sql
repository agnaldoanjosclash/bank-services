CREATE TABLE IF NOT EXISTS "account" (
	"id" BIGINT NOT NULL,
	"account_number" VARCHAR(7) NOT NULL,
	"agency" CHAR(4) NOT NULL,
	"balance" NUMERIC(10,3) NOT NULL,
	"client_document" VARCHAR(14) NOT NULL,
	"document_type" VARCHAR(8) NOT NULL,
	"last_date_time_transfer" TIMESTAMP NULL DEFAULT NULL,
	"last_transfer_value" NUMERIC(10,3) NULL DEFAULT NULL,
	"status" VARCHAR(8) NOT NULL,
	PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "operation" (
	"transaction_id" VARCHAR(255) NOT NULL,
	"operation_date" TIMESTAMP NOT NULL,
	"operation_type" VARCHAR(8) NOT NULL,
	"value" NUMERIC(10,3) NOT NULL,
	"source_account_id" BIGINT NULL DEFAULT NULL,
	"target_account_id" BIGINT NOT NULL,
	PRIMARY KEY ("transaction_id")
);

CREATE TABLE IF NOT EXISTS "transaction" (
	"id" BIGINT NOT NULL,
	"description" VARCHAR(512) NOT NULL,
	"operation_type" VARCHAR(8) NOT NULL,
	"success" CHAR(1) NOT NULL,
	"transaction_date_time" TIMESTAMP NOT NULL,
	"transaction_id" VARCHAR(255) NOT NULL,
	PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "client_cache" (
	"id" BIGINT NOT NULL,
	"document" VARCHAR(14) NOT NULL,
	"document_type" VARCHAR(8) NOT NULL,
	"name" VARCHAR(128) NOT NULL,
	"version" VARCHAR(255) NULL DEFAULT NULL,
	PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "version_cache" (
	"version" VARCHAR(255) NOT NULL,
	"active" CHAR(1) NOT NULL,
	PRIMARY KEY ("version")
);
