package com.example.book_webstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.book_webstore.dto.CouponDTO;
import com.example.book_webstore.model.Coupon;
import com.example.book_webstore.service.CouponService;
import com.example.book_webstore.service.facade.BookQueryFacade;

@Controller
@RequestMapping("/admin/coupons")
public class AdminCouponController {

    private final CouponService couponService;
    private final BookQueryFacade bookQueryFacade;

    public AdminCouponController(CouponService couponService, BookQueryFacade bookQueryFacade) {
        this.couponService = couponService;
        this.bookQueryFacade = bookQueryFacade;
    }

    @GetMapping
    public String listCoupons(Model model) {
        model.addAttribute("coupons", couponService.getAllCoupons());
        return "admin/coupons";
    }

    @GetMapping("/add")
    public String addCouponForm(Model model) {
        CouponDTO coupon = new CouponDTO();
        coupon.setActive(true);
        coupon.setTarget(Coupon.CouponTarget.PRODUCT);
        model.addAttribute("coupon", coupon);
        model.addAttribute("books", bookQueryFacade.getAllBooks());
        return "admin/coupon-form";
    }

    @GetMapping("/edit")
    public String editCouponForm(@RequestParam Long id, Model model) {
        model.addAttribute("coupon", couponService.getCouponById(id));
        model.addAttribute("books", bookQueryFacade.getAllBooks());
        return "admin/coupon-form";
    }

    @PostMapping("/save")
    public String saveCoupon(@ModelAttribute("coupon") CouponDTO couponDTO, RedirectAttributes ra) {
        try {
            if (couponDTO.getId() == null) {
                couponService.createCoupon(couponDTO);
                ra.addFlashAttribute("successMessage", "Tạo coupon thành công.");
            } else {
                couponService.updateCoupon(couponDTO.getId(), couponDTO);
                ra.addFlashAttribute("successMessage", "Cập nhật coupon thành công.");
            }
            return "redirect:/admin/coupons";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return couponDTO.getId() == null
                    ? "redirect:/admin/coupons/add"
                    : "redirect:/admin/coupons/edit?id=" + couponDTO.getId();
        }
    }

    @PostMapping("/delete")
    public String deleteCoupon(@RequestParam Long id, RedirectAttributes ra) {
        try {
            couponService.deleteCoupon(id);
            ra.addFlashAttribute("successMessage", "Đã xóa coupon.");
        } catch (ResponseStatusException e) {
            ra.addFlashAttribute("errorMessage",
                    e.getReason() != null ? e.getReason() : "Không thể xóa mã giảm giá này.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa mã giảm giá này.");
        }
        return "redirect:/admin/coupons";
    }
}
