(ns webchange.editor-v2.dialog.dialog-form.views-upload-audio
  (:require
    [re-frame.core :as re-frame]
    [webchange.editor-v2.translator.translator-form.state.audios :as translator-form.audios]
    [webchange.ui-framework.components.index :refer [file]]
    [webchange.utils.deep-merge :refer [deep-merge]]))

(defn upload-audio
  [{:keys [input-props]}]
  (let [handle-change #(re-frame/dispatch [::translator-form.audios/upload-audio % {}])]
    [file (deep-merge {:type            "audio"
                       :on-change       handle-change
                       :show-file-name? false
                       :with-upload?    false
                       :button-text     "Choose File"}
                      input-props)]))
