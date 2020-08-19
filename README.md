# adx-billing

Generated using [Luminus][1] version "3.55"


## Prerequisites

You will need [Leiningen][2] 2.0 or above installed. For ClojureScript development, install [Figwheel][3]

You also need to have PosgreSQL running, with a `adx-billing` db as user `dev`. Then, before running the application, run this from the application root,

```
lein run migrate
```

[1]: https://luminusweb.com/
[2]: https://figwheel.org/
[3]: https://github.com/technomancy/leiningen

## Running Application

Note: The application uses Keycloak for SSO; so, one must start Keycloak first. Please find the instruction below.

To start Figwheel go to the app root and run:

```bash
lein fig -- -b app -r
```

To start a web server for the application, go to the app root directory and run:

```bash
lein run
```

For sass run:

```bash
lein scss :development once
```

Or,

```bash
lein sass :development auto # This only takes effect when you modify something
```

## Running Keycloak

First we need to start PostgreSQL used by Keycloak.

To start Postgres used by Keycloak, run this:

``` bash
docker run --name sso-postgres --net sso-net -e POSTGRES_PASSWORD=password -d postgres:9.6.17
```
Now start Keycloak like:

``` bash
docker run -d --name sso-keycloak --net sso-net -p 8080:8080 -e KEYCLOAK_USER=keycloak -e KEYCLOAK_PASSWORD=password -e DB_VENDOR=postgres -e DB_ADDR=sso-postgres -e DB_DATABASE=keycloakdb -e DB_USER=keycloak -e DB_PASSWORD=password jboss/keycloak
```

## License

Copyright © 2020 Adxios.
