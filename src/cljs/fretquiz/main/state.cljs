(ns fretquiz.main.state
  (:require [re-frame.core :as rf]))

(rf/reg-event-fx
  :main/activate-stage
  (rf/path [:main :active-stage])
  (fn [_ [_ stage-name]]
    {:db stage-name}))

(rf/reg-sub
  :main/active-stage
  (fn [db _]
    (get-in db [:main :active-stage])))