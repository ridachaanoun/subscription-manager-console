package util;

/**
 * Thrown when an entity could not be found.
 */
public class NotFoundException extends AppException {
    public NotFoundException(String message) { super(message); }
}
