(ns adx-billing.auth.login
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [goog.style]
            [garden.core :refer [css]]
            [reagent.core :as r]
            [adx-billing.auth.validate-login :refer [validate]]))


(defn install-style []
  (goog.style/installStyles
   (css
    [:.block-invalid :.block-reset :.block-failed :.block-success
     [:a  {:color "#fff"}
      [:&:hover {:color "#fff"}]
      [:&:focus {:color "#fff"}]
      [:&:visited {:color "#fff"}]]]
    [:button {:font-family 'RobotoRegular}
     {:font-weight '400}
     {:font-style 'normal}]
    [:a
     {:font-family 'RobotoMedium}
     {:font-weight '500}
     {:font-style 'normal}]
    [:.auth-form :.auth-form-required
     {:font-family 'RobotoMedium}
     {:font-weight '500}
     {:font-style 'normal}]
    [:.auth-errors
     {:font-family 'RobotoBold}
     {:font-weight '700}
     {:font-style 'normal}]
    [:.block-main
     ["> header"
      {:height "100px"}
      {:padding "32px"}
      {:margin '0}]])))

;; .block-main > header {
;;   height: 100px;
;;   padding: 32px;
;;   margin: 0;
;; }

;; .block-main > header .header-brand {
;;   display: flex;
;;   width: 170px;
;; }

;; .block-main > header .header-brand img {
;;   padding-right: 0.25rem;
;; }

;; .block-main > header .header-brand strong {
;;   margin-top: -0.25rem;
;;   color: #fff;
;;   align-self: baseline;
;; }

;; .block-main > header .header-links {
;;   list-style: none;
;;   margin: 0;
;;   padding: 0;
;; }

;; .header-brand {
;;   float: left;
;;   text-align: left;
;; }

;; .header-links {
;;   min-width: 50%;
;;   float: right;
;;   text-align: right;
;; }

;; .header-links li {
;;   float: right;
;;   margin-left: 2rem;
;; }

;; /*
;;   Generic
;;  */
;; a {
;;   text-decoration: none;
;;   color: rgba(255, 255, 255, 0.8);
;;   cursor: pointer;
;; }

;; a:hover, a:focus, a:active {
;;   text-decoration: underline;
;;   color: #fff;
;; }

;; header,
;; .block {
;;   color: #fff;
;; }

;; .modal-card-foot {
;;   justify-content: center;
;; }

;; .checkbox:hover {
;;   color: unset;
;; }

;; .field:not(:last-child) {
;;   margin-bottom: 3rem;
;; }

;; /*
;;   Container
;;  */
;; .block-main {
;;   width: 100%;
;; }

;; .block-main.columns.is-gapless {
;;   margin-bottom: 0;
;; }

;; /*
;;   Blocks
;;  */
;; .block .block-header {
;;   font-size: 2rem;
;;   line-height: 1.25;
;;   margin-bottom: 2rem;
;; }

;; .block .block-header,
;; .block .block-content {
;;   text-align: center;
;; }

;; .block-announcement {
;;   background-color: rgba(0, 0, 0, 0.2);
;; }

;; .block-announcement section {
;;   padding: 10rem 6rem;
;; }

;; .block-announcement .block-header,
;; .block-announcement .block-content {
;;   text-align: left;
;; }

;; .block-announcement .announcement:not(.is-current) {
;;   display: none;
;; }

;; .block-announcement .announcement .title {
;;   font-size: 1.5rem;
;;   color: #fff;
;; }

;; .block-announcement .announcement .published-on {
;;   font-size: 0.75rem;
;;   color: rgba(255, 255, 255, 0.8);
;; }

;; .block-announcement .announcement .content strong {
;;   color: #fff;
;; }

;; .block-announcement .announcement .content ul {
;;   margin-bottom: 2rem;
;; }

;; .block-announcement .pagination li a {
;;   font-size: 0.5rem;
;;   color: rgba(255, 255, 255, 0.4);
;; }

;; .block-announcement .pagination li a:hover {
;;   color: #fff;
;; }

;; .block-announcement .pagination li a.is-current {
;;   font-size: 0.75rem;
;;   color: rgba(255, 255, 255, 0.8);
;; }

;; .block-login {
;;   margin: auto;
;;   max-width: 48rem;
;;   text-align: center;
;; }

;; .block-login .form-group {
;;   margin-bottom: 32px;
;; }

;; .block-login .button.is-green {
;;   background-color: #9c0;
;;   color: #fff;
;; }

;; .block-login .button.is-green:active, .block-login .button.is-green:hover {
;;   background-color: #bfff00;
;; }

;; .block-forgot .form-group {
;;   margin-bottom: 2rem;
;; }

;; .block-success .block-success-icon {
;;   font-size: 108px;
;;   margin-bottom: 2rem;
;; }

;; .block-success .form-group {
;;   margin-top: 2rem;
;; }

;; .block-failed .block-failed-icon {
;;   font-size: 108px;
;;   margin-bottom: 2rem;
;; }

;; .block-failed .form-group {
;;   margin-top: 2rem;
;; }

;; .block-reset .auth-form-newpass {
;;   position: relative;
;; }

;; .block-reset .new-password-feedback {
;;   position: absolute;
;;   top: 1rem;
;;   right: 1rem;
;;   color: #66ccff;
;; }

;; .block-reset .form-group {
;;   margin-top: 2rem;
;; }

;; .block-reset .tooltip-inner {
;;   width: 200px;
;;   font-size: .75rem;
;; }

;; .block-invalid .block-invalid-icon {
;;   font-size: 108px;
;;   margin-bottom: 2rem;
;; }

;; .block-invalid .form-group {
;;   margin-top: 2rem;
;; }

;; /*
;;   Auth Elements
;;  */
;; .auth-message {
;;   margin: 1rem auto;
;;   width: 75%;
;; }

;; .auth-form {
;;   margin: 2rem auto 0;
;;   width: 50%;
;; }

;; @media screen and (max-width: 970px) {
;;   .auth-form {
;;     width: 100%;
;;   }
;; }

;; .auth-form .remember-me {
;;   float: left;
;; }

;; .auth-form .forgot-password {
;;   float: right;
;; }

;; .auth-form .auth-form-required {
;;   color: #f00;
;;   float: left;
;;   font-size: .75rem;
;;   margin-left: .5rem;
;;   margin-top: .25rem;
;;   position: absolute;
;; }

;; .auth-form .form-icon-material > .fa-stack > .fa-lock {
;;   color: #3b96b8;
;;   font-size: 1.25rem;
;; }

;; .content ol {
;;   margin-top: .5em;
;;   margin-bottom: 1em;
;; }



(defn auth! [fields errors]
  (if-let [validation-errors (validate @fields)]
    (reset! errors validation-errors)
    (POST "/auth/auth"
        {:format :json
         :headers
         {"Accept" "application/transit+json"
          "x-csrf-token" (.-value (.getElementById js/document "token"))}
         :params @fields
         :handler #(do
                     (.log js/console (str %))
                     (.replace (.-location js/window) "/guest/list")
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
          (install-style)
          (.getElementById js/document "content"))

(loader/set-loaded! :auth)
