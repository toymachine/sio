(ns sio.test2
  (:require [sio.game :as game])
  (:import
   [java.awt Graphics2D Color]
   [java.util Random]))

(set! *warn-on-reflection* true)

(deftype State [x v])

(deftype Derivative [dx dv])


(defn spring [k b]
  ;creates an acceleration function for a spring
  (fn [^State state t]
    (- (* (- k) (.x state)) (* b (.v state)))))

;evaluate/integrate form the RK4 integrator
(defn evaluate [^State initial t dt ^Derivative derivative acceleration]
  (let [state (State. (+ (.x initial) (* (.dx derivative) dt))
                      (+ (.v initial) (* (.dv derivative) dt)))]
    (Derivative. (.v state)
                 (acceleration state (+ t dt)))))

(defn integrate [^State state t dt acceleration]
  (let [^Derivative a (evaluate state t 0 (Derivative. 0 0) acceleration)
        ^Derivative b (evaluate state t (* dt 1/2) a acceleration)
        ^Derivative c (evaluate state t (* dt 1/2) b acceleration)
        ^Derivative d (evaluate state t dt c acceleration)
        dxdt (* 1/6 (+ (.dx a) (* 2 (+ (.dx b) (.dx c))) (.dx d)))
        dvdt (* 1/6 (+ (.dv a) (* 2 (+ (.dv b) (.dv c))) (.dv d)))]
    (State. (+ (.x state) (* dxdt dt))
            (+ (.v state) (* dvdt dt)))))


;demo
(def acceleration
  (spring 10 1))

(defn sim [steps ^State init dt]
  (loop [i steps
         t 0
         state init]
    (when (> i 0)
      (println i (float t) (float (.x state)) (float (.v state)))
      (recur (dec i) (+ t dt) (integrate state t dt acceleration)))))


(defn simulate [state]
  (println "sim")
  state)

(defn render [^Graphics2D g w h]
  (let [r (Random.)]
    (doto g
      (.setColor Color/black)
      (.fillRect 0 0 w h)
      (.setColor Color/white)
      (.fillRect (.nextInt r w) (.nextInt r h) 5 5))))

(game/start 400 400 25 simulate render (State. 100 0))
        
;(sim 100  1/10)