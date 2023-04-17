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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.siisise.io.FileIO;
import net.siisise.json.JSON;
import net.siisise.json.JSONObject;
import net.siisise.rest.RestException;
import net.siisise.setup.ProfSpool;

/**
 * セッション情報をてきとーに保存するさんぷる.
 * 設定ファイル構成はてきとー.
 * これは1サーバ1プロフィル構成.
 * 
 * {
 *  "https://servername": {
 *                           "account/email/handle/did":
 *                           "password": 暗号化する?
 *                           "session": { "jwsxxxx": }
 *                        }
 * }
 * 
 */
public class XRPCPersister extends XRPC {
    static final String FOLDER = ".siisise/atp";
    static final String PROPFILE = "atp.json";

    static JSONObject loadAll() throws FileNotFoundException, IOException {
            File atpFile = ProfSpool.load(FOLDER, PROPFILE);
            return(JSONObject) JSON.parse(FileIO.binRead(atpFile));
    }

    static void saveAll(JSONObject json) throws IOException {
        byte[] code = json.toJSON().getBytes(StandardCharsets.UTF_8);
        ProfSpool.save(FOLDER, PROPFILE, code);
    }

    /**
     * identify にあった使い方.
     * @param server https://servername
     * @return ATPSession
     */
    public static XRPC service(String server) {
        try {
            JSONObject json;
            try {
                json = loadAll();
            } catch ( FileNotFoundException e ) {
                json = new JSONObject();
            }
            JSONObject serverjson = (JSONObject) json.get(server);
            if ( serverjson != null ) {
                return new XRPC(server, (JSONObject) serverjson.get("session"));
            }
        } catch (IOException ex) {
        } catch (RestException ex) {
        }
        return XRPC.service(server);
    }

    /**
     * 保存するっぽいもの
     * 
     * @param session 
     */
    public static void update(XRPC session) {
        JSONObject sss = session.getSession();
        if ( sss == null ) {
            return;
        }
        JSONObject json;
        try {
            json = loadAll();
        } catch (IOException ex) {
            json = new JSONObject();
        }
        JSONObject serverjson = (JSONObject)json.get(session.getProvider());
        if ( serverjson == null ) {
            serverjson = new JSONObject();
        }
        serverjson.put("session", sss);
        try {
            json.put(session.getProvider(), serverjson);
            saveAll(json);
        } catch (IOException ex) {
        }
    }

    XRPCPersister(String server) {
        super(server);
    }
}
