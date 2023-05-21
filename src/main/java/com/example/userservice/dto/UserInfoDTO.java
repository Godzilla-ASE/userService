package com.example.userservice.dto;

//import com.example.notificationsystem.constant.MessageType;

public class UserInfoDTO {
    private int userid_from;
    private int userid_to;
    private int send_to_client_id;
    private String type;

    private String send_to_client;

    public void setType(String type) {
        this.type = type;
    }


    public void setUserid_from(int userid_from) {
        this.userid_from = userid_from;
    }

    public void setUserid_to(int userid_to) {
        this.userid_to = userid_to;
    }
}
