(ns webchange.interpreter.renderer.scene.components.utils)

(defn remove-nil-fields [object]
  (apply merge (for [[k v] object :when (not (nil? v))] {k v})))

(defn set-not-nil-value
  [object name value]
  (when (-> value nil? not)
    (aset object name value)))

(defn get-position
  [spine-object]
  (let [position (.-position spine-object)]
    {:x (.-x position)
     :y (.-y position)}))

(defn set-position
  [spine-object position]
  (let [{:keys [x y]} (merge (get-position spine-object)
                             (remove-nil-fields position))]
    (-> (.-position spine-object)
        (.set x y))))

(defn get-scale
  [spine-object]
  (let [scale (.-scale spine-object)]
    {:x (.-x scale)
     :y (.-y scale)}))

(defn set-scale
  [spine-object scale]
  (let [{:keys [x y]} (merge (get-scale spine-object)
                             (remove-nil-fields (if (number? scale)
                                                  {:x scale
                                                   :y scale}
                                                  scale)))]
    (-> (.-scale spine-object)
        (.set x y))))

(defn set-visibility
  [spine-object visible?]
  (aset spine-object "visible" visible?))

(defn set-handler
  [object event-name event-handler]
  (let [synonyms {"click" ["click" "tap"]}
        button-events ["click"]]
    (aset object "interactive" true)
    (when (some #{event-name} button-events)
      (aset object "buttonMode" true))                      ;; ToDo: Move "buttonMode" setting to components
    (if (contains? synonyms event-name)
      (doseq [event (get synonyms event-name)]
        (.on object event event-handler))
      (.on object event-name event-handler))))

(defn get-size
  [object]
  {:width  (.-width object)
   :height (.-height object)})
