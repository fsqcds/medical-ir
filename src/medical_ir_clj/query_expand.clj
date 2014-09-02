(ns medical-ir-clj.query-expand
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn remove-punctuation
  [text]
  (str/replace text #"[.,]" " "))

(defn galago-query-operator
  [operator text]
  (str "#" operator "(" (remove-punctuation text) ")"))