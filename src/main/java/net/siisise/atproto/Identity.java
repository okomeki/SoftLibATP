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
import net.siisise.atp.lexicon.required;
import net.siisise.json.JSONObject;
import net.siisise.rest.RestException;

/**
 * ATPSession から分けたもの
 */
public class Identity {
    /**
     * Provides the DID of a repo.
     * @param session
     * @param handle
     * @return 
     */
    public static JSONObject resolveHandle(XRPC session, @format("handle") String handle) throws IOException, RestException {
        JSONObject param = new JSONObject();
        if ( handle != null ) {
            param.put("handle", handle);
        }
        JSONObject out = session.query("com.atproto.identity.resolveHandle", param);
        return out;
    }

    /**
     * 
     * @param session
     * @param handle 
     * @throws java.io.IOException 
     * @throws net.siisise.rest.RestException 
     */
    public static void updateHandle(XRPC session, @required @format("handle")String handle) throws IOException, RestException {
        JSONObject param = new JSONObject();
        param.put("handle", handle);
        session.xrpc("com.atproto.identity.updateHandle", param);
    }
    
    
}
