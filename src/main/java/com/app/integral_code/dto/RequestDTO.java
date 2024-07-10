package com.app.integral_code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private Integer n;
    private double[] yvalues;
    private boolean toText;
    private boolean toPdf;
}
