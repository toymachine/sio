(ns sio.core
  (:require [sio.netty :as netty]))

(defn http-message-received [ctx event]
  (println "http msg recvd")
  (println "write-respnse")
  (netty/write ctx (netty/simple-http-response "Hello World!")))

(defn http-handler []
  (netty/simple-channel-handler
    {:message-received http-message-received
     :channel-connected (fn [ctx event] (println "http-connected!"))
     :channel-disconnected (fn [ctx event] (println "http-disconnected"))}))

(defn start-server []
  (netty/simple-server
    (netty/create-http-pipeline-factory (http-handler))
    8080))



