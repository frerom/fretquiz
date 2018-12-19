(ns fretquiz.views
  (:require
    [re-frame.core :as re-frame]
    [fretquiz.subs :as subs]
    [fretquiz.events :as events]
    ))

(defn fretboard []
  (let [{:keys [nr-of-strings nr-of-frets]} @(re-frame/subscribe [::subs/fretboard])
        {:keys [string-to-guess fret-to-guess]} @(re-frame/subscribe [::subs/position-to-guess])]
    [:div (map (fn [string]
                 [:div (map (fn [fret]
                              [:span (if (and (= string string-to-guess)
                                              (= fret fret-to-guess))
                                       [:b "!"]
                                       "*")])
                            (range nr-of-frets))])
               (range nr-of-strings))]))

(defn alternative-buttons []
  (let [notes @(re-frame/subscribe [::subs/notes])]
    [:div
     (map (fn [note]
            [:button {:on-click #(re-frame/dispatch [::events/answer note])} note])
          notes)]))

(defn result []
  (let [{:keys [note-answered correct?]} @(re-frame/subscribe [::subs/answer])]
    [:div
     (if correct?
       "Nice!"
       (str note-answered " is not correct"))]))

(defn main-panel []
  [:div
   [:h1 "Fretquiz"]
   [fretboard]
   [alternative-buttons]
   [result]
   ])
