package com.app.FitbitDashBoard.Utility;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.app.FitbitDashBoard.controller.MiracleEmployeeSearchResposne;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseWrapper {
    private List<MiracleEmployeeSearchResposne> data;
    private boolean success;
    private String message;
}