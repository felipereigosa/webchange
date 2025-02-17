(ns webchange.editor-v2.activity-dialogs.form.action-unit.views
  (:require
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [webchange.editor-v2.activity-dialogs.form.action-unit.views-menu :refer [unit-menu]]
    [webchange.editor-v2.activity-dialogs.form.action-unit.views-animation :refer [animation-unit]]
    [webchange.editor-v2.activity-dialogs.form.action-unit.views-background-music :refer [background-music-unit]]
    [webchange.editor-v2.activity-dialogs.form.action-unit.views-effect :refer [effect-unit]]
    [webchange.editor-v2.activity-dialogs.form.action-unit.views-guide :refer [guide-unit]]
    [webchange.editor-v2.activity-dialogs.form.action-unit.views-movement :refer [movement-unit]]
    [webchange.editor-v2.activity-dialogs.form.action-unit.views-skip :refer [skip-unit]]
    [webchange.editor-v2.activity-dialogs.form.action-unit.views-phrase :refer [phrase-unit]]
    [webchange.editor-v2.activity-dialogs.form.action-unit.views-text-animation :refer [text-animation-unit]]
    [webchange.editor-v2.activity-dialogs.form.state :as state]
    [webchange.logger.index :as logger]
    [webchange.ui-framework.components.utils :refer [get-class-name]]
    [webchange.utils.drag-and-drop :as utils]))

(defn- unknown-element
  [{:keys [type] :as props}]
  (logger/warn "Unknown element type: " type)
  (logger/trace-folded "Props" props)
  [:div.unknown-unit "Not editable action"])

(defn- inside-parallel-action?
  [parallel-mark]
  (not= parallel-mark :none))

(defn- drag-event->drop-target
  [event parallel-mark]
  (if-let [target (.. event -target)]
    (let [offset-y (.-offsetY event)
          offset-x (.-offsetX event)
          target-height (.. target -clientHeight)]
      (if-not (inside-parallel-action? parallel-mark)
        (cond
          (< offset-y (/ target-height 3)) :before
          (> offset-y (* (/ target-height 3) 2)) :after
          :else :parallel)
        (let [order (if (< offset-y (/ target-height 2))
                      :before :after)
              nesting (if (and (< offset-x 100)
                               (or (and (= order :before)
                                        (= parallel-mark :start))
                                   (and (= order :after)
                                        (= parallel-mark :end))))
                        :outside :inside)]
          (case nesting
            :inside (case order
                      :after :after-inside
                      :before :before-inside)
            :outside order))))))

(defn- get-drop-position
  [drop-target {:keys [parallel-mark] :as props}]
  (let [path (get-in props [:action-path :scene])]
    {:target-path       (if (and (inside-parallel-action? parallel-mark)
                                 (some #{drop-target} [:after :before]))
                          (drop-last 2 path)
                          path)
     :relative-position (case drop-target
                          :after-inside :after
                          :before-inside :before
                          drop-target)}))

(defn action-unit
  [{:keys [parallel-mark action-data action-path type selected?] :as props}]
  (r/with-let [container-ref (r/atom nil)
               on-focus (atom [])
               handle-click #(do
                               (doall
                                (for [callback @on-focus]
                                  (callback)))
                               (re-frame/dispatch [::state/set-selected-action props]))

               ;; d&d
               drop-target (r/atom nil)
               prevent-defaults #(do (.preventDefault %)
                                     (.stopPropagation %))
               handle-drag-start (fn [event]
                                   (re-frame/dispatch [::state/remove-action action-path])
                                   (let [data-transfer (.-dataTransfer event)
                                         s (.stringify js/JSON (clj->js action-data))]
                                     (.setData data-transfer "action-data" s)))
               handle-drag-enter #(prevent-defaults %)
               handle-drag-leave #(do (prevent-defaults %) (reset! drop-target nil))
               handle-drag-over (fn [event]
                                  (prevent-defaults event)
                                  (reset! drop-target (drag-event->drop-target event parallel-mark)))
               handle-drop (fn [event]
                             (prevent-defaults event)
                             (let [{:keys [target-path relative-position]} (get-drop-position @drop-target props)
                                   transfer-data (utils/get-transfer-data event)
                                   transfer-data (if (nil? (:action transfer-data))
                                                   (let [data-transfer (.-dataTransfer event)
                                                         js-action-data (.parse js/JSON (.getData data-transfer "action-data"))
                                                         action-data (js->clj js-action-data :keywordize-keys true)]
                                                     {:action "move-action-unit" :action-data action-data})
                                                   transfer-data)]
                               (re-frame/dispatch [::state/handle-drag-n-drop (merge
                                                                               transfer-data
                                                                               {:target-type       type
                                                                                :target-path       target-path
                                                                                :relative-position relative-position})])
                               (reset! drop-target nil)))

               init-dnd (fn []
                          (.addEventListener @container-ref "dragstart" handle-drag-start)
                          (.addEventListener @container-ref "dragenter" handle-drag-enter)
                          (.addEventListener @container-ref "dragleave" handle-drag-leave)
                          (.addEventListener @container-ref "dragover" handle-drag-over true)
                          (.addEventListener @container-ref "drop" handle-drop))]
    [:div {:ref        #(when (and (nil? @container-ref) (some? %))
                          (reset! container-ref %)
                          (init-dnd))
           :on-click   handle-click
           :draggable  true
           :class-name (get-class-name {"action-unit"     true
                                        "parallel"        (not= parallel-mark :none)
                                        "parallel-start"  (= parallel-mark :start)
                                        "parallel-middle" (= parallel-mark :middle)
                                        "parallel-end"    (= parallel-mark :end)
                                        "selected"        selected?
                                        "drop-target"     (some? @drop-target)
                                        "drop-before"     (some #{@drop-target} [:before :before-inside])
                                        "drop-after"      (some #{@drop-target} [:after :after-inside])
                                        "drop-inside"     (some #{@drop-target} [:after-inside :before-inside])
                                        "drop-parallel"   (= @drop-target :parallel)
                                        })}
     (case type
       :character-animation [animation-unit props]
       :character-movement [movement-unit props]
       :effect [effect-unit props]
       :phrase [phrase-unit (assoc props :on-focus on-focus)]
       :text-animation [text-animation-unit props]
       :skip [skip-unit props]
       :background-music [background-music-unit props]
       :guide [guide-unit props]
       [unknown-element props])
     [unit-menu props]]
    (finally
      (.removeEventListener @container-ref "dragstart" handle-drag-start)
      (.removeEventListener @container-ref "dragenter" handle-drag-enter)
      (.removeEventListener @container-ref "dragleave" handle-drag-leave)
      (.removeEventListener @container-ref "dragover" handle-drag-over)
      (.removeEventListener @container-ref "drop" handle-drop))))
