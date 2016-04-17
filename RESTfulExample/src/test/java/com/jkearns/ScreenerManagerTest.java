package com.jkearns;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.junit.BeforeClass;

import com.jkearns.exceptions.ScreenerException;
import com.jkearns.manager.ScreenerManager;

import junit.framework.*;

public class ScreenerManagerTest extends TestCase {
   
   @BeforeClass
   public static void oneTimeSetUp() {
       // one-time initialization code   
   }

   @AfterClass
   public static void oneTimeTearDown() {
       // one-time cleanup code
   }

   @Test
   public void testGetOneAttribute() throws SAXException, IOException, ParserConfigurationException, URISyntaxException, JSONException, ScreenerException{
	   ScreenerManager mgr = new ScreenerManager();
       String applePeg = mgr.getSingleAttribute("AAPL", "PEGRatio");
       assertNotNull(Double.parseDouble(applePeg)); // PEG Ratio should be a number
   }
   
   @Test
   public void testGetTopStocks() throws NumberFormatException, SAXException, IOException, ParserConfigurationException, URISyntaxException, ScreenerException {
	   ScreenerManager mgr = new ScreenerManager();
	   List<Entry<String, Double>> topStocks = mgr.getTopStocks("PEGRatio", 5);
	   assertTrue(topStocks.size() == 5);
	   
	   List<String> allTickers = mgr.getAllTickers();
	   // top stocks should be a subset of stocks returned by getAllTickers() 
	   for(Entry<String, Double> stock : topStocks) {
		   assertTrue(allTickers.contains(stock.getKey()));
	   }
   }
   
   @Test
   public void testGetAllTickers() throws IOException {
	   ScreenerManager mgr = new ScreenerManager();
	   List<String> tickers = mgr.getAllTickers();
	   assertTrue(tickers.size() > 0);
   }
}