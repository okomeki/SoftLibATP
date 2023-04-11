package net.siisise.atp;

import net.siisise.abnf.ABNF;
import net.siisise.abnf.ABNFReg;
import net.siisise.abnf.rfc.URI3986;
import net.siisise.w3c.did.DIDReg;

/**
 *
 */
public class ATPReg {
    public static ABNFReg REG = new ABNFReg();
    
    // NSIDs
    public static ABNF alpha = REG.rule("alpha", ABNF.range('a','z').or(ABNF.range('A','Z')));
    public static ABNF number = REG.rule("number", ABNF.range('0','9'));
    public static ABNF delim = REG.rule("delim", ABNF.bin('.'));
    public static ABNF segment = REG.rule("segment", alpha.pl(alpha.or(number,ABNF.bin('-')).x()));
    public static ABNF authority = REG.rule("authority", segment.pl(delim.pl(segment).x()));
    public static ABNF name = REG.rule("name",segment);
    public static ABNF nsid = REG.rule("nsid",authority.pl(delim,name));
    public static ABNF nsid_ns = REG.rule("nsid-ns",authority.pl(delim,ABNF.bin('*')));
    
    public static ABNFReg URIREG = new ABNFReg();

    static ABNF fragment = URIREG.rule("fragment",URI3986.fragment);
    static ABNF pchar = URIREG.rule("pchar", URI3986.pchar);
    static ABNF reg_name = URIREG.rule("reg_name", URI3986.regName);
    static ABNF did = URIREG.rule("did", DIDReg.did);
    
    // ATP URI Scheme
    public static ABNF record_id = URIREG.rule("record-id", pchar.ix());
    public static ABNF coll_nsid = URIREG.rule("coll-nsid", nsid);
    public static ABNF path = URIREG.rule("path",ABNF.bin('/').pl(coll_nsid,ABNF.bin('/').pl(record_id).c()).c());
    public static ABNF uriauthority = URIREG.rule("authority",reg_name.or(did));
    public static ABNF atp_url = URIREG.rule("atp-url", ABNF.text("at://").pl(uriauthority,path, ABNF.bin('#').pl(fragment).c()));
    
    public static ABNFReg LexiconURIsReg = new ABNFReg();

    public static ABNF lexalpha = LexiconURIsReg.rule("alpha", ABNF.range('a','z').or(ABNF.range('A','Z')));
    public static ABNF lexnumber = LexiconURIsReg.rule("number", ABNF.range('0','9'));
    public static ABNF def_id = LexiconURIsReg.rule("def-id", lexalpha.pl(lexalpha.or(lexnumber).x()));
    public static ABNF lexuri = LexiconURIsReg.rule("uri", ABNF.text("lex:").pl(nsid, ABNF.bin('#').pl(def_id).c()));
    
}
