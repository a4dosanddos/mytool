package com.trac.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GetTicketNumberAndDescription {

	private static final String USER = "test";
	private static final String PASS = "test";
	private static final String URL_BASE = "http://172.20.10.3:8888/sample_project/ticket/";
	//private static final String URL_BASE = "http://192.168.10.134:8888/sample_project/ticket/";
	private static final String[] TICKET_NUMBER = { "2", "1" };

	public static void main(String[] args) throws Exception {

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USER, PASS.toCharArray());
			}
		});
		
		// チケット番号順にソート
		Arrays.sort(TICKET_NUMBER);

		for (int i = 0; i < TICKET_NUMBER.length; i++) {
			URL url = new URL(URL_BASE + TICKET_NUMBER[i]);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String body;
			while ((body = reader.readLine()) != null) {
				// #[チケット番号] [チケットタイトル] を出力
				if (body.contains("trac-ticket-title")) {
					String title = reader.readLine().replaceAll("<span class=\"summary\">", "")
							.replaceAll("</span>", "").replaceAll("    ", "");
					System.out.println("#" + TICKET_NUMBER[i] + " " + title);

				// Description を出力
				} else if (body.contains("<div class=\"searchable\">")) {
					reader.readLine(); // <p>
					StringBuffer sb = new StringBuffer();
					String tmp;
					while (!(tmp = reader.readLine()).equals("</p>"))
						sb.append(tmp.replaceAll("<br />", "\r\n"));
					System.out.println(sb.toString());
				}
			}

			reader.close();
			con.disconnect();
		}
	}

	private static void printHeader(HttpURLConnection con) {
		System.out.println("----- Headers -----");
		Map<String, List<String>> headers = con.getHeaderFields();
		for (String key : headers.keySet()) {
			System.out.println(key + ": " + headers.get(key));
		}
		System.out.println();
	}
}
