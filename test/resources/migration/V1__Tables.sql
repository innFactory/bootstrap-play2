
create table "contacts" (
  "ID" bigserial PRIMARY KEY NOT NULL,
  "first_name" VARCHAR(255),
  "last_name" VARCHAR(255),
  "zip" INTEGER,
  "city" VARCHAR(255),
  "street" VARCHAR(255),
  "street_2" VARCHAR(255),
  "email" VARCHAR(255),
  "created_by" VARCHAR(255),
  "created_date" TIMESTAMP,
  "changed_by" VARCHAR (255),
  "changed_date" TIMESTAMP,
  "deleted" boolean default false not NULL
);
