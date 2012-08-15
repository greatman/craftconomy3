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
package com.greatmancode.craftconomy3;

import java.io.IOException;

import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.ServiceManager.ServicePriority;
import org.spout.api.plugin.services.EconomyService;

import com.greatmancode.craftconomy3.spout.EconomyServiceHandler;
import com.greatmancode.craftconomy3.utils.MetricsSpout;

/**
 * Class used when the plugin is loaded from Spout.
 * @author greatman
 * 
 */
public class CC3SpoutLoader extends CommonPlugin {

	private static CC3SpoutLoader instance = null;
	private MetricsSpout metrics;

	@Override
	public void onEnable() {
		instance = this;
		try {
			metrics = new MetricsSpout(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
		new Common(false, getLogger()).initialize();
		getEngine().getServiceManager().register(EconomyService.class, new EconomyServiceHandler(), this, ServicePriority.High);

	}

	@Override
	public void onDisable() {
		Common.getInstance().disable();
	}

	public static CC3SpoutLoader getInstance() {
		return instance;
	}

	public MetricsSpout getMetrics() {
		return metrics;
	}

}
