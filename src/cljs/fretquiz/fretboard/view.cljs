(ns fretquiz.fretboard.view
  (:require [re-frame.core :as re-frame]
            [fretquiz.svg :refer [path M L Q Z H]]
            [fretquiz.subs :as subs]))

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

(defn fret-x-position [{:keys [fretboard-length fret-stroke-width nr-of-frets]} fret-nr]
  (- (* (/ 100 (get fret->distance-from-nut (dec nr-of-frets)))
        (/ fretboard-length 100)
        (get fret->distance-from-nut fret-nr))
     fret-stroke-width))

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

(defn fret-hint [{:keys [fret-hint-color fretboard-width] :as ctx} fret-nr]
  (let [x (+ (/ (- (fret-x-position ctx (dec fret-nr))
                   (fret-x-position ctx (- fret-nr 2)))
                2)
             (fret-x-position ctx (- fret-nr 2)))
        y 4]
    [:g
     [diamond {:x      x
               :y      y
               :width  7
               :height 5
               :fill   fret-hint-color}]
     [diamond {:x      x
               :y      (- fretboard-width 4)
               :width  7
               :height 5
               :fill   fret-hint-color}]]))

(defn fret-nr-hints [ctx]
  [:g.fret-nr-hints
   [fret-hint ctx 5]
   [fret-hint ctx 7]
   [fret-hint ctx 10]
   [fret-hint ctx 12]])

(defn frets [{:keys [nr-of-frets fretboard-width fret-color] :as ctx}]
  [:g.frets
   (for [fret-nr (range nr-of-frets)]
     (let [x-position (fret-x-position ctx fret-nr)]
       ^{:key fret-nr} [path {:stroke       fret-color
                              :stroke-width 4
                              :fill         "none"}
                        (M x-position 0)
                        (L x-position fretboard-width)]))])

(defn strings [{:keys [y-padding nr-of-strings fretboard-width fretboard-length string-color] :as ctx}]
  [:g.strings
   (for [string-nr (range nr-of-strings)]
     (let [y-position (string-y-position y-padding fretboard-width nr-of-strings string-nr)]
       ^{:key string-nr} [path {:stroke string-color
                                :stroke-width (+ 1 (* string-nr (/ 2 nr-of-strings)))
                                :stroke-opacity 0.8
                                :fill "none"}
                          (M 0 y-position)
                          (L fretboard-length y-position)]))])

(defn pointer [{:keys [y-padding fretboard-width nr-of-strings string-to-guess fret-to-guess pointer-color] :as ctx}]
  [:g.guess-pointer
   (let [attrs {:x      (- (fret-x-position ctx (dec fret-to-guess)) 10)
                :y      (string-y-position y-padding fretboard-width nr-of-strings (dec string-to-guess))
                :stroke pointer-color
                :fill   "none"}]
     [:g
      [diamond (assoc attrs :width 12 :height 10 :stroke-width 2)]
      [diamond (assoc attrs :width 6 :height 4 :stroke-width 1)]])])

(defn fretboard [{:keys [length width x y string-y-offset fretboard-color] :as ctx}]
  (let [{:keys [nr-of-strings]} @(re-frame/subscribe [::subs/fretboard])
        {:keys [string-to-guess fret-to-guess]} @(re-frame/subscribe [::subs/position-to-guess])
        ctx (assoc ctx :fretboard-length length
                       :fretboard-width width
                       :y-padding string-y-offset
                       :nr-of-strings nr-of-strings
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
                          [path {:stroke-width (+ 1 (* string-nr (/ 2 nr-of-strings)))
                                 :stroke       string-color
                                 :stroke-opacity 0.8}
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

(defn balalaika [{:keys [balalaika-length balalaika-width fretboard-y head-length head-width fretboard-length fretboard-width] :as ctx}]
  [:div {:style {:width  balalaika-width
                 :height balalaika-length}}
   [:svg {:width    balalaika-length
          :height   balalaika-width
          :view-box (str (* head-length 0.75) " 0 " balalaika-length " " balalaika-width)
          :style    {:transform-origin "bottom left"
                     :transform        (str "rotate(90deg) translateX(-" balalaika-width "px)")}
          }
    [head (assoc ctx :width head-width
                     :length head-length)]
    [fretboard (assoc ctx :width fretboard-width
                          :length fretboard-length
                          :x head-length
                          :y fretboard-y)]]])
