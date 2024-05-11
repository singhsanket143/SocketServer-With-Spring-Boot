package com.example.ubersocketserver.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String name;
    private String message;
    private String timeStamp;
}
