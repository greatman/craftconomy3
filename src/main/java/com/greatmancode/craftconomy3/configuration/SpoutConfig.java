package com.greatmancode.craftconomy3.configuration;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.yaml.YamlConfiguration;

import com.greatmancode.craftconomy3.Common;

public class SpoutConfig extends Config {

	private YamlConfiguration config = null;
	public SpoutConfig() {
		File file = new File(Common.getInstance().getConfigurationManager().getDataFolder(), "config.yml");
		if (!file.exists())
		{
			URL inputURL = getClass().getResource("config.yml");
			try {
				FileUtils.copyURLToFile(inputURL, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = new YamlConfiguration(file);
		try {
			config.load();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public int getInt(String path) {
		return config.getNode(path).getInt();
	}

	@Override
	public long getLong(String path) {
		return config.getNode(path).getLong();
	}

	@Override
	public double getDouble(String path) {
		return config.getNode(path).getDouble();
	}

	@Override
	public String getString(String path) {
		return config.getNode(path).getString();
	}

}
