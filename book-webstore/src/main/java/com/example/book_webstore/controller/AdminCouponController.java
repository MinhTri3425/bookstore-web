package com.example.book_webstore.controller;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
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
        model.addAttribute("currentTime", LocalDateTime.now());
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
    public String saveCoupon(
            @ModelAttribute("coupon") CouponDTO couponDTO,
            BindingResult bindingResult,
            @RequestParam(value = "couponId", required = false) Long couponId,
            RedirectAttributes ra) {
        Long resolvedId = couponDTO.getId() != null ? couponDTO.getId() : couponId;
        return resolvedId == null
                ? createCoupon(couponDTO, bindingResult, ra)
                : handleUpdateCoupon(resolvedId, couponDTO, bindingResult, ra);
    }

    @PostMapping("/create")
    public String createCoupon(
            @ModelAttribute("coupon") CouponDTO couponDTO,
            BindingResult bindingResult,
            RedirectAttributes ra) {
        log.info("[Coupon][Create] Submit code={}, type={}, target={}", couponDTO.getCode(), couponDTO.getType(),
                couponDTO.getTarget());
        if (bindingResult.hasErrors()) {
            log.warn("[Coupon][Create] Binding errors: {}", resolveBindingErrorMessage(bindingResult));
            ra.addFlashAttribute("errorMessage", resolveBindingErrorMessage(bindingResult));
            return "redirect:/admin/coupons/add";
        }
        try {
            couponDTO.setId(null);
            couponService.createCoupon(couponDTO);
            ra.addFlashAttribute("successMessage", "Tạo coupon thành công.");
            log.info("[Coupon][Create] Success code={}", couponDTO.getCode());
            return "redirect:/admin/coupons";
        } catch (Exception e) {
            log.error("[Coupon][Create] Failed with error={}", e.getMessage(), e);
            ra.addFlashAttribute("errorMessage", resolveExceptionMessage(e));
            return "redirect:/admin/coupons/add";
        }
    }

    @PostMapping("/update")
    public String updateCoupon(
            @RequestParam("id") Long id,
            @ModelAttribute("coupon") CouponDTO couponDTO,
            BindingResult bindingResult,
            RedirectAttributes ra) {
        return handleUpdateCoupon(id, couponDTO, bindingResult, ra);
    }

    private String handleUpdateCoupon(Long id, CouponDTO couponDTO, BindingResult bindingResult,
            RedirectAttributes ra) {
        log.info("[Coupon][Update] Submit id={}, code={}, type={}, target={}", id, couponDTO.getCode(),
                couponDTO.getType(), couponDTO.getTarget());
        if (bindingResult.hasErrors()) {
            log.warn("[Coupon][Update] Binding errors for id={}: {}", id, resolveBindingErrorMessage(bindingResult));
            ra.addFlashAttribute("errorMessage", resolveBindingErrorMessage(bindingResult));
            return "redirect:/admin/coupons/edit?id=" + id;
        }
        try {
            couponDTO.setId(id);
            couponService.updateCoupon(id, couponDTO);
            ra.addFlashAttribute("successMessage", "Cập nhật coupon thành công.");
            log.info("[Coupon][Update] Success id={}", id);
            return "redirect:/admin/coupons";
        } catch (Exception e) {
            log.error("[Coupon][Update] Failed for id={} with error={}", id, e.getMessage(), e);
            ra.addFlashAttribute("errorMessage", resolveExceptionMessage(e));
            return "redirect:/admin/coupons/edit?id=" + id;
        }
    }

    private String resolveExceptionMessage(Exception e) {
        return (e instanceof ResponseStatusException rse && rse.getReason() != null)
                ? rse.getReason()
                : e.getMessage();
    }

    private String resolveBindingErrorMessage(BindingResult bindingResult) {
        String details = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + " không hợp lệ")
                .distinct()
                .collect(Collectors.joining(", "));
        return details.isBlank()
                ? "Dữ liệu coupon không hợp lệ. Vui lòng kiểm tra lại thời gian và các giá trị số."
                : "Dữ liệu coupon không hợp lệ: " + details + ".";
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
