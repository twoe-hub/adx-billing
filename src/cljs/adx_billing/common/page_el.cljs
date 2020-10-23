(ns adx-billing.common.page-el
  (:require [ajax.core :refer [GET POST]]
            [reagent.core :as rcore]
            [adx-billing.common.util :refer [toggle]]))

(defonce org (rcore/atom "<user>"))
(defonce display-name (rcore/atom "<org>"))


(defn get-user []
  (GET "/profile/welcome"
       {:headers {"Accept" "application/transit+json"}
        :handler #(do
                    (.log js/console %)
                    (reset! display-name (:name %))
                    (reset! org (:org %)))}))

(defn- topbar []
  (get-user)
  [:div.navbar-menu
   [:div.navbar-start
    [:div.navbar-search.navbar-item
     [:form {:action "#"}
      [:div.field.has-addons.is-material
       [:div.control.is-expanded
        [:input.input
         {:type "text"
          :name "top-search"
          :placeholder "Search"}]]
       [:div.control
        [:button.button {:type "submit"}
         [:span.fa.fa-search {:aria-hidden "true"}]]]]]]]
   [:div.navbar-end
    [:div.navbar-welcome.navbar-item
     [:span
      [:p @org]
      [:p [:strong (str "Welcome, " @display-name)]]]]
    [:div#olay.navbar-profile.navbar-item.has-dropdown
     {:on-click #(toggle %)}
     [:div.navbar-link.is-arrowless
      [:span.icon.is-medium
       [:span.fa.fa-2x.fa-user-circle]]]
     [:div#navbar-dropdown.navbar-dropdown.is-boxed.is-right
      [:a.navbar-item {:href "#"}
       [:span.icon.is-medium
        [:span.fa.fa-lg.fa-cog]]
       [:span "User Profile"]]
      [:hr.navbar-divider]
      [:a.navbar-item {:href "/logout"}
       [:span.icon.is-medium
        [:span.icon.is-medium]]
       [:span "Logout"]]]]]])
