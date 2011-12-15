(ns sio.vector)

(set! *warn-on-reflection* true)

(deftype Vector3D [x y z])

(defn vector-3d [x y z]
  (Vector3D. x y z))

(def vector-3d-zero (vector-3d 0 0 0))
  
(defn v+
  ([^Vector3D a ^Vector3D b]
     (vector-3d (+ (.x a) (.x b))
                (+ (.y a) (.y b))
                (+ (.z a) (.z b))))
  ([^Vector3D a ^Vector3D b & more]
     (reduce v+ (v+ a b) more)))

(defn v-
  ([^Vector3D a ^Vector3D b]
     (vector-3d (- (.x a) (.x b))
                (- (.y a) (.y b))
                (- (.z a) (.z b))))
  ([^Vector3D a ^Vector3D b & more]
     (reduce v- (v- a b) more)))

(defn v* [s ^Vector3D a]
  (vector-3d (* (.x a) s)
             (* (.y a) s)
             (* (.z a) s)))

(defn str-vector [^Vector3D v]
  (str "[" (.x v) " " (.y v) " " (.z v) "]"))

(println (str-vector (v* 3.0 (vector-3d 1.0 1.0 1.0))))
(println (str-vector (v+ (vector-3d 1.0 1.0 1.0) (vector-3d 1.0 1.0 1.0) (vector-3d 1.0 1.0 1.0))))
