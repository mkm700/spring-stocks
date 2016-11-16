package org.launchcode.stocks.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.launchcode.stocks.models.Stock;
import org.launchcode.stocks.models.StockHolding;
import org.launchcode.stocks.models.StockLookupException;
import org.launchcode.stocks.models.User;
import org.launchcode.stocks.models.dao.StockHoldingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Chris Bay on 5/17/15.
 */
@Controller
public class StockController extends AbstractController {

    @Autowired
    StockHoldingDao stockHoldingDao;

    @RequestMapping(value = "/quote", method = RequestMethod.GET)
    public String quoteForm(Model model) {

        // pass data to template
        model.addAttribute("title", "Quote");
        model.addAttribute("quoteNavClass", "active");
        return "quote_form";
    }

    @RequestMapping(value = "/quote", method = RequestMethod.POST)
    public String quote(String symbol, Model model) {
    	//????? how can symbol be passed in as a parameter?  where did that come from?
    	//????? why no HttpServletRequest required?
    	
        // TODO - Implement quote lookup
    	//create a new stock instance - catch the exception if there is a problem with the file
    	float stockPrice = 0;
    	String stockName = "";
    	try {
    		Stock stockQuote = Stock.lookupStock(symbol);
    		stockPrice = stockQuote.getPrice();
    		stockName = stockQuote.getName();
    	}
    	catch(StockLookupException slx) {
    		slx.printStackTrace();
    		String errorMessage = slx.getMessage();
    		model.addAttribute("message", errorMessage);
    		return "error";
    	}
    	

        // pass data to template
        model.addAttribute("title", "Quote");
        model.addAttribute("quoteNavClass", "active");
        model.addAttribute("stock_desc", symbol.toUpperCase() + " (" + stockName + ")");
        model.addAttribute("stock_price", stockPrice);

        return "quote_display";
    }

    @RequestMapping(value = "/buy", method = RequestMethod.GET)
    public String buyForm(Model model) {

        model.addAttribute("title", "Buy");
        model.addAttribute("action", "/buy");
        model.addAttribute("buyNavClass", "active");
        return "transaction_form";
    }

    @RequestMapping(value = "/buy", method = RequestMethod.POST)
    public String buy(String symbol, int numberOfShares, HttpServletRequest request, Model model) {

        // TODO - Implement buy action
    	User user = getUserFromSession(request);
    	
    	StockHolding holding = null;
    	try {
    		holding = StockHolding.buyShares(user, symbol, numberOfShares);
    	}
    	catch(StockLookupException slx) {
    		slx.printStackTrace();
    		String errorMessage = slx.getMessage();
    		model.addAttribute("message", errorMessage);
    		return "error";
    	}
    	
		this.stockHoldingDao.save(holding);
		this.userDao.save(user);

		model.addAttribute("confirmMessage", "Confirmed: You bought " + numberOfShares + " shares of " + symbol.toUpperCase());
        model.addAttribute("title", "Buy");
        model.addAttribute("action", "/buy");
        model.addAttribute("buyNavClass", "active");

        return "transaction_confirm";
    }

    @RequestMapping(value = "/sell", method = RequestMethod.GET)
    public String sellForm(Model model) {
        model.addAttribute("title", "Sell");
        model.addAttribute("action", "/sell");
        model.addAttribute("sellNavClass", "active");
        return "transaction_form";
    }

    @RequestMapping(value = "/sell", method = RequestMethod.POST)
    public String sell(String symbol, int numberOfShares, HttpServletRequest request, Model model) {

        // TODO - Implement sell action
    	
    	User user = getUserFromSession(request);
    	
    	StockHolding holding = null;
    	try {
    		holding = StockHolding.sellShares(user, symbol, numberOfShares);
    	}
    	catch(StockLookupException slx) {
    		slx.printStackTrace();
    		String errorMessage = slx.getMessage();
    		model.addAttribute("message", errorMessage);
    		return "error";
    	}
    	
		this.stockHoldingDao.save(holding);
		this.userDao.save(user);

		model.addAttribute("confirmMessage", "Confirmed: You sold " + numberOfShares + " shares of " + symbol.toUpperCase());

        model.addAttribute("title", "Sell");
        model.addAttribute("action", "/sell");
        model.addAttribute("sellNavClass", "active");

        return "transaction_confirm";
    }

}
