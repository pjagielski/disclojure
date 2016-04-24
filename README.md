# Disclojure

A live coding environment for Overtone and Leipzig.

[Name inspiration](https://www.youtube.com/watch?v=W_vM8ePGuRM)

## Usage

```clojure
(require '[repl :refer :all])
(require '[leipzig.live :as live])
(live/jam (track))
```

```
(require '[leipzig.melody :refer :all])
(require '[disclojure.track :as t])
(update-track :beat (times 2 t/lean-beat))
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
