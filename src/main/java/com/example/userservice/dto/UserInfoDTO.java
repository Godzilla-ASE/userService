package com.example.userservice.dto;

//import com.example.notificationsystem.constant.MessageType;

public class UserInfoDTO {
    private int userid_from;
    private int userid_to;
    private int send_to_client_id;
    private String type;

    private String send_to_client;

    public void setSend_to_client(String send_to_client) {
        this.send_to_client = send_to_client;
    }

    public String getSend_to_client() {
        return send_to_client;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setSend_to_client_id(int send_to_client_id) {
        this.send_to_client_id = send_to_client_id;
    }

    public int getSend_to_client_id() {
        return send_to_client_id;
    }

    public int getUserid_from() {
        return userid_from;
    }

    public int getUserid_to() {
        return userid_to;
    }

    public void setUserid_from(int userid_from) {
        this.userid_from = userid_from;
    }

    public void setUserid_to(int userid_to) {
        this.userid_to = userid_to;
    }
}
