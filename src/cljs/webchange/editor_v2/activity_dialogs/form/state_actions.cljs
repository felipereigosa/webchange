(ns webchange.editor-v2.activity-dialogs.form.state-actions
  (:require
    [re-frame.core :as re-frame]
    [webchange.editor-v2.dialog.dialog-form.state.actions :as state-actions]
    [webchange.editor-v2.translator.translator-form.state.scene :as translator-form.scene]
    [webchange.utils.text :refer [text->chunks]]))

(defn- pre_action-type [value] (some #{value} [:concept :scene]))

;; Phrase

(re-frame/reg-event-fx
  ::set-phrase-text
  (fn [{:keys [_]} [_ {:keys [action-path action-type value]}]]
    {:pre [(pre_action-type action-type)]}
    {:dispatch [::state-actions/update-inner-action-by-path {:action-path action-path
                                                             :action-type action-type
                                                             :data-patch  {:phrase-text value}}]}))

(re-frame/reg-event-fx
  ::set-phrase-target
  (fn [{:keys [_]} [_ {:keys [action-path action-type value]}]]
    {:pre [(pre_action-type action-type)]}
    {:dispatch [::state-actions/update-inner-action-by-path {:action-path action-path
                                                             :action-type action-type
                                                             :data-patch  {:target value}}]}))

(re-frame/reg-event-fx
  ::fix-phrase-action
  (fn [{:keys [_]} [_ {:keys [action-path action-source]}]]
    {:pre [(pre_action-type action-source)]}
    {:dispatch [::state-actions/fix-inner-action-by-path {:action-path   action-path
                                                          :action-source action-source
                                                          :action-type   "animation-sequence"}]}))

;; Text Animation

(re-frame/reg-event-fx
  ::set-object-text
  (fn [{:keys [_]} [_ {:keys [object-name text]}]]
    {:dispatch [::translator-form.scene/update-object [object-name] {:text   text
                                                                     :chunks (text->chunks text)}]}))
