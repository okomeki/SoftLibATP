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
import net.siisise.json.JSONObject;
import net.siisise.rest.RestException;

/**
 *
 */
public class Repo {

    public static void appliyWrites(XRPC session, @format("at-identifier") String repo, boolean validate, writes[] writes,
            @format("cid") String swapCommit) throws IOException, RestException {
        JSONObject params = new JSONObject();

        params.put("repo", repo);
        params.put("validate", validate);
        params.put("writes", writes);
        params.put("swapCommit", swapCommit);

        session.xrpc("com.atproto.repo.applyWeites", params);
    }

    public interface writes {
    }

    public static class create implements writes {

        @format("nsid")
        public String collection;
        public String rkey;
        public Object value;
    }

    public static class update implements writes {

        @format("nsid")
        public String collection;
        public String rkey;
        public Object value;
    }

    public static class delete implements writes {

        @format("nsid")
        String collection;
        String rkey;
    }

    public static JSONObject createRecord(XRPC session, @format("at-identifier") String repo, @format("nsid") String collection, String rkey, boolean validate, Object record, @format("cid") String swapCommit) throws IOException, RestException {
        JSONObject params = new JSONObject();
        params.put("repo", repo);
        params.put("collection", collection);
        params.put("rkey", rkey);
        params.put("validate", validate);
        params.put("record", record);
        params.put("swapCommit", swapCommit);

        return session.xrpc("com.atproto.repo.createRecord", params);
    }

    public static void deleteRecord(XRPC session, @format("at-identifier") String repo, @format("nsid") String collection, String rkey, @format("cid") String swapRecord, @format("cid") String swapCommit) throws IOException, RestException {
        JSONObject params = new JSONObject();
        params.put("repo", repo);
        params.put("collection", collection);
        params.put("rkey", rkey);
        params.put("swapRecord", swapRecord);
        params.put("swapCommit", swapCommit);

        session.xrpc("com.atproto.repo.deleteRecord", params);
    }

    public static JSONObject describeRepo(XRPC session, @format("at-identifier") String repo) throws IOException, RestException {
        JSONObject p = new JSONObject();
        p.put("repo", repo);
        return session.query("com.atproto.repo.describeRepo", p);
    }

    public static JSONObject getRecord(XRPC session, @format("at-identifier") String repo, @format("nsid") String collection, String rkey, @format("cid") String cid) throws IOException, RestException {
        JSONObject params = new JSONObject();
        params.put("repo", repo);
        params.put("collection", collection);
        params.put("rkey", rkey);
        params.put("cid", cid);

        return session.xrpc("com.atproto.repo.getRecord", params);
    }

    public static JSONObject listRecords(XRPC session, @format("at-identifier") String repo, @format("nsid") String collection,
            int limit, String rkeyStart, String rkeyEnd, boolean reverse) throws IOException, RestException {
        JSONObject params = new JSONObject();
        params.put("repo", repo);
        params.put("collection", collection);
        if (limit >= 1 && limit <= 100) {
            params.put("limit", limit);
        }
        params.put("rkeyStart", rkeyStart);
        params.put("rkeyEnd", rkeyEnd);
        if (!reverse) {
            params.put("reverse", reverse);
        }
        return session.xrpc("com.atproto.repo.listRecords", params);
    }

    public static JSONObject putRecord(XRPC session, @format("at-identifier") String repo, @format("nsid") String collection, String rkey, boolean validate, Object record, @format("cid") String swapRecord, @format("cid") String swapCommit) throws IOException, RestException {
        JSONObject params = new JSONObject();
        params.put("repo", repo);
        params.put("collection", collection);
        params.put("rkey", rkey);
        params.put("validate", validate);
        params.put("record", record);
        params.put("swapRecord", swapRecord);
        params.put("swapCommit", swapCommit);

        return session.xrpc("com.atproto.repo.putRecord", params);
    }

    public static class strongRef implements Admin.Ref {

        @format("at-uri")
        public String uri;
        @format("cid")
        public String cid;
    }

    /**
     *
     * @param session
     * @param encoding mime encoding
     * @param blob data
     * @return
     * @throws IOException
     * @throws RestException
     */
    public static JSONObject uploadBlob(XRPC session, String encoding, byte[] blob) throws IOException, RestException {
        return session.xrpcUploadBlob("com.atproto.repo.uploadBlob", encoding, blob);

    }
}
