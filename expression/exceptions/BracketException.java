package expression.exceptions;

import java.io.IOException;

public class BracketException extends IOException {
    public BracketException() {
        super("wrong brackets");
    }
}
