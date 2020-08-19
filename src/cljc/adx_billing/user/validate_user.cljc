(ns adx-billing.user.validate-user
  (:require
   [struct.core :as st]))

(def user-schema
  [[:username
    [st/required :message "Required"]
    [st/email :message "Invalid email address"]
    ]
   [:email
    [st/required :message "Required"]
    [st/email :message "Invalid email address"]
    ]
   [:first-name
    [st/required :message "Required"]
    st/string {:message "Must not contain more than 40 characters"
                 :validate (fn [v] (<= (count v) 40))}]
   [:last-name
    [st/required :message "Required"]
    st/string {:message "Must not contain more than 40 characters"
                 :validate (fn [v] (<= (count v) 40))}]])

(defn validate [params]
  (first (st/validate params user-schema)))
