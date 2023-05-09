/*
 * Copyright 2023 okome.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.siisise.atproto;

import java.io.IOException;
import net.siisise.atp.XRPC;
import net.siisise.atp.lexicon.format;
import net.siisise.atp.lexicon.maxLength;
import net.siisise.atp.lexicon.required;
import net.siisise.json.JSONObject;
import net.siisise.rest.RestException;

/**
 *
 */
public class Label {

    /**
     * Metadata tag on an atproto resource (eg, repo or record)
     */
    public static class label {
        @required @format("did") String src;
        @required @format("uri") String uri;
        @format("cid") String cid;
        @required @maxLength(128) String val;
        Boolean neg;
        @required @format("datetime") String cts;
    }
    
    public static JSONObject queryLabels(XRPC session, @required String[] uriPatterns, @format("did")String[] sources, int limit, String cursor) throws IOException, RestException {
        return session.req("uriPattern", uriPatterns)
                .opt("sources", sources)
                .opt(limit > 0, "limit", limit)
                .opt("cursor", cursor)
                .query("com.atproto.label.queryLabel");
    }
    
    /**
     * @deprecated よくわからない形式
     * @param session
     * @param cursor
     * @return
     * @throws IOException
     * @throws RestException 
     */
    /*
    public static JSONObject subscribeLabels(XRPC session, int cursor) throws IOException, RestException {
        session.opt("cursor", cursor)
                .query("com.atproto.label.subscribeLabel");
    }*/
}
