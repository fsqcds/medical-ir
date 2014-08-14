(ns medical-ir-clj.eval
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [medical-ir-clj.core :refer :all]
            [medical-ir-clj.topics :refer [topics]]
            [medical-ir-clj.query-expand :refer :all])
  (:import java.io.PrintStream
           org.lemurproject.galago.core.tools.App))

(def eval-topics (filter #(<= (:number %) 10) topics))

(defn eval-topics-expanded-json
  [query-expansion-function]
  {:queries (map #(hash-map :number (str (:number %))
                            :text (query-expansion-function (:text %)))
                 eval-topics)})

(defn queries-json-file
  [query-expansion-function]
  (let [queries-json-file (java.io.File/createTempFile "queries" ".json")]
    (spit queries-json-file
          (json/write-str (eval-topics-expanded-json query-expansion-function)))
    queries-json-file))

(defn batch-search
  [query-expansion-function]
  (let [queries-json-file (queries-json-file query-expansion-function)
        search-results (java.io.File/createTempFile "search-results" ".json")]
    (App/run (into-array ["batch-search"
                          (str "--index=" (:galago-index-path config))
                          (str queries-json-file)])
      (PrintStream. search-results))
    (.delete queries-json-file)
    search-results))

(defn evaluate
  [query-expansion-function judgements-filepath]
  (let [batch-search-results (batch-search query-expansion-function)]
    (App/main (into-array ["eval"
                           (str "--judgments=" judgements-filepath)
                           (str "--baseline=" batch-search-results)]))))

(defn -main
  [& args]
  (evaluate expand-text-combine (-> "qrelsDS.txt" io/resource io/file str))
  (evaluate expand-text-combine (-> "qrelsLDA.txt" io/resource io/file str)))