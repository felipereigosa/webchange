(ns webchange.editor-v2.activity-form.generic.components.activity-action.views
  (:require
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [webchange.subs :as subs]
    [webchange.editor-v2.activity-form.generic.components.activity-action.add-image.state :as add-image]
    [webchange.editor-v2.activity-form.generic.components.activity-action.add-text.state :as add-text]
    [webchange.editor-v2.activity-form.generic.components.activity-action.state :as scene-action.events]
    [webchange.state.state-activity :as state-activity]
    [webchange.editor-v2.wizard.activity-template.views :refer [template]]
    [webchange.editor-v2.wizard.validator :as validator]
    [webchange.ui-framework.components.index :refer [dialog button]]
    [webchange.utils.scene-data :as utils]))

(def custom-actions {:add-image ::add-image/call-action
                     :add-text  ::add-text/call-action})

(defn- get-action-default-data
  [scene-data action-data]
  (if (contains? action-data :default-props)
    (let [props-key (-> action-data (get :default-props) (keyword))
          saved-props (get-in scene-data [:metadata :saved-props] {})]
      (get saved-props props-key {}))
    {}))

(defn- action-modal-view
  []
  (r/with-let [scene-id (re-frame/subscribe [::subs/current-scene])
               scene-data @(re-frame/subscribe [::subs/scene @scene-id])
               metadata (get scene-data :metadata)
               current-action-name @(re-frame/subscribe [::state-activity/current-action])
               current-action-data (get-in metadata [:actions current-action-name])
               data (r/atom (get-action-default-data scene-data current-action-data))
               {:keys [valid?] :as validator} (validator/init data)
               close #(re-frame/dispatch [::scene-action.events/close])
               save #(if (valid?) (re-frame/dispatch [::scene-action.events/save {:data @data}]))]
    [dialog
     {:title    (:title current-action-data)
      :on-close close
      :actions  [[button {:on-click save
                          :size     "big"}
                  "Save"]
                 [button {:on-click close
                          :variant  "outlined"
                          :color    "default"
                          :size     "big"}
                  "Cancel"]]}
     [template {:template  current-action-data
                :metadata  metadata
                :data      data
                :validator validator}]]))

(defn activity-action-modal
  []
  (let [open? @(re-frame/subscribe [::scene-action.events/modal-state])
        current-action-name @(re-frame/subscribe [::state-activity/current-action])]
    (when open?
      ^{:key current-action-name}
      [action-modal-view])))

(defn get-activity-actions-list
  [scene-data]
  (->> (utils/get-metadata-untracked-actions scene-data)
       (map (fn [[action-name {:keys [title options]}]]
              {:name     action-name
               :text     title
               :on-click (cond
                           (contains? custom-actions action-name) #(re-frame/dispatch [(get custom-actions action-name) action-name])
                           (empty? options) #(re-frame/dispatch [::scene-action.events/save {:action action-name}])
                           :default #(re-frame/dispatch [::scene-action.events/show-actions-form action-name]))}))
       (sort-by :name)))
