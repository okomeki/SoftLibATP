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
package net.siisise.atp;

import java.io.IOException;
import java.util.Map;
import net.siisise.atproto.Server;
import net.siisise.json.JSONObject;
import net.siisise.json.JSONValue;
import net.siisise.rest.RestClient;
import net.siisise.rest.RestException;

/**
 * 実質 XRPC session.
 */
public class XRPC {

    String prov;

    // いろいろ拡張されそうならそのまま保存してもいい
    JSONObject sessionObject;

    /**
     * 将来的には WebSocket
     */
    RestClient rest;

    /**
     * 認証コードなしではじめる.
     *
     * @param prov PDC URL 最後のスラッシュはつけない https://servername
     */
    XRPC(String prov) {
        this.prov = prov;
        rest = new RestClient(prov + "/xrpc/", null);
        rest.addHeader("User-Agent", "SoftLibATP");
    }

    /**
     * rest に access token をセットして使える状態にする.
     *
     * @param rest
     * @param sss XRPCで作ったセッション情報
     * @throws IOException
     * @throws RestException
     */
    XRPC(String prov, JSONObject sss) throws IOException, RestException {
        this(prov);
        refreshSession(sss);
    }

    public String getProvider() {
        return prov;
    }

    public JSONObject getSession() {
        return sessionObject;
    }

    public String getAccessJwt() {
        return (String) sessionObject.get("accessJwt");
    }

    public String getRefreshJwt() {
        return (String) sessionObject.get("refreshJwt");
    }

    public String getHandle() {
        return (String) sessionObject.get("handle");
    }

    public String getDid() {
        return (String) sessionObject.get("did");
    }

    public void refreshSession(JSONObject sss) {
        sessionObject = sss;
        rest.setAccessToken(getAccessJwt());
    }

    /**
     * XRPCPersister.service(prov) 使うといいかも
     *
     * @param prov ぷろばいだ
     * @return セッション
     */
    public static XRPC service(String prov) {
        return new XRPC(prov);
    }

    /**
     * query / GET
     * procedure / POST の場合もあるかもしれない.
     *
     * @param code
     * @param param
     * @return
     * @throws IOException
     * @throws RestException
     */
    public JSONObject query(String code, Map<String, String> param) throws IOException, RestException {
        return (JSONObject) rest.get(code, param);
    }

    public JSONObject query(String code, String... params) throws IOException, RestException {
        return rest.get(code, params);
    }

    /**
     * procedure / POST
     * GET は 本体にデータを置けない.
     *
     * @param code
     * @param param
     * @return
     * @throws java.io.IOException
     * @throws net.siisise.rest.RestException
     */
    public JSONObject xrpc(String code, JSONValue param) throws IOException, RestException {
        return rest.postJSON(code, param);
    }

    public JSONObject xrpc(String code) throws IOException, RestException {
        return rest.post(code);
    }

    public JSONObject xrpcUploadBlob(String code, String mime, byte[] body) throws IOException, RestException {
        return rest.post(code, mime, body);
    }

    /**
     * procedure / POST
     *
     * @param code
     * @param params
     * @return
     * @throws IOException
     * @throws RestException
     */
    public JSONObject xrpc(String code, String... params) throws IOException, RestException {
        JSONObject paramObject = new JSONObject();
        for (int i = 0; i < params.length; i += 2) {
            paramObject.put(params[i], params[i + 1]);
        }
        return xrpc(code, paramObject);
    }

    /**
     * セッションを新しく作る.
     *
     * @param identifier handle または email などアカウントのようなもの
     * @param password パスワード
     * @throws IOException
     * @throws RestException
     */
    public void login(String identifier, String password) throws IOException, RestException {
        JSONObject sessionCode = Server.createSession(this, identifier, password);
        refreshSession(sessionCode);
        XRPCPersister.update(this);
    }

    /**
     * セッションを新しく作る.
     * Manager行き?
     *
     * @param prov サーバのURI
     * @param identifier handle または email など
     * @param password
     * @return
     * @throws IOException
     * @throws RestException
     */
    public static XRPC login(String prov, String identifier, String password) throws IOException, RestException {
        XRPC session = service(prov);
        session.login(identifier, password);
        return session;
    }

    /**
     * com.atproto.identity.resolveHandle
     *
     * Provides the DID of a repo.
     *
     * @param handle
     * @return did
     * @throws IOException
     * @throws RestException
     */
    public String resolveHandle(String handle) throws IOException, RestException {
        JSONObject ret = xrpc("com.atproto.identity.resolveHandle", "handle", handle);
        return (String) ret.get("did");
    }

    /**
     * Updates the handle of the account
     *
     * @param handle
     * @throws IOException
     * @throws RestException
     */
    public void updateHandle(String handle) throws IOException, RestException {
        net.siisise.atproto.Identity.updateHandle(this, handle);
    }

    /**
     * パラメータをJSONにまとめるだけ.
     */
    public class XRPCcall {

        JSONObject params = new JSONObject();

        /**
         * required parameter.
         * @param name parameter name.
         * @param val parameter value.
         * @return this
         */
        public XRPCcall req(String name, Object val) {
            params.put(name, val);
            return this;
        }

        /**
         * optional parameter.
         * @param name parameter name.
         * @param val parameter value.
         * @return this
         */
        public XRPCcall opt(String name, Object val) {
            if (val != null) {
                params.put(name, val);
            }
            return this;
        }

        /**
         * 条件が一致したときだけ追加する.
         * @param b 条件
         * @param name paramerer name
         * @param val parameter value
         * @return this
         */
        public XRPCcall opt(boolean b, String name, Object val) {
            if (b) {
                params.put(name, val);
            }
            return this;
        }

        public JSONObject xrpc(String code) throws IOException, RestException {
            return XRPC.this.xrpc(code, params);
        }

        public JSONObject query(String code) throws IOException, RestException {
            return XRPC.this.query(code, params);
        }
    }

    public XRPCcall req(String name, Object val) {
        XRPCcall c = new XRPCcall();
        return c.req(name, val);
    }

    public XRPCcall opt(String name, Object val) {
        XRPCcall c = new XRPCcall();
        return c.opt(name, val);
    }

    public XRPCcall opt(boolean b, String name, Object val) {
        XRPCcall c = new XRPCcall();
        return c.opt(b, name, val);
    }
}
