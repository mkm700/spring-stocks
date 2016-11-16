package org.launchcode.stocks.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.launchcode.stocks.models.Stock;
import org.launchcode.stocks.models.StockTransaction;
import org.launchcode.stocks.models.User;
import org.launchcode.stocks.models.dao.StockTransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TransactionLogController extends AbstractController {
	
    @Autowired
    protected StockTransactionDao stockTransactionDao;

	@RequestMapping(value = "/log")
    public String transactionLog(HttpServletRequest request, Model model){

    	//display transaction log for current user
		
    	User user = getUserFromSession(request);
    	String username = user.getUserName();
    	int userId = user.getUid();
		List<StockTransaction> transactions = stockTransactionDao.findByUserId(userId);

    	model.addAttribute("transactions", transactions);
    	model.addAttribute("username", username);
        model.addAttribute("title", "Portfolio");
        model.addAttribute("portfolioNavClass", "active");

        return "transaction_log";
    }
}
