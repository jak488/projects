package com.mkyong.rest;
 
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.mkyong.manager.ScreenerManager;
 
@Path("/screener")
public class ScreenerService {

	/**
	 * Name: getProperty
	 * @param ticker
	 * @param param
	 * @returns a parameter of a given stock
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/{ticker}/{parameter}")
	public Response getProperty(@PathParam("ticker") String ticker, @PathParam("parameter") String param) throws SAXException, IOException, ParserConfigurationException, URISyntaxException {
 
		ScreenerManager mgr = new ScreenerManager();
        String attrValue = mgr.getSingleAttribute(ticker, param);
 
		return Response.status(200).entity(attrValue).build();
 
	}
	
	/**
	 * Name: getTickers
	 * @returns list of all ticker symbols from the NYSE
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 */
	@GET
	@Path("/all_tickers")
	public Response getTickers() throws SAXException, IOException, ParserConfigurationException, URISyntaxException {
 
		ScreenerManager mgr = new ScreenerManager();
        String tickersData = mgr.getAllTickers();
        
		return Response.status(200).entity(tickersData).build();
	}
	
	@GET
	@Path("/ordered_tickers/{parameter}")
	public Response getTickersOrderedBy(@PathParam("parameter") String param) throws IOException, SAXException, ParserConfigurationException, URISyntaxException {
		
		ScreenerManager mgr = new ScreenerManager();
		String[] allTickers = mgr.getAllTickers().split("\\n");
		
		Map sortedStocks = new TreeMap<String, String>();
		for(String stock : allTickers) {
			sortedStocks.put(stock, mgr.getSingleAttribute(stock, param));
		}
		
		return Response.status(200).entity(sortedStocks.toString()).build();
	}
 
}