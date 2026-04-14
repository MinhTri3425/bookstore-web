package com.example.book_webstore.service.command;

import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class ShippingCommandInvoker {
    private final Stack<ShippingCommand> history = new Stack<>();

    public void execute(ShippingCommand command) {
        command.execute();
        history.push(command);
    }

    public void undoLast() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }
}