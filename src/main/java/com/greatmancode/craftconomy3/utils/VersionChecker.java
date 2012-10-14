/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2012, Greatman <http://github.com/greatman/>
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
package com.greatmancode.craftconomy3.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class VersionChecker {

	private boolean oldVersion = false;
	private String newVersion = "";

	public VersionChecker(String currentVersion) {
		try {
			URL url = new URL("https://dl.dropbox.com/s/p326su6cxs4tih1/versioncheck?dl=1");

			Scanner s = new Scanner(url.openStream());
			String value = s.next();
			if (!currentVersion.contains(value)) {
				oldVersion = true;
				newVersion = value;
			}
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isOld() {
		return oldVersion;
	}

	public String getNewVersion() {
		return newVersion;
	}

	public static void main(String[] args) {
		VersionChecker versionCheck = new VersionChecker("3.0.1");
		System.out.println(versionCheck.isOld());
		System.out.println(versionCheck.getNewVersion());
	}
}
