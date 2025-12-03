package app.exceptions;

public class ReservationOverlapException extends RuntimeException{
    public ReservationOverlapException(String message) {
        super(message);
    }

    public ReservationOverlapException() {
    }
}
