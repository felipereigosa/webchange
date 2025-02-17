(ns webchange.editor-v2.activity-form.common.objects-tree.state
  (:require
    [re-frame.core :as re-frame]
    [webchange.interpreter.renderer.state.scene :as state-renderer]
    [webchange.state.state :as state]
    [webchange.state.state-activity :as state-activity]))

(re-frame/reg-sub
  ::objects
  (fn []
    [(re-frame/subscribe [::state/objects-data])])
  (fn [[objects-data]]
    (->> objects-data
         (filter (fn [[_ {:keys [editable?]}]]
                   (:show-in-tree? editable?)))
         (map (fn [[object-name {:keys [alias type]}]]
                (let []
                  {:alias (or alias object-name)
                   :name  object-name
                   :type  type}))))))

(defn get-object-data
  [db object-name]
  (-> (state/get-objects-data db)
      (get object-name)))

(re-frame/reg-sub
  ::object-data
  (fn []
    [(re-frame/subscribe [::state/objects-data])])
  (fn [[objects-data] [_ object-name]]
    (get objects-data object-name)))

(re-frame/reg-sub
  ::visible?
  (fn [[_ object-name]]
    [(re-frame/subscribe [::object-data object-name])])
  (fn [[object-data]]
    (get object-data :visible true)))

(defn- get-object-type
  [object-data]
  (cond
    (get-in object-data [:metadata :uploaded-image?]) :uploaded-image
    (get-in object-data [:metadata :added-character?]) :added-character
    (get-in object-data [:metadata :question?]) :question
    :default (-> object-data :type keyword)))

(re-frame/reg-sub
  ::show-remove-button?
  (fn [[_ object-name]]
    [(re-frame/subscribe [::object-data object-name])])
  (fn [[object-data]]
    (some #{(get-object-type object-data)} [:added-character :uploaded-image :question :anchor])))

(re-frame/reg-event-fx
  ::set-object-visibility
  (fn [{:keys [_]} [_ object-name visible?]]
    {:dispatch [::state/update-scene-object
                {:object-name       object-name
                 :object-data-patch {:visible visible?}}
                {:on-success [::update-scene-object-success {:object-name object-name
                                                             :visible?    visible?}]}]}))

(re-frame/reg-event-fx
  ::update-scene-object-success
  (fn [{:keys [_]} [_ {:keys [object-name visible?]}]]
    {:dispatch [::state-renderer/change-scene-object object-name [[:set-visibility {:visible visible?}]]]}))

(re-frame/reg-event-fx
  ::remove-object
  (fn [{:keys [db]} [_ name]]
    (let [object-name (clojure.core/name name)
          object-type (-> (get-object-data db name)
                          (get-object-type))]
      (case object-type
        :uploaded-image {:dispatch [::remove-uploaded-image object-name]}
        :added-character {:dispatch [::remove-added-character object-name]}
        :question {:dispatch [::remove-question object-name]}
        :anchor {:dispatch [::remove-anchor object-name]}
        {}))))

(re-frame/reg-event-fx
  ::remove-uploaded-image
  (fn [{:keys [_]} [_ object-name]]
    (let [data {:name object-name}]
      {:dispatch [::state-activity/call-activity-common-action
                  {:action :remove-image
                   :data   data}]})))

(re-frame/reg-event-fx
  ::remove-added-character
  (fn [{:keys [_]} [_ object-name]]
    (let [data {:name object-name}]
      {:dispatch [::state-activity/call-activity-common-action
                  {:action :remove-character
                   :data   data}]})))

(re-frame/reg-event-fx
  ::remove-question
  (fn [{:keys [_]} [_ object-name]]
    (let [data {:name object-name}]
      {:dispatch [::state-activity/call-activity-common-action
                  {:action :remove-question
                   :data   data}]})))

(re-frame/reg-event-fx
  ::remove-anchor
  (fn [{:keys [_]} [_ object-name]]
    (let [data {:name object-name}]
      {:dispatch [::state-activity/call-activity-common-action
                  {:action :remove-anchor
                   :data   data}]})))
