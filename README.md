# adx-billing

Generated using [Luminus][1] version "3.55"


## Prerequisites

You will need [Leiningen][2] 2.0 or above installed. For ClojureScript development, install [Figwheel][3]

You also need to have PosgreSQL running, with a `adx_billing` db as user `dev`. Then, before running the application, run this from the application root,

```
lein run migrate
```

[1]: https://luminusweb.com/
[2]: https://figwheel.org/
[3]: https://github.com/technomancy/leiningen

## Running Application

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

## License

Copyright © 2020 Adxios.
