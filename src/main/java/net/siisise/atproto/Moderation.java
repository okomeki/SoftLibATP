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
import net.siisise.atproto.Admin.Ref;
import net.siisise.json.JSONObject;
import net.siisise.rest.RestException;

/**
 *
 */
public class Moderation {
    
    public static JSONObject createReport(XRPC session, reasonType reasonType, String reason, Ref subject) throws IOException, RestException {
        JSONObject params = new JSONObject();
        params.put("reasonType", reasonType);
        if ( reason != null ) {
            params.put("reason", reason);
        }
        params.put("subject", subject);
        return session.xrpc("com.atproto.moderation.createReport", params);
    }
    
    public enum reasonType {
        reasonSpam,
        reasonOther
    }
}
