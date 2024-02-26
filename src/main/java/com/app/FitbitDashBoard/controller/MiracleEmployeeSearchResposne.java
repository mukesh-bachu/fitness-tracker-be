package com.app.FitbitDashBoard.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiracleEmployeeSearchResposne {
    private String fname;
    private String employeeName;
    private String country;

    private String lname;
    private String email1;


    private String loginId;
    private String departmentId;
    private String practiceId;

    private String subPractice;
    private String WorkPhoneNo;
    private Boolean isFollowing;
}
