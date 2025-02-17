(ns webchange.editor-v2.activity-dialogs.menu.sections.text-animation.state
  (:require
    [re-frame.core :as re-frame]
    [webchange.editor-v2.activity-dialogs.form.state :as state-dialog]
    [webchange.editor-v2.activity-dialogs.menu.state :as parent-state]
    [webchange.editor-v2.dialog.utils.dialog-action :refer [text-animation-action?]]
    [webchange.editor-v2.text-animation-editor.state :as chunks]
    [webchange.editor-v2.translator.translator-form.state.actions :as translator-form.actions]))

(re-frame/reg-sub
  ::show-current?
  (fn []
    (re-frame/subscribe [::parent-state/selected-action-data]))
  (fn [selected-action-data]
    (text-animation-action? selected-action-data)))

(re-frame/reg-event-fx
  ::open-text-animation-window
  (fn [{:keys [db]} [_]]
    (let [current-action (state-dialog/get-selected-action db)]
      {:dispatch-n [[::translator-form.actions/set-current-phrase-action current-action]
                    [::chunks/open]]})))

(re-frame/reg-sub
  ::available-actions
  (fn []
    [{:text  "Add to scene"
      :value "scene"}]))
