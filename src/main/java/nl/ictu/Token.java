package nl.ictu;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public final class Token {

    @Getter
    @Setter
    public final class Identifier {
        private String type;
        private String value;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("SS_SHOULD_BE_STATIC")
    private String version = "v1";

    private String recipientOIN;
    private Identifier identifier = new Identifier();

    @JsonFormat
        (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date creationDate;
}
