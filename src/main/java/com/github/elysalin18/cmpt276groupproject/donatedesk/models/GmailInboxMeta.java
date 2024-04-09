package com.github.elysalin18.cmpt276groupproject.donatedesk.models;

import java.util.List;

public class GmailInboxMeta {
    private List<InboxMeta> messages;
    
    public GmailInboxMeta() {

    }
    public GmailInboxMeta(List<InboxMeta> messages) {
        this.messages = messages;
    }
    public List<InboxMeta> getMessages() {
        return messages;
    }
    public void setMessages(List<InboxMeta> messages) {
        this.messages = messages;
    }
}
