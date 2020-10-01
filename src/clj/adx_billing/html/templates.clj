(ns adx-billing.html.templates
  (:require
   [hiccup.page :as page]
   [ring.util.anti-forgery :refer [anti-forgery-field]]))

(def announcement false)
(defn- render-ann []
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
        (anti-forgery-field)
        [:h1.block-header "Welcome to e-Billing"]
        [:div.block-content
         [:p.auth-message "Please login with your username and password." [:br]
          "If you do not have one, please contact "
          [:a {:href "mailto:info@adxios.com"} "info@adxios.com"] " to register."]]
        [:div#content]]
       ]
      ]]
    (for [e (:js m)] (page/include-js e))]))
