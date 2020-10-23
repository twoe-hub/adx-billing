(ns adx-billing.common.util)

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

(defn toggle [el]
  (let [el (.-currentTarget el)]
    (if (.contains (.-classList el) "is-active")
      (hide-modal el)
      (show-modal el))))
