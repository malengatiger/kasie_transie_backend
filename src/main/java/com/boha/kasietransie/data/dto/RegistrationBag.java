package com.boha.kasietransie.data.dto;

import lombok.Data;

@Data
public class RegistrationBag {
    private Association association;
    private User user;

    public RegistrationBag(Association association, User user) {
        this.association = association;
        this.user = user;
    }
}
