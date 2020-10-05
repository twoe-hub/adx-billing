(ns adx-billing.html.templates
  (:require [hiccup.page :as page]
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
     (anti-forgery-field)
     [:div#content]]
    (for [e (:js m)] (page/include-js e))]))
