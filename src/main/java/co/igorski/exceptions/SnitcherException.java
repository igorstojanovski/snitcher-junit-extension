package co.igorski.exceptions;

import java.io.IOException;

public class SnitcherException extends Exception {
    public SnitcherException(String message, IOException e) {
        super(message, e);
    }

    public SnitcherException(String message) {

        super(message);
    }
}
