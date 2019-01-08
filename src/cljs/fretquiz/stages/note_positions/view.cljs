(ns fretquiz.stages.note-positions.view
  (:require
    [clojure.string :refer [replace-first]]
    [re-frame.core :as re-frame]
    [fretquiz.subs :as subs]
    [fretquiz.events :as events]
    [fretquiz.fretboard.view :refer [balalaika]]
    [fretquiz.colors :refer [color]]
    [clojure.string :as str]))

(defn grid-template-areas [rows]
  (str/join " "
            (for [row rows]
              (str "\"" (str/join " " (map name row)) "\""))))




(defn note-buttons []
  (let [notes @(re-frame/subscribe [::subs/notes])]
    [:div {:style {:display               "grid"
                   :grid-template-areas   (grid-template-areas [[:. :B]
                                                                [:AsBb :B]
                                                                [:AsBb :A]
                                                                [:GsAb :A]
                                                                [:GsAb :G]
                                                                [:FsGb :G]
                                                                [:FsGb :F]
                                                                [:. :F]
                                                                [:. :E]
                                                                [:DsEb :E]
                                                                [:DsEb :D]
                                                                [:CsDb :D]
                                                                [:CsDb :C]
                                                                [:. :C]])
                   :grid-column-gap       "10px"
                   :grid-row-gap          "10px"
                   :grid-template-columns "90px 90px"
                   :grid-template-rows    "repeat(14 , 42px)"
                   :grid-auto-flow        "column"}}
     (map (fn [note]
            ^{:key note} [:button {:on-click #(re-frame/dispatch [::events/answer note])
                                   :style    {:background "none"
                                              :border     (str "5px solid " (color :punch))
                                              :font-size  "24px"
                                              :grid-area  (-> (replace-first note #"#" "s")
                                                              (replace-first #"/" ""))}}
                          note])
          notes)]))

(defn stage-view []
  (let [fretboard-width 110
        fretboard-length 700
        head-width (+ fretboard-width 60)
        head-length 300
        balalaika-length (+ head-length fretboard-length)
        balalaika-width (max head-width fretboard-width)
        ctx {:fretboard-width   fretboard-width
             :fretboard-length  fretboard-length
             :fretboard-y       (/ (- head-width fretboard-width) 2)
             :nr-of-frets       16
             :fret-stroke-width 2
             :string-y-offset   10
             :fret-color        (color :soya-bean)
             :fretboard-color   (color :indian-khaki)
             :head-color        (color :tamarillo)
             :string-color      (color :white)
             :bridge-color      (color :thunder)
             :nut-color         (color :thunder)
             :fret-hint-color   (color :soya-bean)
             :pointer-color     (color :punch)
             :head-width        head-width
             :head-length       head-length
             :balalaika-length  balalaika-length
             :balalaika-width   balalaika-width}
        fail-animation @(re-frame/subscribe [::subs/fail-class])]
    [:div
     [:h1 "Note positions"]
     [:div {:style {:display               "grid"
                    :grid-template-columns (str balalaika-width "px auto")
                    :grid-template-rows    "auto"
                    :grid-column-gap       "30px"}
            :class fail-animation}
      [balalaika ctx]
      [note-buttons]]]))
