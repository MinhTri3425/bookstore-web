package com.example.book_webstore.service.impl;

import com.example.book_webstore.dto.ShipperDTO;
import com.example.book_webstore.model.Shipper;
import com.example.book_webstore.repository.ShipperRepository;
import com.example.book_webstore.service.ShipperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShipperServiceImpl implements ShipperService {

    private final ShipperRepository shipperRepository;

    public ShipperServiceImpl(ShipperRepository shipperRepository) {
        this.shipperRepository = shipperRepository;
    }

    @Override
    public List<ShipperDTO> getAllShippers() {
        return shipperRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShipperDTO getShipperById(Long id) {
        Shipper shipper = shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Shipper với ID: " + id));
        return toDto(shipper);
    }

    @Override
    public ShipperDTO findShipperByEmail(String email) {
        return shipperRepository.findByEmail(email)
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public ShipperDTO saveShipper(ShipperDTO dto) {
        Shipper shipper;

        // Nếu đã có ID thì là cập nhật, chưa có là tạo mới
        if (dto.getId() != null) {
            shipper = shipperRepository.findById(dto.getId())
                    .orElse(new Shipper());
        } else {
            shipper = new Shipper();
        }

        shipper.setEmail(dto.getEmail());
        shipper.setName(dto.getName());
        shipper.setPhone(dto.getPhone());

        Shipper savedShipper = shipperRepository.save(shipper);
        return toDto(savedShipper);
    }

    @Override
    @Transactional
    public void deleteShipperById(Long id) {
        if (!shipperRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Shipper để xóa!");
        }
        shipperRepository.deleteById(id);
    }

    @Override
    public boolean isUserShipper(String email) {
        return shipperRepository.findByEmail(email).isPresent();
    }

    // --- Mapper: Chuyển đổi dữ liệu ---
    private ShipperDTO toDto(Shipper entity) {
        if (entity == null)
            return null;
        return new ShipperDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getName(),
                entity.getPhone());
    }
}