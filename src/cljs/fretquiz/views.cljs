(ns fretquiz.views
  (:require
    [re-frame.core :as re-frame]
    [fretquiz.subs :as subs]
    [fretquiz.events :as events]
    [fretquiz.svg :refer [path M L Q Z H]]))

(def fret->distance-from-nut [5.613
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

(def color {:tamarillo    "#9b2117"
            :thunder      "#221e21"
            :black        "#000000"
            :punch        "#da3732"
            :pavlova      "#d7c899"
            :soya-bean    "#5c5445"
            :indian-khaki "#c3b092"
            :white        "#efefe7"})

(defn fret-x-position [fret-nr]
  (* 10
     (get fret->distance-from-nut (dec fret-nr))))

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

(defn fret-hint [{:keys [y-padding fret-hint-color]} fret-nr]
  [:g
   [diamond {:x      (+ (/ (- (fret-x-position fret-nr)
                              (fret-x-position (dec fret-nr)))
                           2)
                        (fret-x-position (dec fret-nr)))
             :y      (- y-padding 5)
             :width  3
             :height 2
             :fill   fret-hint-color}]])

(defn fret-nr-hints [ctx]
  [:g.fret-nr-hints
   [fret-hint ctx 5]
   [fret-hint ctx 7]
   [fret-hint ctx 10]
   [fret-hint ctx 12]])

(defn frets [{:keys [nr-of-frets fretboard-width fret-color]}]
  [:g.frets
   (for [fret-nr (range nr-of-frets)]
     (let [x-position (fret-x-position fret-nr)]
       ^{:key fret-nr} [path {:stroke       fret-color
                              :stroke-width (if (zero? fret-nr) 0 2)
                              :fill         "none"}
                        (M x-position 0)
                        (L x-position fretboard-width)]))])

(defn strings [{:keys [y-padding nr-of-strings fretboard-width fretboard-length string-color] :as ctx}]
  [:g.strings
   (for [string-nr (range nr-of-strings)]
     (let [y-position (string-y-position y-padding fretboard-width nr-of-strings string-nr)]
       ^{:key string-nr} [path {:stroke string-color :stroke-width "1" :fill "none"}
                          (M 0 y-position)
                          (L fretboard-length y-position)]))])

(defn pointer [{:keys [y-padding fretboard-width nr-of-strings string-to-guess fret-to-guess pointer-color]}]
  [:g.guess-pointer
   (let [x-position (- (fret-x-position fret-to-guess) 10)
         y-position (string-y-position y-padding fretboard-width nr-of-strings (dec string-to-guess))]
     [diamond {:x      x-position
               :y      y-position
               :width  12
               :height 10
               :fill   pointer-color}])])

(defn fretboard [{:keys [length width x y string-y-offset fretboard-color] :as ctx}]
  (let [{:keys [nr-of-strings nr-of-frets]} @(re-frame/subscribe [::subs/fretboard])
        {:keys [string-to-guess fret-to-guess]} @(re-frame/subscribe [::subs/position-to-guess])
        ctx (assoc ctx :fretboard-length length
                       :fretboard-width width
                       :y-padding string-y-offset
                       :nr-of-strings nr-of-strings
                       :nr-of-frets nr-of-frets
                       :string-to-guess string-to-guess
                       :fret-to-guess fret-to-guess)]
    [:svg {:width    length
           :height   width
           :x        x
           :y        y
           :view-box (str "0 0 " length " " width)}
     [:rect {:x      0
             :y      0
             :width  length
             :height width
             :fill   fretboard-color}]
     [frets ctx]
     [strings ctx]
     [fret-nr-hints ctx]
     [pointer ctx]]))

(defn nut [{:keys [nut-color string-color]} x y]
  (let [attrs {:cx           x
               :cy           (- y 10)
               :stroke       nut-color
               :stroke-width 2
               :fill         "none"}]
    [:g
     [:circle (assoc attrs :r 10)]
     [:circle (assoc attrs :r 6 :stroke string-color :stroke-width 1)]
     [:circle (assoc attrs :r 2)]]))

(defn head-strings [{:keys [length fretboard-width fretboard-y string-y-offset string-color] :as ctx} nr-of-strings]
  [:g
   (for [string-nr (range nr-of-strings)]
     (let [y-start-position (+ fretboard-y
                               (string-y-position string-y-offset fretboard-width nr-of-strings string-nr))
           y-end-position   (+ y-start-position (+ 10 (* string-nr 3)))
           x-position       (+ 40
                               (* (- (dec nr-of-strings) string-nr)
                                  (/ (- length 70)
                                     nr-of-strings)))]
       ^{:key string-nr} [:g
                          [path {:stroke-width 1
                                 :stroke       string-color}
                           (M length y-start-position)
                           (L x-position y-end-position)]
                          [nut ctx x-position y-end-position]]))])

(defn bridge [{:keys [length fretboard-y fretboard-width bridge-width bridge-color]}]
  [:line {:stroke       bridge-color
          :stroke-width bridge-width
          :x1           (- length (/ bridge-width 2))
          :y1           fretboard-y
          :x2           (- length (/ bridge-width 2))
          :y2           (+ fretboard-y fretboard-width)}])

(defn head-shape [{:keys [length width bridge-width head-color]}]
  (let [length (- length bridge-width)]
    [path {:stroke "none"
           :fill   head-color}
     (M 5 0)
     (Q 0 (/ width 2) 5 width)
     (L (- length 30) (- width 10))
     (Q (- length 15) (- width 25) length (- width 30))
     (L length 30)
     (Q (- length 15) 25 (- length 30) 10)
     (Z)]))

(defn head [{:keys [width length] :as ctx}]
  (let [{:keys [nr-of-strings]} @(re-frame/subscribe [::subs/fretboard])
        ctx (assoc ctx :bridge-width 8)]
    [:svg {:width    length
           :height   width
           :x        0
           :y        0
           :view-box (str "0 0 " length " " width)}
     [head-shape ctx]
     [bridge ctx]
     [head-strings ctx nr-of-strings]]))

(defn balalaika []
  (let [fretboard-width  110
        fretboard-length 700
        head-width       (+ fretboard-width 60)
        head-length      300
        fretboard-y      (/ (- head-width fretboard-width) 2)
        string-y-offset  10
        balalaika-length (+ head-length fretboard-length)
        balalaika-width  (max head-width fretboard-width)
        ctx              {:fretboard-width fretboard-width
                          :fretboard-y     fretboard-y
                          :string-y-offset string-y-offset
                          :fret-color      (color :soya-bean)
                          :fretboard-color (color :indian-khaki)
                          :head-color      (color :tamarillo)
                          :string-color    (color :white)
                          :bridge-color    (color :thunder)
                          :nut-color       (color :thunder)
                          :fret-hint-color (color :soya-bean)
                          :pointer-color   (color :punch)}]
    [:svg {:width    balalaika-length
           :height   balalaika-width
           :view-box (str "0 0 " balalaika-length " " balalaika-width)}
     [head (assoc ctx :width head-width
                      :length head-length)]
     [fretboard (assoc ctx :width fretboard-width
                           :length fretboard-length
                           :x head-length
                           :y fretboard-y)]]))

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
                                              :border     (str "5px solid " (color :punch))
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
   [:h1 "FretQuiz"]
   (for [[color-name color-code] (sort (fn [[_ c1] [_ c2]]
                                         (< (apply + (map #(js/parseInt % 16)
                                                          (re-seq #".{1,4}" (.substring c1 1 7))))
                                            (apply + (map #(js/parseInt % 16)
                                                          (re-seq #".{1,4}" (.substring c2 1 7))))))
                                       color)]
     ^{:key color-name} [:div
                         [:span {:style {:display          "inline-block"
                                         :width            "10px"
                                         :height           "10px"
                                         :background-color color-code}}]
                         [:span " " color-name]])
   [balalaika]
   [alternative-buttons]
   [result]])
