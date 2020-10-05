(ns adx-billing.auth.login
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [reagent.core :as r]
            [adx-billing.util.util :refer [toggle-modal hide-modal]]
            [adx-billing.auth.validate-login :refer [validate]]))

(defn get-content! [e]
  (let [data (.-dataset (.-target e))
        url (.-url data)
        toggle (.-toggle data)
        target (.-target data)]

    (GET url
         {:format :json
          :headers {"Accept" "text/html"
                    "Cache-Control" "max-age=1800"}
          :handler #(do
                      (-> js/document
                          (.getElementById target)
                          (.-innerHTML)
                          (set! %))
                      (toggle-modal toggle))
          :error-handler #(.log js/console (str %))})))

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
                       (.replace (.-location js/window) (get-in % [:next]))
                       (reset! fields nil)
                       (reset! errors nil))
           :error-handler #(reset! errors (get-in % [:response :errors]))})))

(defn- policy []
  (let [modal-id "privacy-policy"]
    [:div {:id modal-id
           :class "modal"
           :tab-index "0"
           :on-key-up #(hide-modal % modal-id)
           :style {:justify-content "flex-start"
                   :padding-top "150px"}}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title "Privacy Policy"]
       [:button.delete {:type 'button
                        :on-click #(toggle-modal modal-id)
                        :data-dismiss 'modal
                        :aria-label 'close}]]
      [:section.modal-card-body
       [:div#policy-content.content]]
      [:footer.modal-card-foot {:style {:justify-content 'center}}
       [:button.button {:type 'button
                        :on-click #(toggle-modal modal-id)
                        :data-dismiss 'modal} "Close"]]]]))

(defn- terms []
  (let [modal-id "terms-of-service"]
    [:div {:id modal-id
           :class "modal"
           :tab-index "0"
           :on-key-up #(hide-modal % modal-id)
           :style {:justify-content "flex-start"
                     :padding-top "150px"}}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title "Terms of Service"]
       [:button.delete {:type 'button
                        :on-click #(toggle-modal modal-id)
                        :data-dismiss 'modal
                        :aria-label 'close}]]
      [:section.modal-card-body
       [:div#terms-content.content]]
      [:footer.modal-card-foot {:style {:justify-content 'center}}
       [:button.button {:type 'button
                        :on-click #(toggle-modal modal-id)
                        :data-dismiss 'modal} "Close"]]]]))

(defn header []
  [:header
   [:div.header-brand
    [:img {:src "/img/adxios-brand-white.svg"}]
    [:strong "e-Billing"]]
   [:ul.header-links
    [:li
     [:a {:data-toggle "terms-of-service"
          :data-target "terms-content"
          :data-url "/auth/terms"
          :on-click #(get-content! %)} "Terms of Service"]]
    [:li
     [:a {:data-toggle "privacy-policy"
          :data-target "policy-content"
          :data-url "/auth/policy"
          :on-click #(get-content! %)} "Privacy Policy"]]]])

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
       [:div.column.block.block-main
       (header)
       [:section.section
        [:div.block.block-login
         [:h1.block-header "Welcome to e-Billing"]
         [:div.block-content
          [:p.auth-message "Please login with your username and password." [:br]
           "If you do not have one, please contact "
           [:a {:href "mailto:info@adxios.com"} "info@adxios.com"] " to register."]]
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
            ]]]
         ]]
        ]
       (policy)
       (terms)])))

(r/render [login-form]
          (.getElementById js/document "content"))

(loader/set-loaded! :auth)
