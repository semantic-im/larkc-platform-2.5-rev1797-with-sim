/*
   This file is part of the LarKC platform 
   http://www.larkc.eu/

   Copyright 2010 LarKC project consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.larkc.shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * A very simple SPARQL client which is capable of asking queries to the LarKC
 * platform. This code is taken mostly from the SPARQLClient GUI from the
 * development kit.
 * 
 * @author Christoph Fuchs, Norbert Lanzanasto
 * 
 */
public class SimpleSparqlClient {

	/**
	 * Encoding used to encode the query
	 */
	private static final String ENCODING = "UTF-8";

	private String serverHttp;

	/**
	 * Default address of the LarKC SPARQL server. 
	 */
	public static final String serverLocalhost = "http://localhost:8000";

	/**
	 * Constructor. Uses default address of the LarKC SPARQL server
	 * (localhost:8000).
	 * 
	 * @throws MalformedURLException 
	 */
	public SimpleSparqlClient() throws MalformedURLException {
		this(serverLocalhost);
	}

	/**
	 * Constructor.
	 * 
	 * @param serverAddress
	 *            the address of the LarKC SPARQL server (e.g.
	 *            http://localhost:8000). A trailing forward slash ("/") will be
	 *            added if missing. The path to the SPARQL endpoint will be
	 *            added as well ("sparql/").
	 * 
	 * @throws MalformedURLException if the URL is malformed 
	 */
	public SimpleSparqlClient(String serverAddress) throws MalformedURLException {
		if (!serverAddress.endsWith("/")) {
			serverAddress = serverAddress.concat("/");
		}
		serverHttp = serverAddress + "sparql/";
		
		// Throws an exception if the URL is malformed
		new URL(serverHttp);
	}

	/**
	 * Sends a SPARQL query to the LarKC SPARQL server specified in the
	 * constructor.
	 * 
	 * @param query
	 *            the SPARQL query to send
	 * @return server response as a String
	 * @throws IOException
	 *             if the server is unreachable
	 */
	public String sendQuery(String query) throws IOException {
		try {
			String encodedQuery = "?query=" + URLEncoder.encode(query, ENCODING);
			String reply = fetchUrl(serverHttp + encodedQuery);

			return reply;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return query;
	}

	private String fetchUrl(String urlStr) throws IOException {
		HttpURLConnection connection = null;
			
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			throw new RuntimeException("This should hot have happened.", e);
		}
		
		assert(url != null);
		
		if (url.getProtocol().equals("http")) {
			// connect to the web server
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			// retrieve the content
			InputStream stream = connection.getInputStream();
			return convertStreamToString(stream);

		}
			
		return "Only HTTP is supported.";
	}

	private String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			// handle errors ...
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

}
