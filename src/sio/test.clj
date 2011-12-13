(ns sio.test)

(set! *warn-on-reflection* true)

(deftype State [x v])

(deftype Derivative [dx dv])

(def FORCE 10)
(def MASS 1)

(println (/ FORCE MASS))

(defn euler [^State s dt]
  (let [x (+ (.x s) (* (.v s) dt))
        v (+ (.v s) (* (/ FORCE MASS) dt))]
    (State. x v)))

(defn pstat [t ^State s]
  (println t "pos" (.x s) "vel" (.v s)))

(defn sim [end step init]
  (loop [t 0
         s init]
    (when (<= t end)
      (let [s2 (euler s step)]
        (pstat t s)
        (recur (+ t step) s2)))))

(sim 10 1 (State. 0 0))

  