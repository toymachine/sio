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
            ^JPanel this this]
        (proxy-super paintComponent g)
        (let [size (.getSize this)]
          (render g (.width size) (.height size))
          (.. Toolkit getDefaultToolkit sync)
          (.dispose g))))))


(defn start-interval [interval action]
  (let [listener (reify ActionListener
                   (actionPerformed [this e]
                     (action)))
        timer (Timer. interval listener)]
    (.start timer)))

(defn start [w h fps simulator-func render-func init-state]
  (let [frame (JFrame. "physics")
        ^JPanel panel (render-panel render-func)
        state (atom init-state)]
    (doto frame
      (.setSize w h)
      (.setLocationRelativeTo nil)
      (.add panel)
      (.setVisible true))
    (let [loop
          (fn [] (doto panel
                   (.putClientProperty "game.state" (swap! state simulator-func))
                   (.repaint)))]
      (start-interval (/ 1000 fps) loop))))

