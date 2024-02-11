package com.home.account.data.dto;

import com.home.account.data.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDTO {
    private String id;
    private String description;
    private MessageStatus status;
    private Object data;
}
