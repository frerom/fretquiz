(ns fretquiz.db)

(def default-db
  {:notes             ["A" "A#/Bb" "B" "C" "C#/Db" "D" "D#" "Db" "E" "F" "F#/Gb" "G" "G#/Ab"]
   :fretboard         {:nr-of-strings 6
                       :nr-of-frets   20
                       :tuning        {1 "E"
                                       2 "B"
                                       3 "G"
                                       4 "D"
                                       5 "A"
                                       6 "E"}}
   :position-to-guess {:string-to-guess 4
                       :fret-to-guess   12}
   :answer            {:note-answered nil
                       :correct?      nil}})
