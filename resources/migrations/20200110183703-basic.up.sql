-- :disable-transaction
create table if not exists "public"."access" (
"id" integer not null,
"name" text not null,
"desc" text);
--;;
create table if not exists "public"."aff_doc" (
"id" uuid not null,
"aff_id" uuid not null,
"name" text not null,
"filename" text not null,
"type" text not null,
"url" text not null,
"verified" boolean not null default '0',
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."affiliate" (
"id" uuid not null,
"code" text not null,
"name" text not null,
"overview" text,
"entity_type" text not null,
"industry_id" integer not null,
"aff_type" text not null,
"reg_no" text not null,
"tax_no" text,
"email" text,
"website" text,
"date_est" date not null,
"lang" text default 'en_GB',
"status" text not null default 'ACTIVE',
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."announce_aff" (
"ann_id" uuid not null,
"aff_id" uuid not null);
--;;
create table if not exists "public"."announcement" (
"id" uuid not null,
"title" text not null,
"content" text not null,
"mode" text not null,
"audience" text not null,
"recur_type" text,
"date_published" timestamp,
"expiry_date" timestamp,
"last_announced" timestamp,
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."audit_log" (
"id" uuid not null,
"category" text not null,
"actor" text not null,
"action" text not null,
"username" text not null,
"organization" text,
"subject" text,
"subject_desc" text,
"origin" text not null,
"origin_country" text,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."bank_acc" (
  "id" uuid not null,
  "aff_id" uuid not null,
  "name" text not null,
  "acc_no" text not null,
  "bank" text not null,
  "swift_code" text,
  "currency_id" char(3) not null,
  "branch" text not null,
  "bank_proof" text,
  "version" integer not null default 0,
  "date_created" timestamp not null default now(),
  "last_updated" timestamp not null default now());
--;;
create table if not exists "public"."contact" (
  "id" uuid not null,
  "country_code" varchar(4) not null,
  "area_code" varchar(5),
  "contact" varchar(16) not null,
  "type" text,
  "primal" boolean not null default '1',
  "version" integer not null default 0,
  "date_created" timestamp not null default now(),
  "last_updated" timestamp not null default now());
--;;
create table if not exists "public"."country" (
  "id" char(2) not null,
  "name" text not null);
--;;
create table if not exists "public"."currency" (
  "id" char(3) not null,
  "name" text not null);
--;;
create table if not exists "public"."industry" (
  "id" integer not null,
  "name" text not null);
--;;
create table if not exists "public"."language" (
  "code" text not null,
  "name" text not null,
  "eng_name" text not null);
--;;
create table if not exists "public"."loc_contact" (
"loc_id" uuid not null,
"contact_id" uuid not null);
--;;
create table if not exists "public"."location" (
  "id" uuid not null,
  "aff_id" uuid not null,
  "code" text,
  "name" text not null,
  "line1" text not null,
  "line2" text,
  "line3" text,
  "city" text not null,
  "postcode" text not null,
  "state" text,
  "country_id" char(2) not null,
  "type" text not null,
  "latitude" real,
  "longitude" real,
  "shipping" boolean not null default '0',
  "billing" boolean not null default '0',
  "invalid" boolean not null default '0',
  "version" integer not null default 0,
  "date_created" timestamp not null default now(),
  "last_updated" timestamp not null default now());
--;;
create table if not exists "public"."module" (
  "id" integer not null,
  "parent_id" integer,
  "code" text not null,
  "ordinal" integer not null,
  "access" text,
  "url" text not null,
  "menu_item" boolean);
--;;
create table if not exists "public"."password_history" (
  "id" uuid not null,
  "user_id" uuid not null,
  "password" text,
  "version" integer not null default 0,
  "date_created" timestamp not null default now(),
  "last_updated" timestamp not null default now());
--;;
create table if not exists "public"."password_policy" (
  "id" integer not null,
  "min_length" integer not null default 4,
  "max_age" integer not null default 0,
  "hist_size" integer not null default 0,
  "lock_period" integer not null default 0,
  "max_attempts" integer not null default 0,
  "aff_id" uuid not null,
  "version" integer not null default 0,
  "date_created" timestamp not null default now(),
  "last_updated" timestamp not null default now());
--;;
create table if not exists "public"."registration" (
  "id" integer not null,
  "aff_name" text not null,
  "entity_type" text not null,
  "reg_no" text not null,
  "first_name" text not null,
  "last_name" text not null,
  "email" text not null,
  "token" text not null,
  "version" integer not null default 0,
  "date_created" timestamp not null default now(),
  "last_updated" timestamp not null default now());
--;;
create table if not exists "public"."role"(
  "id" uuid not null,
  "aff_id" uuid not null,
  "name" text not null,
  "desc" text,
  "created_by" text,
  "version" integer not null default 0,
  "date_created" timestamp not null default now(),
  "last_updated" timestamp not null default now());
--;;
create table if not exists "public"."role_access"(
  "role_id" uuid not null,
  "access_id" integer not null);
--;;
create sequence if not exists "public"."seq_pwd_policy" as integer start 1;
--;;
create sequence if not exists "public"."seq_registration" as integer start 1;
--;;
create table if not exists "public"."user" (
  "id" uuid not null,
  "aff_id" uuid not null,
  "username" text not null,
  "password" text not null,
  "first_name" text not null,
  "last_name" text not null,
  "email" text not null,
  "designation" text,
  "lang" text not null default 'en_GB',
  "primal" boolean not null default '0',
  "enabled" boolean not null default '1',
  "locked_at" timestamp,
  "last_login" timestamp,
  "last_pwd_reset" timestamp,
  "version" integer not null default 0,
  "date_created" timestamp not null default now(),
  "last_updated" timestamp not null default now());
--;;
create table if not exists "public"."user_contact" (
  "user_id" uuid not null,
  "contact_id" uuid not null);
--;;
create table if not exists "public"."user_role"(
  "user_id" uuid not null,
  "role_id" uuid not null);
--;;
create table if not exists "public"."quotation" (
"id" uuid not null,
"quote_no" text not null,
"value" decimal(12,2),
"status" text not null default 'DRAFT',
"date_issued" timestamp not null default now(),
"issued_to" uuid not null,
"issued_by" uuid not null,
"cat_id" integer not null,
"sub_cat_id" integer not null,
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."party" (
"id" uuid not null,
"aff_name" text not null,
"user_name" text,
"user_email" text,
"loc_name" text not null,
"line1" text not null,
"line2" text,
"line3" text,
"city" text not null,
"postcode" text not null,
"state" text,
"country_id" char(2) not null,
"contact" varchar(20),
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create sequence if not exists "public"."seq_category" as integer start 1;
--;;
create sequence if not exists "public"."seq_sub_category" as integer start 1;
--;;
create table if not exists "public"."category" (
"id" integer not null,
"name" text not null,
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."sub_category" (
"id" integer not null,
"cat_id" integer not null,
"name" text not null,
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."quote_item" (
"id" uuid not null,
"quote_id" uuid not null,
"desc" text not null,
"recurring" boolean not null default '0',
"recur_type" char(1),
"unit_price" decimal(12,2),
"quantity" integer not null,
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."quote_misc" (
"id" uuid not null,
"quote_id" uuid not null,
"desc" text not null,
"value" decimal(12,2),
"percent" boolean not null default '0',
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."quote_discount" (
"id" uuid not null,
"quote_id" uuid not null,
"desc" text not null,
"value" decimal(12,2),
"percent" boolean not null default '0',
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."work_order" (
"id" uuid not null,
"wo_no" text not null,
"value" decimal(12,2),
"status" text not null default 'DRAFT',
"date_issued" timestamp not null default now(),
"issued_to" uuid not null,
"issued_by" uuid not null,
"cat_id" integer not null,
"sub_cat_id" integer not null,
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."wo_item" (
"id" uuid not null,
"wo_id" uuid not null,
"desc" text not null,
"recurring" boolean not null default '0',
"recur_type" char(1),
"unit_price" decimal(12,2),
"quantity" integer not null,
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."wo_misc" (
"id" uuid not null,
"wo_id" uuid not null,
"desc" text not null,
"value" decimal(12,2),
"percent" boolean not null default '0',
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."wo_discount" (
"id" uuid not null,
"wo_id" uuid not null,
"desc" text not null,
"value" decimal(12,2),
"percent" boolean not null default '0',
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."quote_wo_ref" (
"quote_id" uuid not null,
"wo_id" uuid not null);
--;;
create table if not exists "public"."invoice" (
"id" uuid not null,
"inv_no" text not null,
"value" decimal(12,2),
"status" text not null default 'DRAFT',
"date_issued" timestamp not null default now(),
"issued_to" uuid not null,
"issued_by" uuid not null,
"cat_id" integer not null,
"sub_cat_id" integer not null,
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."inv_item" (
"id" uuid not null,
"inv_id" uuid not null,
"desc" text not null,
"recurring" boolean not null default '0',
"recur_type" char(1),
"unit_price" decimal(12,2),
"quantity" integer not null,
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."inv_misc" (
"id" uuid not null,
"inv_id" uuid not null,
"desc" text not null,
"value" decimal(12,2),
"percent" boolean not null default '0',
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."inv_discount" (
"id" uuid not null,
"inv_id" uuid not null,
"desc" text not null,
"value" decimal(12,2),
"percent" boolean not null default '0',
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."quote_inv_ref" (
"quote_id" uuid not null,
"inv_id" uuid not null);
--;;
create table if not exists "public"."wo_inv_ref" (
"wo_id" uuid not null,
"inv_id" uuid not null);
