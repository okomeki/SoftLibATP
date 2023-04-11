package net.siisise.w3c.did;

import net.siisise.abnf.ABNF;
import net.siisise.abnf.ABNFReg;
import net.siisise.abnf.parser5234.ABNF5234;
import net.siisise.abnf.rfc.URI3986;

/**
 * https://www.w3.org/TR/did-core/#did-syntax
 */
public class DIDReg {
    public static final ABNFReg REG = new ABNFReg(URI3986.REG);
    
    public static final ABNF pctEncoded = REG.rule("pct-encoded", ABNF.bin('%').pl(ABNF5234.HEXDIG, ABNF5234.HEXDIG));
    public static final ABNF idchar = REG.rule("idchar", ABNF5234.ALPHA.or(ABNF5234.DIGIT, ABNF.binlist(".-_"), pctEncoded));
    public static final ABNF methodSpecificId = REG.rule("method-specific-id", idchar.x().pl(ABNF.bin(':')).x().pl(idchar.ix()));
    public static final ABNF methodChar = REG.rule("method-char", ABNF.range(0x61,0x7a).or(ABNF5234.DIGIT));
    public static final ABNF methodName = REG.rule("method-name", methodChar.ix());
    public static final ABNF did = REG.rule("did", ABNF.text("did:").pl(methodName, ABNF.bin(':'), methodSpecificId));
    // https://www.w3.org/TR/did-core/#did-url-syntax
    public static final ABNF didUrl = REG.rule("did-url", did.pl(URI3986.pathAbempty, ABNF.bin('?').pl(URI3986.query).c(), ABNF.bin('#').pl(URI3986.fragment).c()));

}
