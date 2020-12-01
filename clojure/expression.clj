; ========== 10 ==========
(defn constant [value] (fn [vars] value))
(defn variable [name] (fn [vars] (get vars name)))

(defn operation [f]
  (fn [& args]
    (fn [vars] (apply f (mapv #(% vars) args)))))

(defn unary [f]
  (fn [x]
    (fn [vars] (f (x vars)))))

(def add (operation +))
(def subtract (operation -))
(def multiply (operation *))
(def divide (operation
              (fn
                ([x] (/ (double x)))
                ([x & other] (reduce (fn [a b] (/ a (double b))) x other)))))
(def negate (unary -))

(def med (operation
           (fn [& args] (nth (sort args) (quot (count args) 2)))))

(defn avg [& args] (divide (apply add args) (constant (count args))))

(def parseOpFunction
  {'+      add,
   '-      subtract,
   '/      divide,
   '*      multiply,
   'negate negate,
   'med    med,
   'avg    avg})


(defn parseCommon [cnst vars ops]
  (fn [stringExpression]
    (letfn [(parse [expression]
              (cond
                (list? expression) (apply (get ops (first expression)) (mapv parse (rest expression)))
                (number? expression) (cnst expression)
                (symbol? expression) (vars (str expression))))]
      (parse (read-string stringExpression)))))

(def parseFunction (parseCommon constant variable parseOpFunction))

; ========== 11 ==========

(defn proto-get [obj key]
  (cond
    (contains? obj key) (obj key)
    (contains? obj :prototype) (proto-get (obj :prototype) key)
    :else nil))

(defn proto-call [this key & args]
  (apply (proto-get this key) this args))

(defn method [key]
  (fn [this & args] (apply proto-call this key args)))

(defn field [key]
  (fn [this] (proto-get this key)))

(defn constructor [ctor prototype]
  (fn [& args] (apply ctor {:prototype prototype} args)))

(def evaluate (method :evaluate))
(def toString (method :toString))
(def toStringInfix (method :toStringInfix))
(def diff (method :diff))

(def ZERO)
(def ConstantPrototype
  (let [_value (field :value)]
    {:evaluate      (fn [this vars] (_value this))
     :toString      (fn [this] (format "%.1f" (_value this)))
     :toStringInfix (fn [this] (format "%.1f" (_value this)))
     :diff          (fn [_ _] ZERO)
     }))

(defn Constant [value]
  {:prototype ConstantPrototype
   :value     value
   })

(def ZERO (Constant 0))
(def ONE (Constant 1))

(def VariablePrototype
  (let [_name (field :name)]
    {:evaluate      (fn [this var] (get var (_name this)))
     :toString      (fn [this] (_name this))
     :toStringInfix (fn [this] (_name this))
     :diff          (fn [this vars]
                      (if (= vars (_name this))
                        ONE
                        ZERO))
     }))

(defn Variable [name]
  {:prototype VariablePrototype
   :name      name
   })

(def CommonPrototype
  (let [_f (field :function)
        _d (field :diffFunction)
        _symbol (field :symbol)
        _args (field :arguments)]
    {:evaluate      (fn [this vars] (apply (_f this) (mapv #(evaluate % vars) (_args this))))
     :toString      (fn [this] (str "(" (_symbol this) " " (clojure.string/join " " (mapv #(toString %) (_args this))) ")"))
     :toStringInfix (fn [this] (if (== 1 (count (_args this)))
                                 (str (_symbol this) "(" (toStringInfix (first (_args this))) ")")
                                 (str "(" (clojure.string/join (str " " (_symbol this) " ") (mapv #(toStringInfix %) (_args this))) ")")))
     :diff          (fn [this vars]
                      ((_d this) (apply vector (_args this)) (mapv #(diff % vars) (_args this))))
     }))

(defn makeOperation [this & args]
  (assoc this :arguments args))

(defn createOperation [f symbol diffFunction]
  (constructor makeOperation
               {:prototype    CommonPrototype
                :function     f
                :diffFunction diffFunction
                :symbol       symbol
                }))

(def Add (createOperation + '+ (fn [_ ds] (apply Add ds))))

(def Sum (createOperation + 'sum (fn [_ ds] (apply Sum ds))))

(def Subtract (createOperation - '- (fn [_ ds] (apply Subtract ds))))

(def Multiply (createOperation * '* (fn [args ds]
                                      (apply Add (mapv #(apply Multiply (assoc args % (nth ds %))) (range (count args)))))))

(def Divide (createOperation (fn
                               ([x] (/ (double x)))
                               ([x & other] (reduce (fn [a b] (/ a (double b))) x other)))
                             '/
                             (fn [args ds]
                               (Divide
                                 (apply Subtract (mapv #(apply Multiply (assoc args % (nth ds %))) (range (count args))))
                                 (Multiply (apply Multiply (next args)) (apply Multiply (next args)))))))

(def Negate (createOperation - 'negate (fn [_ ds] (Negate (first ds)))))

(def Avg (createOperation
           (fn [& args] (/ (apply + args) (count args)))
           'avg
           (fn [args ds]
             (Divide (apply Sum ds) (Constant (count args))))))

(defn bits [oper s]
  (createOperation (fn [& args]
                     (Double/longBitsToDouble (apply oper (mapv #(Double/doubleToLongBits %) args))))
                   s
                   (fn [_ ds] (apply Add ds))))

(def And (bits bit-and '&))
(def Or (bits bit-or '|))
(def Xor (bits bit-xor (symbol "^")))

(defn PowLog [f]
  (fn [& args]
    (if (< 2 (count args))
      (f (first args) (f (rest args)))
      (f (first args) (second args)))))

(def Pow (createOperation
           (PowLog (fn [a b] (Math/pow a b)))
           '**
           nil))

(def Log (createOperation
           (PowLog (fn [a b] (/ (Math/log (Math/abs b)) (Math/log (Math/abs a)))))
           (symbol "//")
           nil))

(def parseOpObject
  {'+            Add,
   '-            Subtract,
   '/            Divide,
   '*            Multiply,
   'sum          Sum,
   'negate       Negate,
   'avg          Avg,
   '&            And,
   '|            Or,
   (symbol "^")  Xor,
   '**           Pow,
   (symbol "//") Log
   })

(def parseObject (parseCommon Constant Variable parseOpObject))

; ========== 12 ==========

(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)
(defn _show [result]
  (if (-valid? result) (str "-> " (pr-str (-value result)) " | " (pr-str (apply str (-tail result))))
                       "!"))
(defn tabulate [parser inputs]
  (run! (fn [input] (printf "    %-10s %s\n" (pr-str input) (_show (parser input)))) inputs))
(defn _empty [value] (partial -return value))
(defn _char [p]
  (fn [[c & cs]]
    (if (and c (p c)) (-return c cs))))
(defn _map [f result]
  (if (-valid? result)
    (-return (f (-value result)) (-tail result))))
(defn _combine [f a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        (_map (partial f (-value ar))
              ((force b) (-tail ar)))))))
(defn _either [a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar) ar ((force b) str)))))
(defn _parser [p]
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0000})) (str input \u0000)))))
(defn +char [chars] (_char (set chars)))
(defn +char-not [chars] (_char (comp not (set chars))))
(defn +map [f parser] (comp (partial _map f) parser))
(def +parser _parser)
(def +ignore (partial +map (constantly 'ignore)))
(defn iconj [coll value]
  (if (= value 'ignore) coll (conj coll value)))
(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))
(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps)))
(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps))
(defn +or [p & ps]
  (reduce _either p ps))
(defn +opt [p]
  (+or p (_empty nil)))
(defn +star [p]
  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))
(defn +plus [p] (+seqf cons p (+star p)))
(defn +str [p] (+map (partial apply str) p))
(def *digit (+char "0123456789"))
(def *number (+map read-string (+str (+plus *digit))))
(def *string
  (+seqn 1 (+char "\"") (+str (+star (+char-not "\""))) (+char "\"")))
(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))
(def *null (+seqf (constantly 'null) (+char "n") (+char "u") (+char "l") (+char "l")))
(def *all-chars (mapv char (range 32 128)))
(def *letter (+char (apply str (filter #(Character/isLetter %) *all-chars))))
(def *identifier (+str (+seqf cons *letter (+star (+or *letter *digit)))))

(def *doubleNumber (+map read-string (+str (+seq
                                             *ws
                                             (+opt (+char "+-"))
                                             *number
                                             (+str (+opt (+seq (+char ".") *number)))
                                             *ws))))

(def *bracket)
(def *constant (+map Constant (+map double *doubleNumber)))
(def *variable (+map Variable *identifier))

(defn +operation [p] (+map (partial get parseOpObject) (+map symbol (+map str p))))

(declare *negate)
(def *expr0 (+or *constant (delay *negate) *variable (delay *bracket)))

(def +negate (+operation (+seqf str (+char "n") (+char "e") (+char "g") (+char "a") (+char "t") (+char "e"))))

(def *negate (+map
               (fn [mp] ((first mp) (second mp)))
               (+seq +negate *ws *expr0)))

(def *pow (+operation (+seqf str (+char "*") (+char "*"))))
(def *log (+operation (+seqf str (+char "/") (+char "/"))))
(def *operators1 (+or *pow *log))

(def *multiply (+operation (+char "*")))
(def *divide (+operation (+char "/")))
(def *operators2 (+or *multiply *divide))

(def *add (+operation (+char "+")))
(def *subtract (+operation (+char "-")))
(def *operators3 (+or *add *subtract))

(def *and (+operation (+char "&")))
(def *or (+operation (+char "|")))
(def *xor (+operation (+char "^")))
;(applyBin (vector ((first (first s)) f (second (first s))) (rest s))) ) ))

(defn commonExpr [f]
  (fn [operands operators]
    (+map f (+seq *ws operands *ws  (+star (+seq *ws operators *ws operands *ws))) )))

(defn applyBinLeft [mp] (reduce (fn [*leftEx array] ((first array) *leftEx (second array))) (first mp) (second mp)))

(defn applyBinRight [mp]
  (let [f (first mp)
        s (second mp)]
    (if (empty? (first s))
      f
      ((first (first s)) f (applyBinRight (vector (second (first s))  (rest s))) )) ))

(def *leftEx (commonExpr applyBinLeft))
(def *rightEx (commonExpr applyBinRight))

(def *expression (*leftEx (*leftEx (*leftEx (*leftEx (*leftEx (*rightEx *expr0 *operators1) *operators2) *operators3) *and) *or) *xor))

(def *bracket (+seqn 1 *ws (+char "(") *expression (+char ")") *ws))

(def parseObjectInfix (+parser *expression))
