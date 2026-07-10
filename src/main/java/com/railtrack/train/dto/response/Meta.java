package com.railtrack.train.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    private String timestamp;

    private String traceId;

    private String source;

    private Integer executionTime;

}