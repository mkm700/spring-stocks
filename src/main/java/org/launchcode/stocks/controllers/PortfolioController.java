package org.launchcode.stocks.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.launchcode.stocks.models.Stock;
import org.launchcode.stocks.models.StockHolding;
import org.launchcode.stocks.models.StockLookupException;
import org.launchcode.stocks.models.User;
import org.launchcode.stocks.models.dao.StockHoldingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Chris Bay on 5/17/15.
 */
@Controller
public class PortfolioController extends AbstractController {
	
	@Autowired
    protected StockHoldingDao stockHoldingDao;

    @RequestMapping(value = "/portfolio")
    public String portfolio(HttpServletRequest request, Model model){

        // TODO - Implement portfolio display
    	//display name (use Stock.toString()), number of shares owned, 
    	//current price, and total value of shares owned.
    	
    	User user = getUserFromSession(request);
    	String username = user.getUserName();
    	String cash = String.format(Float.toString(user.getCash()));

//    	//Map<String, StockHolding> portfolio = user.getPortfolio();
//    	List<StockHolding> stockHoldings = stockHoldingDao.findByOwnerId(ownerId);
    	
    	//this is the data that will be passed to the template
    	ArrayList<HashMap<String, String>> portfolioData = new ArrayList<HashMap<String, String>>();
    	
    	//for each holding, add an entry to the portfolio data (template)
    	Collection<StockHolding> holdings = user.getPortfolio().values();
    	
    	for (StockHolding holding : holdings) {
    		// assemble a hashmap with the data for this holding - this will be copied to portfolioData
            HashMap<String, String> holdingData = new HashMap<String, String>();
            
            // add shares to the holding data if ther are more than 0
            if (holding.getSharesOwned() > 0 ) {
	            holdingData.put("shares", String.valueOf(holding.getSharesOwned()));
	            
	            // lookup current stock info to get full stock name
	            Stock currentStock = null;
	            try {
	            	currentStock = Stock.lookupStock(holding.getSymbol());
	            }
	            catch (StockLookupException e) {
	            	e.printStackTrace();
	                return this.displayError("Unable to display portfolio", model);
	            }
	            
	            holdingData.put("stockName", currentStock.toString());
	            
	            //add price to the holding data
	            String priceDisplay = "$" + String.format("%,.2f", currentStock.getPrice());
	            holdingData.put("price", priceDisplay);
	            
	            //calculate stock total value
	            float totalValue = currentStock.getPrice() * holding.getSharesOwned();
	            String totalValueDisplay = "$" + String.format("%,.2f", totalValue);
	            holdingData.put("value", totalValueDisplay);
	            
	            portfolioData.add(holdingData); 
            }
    	}
    	

    	model.addAttribute("portfolioData", portfolioData);
    	model.addAttribute("cash", cash);
    	model.addAttribute("username", username);
        model.addAttribute("title", "Portfolio");
        model.addAttribute("portfolioNavClass", "active");

        return "portfolio";
    }

}
