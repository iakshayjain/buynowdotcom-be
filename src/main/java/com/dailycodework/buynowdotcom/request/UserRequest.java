package com.dailycodework.buynowdotcom.request;

import lombok.*;
import org.hibernate.annotations.NaturalId;

@Data
public class UserRequest {

    private String firstName;
    private String lastName;

    @NaturalId
    private String email;
    private String password;
}
