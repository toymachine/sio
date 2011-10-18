(ns sio.handler
  (:import
    [org.jboss.netty.channel
     ChannelUpstreamHandler ChannelDownstreamHandler
     ChannelPipelineFactory Channels ChannelHandler
     MessageEvent WriteCompletionEvent ChannelStateEvent
     ExceptionEvent ChannelState]))

(defn simple-channel-handler [msg-handlers]
  (let [handle (fn [msg ctx event]
                 (when-let [fn (msg msg-handlers)] (fn ctx event)))]
    (reify
      ChannelUpstreamHandler
      (handleUpstream [_ ctx event]
                      (cond
                        (instance? MessageEvent event) (handle :message-received ctx event)
                        (instance? WriteCompletionEvent event) (handle :write-complete ctx event)
                        (instance? ExceptionEvent event) (handle :exception-caught ctx event)
                        (instance? ChannelStateEvent event)
                        (let [event_state (.getState event)
                              event_value (.getValue event)]
                          (cond
                            (= ChannelState/OPEN event_state) (if (true? event_value)
                                                                (handle :channel-open ctx event)
                                                                (handle :channel-close ctx event))
                            (= ChannelState/BOUND event_state) (if (nil? event_value)
                                                                 (handle :channel-unbound ctx event)
                                                                 (handle :channel-bound ctx event))
                            (= ChannelState/CONNECTED event_state) (if (nil? event_value)
                                                                     (handle :channel-disconnected ctx event)
                                                                     (handle :channel-connected ctx event))
                            :else (.sendUpstream ctx event)
                            ))
                        :else (.sendUpstream ctx event)))
      ChannelDownstreamHandler
      (handleDownstream [_ ctx event]
                        (cond
                          (instance? MessageEvent event) (handle :write-requested ctx event)
                          (instance? ChannelStateEvent event)
                          (let [event_state (.getState event)
                                event_value (.getValue event)]
                            (cond
                              (= ChannelState/OPEN event_state) (when (true? event_value)
                                                                  (handle :close-requested ctx event))
                              (= ChannelState/BOUND event_state) (if (nil? event_value)
                                                                   (handle :unbind-requested ctx event)
                                                                   (handle :bind-requested ctx event))
                              (= ChannelState/CONNECTED event_state) (if (nil? event_value)
                                                                       (handle :disconnect-requested ctx event)
                                                                       (handle :connect-requested ctx event))
                              :else (.sendDownstream ctx event)
                              ))
                          :else (.sendDownstream ctx event)
                          )))))

