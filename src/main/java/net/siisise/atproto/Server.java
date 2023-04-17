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
 *
 */
public class Server {

    public static class inviteCode {
        @required String code;
        @required int available;
        @required boolean disabled;
        @required String forAccount;
        @required String createdBy;
        @required @format("datetime") String createdAt;
        @required inviteCodeUse[] uses;
    }
    
    public static class inviteCodeUse {
        @required @format("did") String usedBy;
        @required @format("datetime") String usedAt;
    }

    public static JSONObject createAccount(XRPC session, @required String email, @required @format("handle")String handle, String inviteCode, @required String password, String recoveryKey) throws IOException, RestException {
        JSONObject params = new JSONObject();
        params.put("email", email);
        params.put("handle", handle);
        if ( inviteCode != null ) {
            params.put("inviteCode", inviteCode);
        }
        params.put("password", password);
        if ( recoveryKey != null ) {
            params.put("recoveryKey", recoveryKey);
        }
        return session.xrpc("com.atproto.server.createAccount", params);
    }
    
    public static JSONObject createInviteCode(XRPC session, int useCount) throws IOException, RestException {
        return session.xrpc("com.atproto.server.createInviteCode", "useCount", ""+useCount);
    }
    
    /**
     * 別のセッションを作る のか?
     * @param session
     * @param identifier
     * @param password
     * @return 
     * @throws java.io.IOException
     * @throws net.siisise.rest.RestException
     */
    public static JSONObject createSession(XRPC session, String identifier, String password) throws IOException, RestException {
        JSONObject sessionCode = session.xrpc("com.atproto.server.createSession", "identifier", identifier, "password", password);
        //System.err.println(sessionCode.toJSON(JSONValue.NOBR_MINESC));
        return sessionCode;
    }

    /**
     * 
     * @param session
     * @param did
     * @param password
     * @param token なに?
     */
    public static void deleteAccount(XRPC session, @format("did")String did, String password, String token) throws IOException, RestException {
        session.xrpc("com.atproto.server.deleteAccount", "did", did, "password", password, "token", token);
    }

    /**
     * このセッションを削除するのかな.
     * @param session
     * @throws IOException
     * @throws RestException 
     */
    public static void deleteSession(XRPC session) throws IOException, RestException {
        session.xrpc("com.atproto.server.deleteSession");
    }
    
    public static JSONObject descriveServer(XRPC session, Boolean inviteCodeRequired, String[] availableUserDomains, Links links) throws IOException, RestException {
        JSONObject params = new JSONObject();
        if ( inviteCodeRequired != null) {
            params.put("inviteCodeRequired", inviteCodeRequired.toString());
        }
        params.put("availableUserDomains", availableUserDomains);
        if ( links != null ) {
            params.put("links", links);
        }
        return session.query("com.atproto.server.describeServer", params);
    }
    
    public static class Links {
        public String privacyPolicy;
        public String termsOfService;
    }
    
    public static JSONObject getSession(XRPC session) throws IOException, RestException {
        return session.query("com.atproto.server.getSession");
    }
    
    public static JSONObject refreshSession(XRPC session) throws IOException, RestException {
        JSONObject sessionCode = session.xrpc("com.atproto.server.refreshSession");
        session.refreshSession(sessionCode);
        return sessionCode;
    }
    
    public static void requestAccountDelete(XRPC session) throws IOException, RestException {
        session.xrpc("com.atproto.server.requestAccountDelete");
    }

    /**
     * リセット用tokenをemailでもらう?
     * @param session
     * @param email メールアドレス
     * @throws IOException
     * @throws RestException 
     */
    public static void requestPasswordReset(XRPC session, String email) throws IOException, RestException {
        session.xrpc("com.atproto.server.requestPasswordReset", "email", email);
    }

    /**
     * token を使ってパスワードをリセットする.
     * @param session
     * @param token
     * @param password
     * @throws IOException
     * @throws RestException 
     */
    public static void resetPassword(XRPC session, String token, String password) throws IOException, RestException {
        session.xrpc("com.atproto.server.resetPassword", "token", token, "password", password);
    }
}
