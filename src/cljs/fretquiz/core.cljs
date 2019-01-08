(ns fretquiz.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [fretquiz.events :as events]
   [fretquiz.main.view :as main]
   [fretquiz.config :as config]
   [fretquiz.main.state]
   ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [main/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
