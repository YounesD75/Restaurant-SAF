package com.saf.core1.router;

import com.saf.core1.Message;

public final class RouterMessages {
    private RouterMessages() {}

    public record ScaleUp(int quantity) implements Message {
        @Override public String type() { return "SYSTEM_SCALE_UP"; }
        @Override public Object payload() { return quantity; }
    }

    public record ScaleDown(int quantity) implements Message {
        @Override public String type() { return "SYSTEM_SCALE_DOWN"; }
        @Override public Object payload() { return quantity; }
    }
}