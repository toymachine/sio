(ns sio.core
  (:require sio.netty))

(def handler
  (sio.netty/simple-channel-handler
    {:message-received
     (fn [ctx event]
       ;echo
       (let [channel (.getChannel event)
             message (.getMessage event)]
         (.write channel message)))
     :channel-connected
     (fn [ctx event] (println "connected!"))
     :channel-disconnected
     (fn [ctx event] (println "disconnected"))}))

(defn start-server []
  (sio.netty/simple-server
    (sio.netty/create-pipeline-factory handler)
    8080))



