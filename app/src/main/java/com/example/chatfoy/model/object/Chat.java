package com.example.chatfoy.model.object;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Chat {
    private String idSend;
    private String content;
    private boolean isSeen;
    @ServerTimestamp
    private Date time;


    public Chat() {
    }

    public Chat(String idSend, String content,boolean isSeen, Date time) {
        this.idSend = idSend;
        this.content = content;
        this.time = time;
        this.isSeen = isSeen;
    }

    public Chat(String idSend, String content, boolean isSeen) {
        this.idSend = idSend;
        this.content = content;
        this.isSeen = isSeen;
    }

    public String getIdSend() {
        return idSend;
    }

    public void setIdSend(String idSend) {
        this.idSend = idSend;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
