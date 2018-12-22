(ns fretquiz.svg
  (:require [clojure.string :refer [join]]))

(defn M [x y]
  (str "M" x "," y))

(defn L [x y]
  (str "L" x "," y))

(defn H [x]
  (str "H" x))

(defn Q [x1 y1 x y]
  (str "Q" x1 "," y1 " " x "," y))

(defn Z []
  "Z")

(defn path [attrs & points]
  [:path (assoc attrs :d (join " " points))])
