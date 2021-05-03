(ns webchange.templates.handler
  (:require
    [compojure.api.sweet :refer [api context GET POST PUT DELETE defroutes]]
    [ring.util.response :refer [response]]
    [schema.core :as s]
    [webchange.templates.core :as core]
    [webchange.templates.library]))

(s/defschema Template {:id                              s/Int
                       :name                            s/Str
                       :description                     s/Str
                       :tags                            [s/Str]
                       (s/optional-key :preview)        s/Str
                       (s/optional-key :preview-anim)   [s/Str]
                       (s/optional-key :actions)        (s/maybe s/Any)
                       (s/optional-key :options)        (s/maybe s/Any)
                       (s/optional-key :options-groups) [{:title   s/Str
                                                          :options [s/Str]}]
                       (s/optional-key :fields)         (s/maybe s/Any)
                       (s/optional-key :lesson-sets)    (s/maybe s/Any)
                       (s/optional-key :props)          {:game-changer? (s/maybe s/Bool)}
                       (s/optional-key :version)        (s/maybe s/Int)})

(defroutes templates-api-routes
  (context "/api/templates" []
    :tags ["templates"]
    (GET "/" []
      :return [Template]
      :summary "Returns list of templates"
      (-> (core/get-available-templates) response))
    (GET "/:id/metadata" []
      :path-params [id :- s/Int]
      :return Template
      :summary "Returns template metadata by id"
      (-> (core/get-template-metadata-by-id id) response))))
