INSERT INTO "company" (
                         "settings",
                         "string_attribute_1",
                         "string_attribute_2",
                         "long_attribute_1",
                         "boolean_attribute",
                         "created",
                         "updated")
VALUES (
        JSON '{"region": "region"}',
        'test 1',
        'test 2',
        1,
        false,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
        );

INSERT INTO "company" (  "settings",
                         "string_attribute_1",
                         "string_attribute_2",
                         "long_attribute_1",
                         "boolean_attribute",
                         "created",
                         "updated")
VALUES (
        JSON '{"region": "region"}',
         'test 1',
         'test 2',
         1,
         false,
         CURRENT_TIMESTAMP,
         CURRENT_TIMESTAMP
        );

INSERT INTO "location" (

    "company",
    "name",
    "settings",
    "address_line_1",
    "address_line_2",
    "zip",
    "city",
    "country",
    "created",
    "updated")
VALUES (
        1,
        'Location-1',
        JSON '{"location": "location"}',
        'location_1_address_line_1',
        'location_1_address_line_2',
        'zip1',
        'city1',
        'country1',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
        );



