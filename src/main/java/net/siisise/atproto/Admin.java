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
public class Admin {
    
    public static class actionView {
        @required int id;
        @required actionType action;
        // union
        @required Ref subject;

        @required String[] subjectBlobCids;
        String[] createLabelVals;
        String[] negateLabelVals;
        @required String reason;
        @required @format("did") String createBy;
        @required @format("datetime") String createAt;
        actionReversal reversal;
        @required int[] resolveReportIds;
    }

    /** union #repoRef, com.atproto.repo.strongRef */
    public static interface Ref {
    }
    
    public static class actionViewDetail {
        @required int id;
        @required actionType action;
        // union repoView recordView
        @required View subject;

        @required blobView[] subjectBlobs;
        String[] createLabelVals;
        String[] negateLabelVals;
        @required String reason;
        @required @format("did") String createdBy;
        @required @format("datetime") String createdAt;
        actionReversal reversal;
        @required reportView[] resolvedReports;
    }
    
    public static class actionViewCurrent {
        @required int id;
        @required actionType action;
    }

    public static class actionReversal {
        @required String reason;
        @required @format("did") String createdBy;
        @required @format("datetime") String createdAt;
    }
    
    public enum actionType { // token
        // knownValues
        takedown,   // "Moderation action type: Takedown. Indicates that content should not be served by the PDS."
        flag,       // "Moderation action type: Flag. Indicates that the content was reviewed and considered to violate PDS rules, but may still be served."
        acknowledge // "Moderation action type: Acknowledge. Indicates that the content was reviewed and not considered to violate PDS rules."
    }
    
    public static class reportView {
        @required int id;
        @required Moderation.reasonType reasonType;
        String reason;
        //union
//        repoRef subject;
        @required Ref subject;
        
        @required @format("did") String reportedBy;
        @required @format("datetime") String createdAt;
        @required int[] resolvedByActionIds;
    }

    public static class reportViewDetail {
        @required int id;
        @required Moderation.reasonType reasonType;
        String reason;
        @required View subject;
        @required @format("did") String reportedBy;
        @required @format("datetime") String createdAt;
        @required actionView[] resolvedByActions;
    }
    
    // reportView, recordView
    static interface View {}
    
    static class repoView implements View {
        @required @format("did") String did;
        @required @format("handle") String handle;
        String email;
        @required Object[] relatedRecords;
        @required @format("datetime") String indexedAt;
        @required moderation moderation;
        Server.inviteCode invitedBy;
    }
    
    static class repoViewDetail {
        @required @format("did") String did;
        @required @format("handle") String handle;
        String email;
        @required Object[] relatedRecords;
        @required @format("datetime") String indexedAt;
        @required moderationDetail moderation;
        Label.label[] labels;
        Server.inviteCode invitedBy;
        Server.inviteCode[] invites;
    }
    
    static class repoRef implements Ref {
        @required @format("did") String did;
    }
    
    static class recordView implements View {
        @required @format("at-uri") String uri;
        @required @format("cid") String cid;
        @required Object value;
        @required @format("cid") String[] blobCids;
        @required @format("datetime") String indexedAt;
        @required moderation moderation;
        @required repoView repo;
    }
    
    static class recordViewDetail {
        @required @format("at-uri") String uri;
        @required @format("cid") String cid;
        @required Object value;
        @required blobView[] blobs;
        Label.label[] labels;
        @required @format("datetime") String indexedAt;
        @required moderationDetail moderation;
        @required repoView repo;
    }
    
    static class moderation {
        actionViewCurrent currentAction;
    }
    
    static class moderationDetail {
        actionViewCurrent currentAction;
        @required actionView[] actions;
        @required reportView[] reports;
    }
    
    static class blobView {
        @required @format("cid") String cid;
        @required String mimeType;
        @required int size;
        @required @format("datetime") String createdAt;
        details details;
        moderation moderation;
    }
    
    // imageDetails videoDetails
    static interface details {
        
    }
    
    static class imageDetails implements details {
        @required int width;
        @required int height;
    }
    
    static class videoDetails implements details {
        @required int width;
        @required int height;
        @required int length;
    }
    
    public enum Sort {
        recent,
        usage
    }
    
    /**
     * Admin view of invite codes
     * @param session
     * @param sort recent または usage
     * @param limit min 1 max 500 default 100
     * @param cursor
     * @return 
     * @throws java.io.IOException 
     * @throws net.siisise.rest.RestException 
     */
    public static JSONObject getInviteCodes(XRPC session, Sort sort, int limit, String cursor) throws IOException, RestException {
        return session.opt("sort", sort)
                .opt(limit > 0, "limit", limit)
                .opt("cursor", cursor)
                .query("com.atproto.admin.getInviteCodes");
    }
    
    /**
     * Disable some set of codes and/or all codes associated with a set of users
     * @param session
     * @param codes
     * @param accounts
     * @throws IOException
     * @throws RestException 
     */
    public static void disableInviteCodes(XRPC session, String[] codes, String[] accounts) throws IOException, RestException {
        session.opt("codes", codes)
                .opt("accounts", accounts)
                .xrpc("com.atproto.admin.desableInviteCodes");
    }
    
    public static JSONObject getModerationAction(XRPC session, @required int id) throws IOException, RestException {
        return session.req("id", id)
                .query("com.atproto.admin.getModerationAction");
    }

    public static JSONObject getModerationActions(XRPC session, String subject, int limit, String cursor) throws IOException, RestException {
        return session.opt("subject", subject)
                .opt(limit > 0, "limit", limit)
                .opt("cursor", cursor)
                .query("com.atproto.admin.getModerationActions");
    }
    
    public static JSONObject getModeratonReport(XRPC session, @required int id) throws IOException, RestException {
        return session.req("id", id)
                .query("com.atproto.admin.getModerationReport");
    }
    
    public static JSONObject getModerationReports(XRPC session, String subject, Boolean resolved, int limit, String cursor) throws IOException, RestException {
        return session.opt("subject", subject)
                .opt("resolved", resolved)
                .opt(limit > 0, "limit", limit)
                .opt("cursor", cursor)
                .query("com.atproto.admin.getModerationReports");
    }
    
    public static JSONObject resolveModerationReports(XRPC session, @required int actorId, @required int[] reportIds, @required @format("did")String createdBy) throws IOException, RestException {
        return session.req("actorId", actorId)
                .req("reportIds", reportIds)
                .req("createdBy", createdBy)
                .xrpc("com.atproto.admin.resolveModerationReports");
    }
    
    public static JSONObject reverseModerationAction(XRPC session, @required int id, @required String reason, @required @format("did")String createdBy) throws IOException, RestException {
        return session.req("id", id)
                .req("reason", reason)
                .req("createdBy", createdBy)
                .xrpc("com.atproto.admin.reverseModreationAction");
    }
    
    public static JSONObject getRecord(XRPC session, @required @format("at-uri") String uri, @format("cid") String cid) throws IOException, RestException {
        return session.req("uri", uri)
                .opt("cid", cid)
                .query("com.atproto.admin.getRecord");
    }
    
    public static JSONObject getRepo(XRPC session, @required @format("did") String did) throws IOException, RestException {
        return session.req("did",did).query("com.atproto.admin.getRepo");
    }
}
