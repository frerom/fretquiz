(ns fretquiz.main.view
  (:require
    [clojure.string :refer [replace-first]]
    [re-frame.core :as rf]
    [fretquiz.subs :as subs]
    [fretquiz.events :as events]
    [fretquiz.stages.start-menu.view :as start-menu-view]
    [fretquiz.stages.note-positions.view :as note-positions-view]
    [fretquiz.colors :refer [color]]))

(def stages {:start-menu          start-menu-view/stage-view
             :note-positions note-positions-view/stage-view})

(defn main-panel []
  (let [active-stage @(rf/subscribe [:main/active-stage])]
    [:div
     (for [[color-name color-code] (sort (fn [[_ c1] [_ c2]]
                                           (< (apply + (map #(js/parseInt % 16)
                                                            (re-seq #".{1,4}" (.substring c1 1 7))))
                                              (apply + (map #(js/parseInt % 16)
                                                            (re-seq #".{1,4}" (.substring c2 1 7))))))
                                         color)]
       ^{:key color-name} [:div {:style {:display "inline-block" :margin-right "10px"}}
                           [:span {:style {:display          "inline-block"
                                           :width            "10px"
                                           :height           "10px"
                                           :background-color color-code}}]
                           [:span " " color-name]])
     [:button {:on-click #(rf/dispatch [:main/activate-stage :start-menu])} "Back"]
     [(stages active-stage)]]))
