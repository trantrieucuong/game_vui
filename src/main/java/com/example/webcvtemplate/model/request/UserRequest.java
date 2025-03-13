package com.example.webcvtemplate.model.request;

import com.example.webcvtemplate.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String userCode = UUID.randomUUID().toString();
    private String name;
    private String email;
    private String password;
//    private String avatar;
    private String sdt;
    private String province;
    private String district;
    private String town;
    private String address;
    private UserRole role;


}