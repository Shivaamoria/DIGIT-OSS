DROP TABLE IF EXISTS ilms_case;
DROP TABLE IF EXISTS ilms_case_auditlog;
DROP TABLE IF EXISTS ilms_act;
DROP TABLE IF EXISTS ilms_case_party;
DROP TABLE IF EXISTS ilms_advocate;
DROP TABLE IF EXISTS ilms_hearing;
DROP TABLE IF EXISTS ilms_hearing_auditlog;
DROP TABLE IF EXISTS ilms_judgement;
DROP TABLE IF EXISTS ilms_judgement_auditlog;
DROP TABLE IF EXISTS ilms_court;
DROP TABLE IF EXISTS ilms_document;
DROP TABLE IF EXISTS ilms_payment;
DROP TABLE IF EXISTS ilms_payment_auditlog;

CREATE TABLE IF NOT EXISTS  ilms_case(
	id character varying(64) NOT NULL,
	tenant_id character varying(64),
	case_number character varying(64) UNIQUE,
	cnr_number character varying(32) DEFAULT NULL,
    parent_case_id character varying(32) DEFAULT NULL,
    linked_cases jsonb,
    case_type character varying(32) DEFAULT NULL,
    case_category character varying(64) DEFAULT NULL,
	filing_number character varying(64) DEFAULT NULL,
	filing_date bigint DEFAULT NULL,
    registrtion_date bigint DEFAULT NULL,
    case_summary character varying(200) DEFAULT NULL,
    arising_details character varying(32) DEFAULT NULL,
    policy_or_nonpolicy_matter character varying(32) DEFAULT NULL,
	case_status character varying(64) DEFAULT NULL,
    case_stage character varying(64) DEFAULT NULL,
	case_sub_stage character varying(64) DEFAULT NULL,
	priority character varying(32) DEFAULT NULL,
	recommend_oic character varying(32) DEFAULT NULL,
	remarks character varying(200) DEFAULT NULL,
	additional_details jsonb,
	status character varying(64) DEFAULT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_ilms_case_id PRIMARY KEY (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_case  ON ilms_case
(
  id
);

CREATE INDEX  IF NOT EXISTS  index_case_number_ilms_case  ON ilms_case
(
  case_number
);

CREATE INDEX  IF NOT EXISTS  index_cnr_number_ilms_case  ON ilms_case
(
  cnr_number
);

CREATE TABLE IF NOT EXISTS  ilms_case_auditlog(
	id character varying(64) NOT NULL,
	tenant_id character varying(64),
	case_number character varying(64) DEFAULT NULL,
	cnr_number character varying(32) DEFAULT NULL,
    parent_case_id character varying(32) DEFAULT NULL,
    linked_cases jsonb,
    case_type character varying(32) DEFAULT NULL,
    case_category character varying(64) DEFAULT NULL,
	filing_number character varying(64) DEFAULT NULL,
	filing_date bigint DEFAULT NULL,
    registrtion_date bigint DEFAULT NULL,
    case_summary character varying(200) DEFAULT NULL,
    arising_details character varying(32) DEFAULT NULL,
    policy_or_nonpolicy_matter character varying(32) DEFAULT NULL,
	case_status character varying(64) DEFAULT NULL,
    case_stage character varying(64) DEFAULT NULL,
	case_sub_stage character varying(64) DEFAULT NULL,
	priority character varying(32) DEFAULT NULL,
	recommend_oic character varying(32) DEFAULT NULL,
	remarks character varying(200) DEFAULT NULL,
	additional_details jsonb,
	status character varying(64) DEFAULT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_case_auditlog  ON ilms_case_auditlog
(
  id
);


CREATE TABLE IF NOT EXISTS  ilms_act(
	id character varying(32) NOT NULL,
	case_id character varying(64) NOT NULL,
	name character varying(64) DEFAULT NULL,
	section_number character varying(64) DEFAULT NULL,
	status character varying(64) DEFAULT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
	CONSTRAINT pk_act_id PRIMARY KEY (id),
	CONSTRAINT fk_act_case_id FOREIGN KEY (case_id) REFERENCES ilms_case (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_act  ON ilms_act
(
   id
);
CREATE INDEX  IF NOT EXISTS  index_case_id_ilms_act  ON ilms_act
(
  case_id
);

---- To store the petioner and respondent information which can be identified using party_type column
CREATE TABLE IF NOT EXISTS  ilms_advocate(
	id character varying(32) NOT NULL,
    first_name character varying(32) DEFAULT NULL,
    last_name character varying(32) DEFAULT NULL,
	contact_number character varying(64) DEFAULT NULL,
	status character varying(64) DEFAULT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_advocate_id PRIMARY KEY (id)

);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_advocate  ON ilms_advocate
(
  id
);
CREATE TABLE IF NOT EXISTS  ilms_case_party(
	id character varying(32) NOT NULL,
	case_id character varying(64) NOT NULL,
	advocate_id character varying(64) NOT NULL,
    first_name character varying(32) DEFAULT NULL,
    last_name character varying(32) DEFAULT NULL,
    gender character varying(32) DEFAULT NULL,
    petitioner_type character varying(32) DEFAULT NULL,
    address character varying(32) DEFAULT NULL,
	department_name character varying(64) DEFAULT NULL,
	contact_number character varying(64) DEFAULT NULL,
    party_type character varying(32) DEFAULT NULL,
	status character varying(64) DEFAULT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_party_id PRIMARY KEY (id),
	CONSTRAINT fk_party_case_id FOREIGN KEY (case_id) REFERENCES ilms_case (id),
	CONSTRAINT fk_party_advocate_id FOREIGN KEY(advocate_id) REFERENCES ilms_advocate(id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_case_party  ON ilms_case_party
(
  id
);

CREATE INDEX  IF NOT EXISTS  index_case_id_ilms_case_party  ON ilms_case_party
(
  case_id
);





CREATE TABLE IF NOT EXISTS  ilms_hearing(
   id character varying(32) NOT NULL,
   hearing_number character varying(32) DEFAULT NULL,
   court_number character varying(32) DEFAULT NULL,
bench character varying(32) DEFAULT NULL,
   case_id character varying(32) NOT NULL,
    judge_name jsonb,
   hearing_date bigint,
   business_date bigint,
    hearing_purpose character varying(32)  DEFAULT NULL,
   required_officer character varying(32) DEFAULT NULL,
   affidavit_filing_date bigint,
   affidavit_filing_due_date bigint,
   case_number character varying(32) DEFAULT NULL,
   oath_number character varying(32),
   first_hearing_date bigint,
   previous_hearing_date bigint,
   next_hearing_date bigint,
   is_presence_required boolean,
   hearing_type character varying(32) DEFAULT NULL,
   department_officer character varying(32) DEFAULT NULL,
   remarks character varying(100) DEFAULT NULL,
   additional_details jsonb,
    status character varying(16)  DEFAULT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_hearing_id PRIMARY KEY (id),
   CONSTRAINT fk_hearing_case_id FOREIGN KEY (case_id) REFERENCES ilms_case (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_hearing  ON ilms_hearing
(
   id
);

CREATE INDEX  IF NOT EXISTS  index_case_id_ilms_hearing  ON ilms_hearing
(
   case_id
);

CREATE TABLE IF NOT EXISTS  ilms_hearing_auditlog(
    id character varying(32) NOT NULL,
   hearing_number character varying(32) DEFAULT NULL,
   court_number character varying(32) DEFAULT NULL,
bench character varying(32) DEFAULT NULL,
   case_id character varying(32) NOT NULL,
    judge_name jsonb,
   hearing_date bigint,
   business_date bigint,
    hearing_purpose character varying(32)  DEFAULT NULL,
   required_officer character varying(32) DEFAULT NULL,
   affidavit_filing_date bigint,
   affidavit_filing_due_date bigint,
   case_number character varying(32) DEFAULT NULL,
   oath_number character varying(32),
   first_hearing_date bigint,
   previous_hearing_date bigint,
   next_hearing_date bigint,
   is_presence_required boolean,
   hearing_type character varying(32) DEFAULT NULL,
   department_officer character varying(32) DEFAULT NULL,
   remarks character varying(100) DEFAULT NULL,
   additional_details jsonb,
    status character varying(16)  DEFAULT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_hearing_auditlog  ON ilms_hearing_auditlog
(
   id
);



CREATE TABLE IF NOT EXISTS  ilms_court(
    id character varying(32) NOT NULL,
    case_id character varying(32) NOT NULL,
    court_name character varying(32) DEFAULT NULL,
    district character varying(32) DEFAULT NULL,
    state character varying(32) DEFAULT NULL,
    division character varying(32) DEFAULT NULL,
    status character varying(16) DEFAULT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_court_id PRIMARY KEY (id),
    CONSTRAINT fk_ilms_court_case_id FOREIGN KEY (case_id) REFERENCES ilms_case (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_court  ON ilms_court
(
   id
);

CREATE INDEX  IF NOT EXISTS  index_case_id_ilms_court  ON ilms_court
(
   case_id
);

CREATE TABLE IF NOT EXISTS  ilms_payment(
   id character varying(32) NOT NULL,
   case_id character varying(32) NOT NULL,
   hearing_id character varying(32) NOT NULL,
   fine_imposed_date bigint,
   fine_due_date bigint,
   fine_amount character varying(32) DEFAULT NULL,
    status character varying(16) DEFAULT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_payment_id PRIMARY KEY (id),
   CONSTRAINT fk_hearing_case_id FOREIGN KEY (case_id) REFERENCES ilms_case (id),
   CONSTRAINT fk_payment_hearing_id FOREIGN KEY (hearing_id) REFERENCES ilms_hearing (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_payment ON ilms_payment
(
   id
);

CREATE INDEX  IF NOT EXISTS  index_case_id_ilms_payment ON ilms_payment
(
   case_id
);
CREATE INDEX  IF NOT EXISTS  index_hearing_id_ilms_payment  ON ilms_payment
(
   hearing_id
);

CREATE TABLE IF NOT EXISTS  ilms_payment_auditlog(
   id character varying(32) NOT NULL,
    case_id character varying(32) NOT NULL,
    hearing_id character varying(32) NOT NULL,
    fine_imposed_date bigint,
    fine_due_date bigint,
    fine_amount character varying(32) DEFAULT NULL,
    status character varying(16) DEFAULT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_payment_auditlog ON ilms_payment_auditlog
(
   id
);


CREATE TABLE IF NOT EXISTS  ilms_judgement(
	id character varying(32) NOT NULL,
	case_id character varying(32) NOT NULL,
    order_type character varying(32) DEFAULT NULL,
    order_date bigint NOT NULL,
	decision_status character varying(32) ,
	compliance_date bigint,
	revised_compliance_date bigint,
	order_no_override character varying(32) NOT NULL,
	revised_complaince_reason character varying(32) NOT NULL,
    compliance_status character varying(32) ,
	remarks character varying(64) NOT NULL,
	additional_details jsonb,
    status character varying(16) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint,
    CONSTRAINT pk_judgement_id PRIMARY KEY (id),
	CONSTRAINT fk_judgement_case_id FOREIGN KEY (case_id) REFERENCES ilms_case (id)
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_judgement  ON ilms_judgement
(
   id
);

CREATE INDEX  IF NOT EXISTS  index_case_id_ilms_judgement  ON ilms_judgement
(
   case_id
);

CREATE TABLE IF NOT EXISTS  ilms_judgement_auditlog(
   id character varying(32) NOT NULL,
       case_id character varying(32) NOT NULL,
        order_type character varying(32) DEFAULT NULL,
        order_date bigint NOT NULL,
       decision_status character varying(32) DEFAULT NULL,
       compliance_date bigint,
       revised_compliance_date bigint,
       order_no_override character varying(32) NOT NULL,
       revised_complaince_reason character varying(32) NOT NULL,
        compliance_status character varying(32) ,
       remarks character varying(64) NOT NULL,
       additional_details jsonb,
        status character varying(16) NOT NULL,
        createdby character varying(64),
        createdtime bigint,
        lastmodifiedby character varying(64),
        lastmodifiedtime bigint
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_judgement_auditlog  ON ilms_judgement_auditlog
(
   id
);

CREATE TABLE IF NOT EXISTS  ilms_document(
	id character varying(32) NOT NULL,
	case_id character varying(32) NOT NULL,
	document_id character varying(64) DEFAULT NULL,
	document_type character varying(64) NOT NULL,
	file_store_id character varying(64) NOT NULL,
	remarks character varying(64) DEFAULT NULL,
    status character varying(32) NOT NULL,
    createdby character varying(64),
    createdtime bigint,
    lastmodifiedby character varying(64),
    lastmodifiedtime bigint
);

CREATE INDEX  IF NOT EXISTS  index_id_ilms_document ON ilms_document
(
   id
);

