(ns testapp.core
  (:require [clj-http.client :as http-client]
            [net.cgrand.enlive-html :as html])
  (:gen-class))

(defn fetch-daily-menu-listing-page []
  (html/html-snippet
   (:body (http-client/get
           "http://pest.vakvarju.com/hu/napimenu"))))

(defn select-daily-menus [listing-page]
  (html/select listing-page
               [:div#etlapfelsorol :div.item]))

(defn normalize-daily-menu [menu-div]
  (let [raw-date (-> (html/select menu-div [:div.nev])
                     first :content first)
        raw-foods (-> (html/select menu-div [:div.text :h2])
                      first :content)]
    {:date raw-date
     :food-items (->> (filter string? raw-foods)
                      (map clojure.string/trim))}))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [daily-menus (->> (fetch-daily-menu-listing-page)
                         select-daily-menus
                         (map normalize-daily-menu))]
    (doseq [{:keys [date food-items]} daily-menus]
      (println date)
      (doseq [food-item food-items]
        (println food-item))
      (println)))

  (println "Mahlzeit!"))
