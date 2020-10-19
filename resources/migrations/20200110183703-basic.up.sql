-- :disable-transaction
create table if not exists "public"."guestbook"
(id serial primary key,
name text,
message text,
timestamp timestamp);
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
  "lang_id" integer default 1,
  "status" text not null default 'ACTIVE',
  "version" integer not null default 0,
  "date_created" timestamp not null default now(),
  "last_updated" timestamp not null default now());
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
create table if not exists "public"."access" (
"id" integer not null,
"name" text not null,
"desc" text);
--;;
create table if not exists "public"."bank_acc" (
  "id" uuid not null,
  "aff_id" uuid not null,
  "name" text not null,
  "acc_no" text not null,
  "bank" text not null,
  "swift_code" text,
  "currency_id" varchar(3) not null,
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
  "id" varchar(2) not null,
  "name" text not null);
--;;
create table if not exists "public"."currency" (
  "id" varchar(3) not null,
  "name" text not null);
--;;
create table if not exists "public"."industry" (
  "id" integer not null,
  "name" text not null);
--;;
create table if not exists "public"."language" (
  "id" integer not null,
  "code" text not null,
  "name" text not null,
  "eng_name" text not null);
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
  "country_id" varchar(2) not null,
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
create table if not exists "public"."loc_contact" (
  "loc_id" uuid not null,
  "contact_id" uuid not null);
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
create sequence "public"."seq_registration" as integer start 1;
--;;
create table if not exists "public"."user" (
  "id" uuid not null,
  "aff_id" uuid not null,
  "username" VARCHAR (255) not null,
  "password" VARCHAR (255),
  "first_name" VARCHAR (255) not null,
  "last_name" VARCHAR (255) not null,
  "email" VARCHAR (255) not null,
  "designation" text,
  "primal" boolean not null default '0',
  "visible" boolean not null default '1',
  "enabled" boolean not null default '1',
  "lang_id" integer not null default 1,
  "token" VARCHAR (11) UNIQUE,
  "token_created" timestamp default now(),
  "locked_at" timestamp,
  "last_login" timestamp,
  "pwd_last_reset" timestamp,
  "version" integer not null default 0,
  "valid_from" timestamp,
  "valid_to" timestamp,
  "date_created" timestamp not null default now(),
  "last_updated" timestamp not null default now());
--;;
create table if not exists "public"."user_contact" (
    "user_id" uuid not null,
    "contact_id" uuid not null);
--;;
create table if not exists "public"."password_history" (
 "id" uuid not null,
 "user_id" uuid not null,
 "password" text,
 "version" integer not null default 0,
 "date_created" timestamp not null default now(),
 "last_updated" timestamp not null default now());
--;;
create table if not exists "public"."announcement" (
  "id" uuid not null,
  "title" text not null,
  "content" text not null,
  "mode" text not null,
  "audience" text not null,
  "recur_type" text,
  "date_published" timestamp,
  "expiry_date" date,
  "last_announced" timestamp,
  "version" integer not null default 0,
  "date_created" timestamp not null default now(),
  "last_updated" timestamp not null default now());
--;;
create table if not exists "public"."announce_aff" (
  "ann_id" uuid not null,
  "aff_id" uuid not null);
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
create sequence "public"."seq_pwd_policy" as integer start 1;
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
