(ns fretquiz.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::fretboard
 (fn [db]
   (:fretboard db)))

(re-frame/reg-sub
  ::position-to-guess
  (fn [db]
    (:position-to-guess db)))

(re-frame/reg-sub
  ::notes
  (fn [db]
    (:notes db)))

(re-frame/reg-sub
  ::answer
  (fn [db]
    (:answer db)))
