package utils;

public class ServiceException extends RuntimeException {
    private String error;

    public ServiceException(String error) {
        super(error);
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
