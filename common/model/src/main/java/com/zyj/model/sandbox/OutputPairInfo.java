package com.zyj.model.sandbox;

import com.zyj.core.unit.MemoryUnit;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Getter
public class OutputPairInfo extends Pair<String, String> {
    private Long actualTimeCost;

    private TimeUnit timeUnit;

    private Long actualMemoryCost;

    private MemoryUnit memoryUnit;

    public OutputPairInfo setActualTimeCost(Long actualTimeCost, TimeUnit timeUnit) {
        this.actualTimeCost = actualTimeCost;
        this.timeUnit = timeUnit;
        return this;
    }

    public OutputPairInfo setActualMemoryCost(Long actualMemoryCost, MemoryUnit memoryUnit) {
        this.actualMemoryCost = actualMemoryCost;
        this.memoryUnit = memoryUnit;
        return this;
    }

    @Override
    public String toString() {
        return "OutputPairInfo{" +
                "actualTimeCost=" + actualTimeCost +
                ", timeUnit=" + timeUnit +
                ", actualMemoryCost=" + actualMemoryCost +
                ", memoryUnit=" + memoryUnit +
                ", var1=" + var1 +
                ", var2=" + var2 +
                '}';
    }
}
