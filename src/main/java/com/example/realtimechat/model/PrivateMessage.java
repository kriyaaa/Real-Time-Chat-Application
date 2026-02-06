package com.example.realtimechat.model;

import lombok.Data;

@Data
public class PrivateMessage{
    private String receiver;
    private String content;
}
