package formation_sopra.biblio.errors;

import java.io.Serial;
import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

@SuppressWarnings("java:S110")
public class BadRequestAlertException extends ErrorResponseException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String entityName;
    private final String errorKey;

    public BadRequestAlertException(String defaultMessage, String entityName, String errorKey) {
        this(URI.create("about:blank"), defaultMessage, entityName, errorKey);
    }

    public BadRequestAlertException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(
            HttpStatus.BAD_REQUEST,
            createProblemDetail(type, defaultMessage, entityName, errorKey),
            null
        );
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    private static ProblemDetail createProblemDetail(URI type, String message, String entityName, String errorKey) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setType(type);
        pd.setTitle(message);
        pd.setProperty("message", "error." + errorKey);
        pd.setProperty("params", entityName);
        return pd;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }
}