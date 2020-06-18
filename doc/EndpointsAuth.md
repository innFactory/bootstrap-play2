# Endpoints Authentication/Authorization:

#### /v1/companies/*:

- Not Public (JWT Token)
- User can create, access, update and delete company (**POST**, **GET**, **PATCH**, **DELETE**)

#### /v1/locations/*:

- Not Public (JWT Token)
- User can create, access, update and delete location (**POST**, **GET**, **PATCH**, **DELETE**)

#### /v1/public/*

- Public
- Only Locations will be returned in the "/distance/" Query
- Actorsystem is public