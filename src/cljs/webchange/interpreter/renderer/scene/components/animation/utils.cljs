(ns webchange.interpreter.renderer.scene.components.animation.utils
  (:require
    [webchange.interpreter.pixi :refer [RegionAttachment Skin Spine TextureAtlasRegion]]
    [webchange.interpreter.renderer.scene.components.utils :as utils]
    [webchange.logger.index :as logger]
    [webchange.resources.manager :as resources]))

(def default-region-params
  {:x               0
   :y               0
   :width           100
   :height          100
   :original-width  100
   :original-height 100
   :offset-x        0
   :offset-y        0
   :index           -1})

(def default-region-attachment-params
  {:name    "element"
   :x       0
   :y       0
   :width   100
   :height  100
   :scale-x 1
   :scale-y 1})

(def get-position utils/get-position)
(def set-position utils/set-position)
(def get-scale utils/get-scale)
(def set-scale utils/set-scale)
(def set-visibility utils/set-visibility)

(defn create-region
  [texture custom-params]
  (let [params (merge default-region-params custom-params)]
    (doto (TextureAtlasRegion.)
      (set! -x (:x params))
      (set! -y (:y params))
      (set! -width (:width params))
      (set! -height (:height params))
      (set! -originalWidth (:original-width params))
      (set! -originalHeight (:original-height params))
      (set! -offsetX (:offset-x params))
      (set! -offsetY (:offset-y params))
      (set! -index (:index params))
      (set! -texture texture))))

(defn create-region-attachment
  [region custom-params]
  (let [params (merge default-region-attachment-params custom-params)]
    (doto (RegionAttachment. (:name params))
      (.setRegion region)
      (set! -x (:x params))
      (set! -y (:y params))
      (set! -scaleX (:scale-x params))
      (set! -scaleY (:scale-y params))
      (set! -width (:width params))
      (set! -height (:height params)))))

(defn get-skeleton
  [spine-object]
  (.-skeleton spine-object))

(defn get-animation-state
  [spine-object]
  (.-state spine-object))

(defn has-animation?
  [spine-object animation-name]
  (-> (.. spine-object -state)
      (.hasAnimation animation-name)))

(defn has-skin?
  [spine-object skin-name]
  (-> (get-skeleton spine-object)
      (.-data)
      (.findSkin skin-name)
      (some?)))

(defn set-animation-slot
  ([image-src spine-object slot-name]
   (set-animation-slot image-src spine-object slot-name {}))
  ([image-src spine-object slot-name {:keys [region-params attachment-params slot-attachment-name]}]
   (let [skeleton (get-skeleton spine-object)
         animation-state (get-animation-state spine-object)
         image-resource (resources/get-resource image-src)]
     (when (nil? image-resource)
       (-> (str "Can not set slot <" image-src ">: Resource was not found") js/Error. throw))
     (let [texture (.-texture image-resource)
           attachment (-> texture
                          (create-region region-params)
                          (create-region-attachment attachment-params))
           slot-index (.findSlotIndex skeleton slot-name)
           attachment-name (or slot-attachment-name "boxes")
           new-skin (doto (Skin. (.-url image-resource))
                      (.setAttachment slot-index attachment-name attachment))]
       (.setSkin skeleton new-skin)
       (.setSlotsToSetupPose skeleton)
       (.apply animation-state skeleton)
       (.updateWorldTransform skeleton)
       (.hackTextureBySlotIndex spine-object slot-index texture) ;; ToDo: https://trello.com/c/zCet3flh
       (.update spine-object 0)))))

(defn- reset-hacks
  [skeleton]
  (doseq [slot (.-slots skeleton)]
    (aset slot "hackAttachment" (clj->js nil))
    (aset slot "hackRegion" (clj->js nil))))

(defn set-skin
  [spine-object skin-name]
  (if-not (has-skin? spine-object skin-name)
    (-> (str "Can not set skin <" skin-name ">: Skin does not exist") js/Error. logger/error)
    (do (doto (get-skeleton spine-object)
          (reset-hacks)                                     ;; ToDo: Remove it. See https://trello.com/c/zCet3flh
          (.setSkinByName skin-name)
          (.setSlotsToSetupPose))
        ;; ToDo:  Update animation skin after setting skin by name.
        ;;        Currently update works but skin is probably spoiled by set-animation-slot method
        ;;        See https://trello.com/c/zCet3flh
        (.update spine-object 0))))

(defn set-combined-skin
  [spine-object skin-names]
  (let [skeleton (get-skeleton spine-object)
        animation-state (get-animation-state spine-object)
        skeleton-data (.-data skeleton)
        skins (->> skin-names
                   vals
                   (remove nil?)
                   (map #(.findSkin skeleton-data %)))
        new-skin (Skin. "combined-skin")]
    (doall (for [skin skins]
             (.addSkin new-skin skin)))
    (.setSkin skeleton new-skin)
    (.setToSetupPose skeleton)
    (.apply animation-state skeleton)
    (.updateWorldTransform skeleton)
    (.update spine-object 0)))

(defn set-or-combine-skin
  [spine-object skin-names skin-name]
  (if (seq skin-names)
    (set-combined-skin spine-object skin-names)
    (set-skin spine-object skin-name)))

(defn set-empty-animation
  ([spine-object]
   (set-empty-animation spine-object {}))
  ([spine-object params]
   (let [{:keys [track-index mix-duration]} (merge {:track-index  0
                                                    :mix-duration 0}
                                                   params)]
     (-> (.-state spine-object)
         (.setEmptyAnimation track-index mix-duration)))))

(defn add-animation
  ([spine-object animation-name]
   (add-animation spine-object animation-name {}))
  ([spine-object animation-name params]
   (when-not (has-animation? spine-object animation-name)
     (-> (str "Can not add animation <" animation-name ">: Animation does not exist") js/Error. throw))
   (let [{:keys [track-index loop? delay force-set?]} (merge {:track-index 0
                                                              :delay       0
                                                              :loop?       true
                                                              :force-set?  true}
                                                             params)]
     (when (and (> track-index 0) force-set?)
       (set-empty-animation spine-object {:track-index track-index}))
     (-> (.-state spine-object)
         (.addAnimation track-index animation-name loop? delay)))))

(defn set-animation
  ([spine-object animation-name]
   (set-animation spine-object animation-name {}))
  ([spine-object animation-name params]
   (when-not (has-animation? spine-object animation-name)
     (-> (str "Can not set animation <" animation-name ">: Animation does not exist") js/Error. throw))
   (let [{:keys [track-index loop?]} (merge {:track-index 0
                                             :loop?       true}
                                            params)]
     (-> (.-state spine-object)
         (.setAnimation track-index animation-name loop?)))))

(defn set-animation-speed
  [spine-object animation-speed]
  (when (nil? animation-speed)
    (-> (str "Speed is not defined") js/Error. throw))
  (-> (.-state spine-object)
      (aset "timeScale" animation-speed)))

(defn set-auto-update
  [spine-object auto-update]
  (set! (.-autoUpdate spine-object) auto-update))

(defn set-track-loop
  ([spine-object loop?]
   (set-track-loop spine-object 0 loop?))
  ([spine-object track-number loop?]
   (let [tracks (.. spine-object -state -tracks)
         track (aget tracks track-number)]
     (set! (.-loop track) loop?))))

(defn- set-animation-mix
  [spine-object]
  (doto (.. spine-object -state -data)
    (set! -defaultMix 0.25)))

(defn create-spine-animation
  ([animation-resource props]
   (create-spine-animation animation-resource props {}))
  ([animation-resource
    {:keys [animation-start? speed offset position skin-name skin-names animation-name scale loop]}
    {:keys [set-animation?] :or {set-animation? true}}]
   (let [spine-data (.-spineData animation-resource)
         coordinates {:x (* (- (:x position) (:x offset)) (:x scale))
                      :y (* (- (:y position) (:y offset)) (:y scale))}
         spine-object (doto (Spine. spine-data)
                        (set-or-combine-skin skin-names skin-name)
                        (set-position coordinates)
                        (set-scale scale)
                        (set-animation-speed speed)
                        (set-animation-mix)
                        (set-auto-update animation-start?))]
     (when set-animation?
       (doto spine-object
         (set-animation animation-name)
         (set-track-loop loop)))
     spine-object)))

(defn reset-skeleton
  [container state]
  (resources/get-or-load-resource
    (get-in @state [:props :name])
    {:animation?  true
     :on-complete (fn [resource]
                    (let [spine-animation (create-spine-animation resource (:props @state))]
                      (swap! state assoc :animation spine-animation)
                      (.removeChildren container)
                      (.addChild container spine-animation)
                      ;; Update pose:
                      (.updateWorldTransform (get-skeleton spine-animation))
                      (.update spine-animation 0)))}))
