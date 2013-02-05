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
package com.greatmancode.craftconomy3.utils;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.logging.Level;

import com.greatmancode.craftconomy3.Common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class that allow to check if the plugin is on the latest version.
 */
public class VersionChecker {
	private boolean oldVersion = false;
	private String newVersion = "";

	/**
	 * Initialize the VersionChecker by checking if the plugin is outdated.
	 * @param currentVersion The current plugin version.
	 */
	public VersionChecker(String currentVersion) {
		if (Common.getInstance().getServerCaller().getPluginVersion().contains("SNAPSHOT")) {
			Common.getInstance().sendConsoleMessage(Level.WARNING, "You are running a dev-build! Be sure that you check on the website if there's a new version!");
			return;
		}
		String pluginUrlString = "http://dev.bukkit.org/server-mods/craftconomy/files.rss";
		try {
			URL url = new URL(pluginUrlString);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement.getElementsByTagName("title");
				Element firstNameElement = (Element) firstElementTagName.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				if (!currentVersion.contains(firstNodes.item(0).getNodeValue())) {
					oldVersion = true;
					newVersion = firstNodes.item(0).getNodeValue();
				}
			}
		} catch (Exception e) {
			Common.getInstance().sendConsoleMessage(Level.SEVERE, "Error while trying to check for the latest version. The error is: " + e.getMessage());
		}
	}

	/**
	 * Check if the current version is a old one.
	 * @return True if the current plugin version is old. Else false.
	 */
	public boolean isOld() {
		return oldVersion;
	}

	/**
	 * Returns the new version of the plugin.
	 * @return The new version of the plugin.
	 */
	public String getNewVersion() {
		return newVersion;
	}
}
