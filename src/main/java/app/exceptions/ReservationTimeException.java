package app.exceptions;

public class ReservationTimeException extends RuntimeException{
    public ReservationTimeException(String message) {
        super(message);
    }

    public ReservationTimeException() {
    }
}
