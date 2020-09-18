(ns adx-billing.routes.auth
  (:require
   [adx-billing.auth.validate-login :refer [validate]]
   [adx-billing.layout :as layout]
   [adx-billing.db.core :as db]
   [adx-billing.middleware :as middleware]
   [cheshire.core :refer [parse-string]]
   [clj-http.client :as client]
   [hiccup.page :as page]
   [garden.core :refer [css]]
   [ring.util.http-response :as response]
   [ring.util.response :refer [redirect]]
   [ring.util.anti-forgery :as util]
   ))

(def announcement false)
(defn render-ann []
  ;; [:div {:class "column block block-announcement"}
  ;;  [:section {:class "section"}
  ;;   [:h1 {:class "block-header"} ]
  ;;   [:div {:class "block-content"}
  ;;    [:div {:class "announcement ${i > 0 ? '' : 'is-current'}", :data-id "${i}"}
  ;;     [:div {:class "title"} "${ann.title}"]
  ;;     [:div {:class "subtitle published-on"} "${ann.publishDate.format(&quot;dd/MM/yyyy HH:mm&quot;)}"]
  ;;     [:div {:class "content"} "${ann.content.encodeAsRaw()}"]]
  ;;    [:nav {:class "pagination is-centered", :role "pagination", :aria-label "announcement pagination"}
  ;;     [:ul {:class "pagination-list"}
  ;;      [:g:each {:var "ann", :in "${anns}", :status "i"}
  ;;       [:li
  ;;        [:a {:class "${i > 0 ? '' : 'is-current'}", :data-id "${i}"}
  ;;         [:span {:class "icon is-small"}
  ;;          [:span {:class "fa fa-circle"}]]]]]]]]]]
  )

(defn login-template [m]
  (page/html5
   [:head
    [:meta {:charset "utf-8"}]
    [:title (:title m)]
    (for [e (:css m)] (page/include-css e))
    ]
   [:body
    [:div {:class "block-main" "columns" "is-gapless"}
     [:content {:tag "side"}
      (when announcement (render-ann))]
     [:div.column.block.block-main
      [:header
       [:div.header-brand
        [:img {:src "/img/adxios-brand-white.svg"}]
        [:strong "e-Billing"]]
       [:ul.header-links
        [:li
         [:a {:data-toggle "modal", :data-target "#termsOfService"} "Terms of Service"]]
        [:li
         [:a {:data-toggle "modal", :data-target "#privacyPolicies"} "Privacy Policy"]]]]

      [:section.section
       [:div.block.block-login
        [:h1.block-header "Welcome to e-Billing"]
        [:div.block-content
         [:p.auth-message "Please login with your username and password." [:br]
          "If you do not have one, please contact "
          [:a {:href "mailto:info@adxios.com"} "info@adxios.com"] " to register."]]]
       [:div#content]
       ]
      ]]
    (for [e (:js m)] (page/include-js e))
    ]))


(defn login-page []
  (login-template {:title "Login | e-Billing"
                   :css ["/css/start.css"
                         "/css/views/login/login.css"]
                   :js ["/js/app/cljs_base.js"
                        "/js/app/adx_billing/auth/login.js"]}))

(defn render-login [request]
  (response/content-type
   (response/ok (login-page))
   "text/html; charset=utf-8")
  ;; (layout/render request "auth/login.html")
  )

(defn logout [request]
  (do
    (-> request
        :server-exchange
        (.getSecurityContext)
        (.logout))
    {:status 302
     :headers {"Location" "/"}}))

(defn auth-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/auth/login" {:get render-login}]
   ["/logout" {:get logout}]])
