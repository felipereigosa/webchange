(ns webchange.progress.core
  (:require [webchange.db.core :refer [*db*] :as db]
            [clojure.tools.logging :as log]
            [webchange.auth.core :as auth]
            [webchange.progress.activity :as activity]
            [webchange.events :as events]
            [webchange.progress.tags :as tags]
            [webchange.class.core :as class]
            [webchange.course.core :as course]
            [webchange.progress.finish :refer [get-finished-progress]]
            [java-time :as jt]
            [clojure.string :as str]))

(defn update-default-tags
  [user-id progress]
  (let [
        current-tags (get-in progress [:data :current-tags])
        {date-of-birth :date-of-birth} (db/get-student-by-user {:user_id user-id})
        now (jt/local-date)
        age (if date-of-birth
              (jt/time-between date-of-birth now :years)
              0)
        age-tag (if (<= 4 age) [tags/age-above-or-equal-4] [tags/age-less-4])
        level-tag (if (not (tags/has-one-from tags/learning-level-tags current-tags)) [tags/advanced] [])
        current-tags (-> current-tags
                         (tags/remove-tags tags/age-tags)
                         (concat age-tag)
                         (concat level-tag))]
    (assoc-in progress [:data :current-tags] (vec (set current-tags)))))

(defn get-current-progress [course-slug student-id]
  (let [{course-id :id} (db/get-course {:slug course-slug})
        progress (->> (db/get-progress {:user_id student-id :course_id course-id})
                      (update-default-tags student-id))]
    [true {:progress (:data progress)}]))

(defn get-class-profile [course-slug class-id]
  (let [{course-id :id} (db/get-course {:slug course-slug})
        activities-count (-> (course/get-course-data course-slug)
                             (get :levels)
                             (activity/flatten-activities)
                             (count))
        stats (->> (db/get-course-stats {:class_id class-id :course_id course-id})
                   (map class/with-user)
                   (map class/with-student-by-user))]
    [true {:stats                    stats
           :class-id                 class-id
           :course-name              course-slug
           :course-activities-number activities-count}]))

(defn workflow->grid
  [levels f]
  (let [->lesson (fn [idx lesson level] {:name   (str "L" idx)
                                         :values (map-indexed #(f %1 %2 level idx) (:activities lesson))})
        ->level (fn [idx level] [idx (map-indexed #(->lesson %1 %2 idx) (:lessons level))])]
    (->> levels
         (map-indexed ->level)
         (into {}))))

(defn- with-lesson-info
  [stat]
  (let [[level lesson activity] (str/split (:activity-id stat) #"-")]
    (-> stat
        (assoc-in [:data :level] (if (empty? level) 0 (Integer/parseInt level)))
        (assoc-in [:data :lesson] (if (empty? lesson) 0 (Integer/parseInt lesson)))
        (assoc-in [:data :activity] (if (empty? activity) 0 (Integer/parseInt activity))))))

(defn- stats->hash
  [stats]
  (let [->activity (fn [activity] [(:activity activity) activity])
        ->lesson (fn [[lesson-id activities]] [lesson-id (->> activities
                                                              (map ->activity)
                                                              (into {}))])
        ->level (fn [[level-id lessons]] [level-id (->> lessons
                                                        (group-by :lesson)
                                                        (map ->lesson)
                                                        (into {}))])]
    (->> stats
         (map with-lesson-info)
         (map :data)
         (group-by :level)
         (map ->level)
         (into {}))))

(defn ->percentage [value] (-> value (* 100) float Math/round))

(defn score->value [score is-scored]
  (let [correct (-> (:correct score) (or 1))
        incorrect (-> (:incorrect score) (or 0))]
    (cond
      (and score is-scored) (-> correct
                                (- incorrect)
                                (/ correct)
                                ->percentage)
      (and score) 100
      :else nil)))

(defn activity->score
  [stats]
  (let [hash (stats->hash stats)
        get-stat (fn [level lesson activity] (-> hash
                                                 (get level)
                                                 (get lesson)
                                                 (get activity)))]
    (fn [activity-idx {:keys [activity scored]} level lesson]
      (let [data (get-stat level lesson activity-idx)]
        {:label      activity
         :started    (boolean data)
         :finished   (-> data :score boolean)
         :percentage (score->value (-> data :score) scored)
         :value      (score->value (-> data :score) scored)}))))

(defn time->percentage
  [time expected]
  (if time
    (let [default-expected 300
          expected (or expected default-expected)
          elapsed (-> time (/ 1000) float Math/round)]
      (if (> elapsed expected)
        (-> (/ expected elapsed) ->percentage)
        100))))

(defn time->value
  [time]
  (let [elapsed (-> time (/ 1000) float Math/round)
        minutes (int (/ elapsed 60))
        seconds (int (- elapsed (* minutes 60)))]
    (str minutes "m " seconds "s")))

(defn activity->time
  [stats]
  (let [hash (stats->hash stats)
        get-stat (fn [level lesson activity] (-> hash
                                                 (get level)
                                                 (get lesson)
                                                 (get activity)))]
    (fn [activity-idx {:keys [activity time-expected]} level lesson]
      (let [data (get-stat level lesson activity-idx)]
        {:label      activity
         :started    (boolean data)
         :finished   (-> data :score boolean)
         :percentage (time->percentage (-> data :time-spent) time-expected)
         :value      (time->value (-> data (:time-spent 0)))}))))

(defn get-individual-progress [course-id student-id]
  (let [{user-id :user-id} (db/get-student {:id student-id})
        course-data (-> (db/get-latest-course-version {:course_id course-id})
                        :data)
        stats (db/get-user-activity-stats {:user_id user-id :course_id course-id})]
    [true {:stats  stats
           :scores (workflow->grid (:levels course-data) (activity->score stats))
           :times  (workflow->grid (:levels course-data) (activity->time stats))}]))

(defn save-events! [owner-id course-id events]
  (doseq [{created-at-string :created-at type :type :as data} events]
    (let [created-at (jt/offset-date-time created-at-string)]
      (db/create-event! {
                         :user_id    owner-id
                         :course_id  course-id
                         :created_at created-at
                         :type       type
                         :guid       (java.util.UUID/fromString (:id data))
                         :data       data
                         })
      (events/dispatch (-> data
                           (assoc :user-id owner-id)
                           (assoc :course-id course-id))))))

(defn create-progress! [owner-id course-id data]
  (let [[{id :id}] (db/create-progress! {:user_id owner-id :course_id course-id :data data})]
    [true {:id id}]))

(defn update-progress! [id data]
  (db/save-progress! {:id id :data data})
  [true {:id id}])

(defn save-progress!
  [owner-id course-slug {:keys [progress events]}]
  (let [{course-id :id} (db/get-course {:slug course-slug})]
    (save-events! owner-id course-id events)
    (if-let [{id :id} (db/get-progress {:user_id owner-id :course_id course-id})]
      (update-progress! id progress)
      (create-progress! owner-id course-id progress))))

(defn complete-individual-progress!
  [course-slug student-id {lesson-val :lesson level-val :level activity-val :activity navigation :navigation}]
  (let [level (when level-val (dec level-val))
        lesson (when lesson-val (dec lesson-val))
        activity (when activity-val (dec activity-val))
        {user-id :user-id} (db/get-student {:id student-id})
        {course-id :id} (db/get-course {:slug course-slug})
        course-data (course/get-course-data course-slug)
        levels (get course-data :levels)
        finished (get-finished-progress course-data {:level-idx    level
                                                     :lesson-idx   lesson
                                                     :activity-idx activity})
        current-tags (-> (db/get-progress {:user_id user-id :course_id course-id})
                         :data
                         :current-tags)
        next (activity/next-not-finished-for current-tags levels finished {:level level :lesson lesson :activity activity})
        progress (-> (db/get-progress {:user_id user-id :course_id course-id})
                     :data
                     (assoc :finished finished)
                     (assoc :next next)
                     (assoc :user-mode (if navigation "game-with-nav")))]
    (save-progress! user-id course-slug {:progress progress})
    progress))
