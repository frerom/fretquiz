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

(defn fret-x-position [fret-nr]
  (+ 10 (* 10
      (get fret->distance-from-nut fret-nr))))

(defn string-y-position [y-padding fretboard-width nr-of-strings string-nr]
  (+ y-padding
     (* string-nr
        (/ (- fretboard-width
              (* 2 y-padding))
           (dec nr-of-strings)))))

(defn diamond [{:keys [x y width height] :as attrs}]
  [:polygon (-> (dissoc attrs :x :y :width :height)
                (assoc :points (str (- x (/ width 2)) "," y " "
                                    x "," (- y (/ height 2)) " "
                                    (+ x (/ width 2)) "," y " "
                                    x "," (+ y (/ height 2)))))])

(defn fret-hint [{:keys [x-padding y-padding fretboard-length]} fret-nr]
  [:g
   [diamond {:x            (+ (/ (- (fret-x-position fret-nr)
                                    (fret-x-position (dec fret-nr)))
                                 2)
                              (fret-x-position (dec fret-nr)))
             :y            (- y-padding 5)
             :width        3
             :height       2
             :fill         "red"
             :stroke-width "2"
             :stroke       "red"}]])

(defn fret-nr-hints [ctx]
  [:g.fret-nr-hints
   [fret-hint ctx 5]
   [fret-hint ctx 7]
   [fret-hint ctx 10]
   [fret-hint ctx 12]])

(defn frets [{:keys [nr-of-frets fretboard-width]}]
  [:g.frets
   (for [fret-nr (range (inc nr-of-frets))]
     (let [x-position (fret-x-position fret-nr)]
       ^{:key fret-nr} [:path {:d            (str "M" x-position ",0 L" x-position "," fretboard-width)
                               :stroke       "red"
                               :stroke-width (if (zero? fret-nr) "4" "2")
                               :fill         "none"}]))])

(defn strings [{:keys [y-padding nr-of-strings fretboard-width fretboard-length]}]
  [:g.strings
   (for [string-nr (range nr-of-strings)]
     (let [y-position (string-y-position y-padding fretboard-width nr-of-strings string-nr)]
       ^{:key string-nr} [:path {:d (str "M0," y-position " L" fretboard-length "," y-position) :stroke "red" :stroke-width "2" :fill "none"}]))])

(defn pointer [{:keys [y-padding fretboard-width nr-of-strings string-to-guess fret-to-guess]}]
  [:g.guess-pointer
   (let [x-position (- (fret-x-position fret-to-guess) 10)
         y-position (string-y-position y-padding fretboard-width nr-of-strings (dec string-to-guess))]
     [diamond {:x            x-position
               :y            y-position
               :width        12
               :height       10
               :fill         "black"
               :stroke-width "2"
               :stroke       "black"}])])

(defn fretboard []
  (let [{:keys [nr-of-strings nr-of-frets]} @(re-frame/subscribe [::subs/fretboard])
        {:keys [string-to-guess fret-to-guess]} @(re-frame/subscribe [::subs/position-to-guess])
        fretboard-length 700
        fretboard-width  90
        ctx              {:fretboard-length fretboard-length
                          :fretboard-width  fretboard-width
                          :x-padding        10
                          :y-padding        10
                          :nr-of-strings    nr-of-strings
                          :nr-of-frets      nr-of-frets
                          :string-to-guess  string-to-guess
                          :fret-to-guess    fret-to-guess}]
    [:svg {:width    fretboard-length
           :height   fretboard-width
           :x        200
           :y        110
           :view-box (str "0 0 " fretboard-length " " fretboard-width)}
     [frets ctx]
     [strings ctx]
     [fret-nr-hints ctx]
     [pointer ctx]]))

(defn alternative-buttons []
  (let [notes @(re-frame/subscribe [::subs/notes])]
    [:div {:style {:display               "grid"
                   :grid-template-columns "100px 100px 100px 100px 100px 100px"
                   :grid-auto-rows        "100px"
                   :grid-column-gap       "10px"
                   :grid-row-gap          "10px"}}
     (map (fn [note]
            ^{:key note} [:button {:on-click #(re-frame/dispatch [::events/answer note])
                                   :style    {:background "none"
                                              :border     "5px solid red"
                                              :font-size  "24px"}}
                          note])
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
   [result]])

