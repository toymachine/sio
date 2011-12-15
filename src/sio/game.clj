(ns sio.game
  (:import
   [javax.swing JFrame JPanel Timer]
   [java.awt Toolkit Color Graphics2D]
   [java.awt.event ActionListener]
   [java.util Random]))

(set! *warn-on-reflection* true)

(defn render-panel [render]
  (proxy [JPanel ActionListener] []
    (paintComponent [g]
      (let [^Graphics2D g g
            ^JPanel this this
            size (.getSize this)]
        (proxy-super paintComponent g)
        (if-let [state (.getClientProperty this "game.state")]
          (render g (.width size) (.height size) state))
        (.. Toolkit getDefaultToolkit sync)
        (.dispose g)))))

(defn start-interval [interval action]
  (let [listener (reify ActionListener
                   (actionPerformed [this e]
                     (action)))
        timer (Timer. interval listener)]
    (.start timer)))

;current time seconds
(defn current-time []
  (/ (System/nanoTime) 1000000000))
  
(defn start [w h fps simulator-func render-func init-state]
  (let [frame (JFrame. "physics")
        ^JPanel panel (render-panel render-func)
        t0 (current-time)
        state (atom (list t0 init-state))]
    (doto frame
      (.setSize w h)
      (.setLocationRelativeTo nil)
      (.add panel)
      (.setVisible true))
    (letfn [(swapfn [state t]
              (let [dt (- t (first state))]
                (list t (simulator-func (second state) (- t t0) dt))))
            (simulate []
              (swap! state swapfn (current-time)))
            (loop []
              (doto panel
                (.putClientProperty "game.state" (second (simulate)))
                (.repaint)))]
      (start-interval (/ 1000 fps) loop))))

