(ns adx-billing.auth.login
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [reagent.core :as r]
            [adx-billing.auth.validate-login :refer [validate]]))

(defn auth! [fields errors]
  (if-let [validation-errors (validate @fields)]
    (reset! errors validation-errors)
    (POST "/auth/auth"
        {:format :json
         :headers
         {"Accept" "application/transit+json"
          "x-csrf-token" (.-value (.getElementById js/document "__anti-forgery-token"))}
         :params @fields
         :handler #(do
                     (.log js/console (str %))
                     (.replace (.-location js/window) "/user/list")
                     (reset! fields nil)
                     (reset! errors nil))
         :error-handler #(do
                           (.log js/console (str %))
                           (reset! errors (get-in % [:response :errors])))})))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:p.help.is-danger (string/join error)]))

(defn login-form []
  (let [fields (r/atom {})
        errors (r/atom nil)]
    (fn []
      [:div.columns.is-multiline.is-centered>div.column.is-3
       [errors-component errors :server-error]
       [:form {:method "post", :action "#"}
        ;; username
        [:div.field.is-horizontal
         [:div.field-label.is-normal
          [:label.label "Username"]]
         [:div.field-body>div.field>div.control
          [:input.input.is-normal
           {:type :text
            :name :username
            :on-change #(swap! fields assoc :username (-> % .-target .-value))
            :placeholder "e.g. adeel@ymail.com"
            :value (:username @fields)}]
          [errors-component errors :username]]]
        ;; password
        [:div.field.is-horizontal
         [:div.field-label.is-normal
          [:label.label "Password"]]
         [:div.field-body>div.field>div.control
          [:input.input.is-normal
           {:type :password
            :name :password
            :on-change #(swap! fields assoc :password (-> % .-target .-value))
            :value (:password @fields)}]
          [errors-component errors :password]]]
        ;; login
        [:div.field.is-horizontal
         [:div.field-label]
         [:div.field-body>div.field>div.control
          [:input.button.is-primary
           {:type "button"
            :on-click #(auth! fields errors)
            :value "Login"}]]]]]
      )))

(defn login []
  (let []
    (fn []
      [:div.content
       [login-form]])))

(r/render [login]
          (.getElementById js/document "content"))

(loader/set-loaded! :auth)
