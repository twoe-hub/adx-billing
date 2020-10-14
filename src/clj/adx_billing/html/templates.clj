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

(defn- topbar-search []
  [:div.navbar-start
   [:div.navbar-search.navbar-item
    [:form {:action "#"}
     [:div.field.has-addons.is-material
      [:div.control.is-expanded
       [:input.input
        {:type 'text
         :name 'top-search
         :placeholder 'Search}]]
      [:div.control
       [:button.button {:type 'submit}
        [:span.fa.fa-search {:aria-hidden "true"}]]]]]]])

(defn- topbar-right []
  [:div {:class "navbar-end"}
        [:div {:class "navbar-welcome navbar-item"}
         [:span
          [:p
           [:sec:loggedinuserinfo {:field "affName"}]]
          [:p
           [:strong "Welcome, "
            [:sec:loggedinuserinfo {:field "displayName"} ]]]]]
        [:div {:class "navbar-profile navbar-item has-dropdown"}
         [:div {:class "navbar-link is-arrowless"}
          [:span {:class "icon is-medium"}
           [:span {:class "fa fa-2x fa-user-circle"}]]]
         [:div {:class "navbar-dropdown is-boxed is-right", :id "navbar-dropdown"}
          [:g:link {:class "navbar-item", :namespace "settings", :controller "user", :action "editProfile"}
           [:span {:class "icon is-medium"}
            [:span {:class "fa fa-lg fa-cog"}]]
           [:span "User Profile"]]
          [:hr {:class "navbar-divider"}]
          [:g:link {:class "navbar-item", :url "/logoff"}
           [:span {:class "icon is-medium"}
            [:span {:class "fa fa-lg fa-sign-out-alt"}]]
           [:span "Logout"]]]]])


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
     (let [self (:self item)]
       (println (keyword (str "menu/" (:code self))))
       [:li [:a {:class "navbar-item" :href (:url self)}
             (msg (keyword (str "menu/" (:code self))))]
        (when-let [coll (:children item)]
          (for [item coll]
            (let [self (:self item)]
              [:ul [:li [:a
                         {:class "navbar-item"
                          :href (:url self)} (msg (keyword (str "menu/" (:code self))))
                         ]]])))]))])

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
    [:title (:title m)]
    (for [e (:css m)] (page/include-css e))]
   [:body.has-navbar-fixed-top
    [:header
     [:nav.navbar.main-navbar.is-fixed-top
      {:role 'navigation
       :aria-label "main navigation"}
      [:div.navbar-brand (logo) (burger)]
      [:div.navbar-menu {:id "main-navbar-menu"}
       (topbar-search)
       (topbar-right)]]]
    [:aside.menu.sidebar-menu.is-hidden-touch
     (menu (:menu (:session req)))]
    [:section.section.main-section
     [:div.container.main-container.is-fluid
      [:div {:class "content-header columns is-vcentered"}
       [:div {:class "column is-narrow"}
        [:h1 {:class "title"} ]]
       [:div {:class "column"}
        (tabs (:tabs m))
        (anti-forgery-field)]]
      [:div#content]]]
    (for [e (:js m)] (page/include-js e))]))
