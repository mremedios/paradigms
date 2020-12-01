"use strict";

const cnst = value => () => value;
const variable = name => {
    const ind = vars[name];
    return (...args) => args[ind];
};

const pi = cnst(Math.PI);
const e = cnst(Math.E);

const binaryFunction = h => (f, g) => (...args) => h(f(...args), g(...args));
const add = binaryFunction((a, b) => a + b);
const subtract = binaryFunction((a, b) => a - b);
const multiply = binaryFunction((a, b) => a * b);
const divide = binaryFunction((a, b) => a / b);

const unaryFunction = h => (f) => (...args) => h(f(...args));
const negate = unaryFunction(a => -a);

const avg5 = (...elements) => (...args) => elements.map(f => f(...args)).reduce((answer, cur) => answer + cur) / elements.length;
const med3 = (...elements) => (...args) => elements.map(f => f(...args)).sort((a, b) => b - a)[(elements.length - 1) / 2];

const vars = {
    'x': 0,
    'y': 1,
    'z': 2
};

const consts = new Map([
    ["pi", pi],
    ["e", e]
]);

const operations = new Map([
    ["+", add],
    ["-", subtract],
    ["*", multiply],
    ["/", divide],
    ["negate", negate],
    ["avg5", avg5],
    ["med3", med3]
]);

// :NOTE: Арность
const argumentsNumber = new Map([
    [add, 2],
    [subtract, 2],
    [multiply, 2],
    [divide, 2],
    [negate, 1],
    [avg5, 5],
    [med3, 3]
]);

const parse = source => {
    let stack = [];
    source.split(' ').forEach(token => {
        if (operations.has(token)) {
            let newOp = operations.get(token);
            let args = stack.splice(-argumentsNumber.get(newOp));
            stack.push(newOp(...args));
        } else if (consts.has(token)) {
            stack.push(consts.get(token));
        } else if (!isNaN(parseFloat(token))) {
            stack.push(cnst(parseFloat(token)));
        } else if (token in vars) {
            stack.push(variable(token));
        }
    });
    return stack.pop();
};


let test = add(
    subtract(
        multiply(
            variable('x'),
            variable('x')
        ),
        multiply(
            variable('x'),
            cnst(2)
        )
    ),
    cnst(1)
);


