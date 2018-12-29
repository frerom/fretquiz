(ns fretquiz.events
  (:require
    [re-frame.core :as re-frame]
    [fretquiz.db :as db]
    ))

(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/reg-event-db
  ::answer
  (fn [db [_ answered-note]]
    (let [{:keys [string-to-guess fret-to-guess]} (:position-to-guess db)
          tuning (get-in db [:fretboard :tuning])
          string-notes (drop-while #(not= % (get tuning string-to-guess)) (cycle (:notes db)))
          correct-note (nth string-notes fret-to-guess)
          correct-answer? (= correct-note answered-note)
          new-db (assoc db :answer {:note-answered answered-note
                                    :correct?      correct-answer?})]
      (if correct-answer?
        (assoc new-db :position-to-guess {:string-to-guess (inc (rand-int (get-in new-db [:fretboard :nr-of-strings])))
                                          :fret-to-guess   (inc (rand-int (get-in new-db [:fretboard :nr-of-frets-to-guess-on])))}
                      :fail-animation nil)
        (assoc new-db :fail-animation (if (= (:fail-animation db) 1) 2 1))))))
