package nl.ictu;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Identifier {

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("SS_SHOULD_BE_STATIC")
    private String version = "v1";
    private String bsn;
}
