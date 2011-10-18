(ns sio.core
  (:require sio.handler)
  (:import
    [org.jboss.netty.channel
     ChannelPipelineFactory Channels ChannelHandler]
    [org.jboss.netty.channel.socket.nio NioServerSocketChannelFactory]
    [org.jboss.netty.bootstrap ServerBootstrap]
    [java.util.concurrent Executors]
    [java.net InetSocketAddress]))

(def msg-handlers {:message-received
                   (fn [ctx event]
                     ;echo
                     (let [channel (.getChannel event)
                           message (.getMessage event)]
                       (.write channel message)))
                   :channel-connected
                   (fn [ctx event] (println "connected!"))
                   :channel-disconnected
                   (fn [ctx event] (println "disconnected"))})

;for now we create a new channel handler for each new pipeline (connection)
;this allows us to reload the ns for interactive development
;later we can have a single handler for all pipelines (handler will be stateless anyway)
(defn create-pipeline-factory []
  (reify
    ChannelPipelineFactory
    (getPipeline [_]
                 (Channels/pipeline
                   (into-array ChannelHandler [(sio.handler/simple-channel-handler msg-handlers)])))))

(defn start-server []
  (let [factory (new NioServerSocketChannelFactory
                     (Executors/newCachedThreadPool)
                     (Executors/newCachedThreadPool))
        bootstrap (new ServerBootstrap factory)
        pipeline-factory (create-pipeline-factory)]
    (doto bootstrap
      (.setPipelineFactory pipeline-factory)
      (.setOption "child.tcpNoDelay" true)
      (.setOption "child.keepAlive" true)
      (.bind (new InetSocketAddress 8080)))))




