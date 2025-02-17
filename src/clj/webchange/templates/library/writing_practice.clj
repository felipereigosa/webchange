(ns webchange.templates.library.writing-practice
  (:require
    [webchange.templates.core :as core]
    [webchange.templates.utils.dialog :as dialog]))

(def m {:id          40
        :name        "Writing practice"
        :tags        ["Guided Practice"]
        :lesson-sets ["concepts-single"]
        :props       {:game-changer? true}
        :fields      [{:name "image-src",
                       :type "image"}
                      {:name "letter-src",
                       :type "image"}
                      {:name "letter-path"
                       :type "string"}
                      {:name "letter"
                       :type "string"}]
        :description "An animated character shows how to write a letter. Then users practice writing the same letter. The letter must be traced correctly in order to see it appear on their screen."})

(def t {:assets
        [{:url "/raw/img/library/painting-tablet/background.jpg", :type "image"}
         {:url "/raw/img/ui/back_button_01.png", :type "image"}
         {:url "/raw/img/library/painting-tablet/brush.png", :size 10, :type "image"}
         {:url "/raw/img/library/painting-tablet/felt-tip.png", :size 10, :type "image"}
         {:url "/raw/img/library/painting-tablet/pencil.png", :size 10, :type "image"}
         {:url "/raw/img/library/painting-tablet/eraser.png", :size 10, :type "image"}
         {:url "/raw/img/ui/circle.png" :size 1 :type "image"}
         {:url "/raw/img/ui/checkmark.png" :size 1 :type "image"}
         {:url "/raw/clipart/writing/arrow.png" :type "image"}
         {:url "/raw/clipart/writing/hand.png" :type "image"}
         {:url "/raw/clipart/writing/dot.png" :type "image"}],
        :objects
        {:background  {:type "background", :scene-name "background", :src "/raw/img/library/painting-tablet/background.jpg"},
         :letter-tutorial-path
         {:type         "animated-svg-path",
          :y            -37,
          :width        225,
          :height       300,
          :duration     5000,
          :fill         "transparent",
          :line-cap     "round",
          :path         "",
          :scale-x      4,
          :scale-y      4,
          :visible      false
          :states       {:hidden {:visible false}, :visible {:visible true}},
          :stroke       "#323232",
          :stroke-width 10},
         :letter-tutorial-trace
         {:type         "svg-path",
          :y            -37,
          :width        225,
          :height       300,
          :scene-name   "letter-tutorial-trace",
          :dash         [7 7],
          :data         "",
          :line-cap     "round",
          :rotation     0,
          :scale-x      4,
          :scale-y      4,
          :visible      false
          :states       {:hidden {:visible false}, :visible {:visible true}},
          :stroke       "#898989",
          :stroke-width 4},
         :text-tracing-pattern
         {:type        "text-tracing-pattern"
          :traceable   true
          :repeat-text 2
          :text        " "
          :y           100
          :enable?     false
          :visible     false
          :height      1000
          :spacing     100
          :alias       "Round 2 writing options"
          :dashed false
          :show-lines true
          :editable?  {:select false :drag false :show-in-tree? true}
          :actions     {:next-letter {:on "next-letter-activated" :type "action" :id "letter-finished-dialog"}
                        :finish      {:on "finish" :type "action" :id "text-finished"}
                        :click       {:on "click" :type "action" :id "timeout-timer"}}}
         :practice-canvas
         {:type    "painting-area"
          :tool    "felt-tip"
          :color   "#4479bb"
          :visible false
          :actions {:change {:on "click" :type "action" :id "timeout-timer"}}}
         :painting-toolset
         {:type       "painting-toolset"
          :transition "painting-toolset"
          :actions    {:change {:on "change" :type "action" :id "set-current-tool" :pick-event-param "tool"}}
          :visible    false}
         :colors-palette
         {:type       "colors-palette",
          :transition "colors-palette"
          :x          400
          :height     150
          :actions    {:change {:on "change" :type "action", :id "set-current-color" :pick-event-param "color"}}
          :visible    false}
         :hand
         {:type       "image",
          :x          1600,
          :y          225,
          :src        "/raw/clipart/writing/hand.png"
          :transition "hand",
          :scale 1.5},
         :next-button {:type    "image"
                       :x       1706 :y 132
                       :actions {:click {:id "finish-activity", :on "click", :type "action"}}
                       :filters [{:name "brightness" :value 0}
                                 {:name "glow" :outer-strength 0 :color 0xffd700}]
                       :visible false
                       :src     "/raw/img/ui/checkmark.png"}},
        :scene-objects
        [["background"]
         ["letter-tutorial-trace"
          "letter-tutorial-path"
          "text-tracing-pattern"
          "practice-canvas"
          "painting-toolset"
          "colors-palette"
          "next-button"
          "hand"]],
        :actions
        {:start-scene                 {:type "sequence-data",
                                       :data [{:type "start-activity"}
                                              {:type "lesson-var-provider", :from "concepts-single", :provider-id "concepts", :variables ["current-concept"]}
                                              {:type "set-variable" :var-name "stage" :var-value "1"}
                                              {:type      "set-attribute",
                                               :target    "letter-tutorial-trace",
                                               :from-var  [{:var-name "current-concept", :action-property "attr-value" :var-property "letter"}],
                                               :attr-name "data"}
                                              {:type      "set-attribute",
                                               :target    "letter-tutorial-path",
                                               :from-var  [{:var-name "current-concept", :action-property "attr-value" :var-property "letter"}],
                                               :attr-name "path"}
                                              {:type      "set-attribute",
                                               :target    "text-tracing-pattern",
                                               :from-var  [{:var-name "current-concept", :action-property "attr-value" :var-property "letter"}],
                                               :attr-name "text"}
                                              {:type "action" :id "dialog-instructions"}
                                              {:type      "set-attribute",
                                               :target    "text-tracing-pattern",
                                               :attr-name "enable?",
                                               :attr-value true}
                                              {:type "action" :id "timeout-timer"}

                                              {:type       "test-expression"
                                               :expression ["eq" ["len" ["." "@current-concept" ":letter"]] 1]
                                               :success    {:type "sequence-data"
                                                            :data [{:type "set-variable" :var-name "hand-offset" :var-value 610}
                                                                   {:type "set-attribute"
                                                                    :target "letter-tutorial-trace"
                                                                    :attr-name "x"
                                                                    :attr-value 750}
                                                                   {:type "set-attribute"
                                                                    :target "letter-tutorial-trace"
                                                                    :attr-name "visible"
                                                                    :attr-value true}
                                                                   {:type "set-attribute"
                                                                    :target "letter-tutorial-path"
                                                                    :attr-name "x"
                                                                    :attr-value 750}
                                                                   {:type "set-attribute"
                                                                    :target "letter-tutorial-path"
                                                                    :attr-name "visible"
                                                                    :attr-value true}]}
                                               :fail       {:type "sequence-data"
                                                            :data [{:type "set-variable" :var-name "hand-offset" :var-value 410}
                                                                   {:type "set-attribute"
                                                                    :target "letter-tutorial-trace"
                                                                    :attr-name "x"
                                                                    :attr-value 550}
                                                                   {:type "set-attribute"
                                                                    :target "letter-tutorial-trace"
                                                                    :attr-name "visible"
                                                                    :attr-value true}
                                                                   {:type "set-attribute"
                                                                    :target "letter-tutorial-path"
                                                                    :attr-name "x"
                                                                    :attr-value 550}
                                                                   {:type "set-attribute"
                                                                    :target "letter-tutorial-path"
                                                                    :attr-name "visible"
                                                                    :attr-value true}]}}
                                              {:type "action" :id "show-example"}]},
         :stop-activity               {:type "sequence-data"
                                       :data [{:type "remove-interval" :id "instructions-timeout"}
                                              {:type "stop-activity"}]}
         :finish-activity             {:type "sequence-data"
                                       :data [{:type "remove-interval" :id "instructions-timeout"}
                                              {:type "finish-activity"}]},
         :dialog-instructions (-> (dialog/default "Instructions..")
                                  (assoc :concept-var "current-concept")
                                  (assoc :available-activities ["show-example"])
                                  (assoc :unique-tag "instruction"))
         :dialog-instructions-second-stage (-> (dialog/default "Instructions second stage")
                                               (assoc :concept-var "current-concept")
                                               (assoc :available-activities []))
         :dialog-instructions-third-stage (-> (dialog/default "Instructions third stage")
                                              (assoc :concept-var "current-concept")
                                              (assoc :available-activities ["highlight-tools" "highlight-colors" "highlight-next"]))

         :show-example                {:type "sequence-data"
                                       :data [{:to {:x 1210, :y 540, :loop false, :duration 1.5}, :type "transition", :transition-id "hand"}
                                              {:id "visible", :type "state", :target "letter-tutorial-path"}
                                              {:data [{:type "path-animation", :state "play", :target "letter-tutorial-path"}
                                                      {:to            {:letter-path "", :scale {:x 4, :y 4}, :origin {:x 610, :y -80}, :duration 5},
                                                       :type          "transition",
                                                       :from-var      [{:var-name "current-concept", :action-property "to.letter-path" :var-property "letter"}
                                                                       {:var-name "hand-offset", :action-property "to.origin.x"}],
                                                       :transition-id "hand"}],
                                               :type "parallel"}
                                              {:to {:x 1490, :y 180, :loop false, :duration 1.5}, :type "transition", :transition-id "hand"}
                                              {:type "action" :id "demo-finished"}]}

         :highlight-tools             {:type               "transition"
                                       :transition-id      "painting-toolset"
                                       :return-immediately true
                                       :from               {:brightness 0},
                                       :to                 {:brightness 0.35 :yoyo true :duration 0.5}
                                       :kill-after         3000}
         :highlight-colors            {:type               "transition"
                                       :transition-id      "colors-palette"
                                       :return-immediately true
                                       :from               {:brightness 0},
                                       :to                 {:brightness 0.35 :yoyo true :duration 0.5}
                                       :kill-after         3000}
         :highlight-next              {:type               "transition"
                                       :transition-id      "next-button"
                                       :return-immediately true
                                       :from               {:brightness 0 :glow 0}
                                       :to                 {:brightness 0.1 :glow 10 :yoyo true :duration 0.5 :repeat 5}}
         :set-current-tool            {:type "sequence-data"
                                       :data [{:type        "set-attribute",
                                               :target      "practice-canvas"
                                               :attr-name   "tool"
                                               :from-params [{:param-property "tool", :action-property "attr-value"}]}
                                              {:type        "action"
                                               :from-params [{:param-property "tool", :action-property "id" :template "dialog-tool-%"}]}]}
         :set-current-color           {:type "sequence-data"
                                       :data [{:type        "set-attribute",
                                               :target      "practice-canvas"
                                               :attr-name   "color"
                                               :from-params [{:param-property "color", :action-property "attr-value"}]}
                                              {:type        "action"
                                               :from-params [{:param-property "color", :action-property "id" :template "dialog-color-%"}]}]}

         :dialog-tool-brush           (dialog/default "tool brush")
         :dialog-tool-felt-tip        (dialog/default "tool felt-tip")
         :dialog-tool-pencil          (dialog/default "tool pencil")
         :dialog-tool-eraser          (dialog/default "tool eraser")
         :dialog-color-4487611        (dialog/default "color blue")
         :dialog-color-9616714        (dialog/default "color green")
         :dialog-color-15569322       (dialog/default "color pink")
         :dialog-color-16631089       (dialog/default "color yellow")
         :dialog-color-65793          (dialog/default "color black")
         :letter-finished-dialog      (-> (dialog/default "letter finished")
                                          (assoc :concept-var "current-concept"))

         :demo-finished               {:type "sequence-data"
                                       :data [{:type "set-attribute" :target "letter-tutorial-path" :attr-name "visible" :attr-value false}
                                              {:type "set-attribute" :target "letter-tutorial-trace" :attr-name "visible" :attr-value false}
                                              {:type "set-attribute" :target "hand" :attr-name "visible" :attr-value false}
                                              {:type "set-attribute" :target "text-tracing-pattern" :attr-name "visible" :attr-value true}
                                              {:type "set-variable" :var-name "stage" :var-value "2"}
                                              {:type "action" :id "dialog-instructions-second-stage"}]}

         :text-finished               {:type "sequence-data"
                                       :data [{:type "remove-interval" :id "instructions-timeout"}
                                              {:type "action" :id "letter-finished-dialog"}
                                              {:type "action" :id "text-finished-dialog"}
                                              {:type "set-attribute" :target "letter-tutorial-path" :attr-name "visible" :attr-value false}
                                              {:type "set-attribute" :target "letter-tutorial-trace" :attr-name "visible" :attr-value false}
                                              {:type "set-attribute" :target "text-tracing-pattern" :attr-name "visible" :attr-value false}
                                              {:type "set-attribute" :target "practice-canvas" :attr-name "visible" :attr-value true}
                                              {:type "set-attribute" :target "painting-toolset" :attr-name "visible" :attr-value true}
                                              {:type "set-attribute" :target "colors-palette" :attr-name "visible" :attr-value true}
                                              {:type "set-attribute" :target "next-button" :attr-name "visible" :attr-value true}
                                              {:type "set-variable" :var-name "stage" :var-value "3"}
                                              {:type "action" :id "dialog-instructions-third-stage"}
                                              {:type "action" :id "timeout-timer"}]}
         :text-finished-dialog        (-> (dialog/default "text finished")
                                          (assoc :concept-var "current-concept"))
         :timeout-timer               {:type     "set-interval",
                                       :id       "instructions-timeout",
                                       :action   "timeout-instructions",
                                       :interval 25000}

         :timeout-instructions {:type "test-var-scalar"
                                :var-name "stage"
                                :value    "1"
                                :success {:type "action" :id "dialog-timeout-instructions"}
                                :fail {:type "test-var-scalar"
                                       :var-name "stage"
                                       :value    "2"
                                       :success {:type "action" :id "dialog-timeout-instructions-second-stage"}
                                       :fail {:type "action" :id "dialog-timeout-instructions-third-stage"}}}

         :dialog-timeout-instructions (-> (dialog/default "Timeout instructions")
                                          (assoc :concept-var "current-concept")
                                          (assoc :available-activities ["show-example"]))
         :dialog-timeout-instructions-second-stage
         (-> (dialog/default "Timeout instructions second stage")
             (assoc :concept-var "current-concept")
             (assoc :available-activities []))
         :dialog-timeout-instructions-third-stage
         (-> (dialog/default "Timeout instructions third stage")
             (assoc :concept-var "current-concept")
             (assoc :available-activities ["highlight-tools" "highlight-colors" "highlight-next"]))
         },
        :triggers {:stop {:on "back", :action "stop-activity"}, :start {:on "start", :action "start-scene"}},
        :metadata {:prev   "library", :autostart true
                   :tracks [{:title "1 Instructions stage 1"
                             :nodes [{:type      "dialog"
                                      :action-id :dialog-instructions}
                                     {:type      "dialog"
                                      :action-id :dialog-timeout-instructions}
                                     ]}
                            {:title "2 Instructions stage 2"
                             :nodes [{:type      "dialog"
                                      :action-id :dialog-instructions-second-stage}
                                     {:type      "dialog"
                                      :action-id :dialog-timeout-instructions-second-stage}
                                     ]}
                            {:title "3 Instructions stage 3"
                             :nodes [{:type      "dialog"
                                      :action-id :dialog-instructions-third-stage}
                                     {:type      "dialog"
                                      :action-id :dialog-timeout-instructions-third-stage}
                                     ]}
                            {:title "3 Colors"
                             :nodes [{:type      "dialog"
                                      :action-id :dialog-color-4487611}
                                     {:type      "dialog"
                                      :action-id :dialog-color-9616714}
                                     {:type      "dialog"
                                      :action-id :dialog-color-15569322}
                                     {:type      "dialog"
                                      :action-id :dialog-color-16631089}
                                     {:type      "dialog"
                                      :action-id :dialog-color-65793}]}
                            {:title "4 Tools"
                             :nodes [{:type      "dialog"
                                      :action-id :dialog-tool-brush}
                                     {:type      "dialog"
                                      :action-id :dialog-tool-felt-tip}
                                     {:type      "dialog"
                                      :action-id :dialog-tool-pencil}
                                     {:type      "dialog"
                                      :action-id :dialog-tool-eraser}]}]}})

(defn f
  [t args]
  t)

(core/register-template
  m
  (partial f t))
