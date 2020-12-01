package expression.parser;

import expression.exceptions.BracketException;
import expression.exceptions.IException;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Parser<T> {
    TripleExpression<T> parse(String expression) throws IException, BracketException;
}
