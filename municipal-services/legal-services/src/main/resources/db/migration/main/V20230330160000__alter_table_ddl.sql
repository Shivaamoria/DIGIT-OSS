ALTER TABLE ilms_hearing ADD COLUMN tenant_id character varying(64) DEFAULT NULL;
ALTER TABLE ilms_hearing_auditlog ADD COLUMN tenant_id character varying(64) DEFAULT NULL;

ALTER TABLE ilms_judgement ADD COLUMN tenant_id character varying(64) DEFAULT NULL;
ALTER TABLE ilms_judgement_auditlog ADD COLUMN tenant_id character varying(64) DEFAULT NULL;
