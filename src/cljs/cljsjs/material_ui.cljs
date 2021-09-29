(ns cljsjs.material-ui
  (:require ["@material-ui/core" :as material-ui]
            ["@material-ui/core/styles" :as material-ui-styles]
            ["@material-ui/icons" :as material-ui-icons]))

(aset material-ui-icons "ContentCopy" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "ContentCut" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "ContentPaste" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "DoNotDisturb" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "DoNotDisturbAlt" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "DoNotDisturbOff" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "DoNotDisturbOn" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "FileDownload" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "FileUpload" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "InfoOutline" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "LabelOutline" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "LightbulbOutline" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "LockOutline" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "MailOutline" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "PauseCircleOutline" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "PeopleOutline" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "PersonOutline" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "PieChartOutline" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "PlayCircleOutline" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "RemoveCircleOutline" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "ModeEdit" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "SentimentNeutral" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "SimCardAlert" (aget material-ui-icons "Contacts"))
(aset material-ui-icons "SystemUpdateAlt" (aget material-ui-icons "Contacts"))

(js/goog.exportSymbol "MaterialUI" material-ui)
(js/goog.exportSymbol "MaterialUIIcons" material-ui-icons)
(js/goog.exportSymbol "MaterialUIStyles" material-ui-styles)
