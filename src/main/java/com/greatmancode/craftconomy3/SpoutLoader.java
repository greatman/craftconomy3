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

import com.greatmancode.craftconomy3.events.SpoutListener;
import com.greatmancode.craftconomy3.spout.EconomyServiceHandler;

import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.ServiceManager.ServicePriority;
import org.spout.api.plugin.services.EconomyService;

/**
 * Class used when the plugin is loaded from Spout.
 * @author greatman
 */
public class SpoutLoader extends CommonPlugin implements Loader {
	@Override
	public void onEnable() {
		new Common(this, getLogger()).initialize();
		if (Common.isInitialized()) {
			getEngine().getServiceManager().register(EconomyService.class, new EconomyServiceHandler(), this, ServicePriority.High);
			this.getEngine().getEventManager().registerEvents(new SpoutListener(), this);
		}
	}

	@Override
	public void onDisable() {
		Common.getInstance().disable();
	}

	@Override
	public ServerType getServerType() {
		return ServerType.SPOUT;
	}
}
