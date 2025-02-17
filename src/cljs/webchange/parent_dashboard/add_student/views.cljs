(ns webchange.parent-dashboard.add-student.views
  (:require
    [re-frame.core :as re-frame]
    [webchange.parent-dashboard.add-student.state :as state]
    [webchange.parent-dashboard.layout.views :refer [layout]]
    [webchange.parent-dashboard.ui.index :refer [button circular-progress date input select]]))

(defn- name-control
  []
  (let [value @(re-frame/subscribe [::state/current-name])
        handle-change #(re-frame/dispatch [::state/set-name %])
        error @(re-frame/subscribe [::state/name-validation-error])]
    [input {:placeholder "Name"
            :required?   true
            :value       value
            :error       error
            :on-change   handle-change}]))

(defn- birth-date-control
  []
  (let [value @(re-frame/subscribe [::state/current-birth-date])
        handle-change #(re-frame/dispatch [::state/set-birth-date %])
        error @(re-frame/subscribe [::state/birth-date-validation-error])]
    [date {:placeholder "Birth date"
           :required?   true
           :mask        "mm/dd/yyyy"
           :value       value
           :error       error
           :on-change   handle-change}]))

(defn- course-control
  []
  (let [value @(re-frame/subscribe [::state/current-course])
        options @(re-frame/subscribe [::state/course-options])
        handle-change #(re-frame/dispatch [::state/set-course %])
        error @(re-frame/subscribe [::state/course-validation-error])]
    [select {:placeholder "Course"
             :required?   true
             :value       value
             :error       error
             :options     options
             :on-change   handle-change}]))

(defn- device-control
  []
  (let [value @(re-frame/subscribe [::state/current-device])
        options @(re-frame/subscribe [::state/device-options])
        handle-change #(re-frame/dispatch [::state/set-device %])
        error @(re-frame/subscribe [::state/device-validation-error])]
    [select {:placeholder "Device"
             :value       value
             :error       error
             :options     options
             :on-change   handle-change}]))

(defn- add-student-form
  []
  (re-frame/dispatch [::state/init])
  (fn []
    (let [loading? @(re-frame/subscribe [::state/loading?])
          handle-save #(re-frame/dispatch [::state/save])]
      [:div.add-student-form
       [:div.controls
        [name-control]
        [birth-date-control]
        [course-control]
        [device-control]]
       [:div.message
        [:p
         "Please enter a username or the child’s first name to be shown on their home screen.
          The name cannot be edited once created."]
        [:p
         "Also, we recommend playing TabSchool games on Android Tablets.
          Could you select what device the student will use?"]]
       [:div.actions
        [button {:class-name "submit-button"
                 :disabled   loading?
                 :on-click   handle-save
                 :variant    "contained"
                 :color      "default"}
         (if loading?
           [circular-progress]
           "Submit student")]]])))

(defn add-student-page
  []
  (let [handle-back-click #(re-frame/dispatch [::state/open-dashboard])]
    [layout {:title   "Add a student"
             :actions [[button {:on-click handle-back-click
                                :variant  "text"}
                        "< Back"]]}
     [add-student-form]]))
