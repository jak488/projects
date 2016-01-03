package com.jkearns.rest;
 
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
 * RESTful interface that parses data from manager class(es) and marshals to XML
 * @author Jack
 *
 */
@Path("/screener")
public class ScreenerService {

	/**
	 * Name: getProperty
	 * @param ticker
	 * @param param
	 * @returns a parameter of a given stock in XML
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
        String myString = new JSONObject().put("JSON", "Hello, World!").toString();
 
		return Response.status(200).entity(attrValue).build();
 
	}
	
	/**
	 * Name: getTickers
	 * @returns list of all ticker symbols from the NYSE in XML
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/all_tickers")
	public Response getTickers() throws SAXException, IOException, ParserConfigurationException, URISyntaxException {
 
		ScreenerManager mgr = new ScreenerManager();
        String tickersXml = mgr.getAllTickers();
        
		return Response.status(200).entity(tickersXml).build();
	}
	
	/**
	 * Name: getStocksOrderedBy
	 * @param attr
	 * @returns a list of NYSE stocks sorted by the given attribute in XML
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/ordered_stocks/{attribute}")
	public Response getStocksOrderedBy(@PathParam("attribute") String attr) throws IOException, SAXException, ParserConfigurationException, URISyntaxException {
		
		ScreenerManager mgr = new ScreenerManager();
		List<Entry<String, String>> orderedStocks = mgr.getOrderedStocks(attr);
		StringBuilder stocksXml = new StringBuilder();
		
		for(Map.Entry<String, String> entry : orderedStocks){
			stocksXml.append((entry.getKey() + " ==== " + entry.getValue() + "\n"));
		}
		
		return Response.status(200).entity(stocksXml.toString()).build();
	}
 
}