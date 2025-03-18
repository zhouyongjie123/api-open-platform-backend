package com.zyj.model.sandbox;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SandboxExecutionOutput {
    private List<OutputPairInfo> outputPairInfoList = new ArrayList<>();
}
