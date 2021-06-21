-- ************************************** "company"

CREATE TABLE "company"
(
    "id"                 bigserial NOT NULL,
    "firebase_user"      varchar(500)[] NOT NULL,
    "settings"           json           NOT NULL,
    "string_attribute_1" varchar,
    "string_attribute_2" varchar,
    "long_attribute_1"   bigint         NOT NULL,
    "boolean_attribute"  boolean        NOT NULL,
    "created"            timestamp      NOT NULL,
    "updated"            timestamp      NOT NULL,
    CONSTRAINT "PK_company" PRIMARY KEY ("id")
);

-- ************************************** "location"

CREATE TABLE "location"
(
    "id"             bigserial NOT NULL,
    "company"        bigint       NOT NULL,
    "name"           varchar(255) NOT NULL,
    "settings"       json         NOT NULL,
    "position"       geometry     NOT NULL,
    "address_line_1" varchar(300) NOT NULL,
    "address_line_2" varchar(300) NOT NULL,
    "zip"            varchar(300) NOT NULL,
    "city"           varchar(300) NOT NULL,
    "country"        varchar(300) NOT NULL,
    "created"        timestamp    NOT NULL,
    "updated"        timestamp    NOT NULL,
    CONSTRAINT "PK_location" PRIMARY KEY ("id"),
    CONSTRAINT "FK_location_company" FOREIGN KEY ("company") REFERENCES "company" ("id")
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




