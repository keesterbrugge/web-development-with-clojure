(ns ring-app.core
  (:require [ring.adapter.jetty :as jetty]
            [muuntaja.middleware :as muuntaja]
            [ring.util.http-response :as response]
            [clj-http.client :as client]
            [reitit.ring :as reitit]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn html-handler [request-map]
  (response/ok
   (str "<html><body> your info + my info is 5: " request-map "</body></html>")))

(defn json-handler [request]
  (response/ok
   {:result (get-in request [:body-params :id])}))


(defn wrap-nocache [handler] (fn [request]
                               (-> request
                                   handler
                                   (assoc-in [:headers "Pragma"] "no-cache"))))

(defn wrap-formats [handler]
  (-> handler
      (muuntaja/wrap-format)))

(def routes
  [["/" {:get html-handler
         :post html-handler}]
   ["/echo/:id"
    {:get (fn [{{:keys [id]} :path-params}]
            (response/ok (str "<p> the value is: " id "</p>")))}]
   ["/api" {:middleware [wrap-formats]}
    ["/multiply"
     {:post (fn [{{:keys [a b]} :body-params}]
              (response/ok {:result (* a b)}))}]]])


(def handler
  (reitit/routes
   (reitit/ring-handler
    (reitit/router routes))
   (reitit/create-resource-handler {:path "/"})
   (reitit/create-default-handler
    {:not-found (constantly (response/not-found " my 404 - page not found man :/"))})))

(def server (jetty/run-jetty (-> #'handler
                                 wrap-nocache
                                 #_wrap-formats
                                 wrap-reload)
                              {:port 3001  :join? false}))



(defn -main []
  (.start server))

;; (comment 
(.stop server)
(.start server)

(client/get "http:localhost:3001")
(client/post "http:localhost:3001")

(client/get "http://localhost:3001/echo/5")

(->  (client/post "http://localhost:3001/api/multiply"
                  {:content-type :application/edn
                   :as :clojure
                   :body (str {:a 3 :b 9})})
     :body)

(->  (client/post "http:localhost:3001"
                  {:content-type :json
                   :body "{\"id\" : 2}"
                   })
     (doto prn)
     :body)

(client/get "http://localhost:3001/bullshit")
;; )
