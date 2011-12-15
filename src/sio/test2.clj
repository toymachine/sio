(ns sio.test2
  (:use [sio.vector])
  (:require [sio.game :as game])
  (:import
   [java.awt Graphics2D Color]
   [java.util Random]))

(set! *warn-on-reflection* true)

(deftype State [position velocity])

(deftype Derivative [dx dv])


(defn spring [k b]
  ;creates an acceleration function for a spring
  (fn [^State state t]
    (v- (v* (- k) (.position state))
        (v* b (.velocity state)))))

;evaluate/integrate form the RK4 integrator
(defn evaluate [^State initial t dt ^Derivative derivative acceleration]
  (let [state (State. (v+ (.position initial) (v* dt (.dx derivative)))
                      (v+ (.velocity initial) (v* dt (.dv derivative))))]
    (Derivative. (.velocity state) (acceleration state (+ t dt)))))

(defn integrate [^State state t dt acceleration]
  (let [^Derivative a (evaluate state t 0 (Derivative. vector-3d-zero vector-3d-zero) acceleration)
        ^Derivative b (evaluate state t (* dt 1/2) a acceleration)
        ^Derivative c (evaluate state t (* dt 1/2) b acceleration)
        ^Derivative d (evaluate state t dt c acceleration)
        dxdt (v* 1/6 (v+ (.dx a) (v* 2 (v+ (.dx b) (.dx c))) (.dx d)))
        dvdt (v* 1/6 (v+ (.dv a) (v* 2 (v+ (.dv b) (.dv c))) (.dv d)))]
    (State. (v+ (.position state) (v* dt dxdt))
            (v+ (.velocity state) (v* dt dvdt)))))

;demo
(def acceleration
  (spring 20 0.5))

(defn simulate [state t dt]
  (integrate state t dt acceleration))

(defn render [^Graphics2D g w h ^State state]
  (doto g
    (.setColor Color/black)
    (.fillRect 0 0 w h)
    (.setColor Color/white)
    (.fillRect (- 200 (.x (.position state))) (- 200 (.y (.position state))) 15 15)))

(defn start []
  (game/start 400 400 25 simulate render (State. (vector-3d 200.0 100.0 0) vector-3d-zero)))
        
;(sim 100  1/10)