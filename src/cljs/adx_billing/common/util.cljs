(ns adx-billing.common.util
  (:require [tick.alpha.api :as t]
            [tick.locale-en-us]))

(defn show-modal [el]
  (.add (.-classList el) "is-active")
  (.focus el))

(defn hide-modal
  ([el]
   (.remove (.-classList el) "is-active"))

  ([e id]
   (let [el (.getElementById js/document id)]
     (when (and (= (.-key e) "Escape") (.contains (.-classList el) "is-active"))
       (hide-modal el))))

  ([e id fields errors]
   (let [el (.getElementById js/document id)]
     (when (and (= (.-key e) "Escape") (.contains (.-classList el) "is-active"))
       (do
         (reset! fields {})
         (reset! errors nil)
         (hide-modal el))))))

(defn toggle-modal
  ([id]
   (let [el (.getElementById js/document id)]
     (if (.contains (.-classList el) "is-active")
       (hide-modal el)
       (show-modal el))))
  ([id fields errors]
   (reset! fields {})
   (reset! errors nil)
   (toggle-modal id)))

(defn show-el [el]
  (.remove (.-classList el) "is-hidden"))

(defn hide-el [el]
  (.add (.-classList el) "is-hidden"))

(defn toggle-el [id]
  (let [el (.getElementById js/document id)]
    (if (.contains (.-classList el) "is-hidden")
      (show-el el)
      (hide-el el))))

(defn parse-date-time [s]
  (if (nil? s)
    nil
    (->> s
         (.toISOString)
         (t/parse)
         (t/zoned-date-time))))

(defn format-date-time [dt]
  (if-not (nil? dt)
    (t/format (tick.format/formatter "dd/MM/yyyy HH:mm:ss") dt)
    nil))
