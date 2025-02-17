(ns webchange.editor-v2.activity-dialogs.menu.sections.effects.views
  (:require
    [re-frame.core :as re-frame]
    [webchange.editor-v2.activity-dialogs.menu.sections.common.section-block.views :refer [section-block]]
    [webchange.editor-v2.activity-dialogs.menu.sections.common.options-list.views :refer [options-list]]
    [webchange.editor-v2.activity-dialogs.menu.sections.effects.emotions.views :refer [available-emotions]]
    [webchange.editor-v2.activity-dialogs.menu.sections.effects.movements.views :refer [available-movements]]
    [webchange.editor-v2.activity-dialogs.menu.sections.effects.state :as state]
    [webchange.ui-framework.components.index :refer [icon-button]]
    [webchange.editor-v2.dialog.utils.dialog-action :refer [music-effects skip-effects guide-effects]]))

(def event-type
  [{:id 1 :title "Default" :type "default"}
   {:id 2 :title "Image" :type "image"}
   {:id 3 :title "Questions" :type "question"}])

(defn- get-current-effects-list
  [{:keys [available-effects effect-type]}]
  (filter (fn [{:keys [type]}]
            (if (= "default" effect-type)
              (or (= nil type) (= effect-type type))
              (= effect-type type)))
          available-effects))

(defn- effects-list
  [{:keys [effect-type title]}]
  (let [available-effects @(re-frame/subscribe [::state/available-effects])
        current-effects-list (get-current-effects-list {:available-effects available-effects :effect-type effect-type})]
    (when (not-empty current-effects-list)
      [section-block {:title title}
       [:span.input-label "Available effects:"]
       [options-list {:options       current-effects-list
                      :get-drag-data (fn [{:keys [value]}]
                                       {:action "add-effect-action"
                                        :id     value})}]])))

(defn- guide-effects-list
  []
  (let [options (vals guide-effects)]
    [:div
     [options-list {:options       options
                    :get-drag-data (fn [{:keys [value]}]
                                     {:action value})}]]))

(defn- skip-effects-list
  []
  (let [options (vals skip-effects)]
    [:div
     [options-list {:options       options
                    :get-drag-data (fn [{:keys [value]}]
                                     {:action value})}]]))

(defn- music-effects-list
  []
  (let [options (vals music-effects)]
    [:div
     [options-list {:options       options
                    :get-drag-data (fn [{:keys [value]}]
                                     {:action value})}]]))

(defn- actions-placeholder
  []
  [:div.placeholder
   [:span "Drag and drop available effect"]])

(defn form
  []
  [:div.effects-form
   [actions-placeholder]
   (for [{:keys [id title type]} event-type]
     ^{:key id}
     [effects-list {:title       title
                    :effect-type type}])
   [section-block {:title "Emotions"}
    [available-emotions]]
   [section-block {:title "Movements"}
    [available-movements]]
   [section-block {:title "Guide"}
    [guide-effects-list]]
   [section-block {:title "Skip"}
    [skip-effects-list]]
   [section-block {:title "Music"}
    [music-effects-list]]])
