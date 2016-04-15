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
	 * Call to get a particular stock attribute, such as P/E Ratio, Asking Price, or PEG Ratio, in JSON format.
	 * @param ticker
	 * @param param
	 * @return a parameter of a given stock in JSON
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/{ticker}/{attribute}")
	public Response getStockAttribute(@PathParam("ticker") String ticker, @PathParam("attribute") String attr) throws SAXException, IOException, JSONException, ParserConfigurationException, URISyntaxException {
 
		// get value of a given stock attribute
		ScreenerManager mgr = new ScreenerManager();
        String attrValue = mgr.getSingleAttribute(ticker, attr);
        
        // convert to json
        String JsonString = new JSONObject().put("attribute", attrValue).toString();
 
		return Response.status(200).entity(JsonString).build();
 
	}
	
	/**
	 * Name: getTickers
	 * @return JSON formatted collection of all ticker symbols from the NYSE 
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 * @throws JSONException 
	 */
	@GET
	@Path("/all_tickers")
	public Response getTickers() throws IOException, ParserConfigurationException, URISyntaxException, JSONException {
 
		// get all tickers
		ScreenerManager mgr = new ScreenerManager();
        List<String> allTickers = mgr.getAllTickers();
        
        List<String> tickerList = new ArrayList<String>();
        for(String ticker : allTickers) {
        	tickerList.add(ticker);
        }
        
        // convert to json
        JSONObject tickersJson = new JSONObject();
        tickersJson.put("tickers", tickerList);
        
		return Response.status(200).entity(tickersJson.toString()).build();
	}
	
	/**
	 * Name: getStocksOrderedBy
	 * @param attr
	 * @return JSON formatted collection of NYSE stocks with rankings ordered by the given attribute in the format "stock_symbol":rank, ex: "ADC":30,"ABRN":37,"ADM":2
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 * @throws JSONException 
	 */
	@GET
	@Path("/ranked_stocks/{attribute}")
	public Response getRankedStocks(@PathParam("attribute") String attr) throws IOException, SAXException, ParserConfigurationException, URISyntaxException, JSONException {
		
		// check if the attribute given is a valid numeric attribute
		if(!"PEGRatio".equals(attr)) {
			String errorMessage = "Attribute must be one of the following: PEGRatio";
			return Response.status(200).entity(errorMessage).build();
		}
		
		// then get the list of stocks ordered by the attribute provided
		ScreenerManager mgr = new ScreenerManager();
		List<Entry<String, Double>> orderedStocks = mgr.getOrderedStocks(attr);
		
		// convert to json
		JSONObject stocksJson = new JSONObject();
		Integer idx = 1; // start with stock with largest attribute value
		for(Map.Entry<String, Double> entry : orderedStocks){
			stocksJson.put(entry.getKey(), idx);
			idx++;
		}
		
		return Response.status(200).entity(stocksJson.toString()).build();
	}
	
	/**
	 * Name: getStocksOrderedBy
	 * @param attr
	 * @return JSON-formatted collection of top NYSE stocks ranked by the given attribute in the format "ACC":34.12,"AAT":6.81,"ADM":14.44 etc.
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 * @throws JSONException 
	 */
	@GET
	@Path("/ordered_stocks/{number}/{attribute}")
	public Response getTopStocksByAttr(@PathParam("attribute") String attr, @PathParam("number") Integer numStocks) throws IOException, SAXException, ParserConfigurationException, URISyntaxException, JSONException {
		
		// check if the attribute given is a valid numeric attribute
		if(!"PEGRatio".equals(attr)) {
			String errorMessage = "Attribute must be one of the following: PEGRatio";
			return Response.status(200).entity(errorMessage).build();
		}
		
		// then get the list of top stocks ranked by the attribute provided
		ScreenerManager mgr = new ScreenerManager();
		List<Entry<String, Double>> topStocks = mgr.getTopStocks(attr, numStocks);
		
		// convert to json
		JSONObject stocksJson = new JSONObject();
		for(Map.Entry<String, Double> entry : topStocks){
			stocksJson.put(entry.getKey(), entry.getValue());
		}
		
		return Response.status(200).entity(stocksJson.toString()).build();
	}
 
}