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
    [:p.has-text-danger (string/join error)]))

(defn login-form []
  (let [fields (r/atom {})
        errors (r/atom nil)]
    (fn []
      [:div
       [:div.auth-errors
        [errors-component errors :server-error]]
       [:form#loginForm.auth-form {:method "post", :action "#"}
        ;; username
        [:div.field.form-group-material.has-icons-right
         [:input#username.form-input-material
          {:type :text
           :name :username
           :pattern ".*\\S+.*"
           :on-change #(swap! fields assoc :username (-> % .-target .-value))
           :value (:username @fields)}]
         [:label.form-placeholder-material
          {:for 'username}
          "Username/Email"]
         [:span.form-icon-material.icon.is-medium
          [:i.fa.fa-2x.fa-user-circle]]
         [errors-component errors :username]]
        [:div.field.form-group-material.has-icons-right
         [:input#password.form-input-material
          {:type :password
           :name :password
           :pattern ".*\\S+.*"
           :on-change #(swap! fields assoc :password (-> % .-target .-value))
           :value (:password @fields)}]
         [:label.form-placeholder-material
          {:for 'password}
          "Password"]
         [:span.form-icon-material.icon.is-medium
          [:span.fa-stack.fa-2x
           [:i.fa.fa-stack-1x.fa-circle]
           [:i.fa.fa-stack-1x.fa-lock]]]
         [errors-component errors :password]]
        ;; login
        [:div.field
         [:button.button.is-green.is-fullwidth
          {:type 'button
           :on-click #(auth! fields errors)} 'Login]]
        [:div.field.auth-form-links
         [:div.remember-me
          [:div.checkbox.checkbox-outline.checkbox-outline-white
           [:input#remember-me
            {:type 'checkbox
             :name 'remember-me}]
           [:label {:for 'remember-me} "Remember me"]]]
         [:div.forgot-password
          [:a {:href "#"} "Forgot password"]]
         ]]])))

(defn login []
  (let []
    (fn []
      [login-form])))

(r/render [login]
          (.getElementById js/document "content"))

(loader/set-loaded! :auth)
