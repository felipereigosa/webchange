(ns webchange.editor-v2.activity-form.common.object-form.text-tracing-pattern-form.views
  (:require
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [webchange.editor-v2.activity-form.common.object-form.text-tracing-pattern-form.state :as state]
    [webchange.editor-v2.activity-form.common.object-form.components.scale.views :refer [scale-component]]
    [webchange.ui-framework.components.index :refer [switcher]]))

(defn- dashed-component
  [{:keys [id]}]
  (let [value @(re-frame/subscribe [::state/current-dashed id])]
    [switcher {:checked?  value
               :on-change #(re-frame/dispatch [::state/set-current-dashed id %])                            
               :label     "Dashed line?"}]))

(defn- show-lines-component
  [{:keys [id]}]
  (let [value @(re-frame/subscribe [::state/current-show-lines id])]
    [switcher {:checked?  value
               :on-change #(re-frame/dispatch [::state/set-current-show-lines id %])
               :label     "Show lines?"}]))

(defn form
  [{:keys [id objects-data objects-names]}]
  (r/with-let [_ (re-frame/dispatch [::state/init id objects-data objects-names])]
    [:div.text-tracing-pattern-form
     [dashed-component {:id id}]
     [show-lines-component {:id id}]]))
