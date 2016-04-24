# Disclojure

A live coding environment for Overtone and Leipzig.

Named after [Disclosure](https://www.youtube.com/watch?v=W_vM8ePGuRM)

## Usage

```clojure
(require '[repl :refer :all])
(require '[leipzig.live :as live])
(live/jam (track))
```

```clojure
(require '[leipzig.melody :refer :all])
(require '[example.track :as t])
(update-track :plucky (times 2 t/leanon))
```

## Features

### Drum kits
TDB

### Sampler
TDB
## License

Copyright © 2016 Piotr Jagielski

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
