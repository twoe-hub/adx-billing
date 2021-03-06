(ns adx-billing.common.listing
  (:require [ajax.core :refer [GET POST]]
            [reagent.core :as rcore]
            [adx-billing.common.util :refer [toggle-el hide-el]]
            [adx-billing.msg.bundle :refer [msg]]))

(defonce pg-size 1)
(defonce total (rcore/atom pg-size))

(defn- all-empty? [sx]
  (reduce (fn [x y] (and x y)) (map empty? sx)))

(defn clear [params]
  (reset! params {:username "" :first-name "" :last-name "" :email "" :designation "" :offset (:offset @params) :limit (:limit @params) :sort (:sort @params) :order (:order @params)}))

(defn get-records [url params handler]
  (GET url
       {:headers {"Accept" "application/transit+json"}
        :params @params
        :handler #(do
                    (handler {:records (:records %) :counts (:counts %)})
                    (reset! total (:total %))
                    (when (all-empty? (vals (dissoc @params :limit :offset :sort :order)))
                      (hide-el (.getElementById js/document "listing-filter"))))}))

(defn- sort-list [col url params handler]
  (swap! params assoc
         :offset 0
         :sort (name col)
         :order (* -1 (:order @params)))
  (get-records url params handler))

(defn table-head-row [list-name cols no-sort-cols url params handler]
  [:tr
   (doall (map (fn [col]
                 (if (= col 'id)
                   [:th
                    {:key col}
                    [:div.field
                     [:div.control
                      [:label.checkbox
                       [:input.table-checkbox-all {:id "checkall"
                                                   :type "checkbox"
                                                   :name "checkall"}]]]]]
                   (if (some #{col} no-sort-cols)
                     [:th
                      {:key col}
                      (msg (keyword (str list-name ".cols/" (name col))))]
                     [:th.is-sortable
                      {:key col
                       :on-click #(sort-list col url params handler)}
                      (msg (keyword (str list-name ".cols/" (name col))))
                      [:span.icon.is-small
                       [:span {:class (str "fa "
                                           (if (= (name col) (:sort @params))
                                             (if (> (:order @params) 0)
                                               "fa-sort-up"
                                               "fa-sort-down")
                                             "fa-sort"))}]]])))
               cols))
   [:th.filter {:on-click #(toggle-el "listing-filter")}
    [:span.icon.is-small
     [:i.fas.fa-filter]]]])

(defn- prefix [sx]
  (vec (let [sx (seq sx)]
         (if (not= (first sx) 1)
           (if (not= (first sx) 2)
             (conj sx nil 1)
             (conj sx 1))
           sx))))

(defn- suffix [sx last-pg]
  (if (not= (last sx) last-pg)
    (if (not= (last sx) (dec last-pg))
      (conj sx nil last-pg)
      (conj sx last-pg))
    sx))

(defn- neighbr [curr-pg last-pg]
  (cond
    (== curr-pg 1) (if (< last-pg 3)
                     [curr-pg (inc curr-pg)]
                     [curr-pg (inc curr-pg) (+ curr-pg 2)])
    (== curr-pg last-pg) (if (< last-pg 3)
                      [(dec curr-pg) curr-pg]
                      [(- curr-pg 2) (dec curr-pg) curr-pg])
    :else [(dec curr-pg) curr-pg (inc curr-pg)]))

(defn- pages [curr-pg last-pg]
  (cond
    (<= last-pg 1) []
    :else (suffix (prefix (neighbr curr-pg last-pg)) last-pg)))

(defn pagination-ui [url params handler]
  (let [last-pg (int (Math/ceil (/ @total pg-size)))
        curr-pg (inc (/ (@params :offset) pg-size))
        sx (pages curr-pg last-pg)
        indices (range (count sx))]
    (when (> last-pg 1)
      [:nav.pagination.mr-30 {:role "navigation", :aria-label "pagination"}
       [:a
        {:class (str "pagination-previous" (when (= curr-pg 1) " disabled"))
         :on-click #(do
                      (swap! params assoc :offset (- (get @params :offset) pg-size))
                      (get-records url params handler))}
        [:span.icon.is-small
         [:i.fa.fa-chevron-left]]]
       [:a
        {:class (str "pagination-next" (when (= curr-pg last-pg ) " disabled"))
         :on-click #(do
                      (swap! params assoc :offset (+ (get @params :offset) pg-size))
                      (get-records url params handler))}
        [:span.icon.is-small
         [:i.fa.fa-chevron-right]]]
       [:ul.pagination-list {:style {:list-style "none"}}
        (doall
         (for [m (zipmap indices sx)]
           (if (nil? (val m))
             [:li {:key (key m)}
              [:span.pagination-ellipsis "???"]]
             [:li {:key (key m)}
              [:a.pagination-link
               {:class (if (= (val m) (inc (/ (get @params :offset) pg-size)))
                         "is-current"
                         "")
                :aria-label (str "Goto page " (val m))
                :aria-current (str (val m))
                :on-click #(do
                             (swap! params assoc :offset (* (dec (val m)) pg-size))
                             (get-records url params handler))}
               (str (val m))]])))]])))
