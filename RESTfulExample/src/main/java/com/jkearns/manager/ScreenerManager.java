package com.jkearns.manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
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
	
	private final String baseUrl = "http://query.yahooapis.com/v1/public/yql?q=";

	/**
	 * Name: escapeURL
	 * @param urlString
	 * @returns escaped URL
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public String escapeUrl(String urlString) throws MalformedURLException, URISyntaxException {
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
	 * @returns full list of NYSE stock tickers
	 * @throws IOException
	 */
	public String getAllTickers() throws IOException {
        FileReader fileReader = new FileReader("C:\\Users\\Jack\\GitWorkspace\\RESTfulExample\\tickers.txt");

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line = null;
        StringBuilder tickers = new StringBuilder();
        boolean firstLine = true;
        while((line = bufferedReader.readLine()) != null) {
        	if(!firstLine) {
        		tickers.append(line.split("\\s+")[0] + "\n");
        	}
        	firstLine = false;
        }   
        return tickers.toString();
	}
	
	/**
	 * Name: getOrderedStocks
	 * @param attr
	 * @returns a list of NYSE stocks sorted by the given attribute
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 */
	public List<Entry<String, String>> getOrderedStocks(String attr) throws SAXException, IOException, ParserConfigurationException, URISyntaxException {
		String[] allTickers = this.getAllTickers().split("\\n");
		
		Map<String, String> unsortedStocks = new TreeMap<String, String>();
		for(String stock : allTickers) {
			String singleAttr = this.getSingleAttribute(stock, attr);
			if(singleAttr != null && !singleAttr.equals("")) {
				unsortedStocks.put(stock, this.getSingleAttribute(stock, attr));
			}
		}
		
		// Sort the map by value
		Set<Entry<String, String>> set = unsortedStocks.entrySet();
        List<Entry<String, String>> sortedStockList = new ArrayList<Entry<String, String>>(set);
		Collections.sort(sortedStockList, new Comparator<Map.Entry<String, String>>()
        {
            public int compare( Map.Entry<String, String> o1, Map.Entry<String, String> o2 )
            {
                return (Double.valueOf(o2.getValue()).compareTo(Double.valueOf(o1.getValue())));
            }
        } );
		return sortedStockList;
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
		String yql = "select " + attr + " from yahoo.finance.quotes where symbol in ( '" + ticker + "' )";
		String yqlUrlString = baseUrl + yql + "&env=store://datatables.org/alltableswithkeys";
		
		YahooClient client = new YahooClient();
	    String response = client.sendGET(escapeUrl(yqlUrlString));
	    
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = (Document) builder.parse(new InputSource(new StringReader(response.toString())));
	    Element ratio = document.getDocumentElement();
	    if(ratio.getElementsByTagName(attr) != null && ratio.getElementsByTagName(attr).item(0) != null) {
	    	return ratio.getElementsByTagName(attr).item(0).getTextContent();
	    }
	    else {
	    	return "";
	    }
	}
}