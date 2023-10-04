-- ************************************** "company"

CREATE TABLE "company"
(
    "id"                 varchar      NOT NULL,
    "settings"           json,
    "string_attribute_1" varchar,
    "string_attribute_2" varchar,
    "long_attribute_1"   bigint,
    "boolean_attribute"  boolean,
    "created"            timestamp    NOT NULL,
    "updated"            timestamp    NOT NULL,
    CONSTRAINT "PK_company" PRIMARY KEY ("id")
);

-- ************************************** "location"

CREATE TABLE "location"
(
    "id"             varchar      NOT NULL,
    "company"        varchar      NOT NULL,
    "name"           varchar(255),
    "settings"       json,
    "address_line_1" varchar(300),
    "address_line_2" varchar(300),
    "zip"            varchar(300),
    "city"           varchar(300),
    "country"        varchar(300),
    "created"        timestamp    NOT NULL,
    "updated"        timestamp    NOT NULL,
    CONSTRAINT "PK_location" PRIMARY KEY ("id"),
    CONSTRAINT "FK_location_company" FOREIGN KEY ("company") REFERENCES "company" ("id") ON DELETE CASCADE
);


-- ************************************** "user_password_reset_tokens:"

CREATE TABLE "user_password_reset_tokens"
(
    "user_id"     varchar   NOT NULL,
    "token"       varchar   NOT NULL,
    "created"     timestamp NOT NULL,
    "valid_until" timestamp NOT NULL,
    CONSTRAINT "PK_user_password_reset_tokens" PRIMARY KEY ("user_id")
);




