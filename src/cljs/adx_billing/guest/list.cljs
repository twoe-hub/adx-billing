(ns adx-billing.guest.list
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [reagent.core :as r]
            [tick.alpha.api :as t]
            [tick.locale-en-us]))

(defn get-messages [messages]
  (GET "/messages"
       {:headers {"Accept" "application/transit+json"}
        :handler #(reset! messages (map
                                    (fn [m] (assoc m :timestamp (t/parse (.-rep (:timestamp m)))))
                                    (:messages %)))}))

(defn message-list [messages]
  [:ul.messages
   (for [{:keys [timestamp message name]} @messages]
     ^{:key timestamp}
     [:li
      [:time (t/format (t/formatter "dd/MM/yyyy HH:mm") timestamp)]
      [:p message]
      [:p " - " name]])])

(defn guests []
  (let [messages (r/atom nil)]
    (get-messages messages)
    (fn []
      [:div.content>div.columns.is-multiline
       [:div.column>div.is-horizontal
        [:h4 "Messages"]
        [message-list messages]]])))

(r/render [guests] (.getElementById js/document "tab-content"))

(loader/set-loaded! :guest-list)
