package com.jkearns.manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jkearns.yahooclient.*;;

/**
 * ScreenerManager
 * @author Jack
 *
 */
public class ScreenerManager {
	
	private final String yqlBaseUrl = "http://query.yahooapis.com/v1/public/yql?q=";

	/**
	 * Name: escapeURL
	 * @param urlString
	 * @return escaped version of the URL provided
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	private String escapeUrl(String urlString) throws MalformedURLException, URISyntaxException {
		URL url= new URL(urlString);
		URI youAreEye = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
		return youAreEye.toASCIIString();
	}
	
	/**
	 * Name: getAllTickers()
	 * Reads a locally saved file of the following format:
	 * 	ACT Symbol|Security Name|Exchange|CQS Symbol|ETF|Round Lot Size|Test Issue|NASDAQ Symbol
	 * 	A|Agilent Technologies, Inc. Common Stock|N|A|N|100|N|A
	 * 	AA|Alcoa Inc. Common Stock|N|AA|N|100|N|AA
	 * 	AA$|Alcoa Inc. $3.75 Preferred Stock|A|AAp|N|100|N|AA-
	 * 	etc
	 * @return full List of NYSE stock tickers
	 * @throws IOException
	 */
	public List<String> getAllTickers() throws IOException {
		// get file containing list of ticker symbols 
        FileReader fileReader = new FileReader("C:\\Users\\Jack\\GitWorkspace\\RESTfulExample\\tickers.txt");

        // read file line by line
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = new String();
        List<String> tickers = new ArrayList<String>();
        boolean firstLine = true;
        while((line = bufferedReader.readLine()) != null) {
        	if(!firstLine) {
        		tickers.add(line.split("\\s+")[0]);
        	}
        	firstLine = false;
        }   
        
        return tickers;
	}
	
	/**
	 * Name: getOrderedStocks
	 * @param attr
	 * @return a List of Map.Entries of NYSE stocks sorted by the given attribute
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 */
	public List<Entry<String, Double>> getOrderedStocks(String attr) throws SAXException, IOException, ParserConfigurationException, URISyntaxException {
		List<String> allTickers = this.getAllTickers();
		
		// convert list of stock tickers to a map with the value for each entry being the stock's corresponding attribute value
		Map<String, Double> unsortedStocks = new TreeMap<String, Double>();
		for(String stock : allTickers) {
			String singleAttr = this.getSingleAttribute(stock, attr);
			if(singleAttr != null && !singleAttr.equals("")) {
				unsortedStocks.put(stock, Double.valueOf(this.getSingleAttribute(stock, attr)));
			}
		}
		
		// Sort the map by value, which involves converting the Map to a List of Map.Entries.
        List<Entry<String, Double>> sortedStockList = new ArrayList<Entry<String, Double>>(unsortedStocks.entrySet());
		Collections.sort(sortedStockList, new Comparator<Map.Entry<String, Double>>()
        {
            public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
            {
                return o2.getValue().compareTo(o1.getValue());
            }
        } );
		
		return sortedStockList;
	}
	
	/**
	 * Name: getTopStocks
	 * @param attr
	 * @param numStocks
	 * @return list of top N stocks ordered by a particular attribute
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 */
	public List<Entry<String, Double>> getTopStocks(String attr, Integer numStocks) throws SAXException, IOException, ParserConfigurationException, URISyntaxException {
		List<Entry<String, Double>> allOrderedStocks = this.getOrderedStocks(attr);
		return allOrderedStocks.subList(0, numStocks - 1);
	}
	
	/**
	 * Name: getSingleAttribute
	 * @param ticker
	 * @param attr
	 * @returns a single stock attribute from the Yahoo Finance API
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 */
	public String getSingleAttribute(String ticker, String attr) throws SAXException, IOException, ParserConfigurationException, URISyntaxException {
		//build yahoo query language URL string
		String yql = "select " + attr + " from yahoo.finance.quotes where symbol in ( '" + ticker + "' )";
		String yqlUrlString = this.yqlBaseUrl + yql + "&env=store://datatables.org/alltableswithkeys";
		
		// send get request
		YahooClient client = new YahooClient();
	    String response = client.sendGET(escapeUrl(yqlUrlString)); // need to escape YQL URL before sending request
	    
	    // parse XML response
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = (Document) builder.parse(new InputSource(new StringReader(response.toString())));
	    Element attributeInfo = document.getDocumentElement();
	    
	    // we only expect one XML element to be returned, so get the first one and return its text content
	    if(attributeInfo.getElementsByTagName(attr) != null && attributeInfo.getElementsByTagName(attr).item(0) != null) {
	    	return attributeInfo.getElementsByTagName(attr).item(0).getTextContent();
	    }
	    else {
	    	return "";
	    }
	}
}