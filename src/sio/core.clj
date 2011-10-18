(ns sio.core
  (:require sio.netty)
  (:import
    [org.jboss.netty.handler.codec.http DefaultHttpResponse HttpVersion HttpResponseStatus]
    [org.jboss.netty.buffer ChannelBuffers]
    [org.jboss.netty.util CharsetUtil]))

(defn handler []
  (println "crete handler2!")
  (sio.netty/simple-channel-handler
    {:message-received
     (fn [ctx event]
       (let [channel (.getChannel event)
             message (.getMessage event)]
         (.write channel message)))
     :channel-connected
     (fn [ctx event] (println "connected!"))
     :channel-disconnected
     (fn [ctx event] (println "disconnected"))}))

(defn http-handler []
  (sio.netty/simple-channel-handler
    {:message-received
     (fn [ctx event]
       (println "http msg recvd")
       (let [response (new DefaultHttpResponse HttpVersion/HTTP_1_0 HttpResponseStatus/OK)]
         (doto response
           (.setContent (ChannelBuffers/copiedBuffer "Hello World!" CharsetUtil/UTF_8)))
         (println "write-respnse")
         (.write (.getChannel ctx) response)))
     :channel-connected
     (fn [ctx event] (println "http-connected!"))
     :channel-disconnected
     (fn [ctx event] (println "http-disconnected"))}))

(defn start-server []
  (sio.netty/simple-server
    (sio.netty/create-http-pipeline-factory (http-handler))
    8080))



