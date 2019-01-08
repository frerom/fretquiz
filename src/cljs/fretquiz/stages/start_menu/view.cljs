(ns fretquiz.stages.start-menu.view
  (:require [re-frame.core :as rf]))

(defn stage-view []
  [:div
   [:h1 "Fingerboard Guide"]
   [:button {:on-click #(rf/dispatch [:main/activate-stage :note-positions])} "Note positions"]])