(ns adx-billing.auth.login
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [reagent.core :as r]
            [adx-billing.auth.validate-login :refer [validate]]))

(defn auth! [e fields errors]
  (.preventDefault e)
  (if-let [validation-errors (validate @fields)]
    (reset! errors validation-errors)
    (POST "/auth/auth"
        {:format :json
         :headers
         {"Accept" "application/transit+json"
          "x-csrf-token" (.-value (.getElementById js/document "__anti-forgery-token"))}
         :params @fields
         :handler #(do
                     ;; (.log js/console (str %))
                     (.replace (.-location js/window) (get-in % [:next]))
                     (reset! fields nil)
                     (reset! errors nil))
         :error-handler #(do
                           ;; (.log js/console (str %))
                           (reset! errors (get-in % [:response :errors])))})))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:p.has-text-danger (string/join error)]))

(defn username-err-span []
  [:span.form-icon-material.icon.is-medium
   [:i.fa.fa-2x.fa-user-circle]])

(defn pwd-err-span []
  [:span.form-icon-material.icon.is-medium
   [:span.fa-stack.fa-2x
    [:i.fa.fa-stack-1x.fa-circle]
    [:i.fa.fa-stack-1x.fa-lock]]])

(defn render-input [fields errors m]
  [:div.field.form-group-material.has-icons-right
   [:input#username.form-input-material
    {:type (:type m)
     :name (:name m)
     :pattern ".*\\S+.*"
     :on-change #(swap! fields assoc (:name m) (-> % .-target .-value))
     :value ((:name m) @fields)}]
   [:label.form-placeholder-material
    {:for (:id m)}
    (:label m)]
   (:awesome-span m)
   [errors-component errors (:name m)]]
  )

(defn username-input [fields errors]
  (render-input fields errors
                {:type :text
                 :id 'username
                 :name :username
                 :label "Username/Email"
                 :awesome-span (username-err-span)}))

(defn pwd-input [fields errors]
  (render-input fields errors
                {:type :password
                 :id 'password
                 :name :password
                 :label "Password"
                 :awesome-span (pwd-err-span)}))

(defn login-form []
  (let [fields (r/atom {})
        errors (r/atom nil)]
    (fn []
      [:div
       [:div.auth-errors
        [errors-component errors :server-error]]
       [:form#loginForm.auth-form {:method "post", :action "#"}
        (username-input fields errors)
        (pwd-input fields errors)
        [:div.field
         [:input.button.is-green.is-fullwidth
          {:type 'submit
           :on-click #(auth! % fields errors)
           :value 'Login}]]
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

(r/render [login-form]
          (.getElementById js/document "content"))

(loader/set-loaded! :auth)
