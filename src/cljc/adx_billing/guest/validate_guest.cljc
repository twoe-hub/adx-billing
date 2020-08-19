(ns adx-billing.guest.validate-guest
  (:require
   [struct.core :as st]))

(def message-schema
  [[:name
    [st/required :message "Required"]
    st/string]
   [:message
    [st/required :message "Required"]
    st/string
    {:message "Message must contain at least 10 characters"
     :validate (fn [msg] (>= (count msg) 10))}]])

(defn validate [params]
  (first (st/validate params message-schema)))
