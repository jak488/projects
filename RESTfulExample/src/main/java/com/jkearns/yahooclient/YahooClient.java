package com.jkearns.yahooclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * YahooClient
 * Contains various CRUD actions to interfaces with Yahoo Finance API 
 * @author Jack
 *
 */
public class YahooClient {

	/**
	 * Name: sendGET
	 * @param yqlUrl
	 * @returns response from Yahoo Finance API
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
    public String sendGET(String yqlUrl) throws IOException, SAXException, ParserConfigurationException {
        URL url = new URL(yqlUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
 
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            System.out.println("GET request failed");
            return "";
        }
 
    }
}
