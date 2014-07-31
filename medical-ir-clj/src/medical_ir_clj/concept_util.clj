(ns medical-ir-clj.concept-util
  (:require [medical-ir-clj.umls-db.entities :refer :all]
            [korma.core :refer :all]))

(defn concept-name
  [concept-id]
  (-> (select mrconso
              (fields :str)
              (where {:cui concept-id
                      :ts "P"
                      :stt "PF"
                      :ispref "Y"
                      :lat "ENG"})
              (limit 1))
      first
      :str))
