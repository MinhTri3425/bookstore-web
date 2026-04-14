package com.example.book_webstore.service.command;

public interface ShippingCommand {
    void execute();

    void undo();

}
