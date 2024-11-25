package nl.ictu;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


@Getter
@Setter
@ToString
public final class Token {
    private final String version = "v1";
    private String sinkOIN;
    private String identifierType;
    private String identifierValue;
    private Date creationDate;
}
