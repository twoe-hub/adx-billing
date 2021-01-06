(ns adx-billing.user.user
  (:require [ajax.core :refer [GET POST]]
            [cljs.loader :as loader]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [reagent.core :as rcore]
            [reagent.dom :as rdom]
            [adx-billing.common.util :as util]
            [adx-billing.common.page-el :as page-el]
            [adx-billing.msg.bundle :refer [msg]]
            [adx-billing.user.validate :refer [validate]]
            [adx-billing.common.util :refer [toggle-el hide-el]]))

(defonce cols
  ['id 'no 'username 'first-name 'last-name 'email 'designation 'last-login
   'date-created 'enabled])
(defonce pg-size 1)

(defonce total (rcore/atom pg-size))
(defonce users (rcore/atom {}))
(defonce status-counts (rcore/atom nil))

(defn show-modal [elem]
  (.add (.-classList elem) "is-active")
  (.focus elem))

(defn hide-modal [elem fields errors]
  (reset! fields {})
  (reset! errors nil)
  (.remove (.-classList elem) "is-active"))

(defn toggle-modal
  ([class fields errors]
   (let [elem (gdom/getElement "edit-modal")]
     (if (.contains (.-classList elem) "is-active")
       (hide-modal elem fields errors)
       (show-modal elem))))
  ([e class fields errors]
   (when (= (.-key e) "Escape")
     (toggle-modal class fields errors))))

(defn date-time-handler [m]
  (assoc m
         :date-created (util/parse-date-time (:date-created m))
         :last-login (util/parse-date-time (:last-login m))))

(defn clear [params]
  (reset! params {:username "" :first-name "" :last-name "" :email "" :designation "" :offset (:offset @params) :limit (:limit @params)}))

(defn all-empty? [sx]
  (reduce (fn [x y] (and x y)) (map empty? sx)))

(defn get-users [params]
  (GET "/user/users"
       {:headers {"Accept" "application/transit+json"}
        :params @params
        :handler #(do
                    (reset! users (map date-time-handler (:users %)))
                    (reset! status-counts (:status-counts %))
                    (reset! total (:total %))
                    (when (all-empty? (vals (dissoc @params :limit :offset)))
                      (hide-el (.getElementById js/document "listing-filter")))
                    )}))

(defn save-user [fields errors]                                        ;
  (if-let [validation-errors (validate @fields)]
    (reset! errors validation-errors)
    (POST "/user/save"
          {:format :json
           :headers
           {"Accept" "application/transit+json"
            "x-csrf-token" (.-value (.getElementById js/document "token"))}
           :params @fields
           :handler #(do
                       (swap! users (fn[n] (conj n @fields)))
                       (toggle-modal "edit-modal" fields errors))
           :error-handler #(do
                             (.log js/console (str %))
                             (reset! errors (get-in % [:response :errors])))})))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:p.help.is-danger (string/join error)]))

(defn input-el
  "An input element which updates its value on change"
  [id name type class placeholder param-name params]
  [:input {:id id
           :name name
           :type type
           :class class
           :placeholder placeholder
           :value ((keyword param-name) @params)
           :on-change #(swap! params assoc
                              (keyword param-name) (-> % .-target .-value))
           }])

(defn edit-form [fields errors]
  (fn []
    [:div.columns.is-multiline.is-centered>div.column
     [errors-component errors :server-error]
     [:form {:method "POST", :action "#"}
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
      [:div.field.is-horizontal
       [:div.field-label.is-normal
        [:label.label "First Name"]]
       [:div.field-body>div.field>div.control
        [:input.input.is-normal
         {:type :text
          :name :first-name
          :on-change #(swap! fields assoc :first-name (-> % .-target .-value))
          :placeholder "e.g. Adeel"
          :value (:first-name @fields)}]
        [errors-component errors :first-name]]]
      [:div.field.is-horizontal
       [:div.field-label.is-normal
        [:label.label "Last Name"]]
       [:div.field-body>div.field>div.control
        [:input.input.is-normal
         {:type :text
          :name :last-name
          :on-change #(swap! fields assoc :last-name (-> % .-target .-value))
          :placeholder "e.g. Ansari"
          :value (:last-name @fields)}]
        [errors-component errors :last-name]]]
      [:div.field.is-horizontal
       [:div.field-label.is-normal
        [:label.label "Email"]]
       [:div.field-body>div.field>div.control
        [:input.input.is-normal
         {:type :text
          :name :email
          :on-change #(swap! fields assoc :email (-> % .-target .-value))
          :placeholder "e.g. adeel@ymail.com"
          :value (:email @fields)}]
        [errors-component errors :email]]]
      ]]))

(defn modal-ui [fields errors]
  (let [modal-id "edit-modal"]
    [:div.modal
     {:id modal-id
      :tab-index "0"
      :on-key-up #(toggle-modal % modal-id fields errors)
      :style {:justify-content "flex-start"
              :padding-top "150px"}}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title ""]
       [:button.delete
        {:aria-label "close"
         :on-click #(toggle-modal modal-id fields errors)}]]
      [:section.modal-card-body [(edit-form fields errors)]]
      [:footer.modal-card-foot.is-right.pt-50.pb-20.pr-20
       {:style {:justify-content "right"}}
       [:button.button {:on-click #(toggle-modal modal-id fields errors)}
        "Cancel"]
       [:button.button {:on-click #(save-user fields errors)}
        "Save"]]]]))

(defn prefix [sx]
  (vec (let [sx (seq sx)]
         (if (not= (first sx) 1)
           (if (not= (first sx) 2)
             (conj sx nil 1)
             (conj sx 1))
           sx))))

(defn suffix [sx last-pg]
  (if (not= (last sx) last-pg)
    (if (not= (last sx) (dec last-pg))
      (conj sx nil last-pg)
      (conj sx last-pg))
    sx))

(defn neighbr [curr-pg last-pg]
  (cond
    (== curr-pg 1) (if (< last-pg 3)
                     [curr-pg (inc curr-pg)]
                     [curr-pg (inc curr-pg) (+ curr-pg 2)])
    (== curr-pg last-pg) (if (< last-pg 3)
                      [(dec curr-pg) curr-pg]
                      [(- curr-pg 2) (dec curr-pg) curr-pg])
    :else [(dec curr-pg) curr-pg (inc curr-pg)]))

(defn pages [curr-pg last-pg]
  (cond
    (<= last-pg 1) []
    :else (suffix (prefix (neighbr curr-pg last-pg)) last-pg)))

(defn pagination-ui [params]
  (let [last-pg (int (Math/ceil (/ @total pg-size)))]
    (when (> last-pg 1)
      [:nav.pagination.mr-30 {:role "navigation", :aria-label "pagination"}
       [:a.pagination-previous
        {:on-click #(do
                      (swap! params assoc :offset (- (get @params :offset) pg-size))
                      (get-users params))} "Previous"]
       [:a.pagination-next
        {:on-click #(do
                      (swap! params assoc :offset (+ (get @params :offset) pg-size))
                      (get-users params))} "Next page"]
       [:ul.pagination-list {:style {:list-style "none"}}
        (let [curr-pg (inc (/ (@params :offset) pg-size))
              sx (pages curr-pg last-pg)
              indices (range (count sx))]
          (doall
           (for [m (zipmap indices sx)]
             (if (nil? (val m))
               [:li {:key (key m)}
                [:span.pagination-list "…"]]
               [:li {:key (key m)}
                [:a.pagination-link
                 {:class (if (= (val m) (inc (/ (get @params :offset) pg-size)))
                           "is-current"
                           "")
                  :aria-label "Goto page 1"
                  :aria-current (if (= (val m) curr-pg) "page" "")
                  :on-click #(do
                               (swap! params assoc :offset (* (dec (val m)) pg-size))
                               (get-users params))}
                 (str (val m))]]))))]])))

(defn action-ui [fields errors]
  [:div.field.is-grouped.is-grouped-centered
   [:div.control
    [:button.button {:disabled true} "Delete"]]
   [:div.control
    [:button.button.pl-25.pr-25
     {:on-click #(toggle-modal "edit-modal" fields errors)} "Add"]]])

(defn quick-filter [params]
  [:div#quick-filter.tabs.is-flex
   [:ul
    [:li {:key "all"}
     [:a {:on-click #((swap! params dissoc :enabled)
                      (get-users params))}
      (str (msg (keyword (str "user.qf-label/" "all"))) ": " (:all @status-counts))]]
    [:li {:key "active"}
     [:a {:on-click #((swap! params assoc :enabled "true")
                      (get-users params))}
      (str (msg (keyword (str "user.qf-label/" "active"))) ": " (:active @status-counts))]]
    [:li {:key "inactive"}
     [:a {:on-click #((swap! params assoc :enabled "false")
                      (get-users params))}
      (str (msg (keyword (str "user.qf-label/" "inactive"))) ": " (:inactive @status-counts))]]]])

(defn table-head-row [params]
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
                   [:th.is-sortable
                    {:key col} (msg (keyword (str "user.cols/" (name col))))]))
               cols))
   [:th.filter {:on-click #(toggle-el "listing-filter")}
    [:span.icon.is-small
     [:i.fas.fa-filter]]]])

(defn table-filter-row [params]
  [:tr#listing-filter.is-hidden
   [:th]
   [:th]
   [:th>div.field
    [:div.control
     [input-el 'username 'username 'text "input" "" "username" params]]]
   [:th>div.field
    [:div.control
     [input-el 'first-name 'first-name 'text "input" "" "first-name" params]]]
   [:th>div.field
    [:div.control
     [input-el 'last-name 'last-name 'text "input" "" "last-name" params]]]
   [:th>div.field
    [:div.control
     [input-el 'email 'email 'text "input" "" "email" params]]]
   [:th>div.field
    [:div.control
     [input-el 'designation 'designation 'text "input" "" "designation" params]]]
   [:th
    [:div.field
     [:div.control.is-expanded.has-icons-left
      [input-el "last-login-from" "last-login-from" "text"
       "input input-datepicker date-from" "From" "last-login-from" params]
      [:span.icon.is-small.is-left.open-date-from
       [:span.fas.fa-calendar]]]]
    [:div.field
     [:div.control.is-expanded.has-icons-left
      [input-el "last-login-to" "last-login-to" "text"
       "input input-datepicker date-to" "To" "last-login-to" params]
      [:span.icon.is-small.is-left.open-date-to
       [:span.fas.fa-calendar]]]]]
   [:th]
   [:th {:col-span "2"}
    [:div.buttons
     [:button.clear-filter-button.button.is-fullwidth
      {:type "button"
       :on-click #(clear params)} "Clear"]
     [:button.filter-button.button.is-fullwidth.is-primary
      {:type "button"
       :on-click #(get-users params)} "Search"]]]])

(defn table-ui [params]
  [:table.listing-table.table.is-fullwidth.is-striped.is-hoverable
   [:thead
    [table-head-row params]
    [table-filter-row params]]
   [:tbody.listing-content
    (for [{:keys [no id username first-name last-name email
                  designation last-login date-created enabled]} @users]
      ^{:key id}
      [:tr {:style {:border "none"}}
       [:td {:style {:border "none"}}
        [:label.checkbox
         [:input {:type "checkbox" :name "id" :value id}]]]
       [:td {:style {:border "none"}} no]
       [:td {:style {:border "none"}} username]
       [:td {:style {:border "none"}} first-name]
       [:td {:style {:border "none"}} last-name]
       [:td {:style {:border "none"}} email]
       [:td {:style {:border "none"}} designation]
       [:td {:style {:border "none"}} (util/format-date-time last-login)]
       [:td {:style {:border "none"}} (util/format-date-time date-created)]
       [:td {:col-span "2"
             :style {:border "none"}} enabled]])]])

(defn content []
  (let [params (rcore/atom {:offset 0 :limit pg-size})
        fields (rcore/atom {})
        errors (rcore/atom nil)]
    (get-users params)
    (fn []
      [:div
       [:div#notice]
       [quick-filter params]
       [:div.listing.table-container.is-sortable
        [:form.listing-filter-form {:auto-complete "off"
                                    :method "POST",
                                    :action "#"}
         [input-el 'limit 'limit 'hidden "" "" "offset" params]
         [input-el 'offset 'offset 'hidden "" "" "offset" params]
         [table-ui params]]]
       [pagination-ui params]
       [action-ui fields errors]
       [modal-ui fields errors]])))

(rdom/render [page-el/topbar] (gdom/getElement "topbar"))
(rdom/render [content] (gdom/getElement "content"))

(loader/set-loaded! :user)
