(ns fretquiz.db)

(def default-db
  {:notes             ["A" "A#/Bb" "B" "C" "C#/Db" "D" "D#/Eb" "E" "F" "F#/Gb" "G" "G#/Ab"]
   :fretboard         {:nr-of-strings           6
                       :nr-of-frets-to-guess-on 12
                       :tuning                  {1 "E"
                                                 2 "B"
                                                 3 "G"
                                                 4 "D"
                                                 5 "A"
                                                 6 "E"}}
   :position-to-guess {:string-to-guess 4
                       :fret-to-guess   12}
   :answer            {:note-answered nil
                       :correct?      nil}
   :fail-animation    nil})
