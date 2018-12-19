(ns fretquiz.db)

(def default-db
  {:notes             ["A" "A#/Bb" "B" "C" "C#/Db" "D" "D#" "Db" "E" "F" "F#/Gb" "G" "G#/Ab"]
   :fretboard         {:nr-of-strings 6
                       :nr-of-frets   12
                       :tuning        {0 "E"
                                       1 "B"
                                       2 "G"
                                       3 "D"
                                       4 "A"
                                       5 "E"}}
   :position-to-guess {:string-to-guess 4
                       :fret-to-guess   2}
   :answer            {:note-answered nil
                       :correct?      nil}})
