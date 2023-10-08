INSERT INTO "company" (  "id",
                         "settings",
                         "string_attribute_1",
                         "string_attribute_2",
                         "long_attribute_1",
                         "boolean_attribute",
                         "created",
                         "updated")
VALUES (
        '0ce84627-9a66-46bf-9a1d-4f38b82a38e3',
        JSON '{"region": "region"}',
        'test 1',
        'test 2',
        1,
        false,
        '2022-03-07T00:00:00.001Z',
        '2022-03-07T00:00:00.001Z'
        );

INSERT INTO "company" (  "id",
                         "settings",
                         "string_attribute_1",
                         "string_attribute_2",
                         "long_attribute_1",
                         "boolean_attribute",
                         "created",
                         "updated")
VALUES (
        '7059f786-4633-4ace-a412-2f2e90556f08',
        JSON '{"region": "region"}',
         'test 1',
         'test 2',
         1,
         false,
         '2022-03-07T00:00:00.001Z',
         '2022-03-07T00:00:00.001Z'
        );

INSERT INTO "location" (
    "id",
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
        '592c5187-cb85-4b66-b0fc-293989923e1e',
        '0ce84627-9a66-46bf-9a1d-4f38b82a38e3',
        'Location-1',
        JSON '{"location": "location"}',
        'location_1_address_line_1',
        'location_1_address_line_2',
        'zip1',
        'city1',
        'country1',
        '2022-03-07T00:00:00.001Z',
        '2022-03-07T00:00:00.001Z'
        );



