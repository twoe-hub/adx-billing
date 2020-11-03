(ns adx-billing.html.templates
  (:require [hiccup.page :as page]
            [clojure.pprint :refer [pprint]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [adx-billing.msg.bundle :refer [msg]]))

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

(defn- logo []
  [:div {:class "navbar-brand-title navbar-item"}
   [:img {:src "/img/adxios-brand-white.svg"}]
   [:strong "e-Billing"]])

(defn- burger []
  [:a {:class "navbar-burger burger"
       :role "button"
       :aria-label "menu"
       :aria-expanded "false"
       :data-target "main-navbar-menu"}
   [:span {:aria-hidden "true"}]
   [:span {:aria-hidden "true"}]
   [:span {:aria-hidden "true"}]])

(defn- menu [coll]
  [:ul.menu-list
   (for [item coll]
     (let [self (:self item)
           icon (msg (keyword (str "menu.icon/" (:code self))))
           text (msg (keyword (str "menu.text/" (:code self))))]
       [:li [:a {:class "navbar-item" :href (:url self)}
             [:span.icon
              [:i
               {:class (str "fa fa-lg " icon)
                            :aria-hidden 'true}]] text]
        (when-let [coll (:children item)]
          (for [item coll]
            (let [self (:self item)
                  text (msg (keyword (str "menu.text/" (:code self))))]
              [:ul [:li [:a
                         {:class "navbar-item" :href (:url self)} text]]])))]))])

(defn- tabs [coll]
  ;; [:div {:class "tabs is-pivot is-right"}
  ;;  [:ul
  ;;   [:li {:class 'is-active}
  ;;    [:a {:href "/user/list"} "Users"]]]]
  )

(defn login-template [m]
  (page/html5
   [:head
    [:meta {:charset "utf-8"}]
    [:title (:title m)]
    (for [e (:css m)] (page/include-css e))]
   [:body
    [:div {:class "block-main" "columns" "is-gapless"}
     [:content {:tag "side"}
      (when announcement (render-ann))]
     (anti-forgery-field)
     [:div#content]]
    (for [e (:js m)] (page/include-js e))]))

(defn base-template [req m]
  (page/html5
   [:head
    [:meta {:charset "utf-8"}]
    [:title (str (:title m) " | e-Billing")]
    (for [e (:css m)] (page/include-css e))]
   [:body.has-navbar-fixed-top
    [:header
     [:nav.navbar.main-navbar.is-fixed-top
      {:role 'navigation
       :aria-label "main navigation"}
      [:div.navbar-brand (logo) (burger)]
      [:div#topbar.navbar-menu]]]
    [:aside.menu.sidebar-menu.is-hidden-touch
     (menu (:menu (:session req)))]
    [:section.section.main-section
     [:div.container.main-container.is-fluid
      [:div.content-header.columns.is-vcentered
       [:div.column.is-narrow
        [:h1.title (:title m)]]
       [:div.column
        (tabs (:tabs m))
        (anti-forgery-field)]]
      [:div#content]]]
    (for [e (:js m)] (page/include-js e))]))
