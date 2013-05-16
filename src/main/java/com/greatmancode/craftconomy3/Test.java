/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
 *
 * Craftconomy3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Craftconomy3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Craftconomy3.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.greatmancode.craftconomy3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
	public static void main(String[] args) {
		Pattern p = Pattern.compile(".*?:");
		Matcher m = p.matcher("metric_start_error: \"Unable to load Metrics! The error is: %s\"");

		if (m.find()) {
			System.out.println(m.group(0));
		}


		try {
			System.out.println(new File(Test.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "lang.yml").getAbsolutePath());
			BufferedReader br = new BufferedReader(new FileReader(new File(Test.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "lang.yml")));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Test.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "output.yml")));
			String line;
			while ((line = br.readLine()) != null) {
				m = p.matcher(line);
				if (m.find()) {
					String key = m.group(0).replace(":", "");
					bw.write("languageManager.addLanguageEntry(\"" + key + "\",\"" + line.replace(key, "").replace("\"", "").replaceFirst(": ", "") + "\");\n");
				}

			}
			bw.close();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
