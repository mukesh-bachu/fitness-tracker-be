package com.app.FitbitDashBoard.Utility;


import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeNode {

    private String label;
    private boolean expanded;
    private Map<String, String> data;
    private List<TreeNode> children;
}
