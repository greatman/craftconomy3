package com.greatmancode.craftconomy3;

import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.ServiceManager.ServicePriority;
import org.spout.api.plugin.services.EconomyService;

import com.greatmancode.craftconomy3.spout.EconomyServiceHandler;

public class SpoutLoader extends CommonPlugin {

	private static SpoutLoader instance = null;
	@Override
	public void onEnable() {
		instance = this;
		new Common(false, getLogger()).initialize();
		getEngine().getServiceManager().register(EconomyService.class, new EconomyServiceHandler(), this, ServicePriority.Normal);
	}

	@Override
	public void onDisable() {

	}
	
	public static SpoutLoader getInstance() {
		return instance;
	}

}
