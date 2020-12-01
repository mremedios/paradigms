(defn sameLength [args]
      {:pre  [(every? vector? args)]
       :post [(not (nil? %))]}
      (apply == (mapv count args)))

(defn isVectors [args]
      {:pre [(not (nil? args))]}
      (and (every? (fn [x] (and (vector? x) (every? number? x))) args) (sameLength args)))

(defn isMatrices [args]
      {:pre [(not (nil? args))]}
      (and (every? vector? args) (every? (fn [x] (isVectors x)) args)))

(defn isTensors [& args]
      {:pre [(not (nil? args))]}
      (or (isVectors args) (and (every? vector? args) (sameLength args) (recur (into [] (apply concat args))))))

(defn com [check]
      (fn [f]
          {:pre [(not (nil? f))]
          :post [(not (nil? %))]}
          (fn [& args]
              {:pre [(check args)]
              :post [(check args)]}
              (apply mapv f args)))
      )

(def doVect (com isVectors))

(def v+ (doVect +))
(def v- (doVect -))
(def v* (doVect *))

(defn scalar [& args]
      {:pre [(isVectors args)]
      :post [(number? %)]}
      (apply + (apply v* args)))

(defn part [x y ind1 ind2]
      (- (* (nth x ind1) (nth y ind2)) (* (nth x ind2) (nth y ind1))))

(defn vect [& args]
      {:pre [(isVectors args) (== 3 (count (nth args 0)))]
      :post [(isVectors [%])]}
      (reduce (fn [x y] (vector
                          (part x y 1 2)
                          (part x y 2 0)
                          (part x y 0 1))) args))

(defn v*s [vec & args]
      {:pre  [(isVectors [vec]) (every? number? args)]
       :post [(isVectors [%])]}
      (mapv (partial * (apply * args)) vec))

(def doMat (com isMatrices))

(def m+ (doMat v+))
(def m- (doMat v-))
(def m* (doMat v*))

(defn m*s [mat & args]
      {:pre  [(isMatrices [mat]) (every? number? args)]
       :post [(isMatrices [%])]}
      (mapv (partial (fn [scalar vec] (v*s vec scalar)) (apply * args)) mat))

(defn m*v [mat v]
      {:pre [(isMatrices [mat]) (isVectors [v])]
       :post [(isVectors [%])]}
      (mapv (fn [vec] (scalar vec v)) mat))

(defn transpose [mat]
      {:pre [(isMatrices [mat])]
       :post [(isMatrices [%])]}
      (apply mapv vector mat))

(defn m*m [& args]
      {:pre [(isMatrices args)]
       :post [(isMatrices [%])]}
      (reduce (fn [a b]
                  {:pre [(sameLength [(nth a 0) (nth (transpose b) 0)])]
                   :post [(isMatrices [%])]}
                  (mapv (partial m*v (transpose b)) a)) args))

(defn doTensor [f]
      (fn [& args]
          {:pre [(apply isTensors args)]
           :post [(isTensors %)]}
          (letfn [(function [& args]
                            {:pre [(or (isVectors args) (and (every? vector? args) (sameLength args)))]}
                            (if (isVectors args)
                              (apply f args)
                              (apply mapv function args)))]
                 (apply function args)))
      )

(def t+ (doTensor v+))
(def t- (doTensor v-))
(def t* (doTensor v*))
