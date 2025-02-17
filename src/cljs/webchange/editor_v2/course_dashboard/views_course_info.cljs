(ns webchange.editor-v2.course-dashboard.views-course-info
  (:require
    [cljs-react-material-ui.reagent :as ui]
    [cljs-react-material-ui.icons :as ic]
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [webchange.editor-v2.events :as editor-events]
    [webchange.editor-v2.components.file-input.views :refer [select-file-form]]
    [webchange.editor-v2.assets.events :as assets-events]
    [webchange.editor-v2.subs :as editor-subs]
    [webchange.state.state-course :as state-course]
    [webchange.routes :refer [redirect-to]]
    [webchange.subs :as subs]))

(defn- get-styles
  []
  (let [image-size 60
        image-style {:height       image-size
                     :margin-right "15px"
                     :width        image-size}]
    {:file-input      {:display "none"}
     :image           image-style
     :image-clickable (merge image-style
                             {:cursor "pointer"})
     :image-label     {:margin-right "16px"}}))

(defn- image
  [{:keys [src loading? on-click]}]
  (let [styles (get-styles)]
    (if loading?
      [ui/avatar {:style (:image styles)}
       [ui/circular-progress]]
      [ui/tooltip {:title     "Click to change course image"
                   :placement "top"}
       [ui/avatar (merge {:on-click on-click
                          :style    (:image-clickable styles)}
                         (if (some? src) {:src src} {}))
        (when (nil? src) [ic/image])]])))

(defn- course-image
  [{:keys [data]}]
  (r/with-let [uploading (r/atom false)
               file-input (atom nil)

               handle-image-click #(.click @file-input)
               handle-finish-upload (fn [result]
                                      (reset! uploading false)
                                      (swap! data assoc :image-src (:url result)))
               handle-start-upload (fn [js-file]
                                     (reset! uploading true)
                                     (re-frame/dispatch [::assets-events/upload-asset js-file {:type      :image
                                                                                                 :on-finish handle-finish-upload}]))
               handle-input-change #(-> % (.. -target -files) (.item 0) handle-start-upload)
               styles (get-styles)]
    [ui/grid {:container   true
              :justify     "flex-start"
              :align-items "center"}
     [ui/typography {:variant "body1"
                     :style   (:image-label styles)}
      "Course Image"]
     [image {:src      (:image-src @data)
             :loading? @uploading
             :on-click handle-image-click}]
     [:input {:type      "file"
              :ref       #(reset! file-input %)
              :on-change handle-input-change
              :style     (:file-input styles)}]]))

(defn course-info
  [{:keys [title iconFlag?]}]
  (let [course-id @(re-frame/subscribe [::subs/current-course])
        info @(re-frame/subscribe [::state-course/course-info])]
    (if (empty? info)
      [ui/circular-progress]
      (r/with-let [data (r/atom info)]
        [ui/card {:style {:margin      "12px"
                          :flex-shrink "0"}}
         [ui/card-content {:style {:position "relative"}}
          [ui/typography {:variant "h5"
                          :style   {:margin-bottom   "12px"
                                    :display         "flex"
                                    :justify-content "space-between"}}
           title]
          (if iconFlag?
            [:div
             [ui/tooltip {:title "Show course table"}
              [ui/icon-button {:on-click #(redirect-to :course-table :id course-id)
                               :style    {:position "absolute"
                                          :margin   "8px"
                                          :top      "0"
                                          :right    "0"}}
               [ic/menu]]]
             [ui/tooltip {:title "Manage scenes crossing"}
              [ui/icon-button {:on-click #(redirect-to :scenes-crossing :course-id course-id)
                               :style    {:position "absolute"
                                          :margin   "8px"
                                          :top      "0"
                                          :right    "64px"}}
               [ic/compare-arrows]]]])
          [ui/grid {:container   true
                    :justify     "space-between"
                    :spacing     24
                    :align-items "center"}
           [ui/grid {:item true :xs 4}
            [ui/text-field {:label         "Name"
                            :full-width    true
                            :default-value (:name @data)
                            :variant       "outlined"
                            :on-change     #(swap! data assoc :name (-> % .-target .-value))}]]
           [ui/grid {:item true :xs 4}
            [ui/text-field {:label         "Language"
                            :full-width    true
                            :variant       "outlined"
                            :default-value (:lang @data)
                            :on-change     #(swap! data assoc :lang (-> % .-target .-value))}]]
           [ui/grid {:item true :xs 4}
            [course-image {:data data}]]]]
         [ui/card-actions
          [ui/button {:color    "secondary"
                      :style    {:margin-left "auto"}
                      :on-click #(re-frame/dispatch [::editor-events/edit-course-info @data])}
           "Save"]]]))))
