(ns webchange.student-dashboard.timeline.state
  (:require
    [re-frame.core :as re-frame]
    [webchange.events :as events]
    [webchange.interpreter.events :as ie]
    [webchange.interpreter.lessons.activity :as lessons-activity]
    [webchange.progress.activity :as common-activity]
    [webchange.state.state-course :as state-course]))

(defn- scene-name->scene [scene-name scenes]
  (let [{:keys [name preview type]} (get scenes (keyword scene-name))]
    {:id    scene-name
     :type  type
     :name  name
     :image preview}))

(defn- activity-letter
  [db {:keys [level lesson] :as activity}]
  (if-let [lesson-set-name (get-in db [:course-data :levels level :lessons lesson :lesson-sets :concepts-single])]
    (let [item-id (-> (get-in db [:lessons lesson-set-name :item-ids])
                      first)
          letter (get-in db [:dataset-items item-id :data :letter])]
      (assoc activity :letter letter))
    activity))

(re-frame/reg-sub
  ::finished-activities
  (fn [db]
    (let [scenes (get-in db [:course-data :scene-list])
          activities (common-activity/flatten-activities (get-in db [:course-data :levels]))]
      (->> activities
           (filter #(lessons-activity/finished? db % activities))
           (map #(activity-letter db %))
           (map (fn [{:keys [activity activity-name level lesson letter] :as activity-data}]
                  (let [{:keys [name preview]} (get scenes (keyword activity-name))]
                    {:id       (str level "-" lesson "-" activity)
                     :title    name
                     :activity activity-data
                     :letter   letter
                     :preview  preview})))))))

(defn get-next-activity
  [db]
  (let [scenes (get-in db [:course-data :scene-list])
        next (lessons-activity/get-progress-next db)
        activity (-> (get-in db [:course-data :levels])
                     (get-in [(:level next) :lessons (:lesson next) :activities (:activity next)]))]
    (merge next (scene-name->scene (:activity activity) scenes))))


(re-frame/reg-event-fx
  ::open-next-activity
  (fn [{:keys [db]} [_]]
    (let [next-activity (get-next-activity db)]
      {:dispatch [::open-activity next-activity]})))

(re-frame/reg-event-fx
  ::open-activity
  (fn [{:keys [db]} [_ activity]]
    (let [course (:current-course db)
          activity (select-keys activity [:level :lesson :activity :activity-name])]
      {:db         (lessons-activity/add-loaded-activity db activity)
       :dispatch-n (list [::ie/set-current-scene (:activity-name activity)]
                         [::events/redirect (str "/courses/" course)])})))

(re-frame/reg-sub
  ::loading?
  (fn [db]
    (or (get-in db [:loading :load-course])
        (get-in db [:loading :load-progress]))))

(re-frame/reg-sub
  ::course-finished?
  (fn []
    (re-frame/subscribe [::state-course/course-finished?]))
  (fn [course-finished?]
    course-finished?))
