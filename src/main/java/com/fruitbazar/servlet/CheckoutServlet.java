package com.fruitbazar.servlet;

import com.fruitbazar.model.CartItem;
import com.fruitbazar.model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart.jsp");
            return;
        }

        String name = req.getParameter("fullName");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");

        double total = 0;
        for (CartItem item : cart.values()) {
            total += item.getSubtotal();
        }

        String orderId = "SM" + System.currentTimeMillis();

        req.setAttribute("orderId", orderId);
        req.setAttribute("customerName", name);
        req.setAttribute("address", address);
        req.setAttribute("phone", phone);
        req.setAttribute("total", total);
        req.setAttribute("items", cart.values());

        // Clear the cart after a successful order
        cart.clear();

        RequestDispatcher rd = req.getRequestDispatcher("/orderconfirmation.jsp");
        rd.forward(req, resp);
    }
}
