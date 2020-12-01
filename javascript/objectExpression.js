"use strict";

const vars = {
    'x': 0,
    'y': 1,
    'z': 2
};

Const.ZERO = new Const(0);
Const.ONE = new Const(1);
const ln = x => new Log(new Const(Math.E), x);

function сommon(name, evaluate, toString, diff) {
    name.prototype.evaluate = evaluate;
    name.prototype.toString = toString;
    name.prototype.diff = diff;
    name.prototype.prefix = toString;
    name.prototype.postfix = toString;
}

function Const(value) {
    this.value = value;
}

сommon(
    Const,
    function () {
        return this.value;
    },
    function () {
        return this.value.toString();
    },
    function () {
        return Const.ZERO;
    }
);

function Variable(name) {
    this.name = name;
    this.ind = vars[name];
}

сommon(
    Variable,
    function (...args) {
        return args[this.ind];
    },
    function () {
        return this.name;
    },
    function (d) {
        if (d === this.name) {
            return Const.ONE;
        }
        return Const.ZERO;
    }
);


function Operation(...args) {
    this.args = args;
}

сommon(
    Operation,
    function (...values) {
        return this.op(...this.args.map(x => x.evaluate(...values)));
    },
    function () {
        return this.args.join(' ') + ' ' + this.opString;
    },
    function (d) {
        return this.diffOp(this.args, d);
    }
);

Operation.prototype.prefix = function () {
    return '(' + this.opString + ' ' + this.args.map(x => x.prefix()).join(' ') + ')';
};

Operation.prototype.postfix = function () {
    return '(' + this.args.map(x => x.postfix()).join(' ') + ' ' + this.opString + ')';
};

function makeOperation(op, opString, diffOp) {
    let Oper = function (...args) {
        Operation.apply(this, args);
    };
    Oper.prototype.constructor = Operation;
    Oper.prototype = Object.create(Operation.prototype);
    Oper.prototype.op = op;
    Oper.prototype.opString = opString;
    Oper.prototype.diffOp = diffOp;
    return Oper;
}

const Negate = makeOperation(a => -a, 'negate', (args, d) => new Negate(args[0].diff(d)));

const Add = makeOperation((a, b) => a + b, '+', (args, d) => new Add(args[0].diff(d), args[1].diff(d)));

const Subtract = makeOperation((a, b) => a - b, '-', (args, d) => new Subtract(args[0].diff(d), args[1].diff(d)));

const Multiply = makeOperation((a, b) => a * b, '*', function (args, d) {
    return new Add(
        new Multiply(args[0].diff(d), args[1]),
        new Multiply(args[0], args[1].diff(d))
    )
});

const Divide = makeOperation((a, b) => a / b, '/', function (args, d) {
    return new Divide(
        new Subtract(
            new Multiply(args[0].diff(d), args[1]),
            new Multiply(args[0], args[1].diff(d))
        ),
        new Multiply(args[1], args[1])
    )
});


const Power = makeOperation((a, b) => Math.pow(a, b), 'pow', function (args, d) {
    return new Multiply(
        this,
        new Multiply(args[1], ln(args[0])).diff(d)
    )
});

const Log = makeOperation((a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)), 'log', function (args, d) {
    return new Divide(
        new Subtract(
            new Divide(new Multiply(args[1].diff(d), ln(args[0])), args[1]),
            new Divide(new Multiply(args[0].diff(d), ln(args[1])), args[0])
        ),
        new Multiply(ln(args[0]), ln(args[0]))
    )
});

const Sumexp = makeOperation((...pows) => pows.map(x => Math.exp(x)).reduce(((answer, cur) => answer + cur), 0), 'sumexp',
    function (args, d) {
        return args.reduce((acum, cur) => new Add(acum, new Multiply(new Power(new Const(Math.E), cur), cur.diff(d))), Const.ZERO)
    });

const Softmax = makeOperation((...pows) => Math.exp(pows[0]) / pows.map(x => Math.exp(x)).reduce(((answer, cur) => answer + cur)), 'softmax',
    function (args, d) {
        return new Divide(new Power(new Const(Math.E), args[0]), new Sumexp(...args)).diff(d);
    });

const operations = {
    '+': Add,
    '-': Subtract,
    '*': Multiply,
    '/': Divide,
    'negate': Negate,
    'pow': Power,
    'log': Log,
    'sumexp': Sumexp,
    'softmax': Softmax
};

function parse(source) {
    let stack = [];
    source.split(" ").filter(x => x !== " ").forEach(token => {
        if (token in operations) {
            let newOp = operations[token];
            let args = stack.splice(-newOp.prototype.op.length);
            stack.push(new newOp(...args));
        } else if (token in vars) {
            stack.push(new Variable(token));
        } else if (!isNaN(parseFloat(token))) {
            stack.push(new Const(parseFloat(token)));
        }
    });
    return stack.pop();
}

function ParsingError(message) {
    this.message = message;
}

ParsingError.prototype = Object.create(Error.prototype);
ParsingError.prototype.name = "ParsingError";
ParsingError.prototype.constructor = ParsingError;

const parsePrefix = s => parseCommon(s, 'pre');
const parsePostfix = s => parseCommon(s, 'post');

function parseCommon(s, mode) {
    s = s.trim();
    let index = 0;
    if (index === s.length) {
        throw new ParsingError("Empty expression");
    }

    function skipWhitespace() {
        while (/\s/.test(s[index])) {
            index++;
        }
    }

    function nextToken() {
        skipWhitespace();
        let from = index;
        while (index < s.length && s[index] !== ' ' && s[index] !== ')' && s[index] !== '(') {
            index++;
        }
        return s.substring(from, index);
    }

    function parseElement() {
        skipWhitespace();
        if (s[index] === '(') {
            return parseExpression();
        }
        let token = nextToken();
        if (token in operations) {
            return token;
        }
        if (token in vars) {
            return new Variable(token);
        }
        if ((parseFloat(token).toString() === token)) {
            return new Const(parseFloat(token));
        }
        if (token === '') {
            throw new ParsingError("Expected operation at " + index + " position");
        }
        throw new ParsingError("Invalid token at " + (index - token.length) + " position, got \'" + token + "\', expected argument or operation");
    }

    function parseExpression() {
        if (s[index] !== '(') {
            let t = parseElement();
            if (t in operations) {
                throw new ParsingError("Missing opening bracket at " + index + " position");
            }
            return t;
        } else {
            index++;
            let newOp;
            let operands = [];

            if (mode === 'pre') {
                let expectedOperation = nextElement();
                if (!(expectedOperation in operations)) {
                    throw new ParsingError("Expected operation at " + (index - expectedOperation.length) + " position, got \'" + expectedOperation + "\'");
                }
                newOp = operations[expectedOperation];
            }

            let arg = parseElement();
            while (!(arg in operations) && s[index] !== ')') {
                operands.push(arg);
                arg = parseElement();
            }
            if (mode === "post") {
                if (!(arg in operations)) {
                    throw new ParsingError("Expected operation at " + index + " position");
                }
                newOp = operations[arg];
            }

            if (operands.length !== newOp.prototype.op.length && newOp !== Sumexp && newOp !== Softmax) {
                throw new ParsingError("Wrong arguments number for \'" + newOp.prototype.opString + "\' at " + (index - arg.length) + " position. Expected " +
                    newOp.prototype.op.length + ", got " + operands.length);
            }

            skipWhitespace();
            if (s[index] !== ")") {
                throw new ParsingError("Missing closing bracket at " + index + " position");
            }
            index++;
            return new newOp(...operands);
        }
    }

    let result = parseExpression();
    if (index < s.length) {
        throw new ParsingError("Unexpected symbols at " + index + " position");
    }

    return result;
}