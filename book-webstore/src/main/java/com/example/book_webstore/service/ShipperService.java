package com.example.book_webstore.service;

import java.util.List;
import com.example.book_webstore.dto.ShipperDTO;

public interface ShipperService {
    List<ShipperDTO> getAllShippers();

    ShipperDTO getShipperById(Long id);

    ShipperDTO findShipperByEmail(String email);

    ShipperDTO saveShipper(ShipperDTO shipper);

    void deleteShipperById(Long id);

    boolean isUserShipper(String email);
}
