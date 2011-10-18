(ns sio.core
  (:require [sio.netty :as netty]))

(defn handle-http-request [ctx resource-path]
  (netty/write ctx (netty/simple-html-response (slurp resource-path))))

(defn http-message-received [ctx event]
  (let [request (.getMessage event)
        uri (.getUri request)]
    (case uri
      "/" (handle-http-request ctx "static/index.html")
      (handle-http-request ctx "static/notfound.html"))))

(defn http-handler []
  (println "new http handler2")
  (netty/simple-channel-handler
   {:message-received http-message-received
    :channel-connected (fn [ctx event] (println "http-connected!"))
    :channel-disconnected (fn [ctx event] (println "http-disconnected"))}))

(defn start-server []
  (netty/simple-server
   (netty/create-http-pipeline-factory http-handler)
   8080))




