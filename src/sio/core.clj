(ns sio.core
  (:require [sio.netty :as netty])
  (:import
   [org.jboss.netty.handler.codec.http HttpRequest]
   [org.jboss.netty.handler.codec.http.websocketx WebSocketFrame WebSocketServerHandshaker
    WebSocketServerHandshakerFactory CloseWebSocketFrame PingWebSocketFrame TextWebSocketFrame
    PongWebSocketFrame]))

(def ^:dynamic *ws-handshaker-factory*
  (new WebSocketServerHandshakerFactory "ws://localhost:8080/websocket" nil false))

(defn handle-http-request [ctx resource-path content-type]
  (netty/write ctx (netty/simple-html-response (slurp resource-path) content-type)))

(defn handle-websocket-handshake-request [ctx request]
  (if-let [handshaker (.newHandshaker *ws-handshaker-factory* ctx request)]
    (do
      (println "exec opening handshake")
      (.executeOpeningHandshake handshaker ctx request)
      (println "opening handshake send"))
    (do
      (println "unsupported websocket version response")
      (.sendUnsupportedWebSocketVersionResponse *ws-handshaker-factory* ctx))))
    
(defn handle-websocket-frame [ctx frame]
  (println "handle ws frame" frame)
  (cond
   (instance? CloseWebSocketFrame frame)
   (printf "TODO")
   ;
   (instance? PingWebSocketFrame frame)
   (netty/write ctx (new PongWebSocketFrame (.getBinaryData frame)))
   ;
   (instance? TextWebSocketFrame frame)
   (let [text-msg (.getText frame)]
     (println "recvd msg: " text-msg)
     (netty/write ctx (new TextWebSocketFrame (.toUpperCase text-msg))))))
    
(defn http-message-received [ctx event]
  (let [request (.getMessage event)]
    (cond
     (instance? HttpRequest request)
     (let [uri (.getUri request)]
       (println "http request, uri" uri)
       (case uri
         "/" (handle-http-request ctx "static/index.html" "text/html")
         "/static/index.js" (handle-http-request ctx "static/index.js" "text/javascript")
         "/websocket" (handle-websocket-handshake-request ctx request)
         (handle-http-request ctx "static/notfound.html" "text/html")))
     (instance? WebSocketFrame request)
     (handle-websocket-frame ctx request)))
  (println "http message recvd done"))

(defn http-handler []
  (netty/simple-channel-handler
   {:message-received http-message-received
    :channel-connected (fn [ctx event] (println "http-connected!"))
    :channel-disconnected (fn [ctx event] (println "http-disconnected"))}))

(defn start-server []
  (netty/simple-server
   (netty/create-http-pipeline-factory (var http-handler))
   8080))




