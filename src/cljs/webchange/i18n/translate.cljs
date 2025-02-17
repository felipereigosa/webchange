(ns webchange.i18n.translate
  (:require
    [re-frame.core :as re-frame]
    [taoensso.tempura :refer [tr]]
    [webchange.state.core :as state]))

(def translations
  {"english" {:book-library        "Book library"
              :categories          {:animals            "Animals"
                                    :family-and-friends "Family & Friends" ;; <- non-breaking space before "Friends"
                                    :science-stem       "Science"
                                    :sports             "Sports"
                                    :vehicles           "Vehicles"}
              :continue            "Continue"
              :error-general       "Something went wrong.. :("
              :exit                "Exit"
              :favorite            "Favorite"
              :favorite-books      "My Favorite books"
              :goodbye             "Goodbye!"
              :great-work          "Great work!"
              :home-page           "Home"
              :loading             "Loading"
              :next                "Next"
              :no-books            "There are no books yet..."
              :play                "Play"
              :read                {:read      "Read"
                                    :to-me     "Read to me"
                                    :by-myself "Read by myself"}
              :search              "Search"
              :search-book-library "Search Book Library"
              :skip                "Skip"
              :wait                "Please, wait..."
              :watch               "Watch"}
   "spanish" {:book-library        "Biblioteca"
              :categories          {:animals            "Animales"
                                    :family-and-friends "Familia y Amigos" ;; <- non-breaking space before "Amigos"
                                    :science-stem       "Ciencia"
                                    :sports             "Deportes"
                                    :vehicles           "Vehículos"}
              :continue            "Seguir"
              :error-general       "Algo salió mal.. :("
              :exit                "Salida"
              :favorite            "Favoritos"
              :favorite-books      "Mis libros favoritos"
              :goodbye             "Adiós!"
              :great-work          "¡Bien hecho!"
              :home-page           "Inicio"
              :loading             "Cargando"
              :next                "Sigue"
              :no-books            "Aún no hay libros..."
              :play                "Juega"
              :read                {:read      "Leer"
                                    :to-me     "¡Leeme!"
                                    :by-myself "Yo leo"}
              :search              "Buscar"
              :search-book-library "Buscar"
              :skip                "Omitir"
              :wait                "Espere por favor..."
              :watch               "Mira"}})

(defn- translate
  [language key]
  (tr {:dict translations} [language "english"] key))

(re-frame/reg-sub
  ::t
  (fn []
    (re-frame/subscribe [::state/current-course-id]))
  (fn [language [_ key]]
    (if (map? key)
      (->> key
           (map (fn [[field key]]
                  [field (translate language key)]))
           (into {}))
      (translate language key))))

