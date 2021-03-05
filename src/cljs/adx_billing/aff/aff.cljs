(ns adx-billing.aff.aff
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [reagent.core :as rcore]
            [reagent.dom :as rdom]
            [adx-billing.common.listing :as ls]
            [adx-billing.common.util :as util]
            [adx-billing.common.page-el :as page-el]
            [adx-billing.msg.bundle :refer [msg]]
            [adx-billing.user.validate :refer [validate]]
            [adx-billing.common.util :refer [toggle-el hide-el]]))

(defonce url "/aff/affs")
(defonce cols ['id 'no 'code 'name 'reg-no 'tax-no 'entity-type 'industry-id 'date-est 'website])
(defonce no-sort-cols ['no 'website])
(defonce params (rcore/atom {:sort "name" :order 1 :offset 0 :limit ls/pg-size}))
(defonce affs (rcore/atom {}))
(defonce counts (rcore/atom {}))

(defn handler [m]
  (do
    (reset! affs (map
                  #(assoc % :date-est (util/parse-date-time (:date-est %)))
                  (:records m)))
    (reset! counts (:counts m))))

(defn input-el
  "An input element which updates its value on change"
  [id name type class placeholder param-name]
  [:input {:id id
           :name name
           :type type
           :class class
           :placeholder placeholder
           :value ((keyword param-name) @params)
           :on-change #(swap! params assoc
                              (keyword param-name) (-> % .-target .-value))
           }])

(defn action-ui [fields errors]
  [:div.field.is-grouped.is-grouped-centered
   [:div.control
    [:button.button {:disabled true} "Delete"]]
   [:div.control
    [:button.button.pl-25.pr-25 "Create"]]])

(defn quick-filter []
  [:div#quick-filter.tabs.is-flex
   [:ul
    [:li {:key "all"}
     [:a {:on-click #((swap! params dissoc :enabled)
                      (ls/get-records url params handler))}
      (str (msg (keyword (str "aff.qf-label/" "all"))) ": " (:all @counts))]]
    [:li {:key "active"}
     [:a {:on-click #((swap! params assoc :enabled "true")
                      (ls/get-records url params handler))}
      (str (msg (keyword (str "aff.qf-label/" "active"))) ": " (:active @counts))]]
    [:li {:key "inactive"}
     [:a {:on-click #((swap! params assoc :enabled "false")
                      (ls/get-records url params handler))}
      (str (msg (keyword (str "aff.qf-label/" "inactive"))) ": " (:inactive @counts))]]]])

(defn table-filter-row []
  [:tr#listing-filter.is-hidden
   [:th]
   [:th]
   [:th>div.field
    [:div.control
     [input-el 'username 'username 'text "input" "" "username"]]]
   [:th>div.field
    [:div.control
     [input-el 'first-name 'first-name 'text "input" "" "first-name"]]]
   [:th>div.field
    [:div.control
     [input-el 'last-name 'last-name 'text "input" "" "last-name"]]]
   [:th>div.field
    [:div.control
     [input-el 'email 'email 'text "input" "" "email"]]]
   [:th>div.field
    [:div.control
     [input-el 'designation 'designation 'text "input" "" "designation"]]]
   [:th
    [:div.field
     [:div.control.is-expanded.has-icons-left
      [input-el "last-login-from" "last-login-from" "text"
       "input input-datepicker date-from" "From" "last-login-from"]
      [:span.icon.is-small.is-left.open-date-from
       [:span.fas.fa-calendar]]]]
    [:div.field
     [:div.control.is-expanded.has-icons-left
      [input-el "last-login-to" "last-login-to" "text"
       "input input-datepicker date-to" "To" "last-login-to"]
      [:span.icon.is-small.is-left.open-date-to
       [:span.fas.fa-calendar]]]]]
   [:th]
   [:th {:col-span "2"}
    [:div.buttons
     [:button.clear-filter-button.button.is-fullwidth
      {:type "button"
       :on-click #(ls/clear params)} "Clear"]
     [:button.filter-button.button.is-fullwidth.is-primary
      {:type "button"
       :on-click #(ls/get-records url params handler)} "Search"]]]])

(defn table-ui []
  [:table.listing-table.table.is-fullwidth.is-striped.is-hoverable
   [:thead
    [ls/table-head-row "aff" cols no-sort-cols url params handler]
    (table-filter-row)]
   [:tbody.listing-content
    (for [{:keys [id no code name reg-no tax-no entity-type
                  industry-id date-est website]} @affs]
      ^{:key id}
      [:tr {:style {:border "none"}}
       [:td {:style {:border "none"}}
        [:label.checkbox
         [:input {:type "checkbox" :name "id" :value id}]]]
       [:td {:style {:border "none"}} no]
       [:td {:style {:border "none"}} code]
       [:td {:style {:border "none"}} name]
       [:td {:style {:border "none"}} reg-no]
       [:td {:style {:border "none"}} tax-no]
       [:td {:style {:border "none"}} entity-type]
       [:td {:style {:border "none"}} industry-id]
       [:td {:style {:border "none"}} (util/format-date-time date-est)]
       [:td {:col-span "2"
             :style {:border "none"}} website]])]])

(defn content []
  (let [fields (rcore/atom {})
        errors (rcore/atom nil)]
    (ls/get-records url params handler)
    (fn []
      [:div
       [:div#notice]
       (quick-filter)
       [:div.listing.table-container.is-sortable
        [:form.listing-filter-form {:auto-complete "off"
                                    :method "POST",
                                    :action "#"}
         [input-el 'limit 'limit 'hidden "" "" "offset"]
         [input-el 'offset 'offset 'hidden "" "" "offset"]
         (table-ui)]]
       [ls/pagination-ui url params handler]
       [action-ui fields errors]])))

(rdom/render [page-el/topbar] (gdom/getElement "topbar"))
(rdom/render [content] (gdom/getElement "content"))

(loader/set-loaded! :aff)
