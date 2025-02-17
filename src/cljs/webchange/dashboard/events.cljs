(ns webchange.dashboard.events
  (:require
    [re-frame.core :as re-frame]
    [webchange.dashboard.classes.events :as classes-events]
    [webchange.dashboard.schools.events :as schools-events]
    [webchange.dashboard.courses.events :as courses-events]
    [webchange.dashboard.students.events :as students-events]
    [webchange.validation.validate :refer [validate]]))

(re-frame/reg-event-fx
  ::set-main-content
  (fn [{:keys [db]} [_ screen {:keys [class-id student-id]}]]
    (let [events (atom (list))]
      (when class-id (swap! events conj [::classes-events/set-current-class class-id]))
      (when student-id (swap! events conj [::students-events/set-current-student student-id]))
      {:db         (assoc-in db [:dashboard :current-main-content] screen)
       :dispatch-n @events})))

(re-frame/reg-event-fx
  ::open-student-profile
  (fn [{:keys [_]} [_ student-id]]
    {:dispatch-n (list
                   [::classes-events/load-classes]
                   [::students-events/load-unassigned-students]
                   [::students-events/load-student student-id])}))

(re-frame/reg-event-fx
  ::open-students
  (fn [{:keys [db]} [_ class-id]]
    {:dispatch-n (list
                   [::classes-events/load-classes]
                   [::students-events/load-unassigned-students]
                   [::students-events/load-students class-id])}))

(re-frame/reg-event-fx
  ::open-schools
  (fn [{:keys [db]} _]
    {:dispatch-n (list
                   [::schools-events/load-schools])}))

(re-frame/reg-event-fx
  ::open-courses
  (fn [{:keys [db]} _]
    {:dispatch-n (list
                   [::courses-events/load-courses]
                   [::courses-events/load-books]
                   [::courses-events/load-published-courses]
                   [::courses-events/load-published-books])}))

(re-frame/reg-event-fx
  ::open-classes
  (fn [{:keys [db]} _]
    {:dispatch-n (list
                   [::classes-events/load-classes]
                   [::students-events/load-unassigned-students])}))

(re-frame/reg-cofx
  :validate
  (fn [co-effects entity-type]
    (let [entity-data (->> co-effects :event last)]
      (assoc co-effects :validation-errors (validate entity-type entity-data)))))

(re-frame/reg-event-fx
  ::show-delete-school-form
  (fn [{:keys [db]} [_ school-id]]
    {:db       (assoc-in db [:dashboard :current-school-id] school-id)
     :dispatch [::schools-events/open-delete-modal]}))
