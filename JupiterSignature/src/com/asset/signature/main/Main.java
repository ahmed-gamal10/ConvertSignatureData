package com.asset.signature.main;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.asset.handlers.ConnectionHandler;
import com.asset.handlers.PropertyHandler;
import com.asset.utilities.Constants;

public class Main {

	public static void main(String[] args) {
		
		JFileChooser filechooser = new JFileChooser();
		String file_path = "";
		FileNameExtensionFilter propertyfilter = new FileNameExtensionFilter(
				"properties files (*.properties)", "properties");
		filechooser.setDialogTitle("Select Property file");
		filechooser.setFileFilter(propertyfilter);
		filechooser.setCurrentDirectory(new File(System
				.getProperty("user.home")));
		int result = filechooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			file_path = filechooser.getSelectedFile().getAbsolutePath();

			System.out.println("Selected file: " + file_path);
			/*
			 * Read Property file
			 */
			PropertyHandler property = new PropertyHandler(file_path);
			HashMap<String, String> propertiesMap = property.getProperties();
			String tables = propertiesMap.get(Constants.tables);
			String coulmns = propertiesMap.get(Constants.coulmns);
			String[] tabless;
			String[] coulmnss;
			if (tables.contains(",")) {
				String[] tables_array = tables.split(",");
				tabless = tables_array;
			} else {
				String[] tables_array = { tables };
				tabless = tables_array;
			}

			if (coulmns.contains(",")) {
				String[] coulmns_array = coulmns.split(",");
				coulmnss = coulmns_array;
			} else {
				String[] coulmns_array = { coulmns };
				coulmnss = coulmns_array;
			}

			/*
			 * Connect to Database
			 */
			 
			ConnectionHandler connectionhandler = new ConnectionHandler(propertiesMap.get(Constants.db_driver),
					propertiesMap.get(Constants.server_ip),
					propertiesMap.get(Constants.port),
					propertiesMap.get(Constants.db_Name),
					propertiesMap.get(Constants.db_schema),
					propertiesMap.get(Constants.db_username),
					propertiesMap.get(Constants.db_password), tabless,
					coulmnss);

			connectionhandler.getConnection();
			connectionhandler.start_generating_SQLScript(tabless, coulmnss);
		}

	}

}
