package nl.ictu;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.Setter;

/**
 * Deze class kan vervangen worden door een @Data of record class
 *
 * ook is het handig om een domain-object/model in een package te zetten dat zijn intentie/bedoeling duidelijk maakt
 */
@Getter
@Setter
public final class Token {

    @SuppressFBWarnings("SS_SHOULD_BE_STATIC")
    private String version = "v1";
    private String bsn;
    private String recipientOIN;
    private Long creationDate;
}
