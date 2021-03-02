(ns adx-billing.qutn.validate
  (:require [adx-billing.msg.bundle :refer [msg]]
            [struct.core :as st]))

(def qutn-schema
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
  (first (st/validate params qutn-schema)))
