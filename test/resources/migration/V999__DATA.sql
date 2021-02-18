INSERT INTO "company" ("id",
                       "firebase_user",
                       "settings",
                       "created",
                       "updated",
                       "string_attribute_1",
                       "string_attribute_2",
                       "long_attribute_1",
                       "boolean_attribute")
VALUES ('231f5e3d-31db-4be5-9db9-92955e03507c',
        '{"test@test.de","test2@test.de"}',
        JSON '{"region": "region"}',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'notest1',
        'notest2',
        10,
        true);

INSERT INTO "company" ("id",
                       "firebase_user",
                       "settings",
                       "created",
                       "updated",
                       "string_attribute_1",
                       "string_attribute_2",
                       "long_attribute_1",
                       "boolean_attribute")
VALUES ('b492fa98-ab60-4596-ac3c-256cc4957797',
        '{"test@test6.de","test2@test6.de"}',
        JSON '{"region": "region"}',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'test1',
        'test2',
        5,
        false
        );

INSERT INTO "location" ("company", "name", "settings", "position", "address_line_1", "address_line_2", "city", "zip",
                        "country", "created", "updated")
VALUES ('231f5e3d-31db-4be5-9db9-92955e03507c',
        'Location-1',
        JSON '{"location": "location"}',
        ST_GeomFromText('POINT(0.0 0.0)', 4326),
        'location_1_address_line_1',
        'location_1_address_line_2',
        'city1',
        'zip1',
        'country1',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);



