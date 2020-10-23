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
lein fig:dev # or, lein fig:prod
```

To start a the application server, fire another terminal, and go to the app root directory and run:

```bash
lein repl # you may use, lein run; but then you wouldn't be able to restart/reset-db/etc., quickly.
```

then at the prompt

```clojure
(start) ;; to stop use, (stop); to restart, use (restart), to reset db, use (reset-db)
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
