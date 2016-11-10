package com.asset.handlers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import com.asset.utilities.Constants;

public class PropertyHandler {
	Properties prop;
	InputStream input = null;

	public PropertyHandler(String file_Path) {
		loadfile(file_Path);
	}

	private void loadfile(String file_path) {
		try {
			input = new FileInputStream(file_path);
			// load a properties file
			prop = new Properties();
			prop.load(input);

			// get the property value and print it out
			System.out.println(prop.getProperty("db_url"));
			System.out.println(prop.getProperty("db_username"));
			System.out.println(prop.getProperty("db_password"));
			System.out.println(prop.getProperty("db_driver"));
			System.out.println(prop.getProperty("tables"));
			System.out.println(prop.getProperty(Constants.coulmns));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public HashMap<String, String> getProperties() {

		try {
			HashMap<String, String> propertiesMap = new HashMap<String, String>();
			propertiesMap.put(Constants.db_Name,
					prop.getProperty(Constants.db_Name));
			propertiesMap.put(Constants.db_driver,
					prop.getProperty(Constants.db_driver));
			propertiesMap.put(Constants.db_schema,
					prop.getProperty(Constants.db_schema));
			propertiesMap.put(Constants.db_username,
					prop.getProperty(Constants.db_username));
			propertiesMap.put(Constants.db_password,
					prop.getProperty(Constants.db_password));
			propertiesMap.put(Constants.tables,
					prop.getProperty(Constants.tables));
			propertiesMap.put(Constants.coulmns,
					prop.getProperty(Constants.coulmns));
			propertiesMap.put(Constants.server_ip,
					prop.getProperty(Constants.server_ip));
			propertiesMap.put(Constants.port, prop.getProperty(Constants.port));

			return propertiesMap;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
