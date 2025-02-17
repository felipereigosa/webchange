(ns webchange.question.create-thumbs-up-n-down
  (:require
    [webchange.question.common.check-button :as check-button]
    [webchange.question.common.layout-markup :refer [get-layout-coordinates]]
    [webchange.question.common.options-list :as options-list]
    [webchange.question.common.substrate :as substrate]
    [webchange.question.common.task-image :as task-image]
    [webchange.question.common.task-text :as task-text]
    [webchange.question.common.params :as params]
    [webchange.question.get-question-data :refer [param-name->object-name]]
    [webchange.question.utils :refer [merge-data task-has-image? task-has-text?]]))

(defn- create-options
  [{:keys [mark-options] :as form-data}
   layout
   {:keys [question-id] :as props}
   data-names
   creation-options]
  (let [options (->> mark-options
                     (map (fn [mark-option]
                            (let [value-prop-name (str mark-option "-value")
                                  text-prop-name (str mark-option "-text")
                                  image-prop-name (str mark-option "-image")]
                              {:value            (->> value-prop-name keyword (get form-data))
                               :text-name        (param-name->object-name (str "options-" "option-" mark-option "-text") question-id)
                               :text-props       (->> text-prop-name keyword (get form-data))
                               :text-param-name  text-prop-name
                               :image-name       (param-name->object-name (str "options-" "option-" mark-option "-image") question-id)
                               :image-props      (->> image-prop-name keyword (get form-data))
                               :image-param-name image-prop-name}))))]
    (options-list/create options form-data props layout data-names creation-options)))

(defn create
  [{:keys [alias options] :as form-data}
   data-names
   {:keys [object-name question-id task-image-param-name] :as props}
   {:keys [visible?] :as creation-options}]
  (let [{options-label :label} options
        substrate-name (str object-name "-substrate")
        options-name (str object-name "-options")
        task-text-group-name (str object-name "-task-text-group")

        task-image-object-name (param-name->object-name task-image-param-name question-id)
        task-image-container-name (str task-image-object-name "-container")

        has-image? (task-has-image? form-data)
        has-text? (task-has-text? form-data)

        layout (get-layout-coordinates form-data)]
    (cond-> {:objects {(keyword object-name) {:type      "group"
                                              :alias     alias
                                              :x         (:x params/template-size)
                                              :y         (:y params/template-size)
                                              :children  (cond-> [substrate-name
                                                                  options-name
                                                                  (get-in data-names [:check-button :objects :main])]
                                                                 has-text? (conj task-text-group-name)
                                                                 has-image? (conj task-image-container-name))
                                              :visible   visible?
                                              :editable? {:show-in-tree? true}}}}
            :always (merge-data (substrate/create {:object-name substrate-name}))
            :always (merge-data (create-options form-data
                                                layout
                                                (merge props
                                                       {:object-name options-name
                                                        :label-type  options-label
                                                        :question-id question-id})
                                                data-names
                                                creation-options))
            :always (merge-data (check-button/create data-names layout creation-options))
            has-text? (merge-data (task-text/create form-data
                                                    layout
                                                    (merge props
                                                           {:object-name task-text-group-name
                                                            :param-name  "task-text"
                                                            :question-id question-id})))
            has-image? (merge-data (task-image/create form-data
                                                      layout
                                                      (merge props
                                                             {:param-name  task-image-param-name
                                                              :container-name task-image-container-name
                                                              :image-name     task-image-object-name}))))))
