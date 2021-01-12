(ns adx-billing.db.util
  (:require [clojure.string :as string]))

(defn str-regex [s]
  (if (re-matches #"[-@\.\w]+" s)
    (str "'.*" s ".*'")
    "'.*'"))
