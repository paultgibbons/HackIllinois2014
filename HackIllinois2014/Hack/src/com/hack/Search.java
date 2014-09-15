package com.hack;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Search {

	public static SearchResults getInfo(String query) {
		try {
			String[] idAndName = getTopResult(searchByText(query));
			SearchResults result = parse(searchById(idAndName[0]));
			result.name = idAndName[1];
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String searchByText(String query) throws Exception {
		return search("search^25|5|1|1^" + query + "^d03b26e60936db9f");
	}

	private static String[] getTopResult(String input) throws Exception {
		String[] result = { "", "" };

		// id
		String startKey = "<item code=\"";
		int startIndex = input.indexOf(startKey) + startKey.length();
		int endIndex = input.indexOf("\"", startIndex + 1);
		result[0] = input.substring(startIndex, endIndex);

		// name
		startKey = "title=\"";
		startIndex = input.indexOf(startKey) + startKey.length();
		endIndex = input.indexOf("\"", startIndex + 1);
		result[1] = input.substring(startIndex, endIndex);

		return result;
	}

	private static String searchById(String id) throws Exception {
		return search("detail^" + id + "^1^d03b26e60936db9f");
	}

	private static SearchResults parse(String input) throws Exception {
		// TODO !
		// list of stuff
		// use, miss a dose, side effects, brand names, storage, best taken
		String[] codes = { "187", "194", "205", "4", "227", "193" };
		SearchResults results = new SearchResults();

		for (int idx = 0; idx < codes.length; idx++) {
			String temp = "<RECORD><LEXICOMP_CONTENT_SECTION_CODE>"
					+ codes[idx]
					+ "</LEXICOMP_CONTENT_SECTION_CODE><DESCRIPTION>";

			int index = input.indexOf(temp);

			if (index == -1) {
				results.data.add(null);
				continue;
			}

			String entry = "";
			// do something different for brand names
			if (codes[idx].equals("4")) {
				String start = "&gt;&lt;li&gt;";
				String separator = "&lt;/li&gt;&lt;li&gt;";
				int i = input.indexOf(start, index);
				if (i >= 0) {
					int stopIndex = input.indexOf("\n", i + 1);
					boolean first = true;
					for (int j = i + 1; i >= 0; i = input.indexOf(separator,
							i + 1)) {
						if (i > stopIndex)
							break;
						j = input.indexOf(separator, i + 1);
						if (j < 0)
							break;
						if (first) {
							entry += input.substring(i + start.length(), j)
									+ "\n";
							first = false;
						} else {
							entry += input.substring(i + separator.length(), j)
									+ "\n";
						}
					}
					int j;
					if (i >= 0 && (j = input.indexOf("&lt;/li&gt;&lt;/ul&gt;", i + 1)) >= 0) {
						if (j >= 0)
						entry += input.substring(i + separator.length(), j)
								+ "\n";
					}
				}
			} else {
				String start = "class='content'&gt;";
				String end = "&lt;/";
				int i = input.indexOf(start, index);
				int stopIndex = input.indexOf("\n", i + 1);
				for (int j = i + 1; i >= 0; i = input.indexOf(start, i + 1)) {
					if (i > stopIndex)
						break;
					j = input.indexOf(end, i + 1);
					String line = input.substring(i + start.length(), j) + "\n";
					if (line.startsWith("&lt;"))
						line = line.substring(line.indexOf("\'&gt;") + 5);
					entry += line;
				}
			}

			results.data.add(entry);
		}

		return results;
	}

	private static String search(String query) throws Exception {
		Socket mySocket = new Socket("sandbox.e-imo.com", 42019);
		PrintWriter out = new PrintWriter(mySocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				mySocket.getInputStream(), "US-ASCII"));
		InputStream in2 = mySocket.getInputStream();
		out.println(query);
		int L = in2.read() * (256 * 256 * 256) + in2.read() * (256 * 256)
				+ in2.read() * (256) + in2.read();
		char res[] = new char[L];
		for (int i = 0; i < L; i++)
			res[i] = (char) in.read();
		out.close();
		in.close();
		in2.close();
		mySocket.close();
		return new String(res, 0, L);
	}

}
