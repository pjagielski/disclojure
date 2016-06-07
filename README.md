# Disclojure

A live coding environment for Overtone and Leipzig.

## Usage

1. Create a melody using Leipzig:

```clojure
(require '[repl :refer :all])
(require '[leipzig.melody :refer :all])
(require '[leipzig.scale :as scale])
(def melody
  (->> (phrase [2 1/2 1/2 1/2 2.5 1/2 1/2 1/2 2.5 1/2 1/2 1/2 2.5 1 1]
               [0 -1 0 2 -3 -4 -3 -1 -5 -6 -5 -3 -7 -6 -5])
       (where :pitch (comp scale/G scale/minor))
       (all :part :plucky)
       (all :amp 1)))
```
2. Initialize track state:

```clojure
(require '[disclojure.live :as l])
(def initial-track
  {:plucky (times 2 leanon)})

(defonce state
         (l/reset-track initial-track))
```
3. Loop your track forever and change on the fly:

```clojure
(require '[leipzig.live :as live])
(live/jam (l/track))
(l/assoc-track :plucky (times 2 melody))
(defn last-frame (fn [e] (>= (:time e) 12)))
(l/update-track :plucky (partial remove last-frame))
```

## Features

### Drum kits

```clojure
(require '[disclojure.kit :as kit])
```

Load drum kit from directory:
```clojure
(kit/load-kit! (io/file "work/beats/big_room"))
```

Play a drum sound from a kit:
```clojure
(leipzig.live/play-note {:part :beat :drum :kick :amp 1})
```

This should play `kick.wav` from drum kit. See "Track helpers" for details of creating drum patterns.

### Sampler
```clojure
(require '[disclojure.sampler :as samples])
```

Load samples from directory:
```clojure
(samples/load-samples! (io/file "work/samples"))
```

Play a sample:
```clojure
(leipzig.live/play-note {:part :sampler :sample :loop :bpm 130})
```

There is a convention of naming samples adopted from Sonic-Pi. Basically, you can include BPM and number of beats in a sample name.
So a file named `124_4_worm.wav` would be mapped to following structure:
```clojure
{:bpm 124, :beats 4, :name :worm, :sound #<buffer[live]: ...}
```

This makes it easy to match sample rate for current BPM of the track. More in the next chapter.

### Track helpers
```clojure
(require '[disclojure.track :refer [tap sampler] :as track)
```

Making beat patterns with `tap`:
```clojure
(def da-beats
  (->>
    (reduce with
            [(tap :fat-kick (range 8) 8)
             (tap :kick (range 8) 8)
             (tap :snare (range 1 8 2) 8)
             (tap :close-hat (sort (concat [3.75 7.75] (range 1/2 8 1))) 8)
             (let [horns [0 1/2 5/4 2]]
               (tap :horn (concat horns
                                  (map (partial + 4) horns)) 8)) ])
    (all :part :beat)))
```

Basically, `tap` takes a drum name and a collection of beat times to create a drum pattern. `(range 8)` will play every beat (kick),
`(range 1 8 2)` - every even beat (snare) and so on.

Sequencing samples with `sampler`:

```clojure
(track/sampler [[0 :apache 16 2]
               [(range 0 16 4) :funky-drummer 4 2]
               [(range 0 16 4) :hotpants 4 2]])
```

The `sampler` function takes a vector of sample pattern with following structure:
`[times sample_name beats amp <start_beat>]`
* `times` could be a beat or collection of beats where the sample should play
* `sample_name` is a name created by `load-samples!`
* `beats` is the number of beats to play
* `amp` is the amplitude of the sample
* optional `start_beat` allows to play a part of sample from specific point

### Custom instruments

Define an Overtone synth and map it to Leipzig `live/play-note`:

```clojure
(require '[overtone.live :refer :all])
(defsynth da-funk ...)
(defmethod live/play-note :da-funk [{hertz :pitch seconds :duration amp :amp cutoff :cutoff}]
  (when hertz
    (da-funk :freq hertz :dur seconds :amp (or amp 1) :cutoff (or cutoff 1500))))
```

## License

Copyright © 2016 Piotr Jagielski

The project name refers to [Disclosure](https://www.youtube.com/watch?v=W_vM8ePGuRM)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
