(ns webchange.editor-v2.diagram.graph-builder.graph
  (:require
    [webchange.editor-v2.diagram.graph-builder.filters.phrases :refer [get-phrases-graph]]
    [webchange.editor-v2.diagram.graph-builder.scene-parser.parser :refer [parse-scene]]))

(defn get-diagram-graph
  ([scene-data diagram-mode]
    (get-diagram-graph scene-data diagram-mode {}))
  ([scene-data diagram-mode params]
   (cond
     (= diagram-mode :phrases) (-> scene-data
                                   (parse-scene)
                                   (get-phrases-graph))
     (= diagram-mode :translation) (-> scene-data
                                       (parse-scene (select-keys params [:start-node :concept-data])))
     :default (-> scene-data
                  (parse-scene)))))
