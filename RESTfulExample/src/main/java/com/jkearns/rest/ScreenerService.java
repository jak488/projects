package com.jkearns.rest;
 
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.json.JSONException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.jkearns.manager.ScreenerManager;
 
/**
 * ScreenerService
 * RESTful interface that parses data from manager class(es) and converts to JSON
 * @author Jack
 *
 */
@Path("/screener")
public class ScreenerService {

	/**
	 * Name: getProperty
	 * Convenience call to get a particular stock attribute in JSON format.
	 * @param ticker
	 * @param param
	 * @returns a parameter of a given stock in JSON
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/{ticker}/{attribute}")
	public Response getStockAttribute(@PathParam("ticker") String ticker, @PathParam("attribute") String attr) throws SAXException, IOException, JSONException, ParserConfigurationException, URISyntaxException {
 
		ScreenerManager mgr = new ScreenerManager();
        String attrValue = mgr.getSingleAttribute(ticker, attr);
        String JsonString = new JSONObject().put("attribute", attrValue).toString();
 
		return Response.status(200).entity(JsonString).build();
 
	}
	
	/**
	 * Name: getTickers
	 * @returns list of all ticker symbols from the NYSE in JSON
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 * @throws JSONException 
	 */
	@GET
	@Path("/all_tickers")
	public Response getTickers() throws IOException, ParserConfigurationException, URISyntaxException, JSONException {
 
		ScreenerManager mgr = new ScreenerManager();
        List<String> allTickers = mgr.getAllTickers();
        
        List<String> tickerList = new ArrayList<String>();
        for(String ticker : allTickers) {
        	tickerList.add(ticker);
        }
        
        JSONObject tickersJson = new JSONObject();
        tickersJson.put("tickers", tickerList);
        
		return Response.status(200).entity(tickersJson.toString()).build();
	}
	
	/**
	 * Name: getStocksOrderedBy
	 * @param attr
	 * @returns a list of NYSE stocks sorted by the given attribute in JSON
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 * @throws JSONException 
	 */
	@GET
	@Path("/ordered_stocks/{attribute}")
	public Response getStocksOrderedBy(@PathParam("attribute") String attr) throws IOException, SAXException, ParserConfigurationException, URISyntaxException, JSONException {
		
		ScreenerManager mgr = new ScreenerManager();
		List<Entry<String, String>> orderedStocks = mgr.getOrderedStocks(attr);
		JSONObject stocksJson = new JSONObject();
		
		for(Map.Entry<String, String> entry : orderedStocks){
			stocksJson.put(entry.getKey(), entry.getValue());
		}
		
		return Response.status(200).entity(stocksJson.toString()).build();
	}
 
}