package com.example.chatfoy.model.object;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomChat implements Serializable {
    private String roomId;
    private String roomName;
    private ArrayList<String> listIdMember;
    private String lastChat;
    private int type;

    public RoomChat() {
    }

    public RoomChat(String roomId, ArrayList<String> listIdMember, String lastChat, int type) {
        this.roomId = roomId;
        this.listIdMember = listIdMember;
        this.lastChat = lastChat;
        this.type = type;

    }

    public RoomChat(String roomId, ArrayList<String> listIdMember, int type) {
        this.roomId = roomId;
        this.listIdMember = listIdMember;
        this.type = type;

    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLastChat() {
        return lastChat;
    }

    public void setLastChat(String lastChat) {
        this.lastChat = lastChat;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<String> getListIdMember() {
        return listIdMember;
    }

    public void setListIdMember(ArrayList<String> listIdMember) {
        this.listIdMember = listIdMember;
    }
}
