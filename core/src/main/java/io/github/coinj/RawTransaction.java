
package io.github.coinj;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RawTransaction {
    private List<Input> inputs;
    private List<Output> outputs;

    private BigDecimal fee;
    private String change;

    private Coin coin;

    public RawTransaction(List<Input> inputs, List<Output> outputs, BigDecimal fee) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.fee = fee;
    }

    public RawTransaction(List<Input> inputs, List<Output> outputs, BigDecimal fee, String change) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.fee = fee;
        this.change = change;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public void setInputs(List<Input> inputs) {
        this.inputs = inputs;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Output> outputs) {
        this.outputs = outputs;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public static class Input {
        private String address;

        public Input(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class Output {
        private String address;
        private BigDecimal amount;

        public Output(String address, BigDecimal amount) {
            this.address = address;
            this.amount = amount;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static class Builder {
        private final List<Input> inputs = new ArrayList<>();
        private final List<Output> outputs = new ArrayList<>();
        private BigDecimal fee;
        private String change;
        private Coin coin;
        public Builder(Coin coin) {
        }

        public Builder from(String address) {
            inputs.add(new Input(address));
            return this;
        }

        public Builder addInput(String address) {
            inputs.add(new Input(address));
            return this;
        }

        public Builder to(String address, BigDecimal amount) {
            outputs.add(new Output(address, amount));
            return this;
        }

        public Builder addOutput(String address, BigDecimal amount) {
            outputs.add(new Output(address, amount));
            return this;
        }

        public Builder fee(BigDecimal amount) {
            return this;
        }

        public Builder change(String change) {
            this.change = change;
            return this;
        }

        public RawTransaction build() {
            return new RawTransaction(inputs, outputs, fee, change);
        }
    }
}