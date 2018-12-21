(ns fretquiz.views
  (:require
    [re-frame.core :as re-frame]
    [fretquiz.subs :as subs]
    [fretquiz.events :as events]
    ))

(def fret->distance-from-nut [0
                              5.613
                              10.910
                              15.910
                              20.630
                              25.085
                              29.289
                              33.258
                              37.004
                              40.540
                              43.877
                              47.027
                              50.000
                              52.806
                              55.455
                              57.955
                              60.315
                              62.542
                              64.645
                              66.629
                              68.502])

(defn fret-x-position [x-padding fret-length fret-nr]
  (+ x-padding (* (* 2 (/ (- fret-length (* 2 x-padding)) 100)) (get fret->distance-from-nut fret-nr))))

(defn string-y-position [y-padding fret-width nr-of-strings string-nr]
  (+ y-padding (* string-nr (/ (- fret-width (* 2 y-padding)) nr-of-strings))))

(defn svg-fretboard []
  (let [{:keys [nr-of-strings nr-of-frets]} @(re-frame/subscribe [::subs/fretboard])
        {:keys [string-to-guess fret-to-guess]} @(re-frame/subscribe [::subs/position-to-guess])
        fret-length 800
        fret-width 80
        x-padding 10
        y-padding 3]
    [:svg {:width fret-length :height fret-width :view-box (str "0 0 " fret-length " " fret-width) :style {:padding "10px"}}
     [:g
      (for [fret-nr (range (inc nr-of-frets))]
        (let [x-position (fret-x-position x-padding fret-length fret-nr)]
          ^{:key fret-nr} [:path {:d            (str "M" x-position ",0 L" x-position "," fret-width)
                                  :stroke       "red"
                                  :stroke-width (if (zero? fret-nr) "4" "2")
                                  :fill         "none"}]
          ))]
     [:g
      (for [string-nr (range (inc nr-of-strings))]
        (let [y-position (string-y-position y-padding fret-width nr-of-strings string-nr)]
          ^{:key string-nr} [:path {:d (str "M0," y-position " L" fret-length "," y-position) :stroke "red" :stroke-width "2" :fill "none"}]
          ))]
     [:g
      (let [x-position (+ (/ (- (fret-x-position x-padding fret-length fret-to-guess)
                                (fret-x-position x-padding fret-length (dec fret-to-guess)))
                             2)
                          (fret-x-position x-padding fret-length (dec fret-to-guess)))
            y-position (string-y-position y-padding fret-width nr-of-strings string-to-guess)]
        [:circle {:cx           x-position
                  :cy           y-position
                  :r            "5"
                  :fill         "black"
                  :stroke-width "2"
                  :stroke       "black"}])]]))

(defn alternative-buttons []
  (let [notes @(re-frame/subscribe [::subs/notes])]
    [:div
     (map (fn [note]
            ^{:key note} [:button {:on-click #(re-frame/dispatch [::events/answer note])} note])
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
   [svg-fretboard]
   [alternative-buttons]
   [result]
   ])

