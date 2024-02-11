INSERT INTO
   "account" ("id", "account_number", "agency", "balance", "client_document", "document_type", "last_date_time_transfer", "last_transfer_value", "status")
VALUES
	(2, '1000002', '0099', 0.000, '00000000000', 'CPF', '2024-02-10 20:38:31.056013', 0.000, 'INACTIVE'),
	(1, '1000001', '0099', 8200.000, '00000000001', 'CPF', '2024-02-11 00:06:18.988042', 900.000, 'ACTIVE'),
	(3, '3000001', '0105', 6800.000, '00000000002', 'CPF', NULL, NULL, 'ACTIVE');

INSERT INTO
   "client_cache" ("id", "document", "document_type", "name", "version")
VALUES
	(1, '00000000002', 'CPF', 'Exemplo 02 da Silva', 'V1'),
	(2, '00000000001', 'CPF', 'Exemplo 01 da Silva', 'V1');

INSERT INTO
   "version_cache" ("version", "active")
VALUES
	('V1', 'A');
