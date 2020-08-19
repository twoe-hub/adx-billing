(ns adx-billing.guest.sign
  (:require [ajax.core :refer [GET POST]]
            [reagent.core :as r]
            [cljs.loader :as loader]
            [clojure.string :as string]

            [adx-billing.user.validate-user :refer [validate]]))

(defn send-message! [fields errors]
  (if-let [validation-errors (validate @fields)]
    (reset! errors validation-errors)
    (POST "/message"
          {:format :json
           :headers
           {"Accept" "application/transit+json"
            "x-csrf-token" (.-value (.getElementById js/document "token"))}
           :params @fields
           :handler #(do
                       (reset! fields nil)
                       (reset! errors nil))
           :error-handler #(do
                             (.log js/console (str %))
                             (reset! errors (get-in % [:response :errors])))})))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:p.help.is-danger (string/join error)]))

(defn message-form []
  (let [fields (r/atom {})
        errors (r/atom nil)]
    (fn []
      [:div.columns.is-multiline>div.column.is-9
       [errors-component errors :server-error]
       ;; Name
       [:div.field.is-horizontal
        [:div.field-label.is-normal
         [:label.label "Name"]]
        [:div.field-body>div.field>div.control
         [:input.input.is-normal
          {:type :text
           :name :name
           :on-change #(swap! fields assoc :name (-> % .-target .-value))
           :placeholder "e.g. Adeel Ansari"
           :value (:name @fields)}]
         [errors-component errors :name]]]
       ;; Message
       [:div.field.is-horizontal
        [:div.field-label.is-normal
         [:label.label "Message"]]
        [:div.field-body>div.field>div.control
         [:textarea.textarea
          {:name :message
           :value (:message @fields)
           :on-change #(swap! fields assoc :message (-> % .-target .-value))
           :placeholder "e.g. Hello there..."}]
         [errors-component errors :message]]]
       ;; Send
       [:div.field.is-horizontal
        [:div.field-label]
        [:div.field-body>div.field>div.control
         [:button.button.is-primary
          {:on-click #(send-message! fields errors)
           :value "send"} "Send"]]]]
      )))

(defn sign []
  (let []
    (fn []
      [:div.content>div.columns>div.column.is-two-thirds
       [message-form]])))

(r/render [sign] (.getElementById js/document "tab-content"))

(loader/set-loaded! :guest-sign)
