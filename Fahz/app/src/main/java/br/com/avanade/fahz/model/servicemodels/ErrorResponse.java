package br.com.avanade.fahz.model.servicemodels;

public class ErrorResponse extends CoreResponse {

    public static final int NO_INTERNET = -10;
    public static final int FAILURE = -11;
    public static final int EMPTY_DATA = -20;
    public static final int INVALID_DATA = -21;
    public static final int CANT_REACH_SERVICE = -32;
    public static final int UNKOWN_HOST = -9;

    /**
     * Use {@link #ErrorResponse(int)}.
     */
    private ErrorResponse() {
    }

    public ErrorResponse(int code) {
        setCode(code);
    }

    public ErrorResponse(int code, String msg) {
        setCode(code);
        setMessage(msg);
    }

    @Override
    public String toString() {
        return "Error Response{" +
                "code: " + getCode() +
                ", message: " + getMessage();
    }
}
