package com.example.demo.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.common.FlashData;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.service.BaseService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/orderdetails")
public class OrderDetailsController {
	@Autowired
	BaseService<Order> orderService;
	@Autowired
	BaseService<OrderDetail> orderDetailService;
	
	/*
	 * 新規作成画面表示
	 */
	@GetMapping(value = "/create/{orderId}")
	public String form(@PathVariable Integer orderId, OrderDetail orderDetail, Model model, RedirectAttributes ra) {
		try {
			// 存在確認
			Order order = orderService.findById(orderId);
			model.addAttribute("order", order);
		} catch (Exception e) {
			FlashData flash = new FlashData().danger("該当データがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/orders";
		}
		model.addAttribute("orderDetail", orderDetail);
		return "admin/orderdetails/create";
	}

	/*
	 * 新規登録
	 */
	@PostMapping(value = "/create/{orderId}")
	public String register(@PathVariable Integer orderId, @Valid OrderDetail orderDetail, BindingResult result, Model model, RedirectAttributes ra) {
		FlashData flash;
		try {
			// 存在確認
			Order order = orderService.findById(orderId);
			orderDetail.setOrder(order);
			model.addAttribute("order", order);
		} catch (Exception e) {
			flash = new FlashData().danger("該当データがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/orders";
		}
		try {
			if (result.hasErrors()) {
				return "admin/orderdetails/create";
			}
			// 新規登録
			orderDetailService.save(orderDetail);
			flash = new FlashData().success("新規作成しました");
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
		}
		ra.addFlashAttribute("flash", flash);
		return "redirect:/admin/orders/view/{orderId}";
	}
	
	/*
	 * 編集画面表示
	 */
	@GetMapping(value = "/edit/{id}")
	public String edit(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		try {
			// 存在確認
			OrderDetail orderDetail = orderDetailService.findById(id);
			model.addAttribute("orderDetail", orderDetail);
		} catch (Exception e) {
			FlashData flash = new FlashData().danger("該当データがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/orders";
		}
		return "admin/orderdetails/edit";
	}

	/*
	 * 更新
	 */
	@PostMapping(value = "/edit/{id}")
	public String update(@PathVariable Integer id, @Valid OrderDetail orderDetail, BindingResult result, Model model, RedirectAttributes ra) {
		FlashData flash;
		try {
			if (result.hasErrors()) {
				return "admin/orderdetails/edit";
			}
			OrderDetail test = orderDetailService.findById(id);
			orderDetail.setOrder(test.getOrder());
			// 更新
			orderDetailService.save(orderDetail);
			flash = new FlashData().success("更新しました");
		} catch (Exception e) {
			flash = new FlashData().danger("該当データがありません");
			return "redirect:/admin/orders";
		}
		ra.addFlashAttribute("flash", flash);
		Integer orderId = orderDetail.getOrder().getId();
		return "redirect:/admin/orders/view/" + orderId;
	}
}
