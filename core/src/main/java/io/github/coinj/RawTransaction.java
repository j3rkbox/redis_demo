
package io.github.coinj;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RawTransaction {
    private List<Input> inputs;
    private List<Output> outputs;

    private BigDecimal fee;