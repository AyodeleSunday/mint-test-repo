package mint.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MintException extends RuntimeException{
    private static final long serialVersionUID = -5919447122348400126L;
    private String code = "10500";

    /**
     * Creates a new instance of
     * <code>CmsServiceException</code> without detail message.
     */
    public MintException() {
    }

    /**
     * Constructs an instance of
     * <code>CmsServiceException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public MintException(String msg) {
        super(msg);
    }

    public MintException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }
}
