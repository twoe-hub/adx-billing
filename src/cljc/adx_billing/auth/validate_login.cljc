(ns adx-billing.auth.validate-login
  (:require
   [struct.core :as st]))

(def login-schema
  [[:username
    [st/required :message "Required"]
    st/string]
   [:password
    [st/required :message "Required"]
    st/string]])

(defn validate [params]
  (first (st/validate params login-schema)))
