(ns sio.core
  (:require [sio.netty :as netty]))

(defn handle-http-request [ctx resource-path]
  (netty/write ctx (netty/simple-html-response (slurp resource-path))))

(defn handle-websocket-request [ctx request]
  (let [response (netty/websocket-handshake-response request)]
    (println "upgrayyed")
    (netty/websocket-upgrade ctx response)))
  
(defn http-message-received [ctx event]
  (let [request (.getMessage event)
        uri (.getUri request)]
    (case uri
      "/" (handle-http-request ctx "static/index.html")
      "/static/index.js" (handle-http-request ctx "static/index.js")
      "/websocket" (handle-websocket-request ctx request)
      (handle-http-request ctx "static/notfound.html"))))

(defn http-handler []
  (println "hello2!")
  (netty/simple-channel-handler
   {:message-received http-message-received
    :channel-connected (fn [ctx event] (println "http-connected!"))
    :channel-disconnected (fn [ctx event] (println "http-disconnected"))}))

(defn start-server []
  (netty/simple-server
   (netty/create-http-pipeline-factory (var http-handler))
   8080))




