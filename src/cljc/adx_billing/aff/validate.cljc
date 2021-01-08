(ns adx-billing.aff.validate
  (:require [adx-billing.msg.bundle :refer [msg]]
            [struct.core :as st]))

(def aff-schema
  [[:username
    [st/required :message (msg :validation/required)]
    [st/email :message (msg :validation/invalid-email)]
    ]
   [:email
    [st/required :message (msg :validation/required)]
    [st/email :message (msg :validation/invalid-email)]
    ]
   [:first-name
    [st/required :message (msg :validation/required)]
    st/string {:message (msg :validation/char-limit-exceeded 40)
                 :validate (fn [v] (<= (count v) 40))}]
   [:last-name
    [st/required :message (msg :validation/required)]
    st/string {:message (msg :validation/char-limit-exceeded 40)
                 :validate (fn [v] (<= (count v) 40))}]])

(defn validate [params]
  (first (st/validate params aff-schema)))
