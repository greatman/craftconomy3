package com.greatmancode.craftconomy3;

import java.util.logging.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "Craftconomy3", name = "Craftconomy", version = "3.0.5")
@NetworkMod(clientSideRequired = false, serverSideRequired = true)
public class ForgeLoader implements Loader {

	@Instance("Generic")
	public static ForgeLoader instance;
	public ForgeLoader() {
		instance = this;
	}
	@Mod.ServerStarted
	public void load(FMLServerStartedEvent event) {
		new Common(this, Logger.getLogger("Minecraft")).initialize();
	}

	@Override
	public ServerType getServerType() {
		return ServerType.FORGE;
	}

}
