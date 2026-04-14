package com.example.book_webstore.service.command.concrete;

import com.example.book_webstore.model.Shipper;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.repository.ShippingRepository;
import com.example.book_webstore.service.command.ShippingCommand;

public class AcceptOrderCommand implements ShippingCommand {
    private final Shipping shipping;
    private final Shipper shipper;
    private final ShippingRepository shippingRepository;

    public AcceptOrderCommand(Shipping shipping, Shipper shipper, ShippingRepository shippingRepository) {
        this.shipping = shipping;
        this.shipper = shipper;
        this.shippingRepository = shippingRepository;
    }

    @Override
    public void execute() {
        if (shipping.getShipper() != null)
            throw new RuntimeException("Đơn đã có người nhận");
        shipping.setShipper(shipper);
        shipping.setStatus(Shipping.ShippingStatus.SHIPPING);
        shippingRepository.save(shipping);
    }

    @Override
    public void undo() {
        shipping.setShipper(null);
        shipping.setStatus(Shipping.ShippingStatus.PENDING);
        shippingRepository.save(shipping);
    }
}
