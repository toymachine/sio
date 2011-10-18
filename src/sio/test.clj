(ns sio.test)

(defn hello []
  (println "Hello World!"))

(defprotocol Blaat
  (aap [this b]))

(deftype X [ax bx]
  Blaat
  (aap [this b]
       (+ ax bx b)))

(deftype Y [ax bx]
  Blaat
  (aap [this b]
       (- ax bx b)))

(def xx
  (let [ax 20
        bx 20]
    (reify Blaat
      (aap [this b]
           (* ax bx b)))))

(println (aap (X. 10 20) 30))
(println (aap (Y. 10 20) 30))

(println (aap xx 30))

(println (butlast (concat [1 2] [3 4])))
