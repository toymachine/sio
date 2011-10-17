(ns sio.core
  (:import
    [org.jboss.netty.channel
     ChannelUpstreamHandler ChannelDownstreamHandler
     ChannelPipelineFactory Channels ChannelHandler]
    [org.jboss.netty.channel.socket.nio NioServerSocketChannelFactory]
    [org.jboss.netty.bootstrap ServerBootstrap]
    [java.util.concurrent Executors]
    [java.net InetSocketAddress]))

(defn create-handler []
  (reify
    ChannelUpstreamHandler
    (handleUpstream [_ ctx event]
                    (println ctx event))
    ChannelDownstreamHandler
    (handleDownstream [_ ctx event]
                      (println ctx event))))

(defn create-pipeline-factory [handler]
  (reify
    ChannelPipelineFactory
    (getPipeline [_]
                 (Channels/pipeline
                   (into-array ChannelHandler [handler])))))

(defn start-server []
  (let [factory (new NioServerSocketChannelFactory
                     (Executors/newCachedThreadPool)
                     (Executors/newCachedThreadPool))
        bootstrap (new ServerBootstrap factory)
        handler (create-handler)
        pipeline-factory (create-pipeline-factory handler)]
    (doto bootstrap
      (.setPipelineFactory pipeline-factory)
      (.setOption "child.tcpNoDelay" true)
      (.setOption "child.keepAlive" true)
      (.bind (new InetSocketAddress 8080)))))






