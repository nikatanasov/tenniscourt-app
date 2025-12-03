package app.exceptions;

public class WalletInactiveException extends RuntimeException{
    public WalletInactiveException(String message) {
        super(message);
    }

    public WalletInactiveException() {
    }
}
