package com.asset.handlers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.Beans;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;

import javax.comm.CommDriver;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.asset.utilities.Constants;
import com.topaz.sigplus.SigPlus;

public class SignatureHandler {

	public SigPlus sigObj = null;
	public static String TABLETMODEL = "SignatureGemLCD4X3";
	public static String TABLETPORT = "HID1";
	private boolean dataChanged = false;
	String drivername = "com.sun.comm.Win32Driver";
	StringBuilder update_Script = new StringBuilder();

	public SignatureHandler() {
		try {
			CommDriver driver = (CommDriver) Class.forName(drivername)
					.newInstance();
			driver.initialize();
			ClassLoader cl = (com.topaz.sigplus.SigPlus.class).getClassLoader();
			sigObj = (SigPlus) Beans.instantiate(cl,
					"com.topaz.sigplus.SigPlus");
			sigObj.setVisible(true);
			sigObj.setTabletClippingMode(true);
			sigObj.enableInputMethods(true);
			sigObj.setTabletModel(TABLETMODEL);
			sigObj.setTabletComPort(TABLETPORT);
			sigObj.setImageTransparentMode(false);
			sigObj.clearTablet();
			sigObj.setEnabled(true);
			int width = 2000;
			int height = 2000;

			sigObj.setBounds(134, 160, 800, 400);
			sigObj.setImageXSize(width);
			sigObj.setImageYSize(height);
			// Set the logical sizes to match JDoc's
			sigObj.setTabletLogicalXSize(width * 4);
			sigObj.setTabletLogicalYSize(height * 4);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	public void convert(ResultSet rs, String table_name, String coulmn_name) {
		try {
			while (rs.next()) {
				sigObj.setSigString(rs.getString(2));
				BufferedImage bi = new BufferedImage(sigObj.getWidth(),
						sigObj.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics g = bi.getGraphics();
				sigObj.paint(g);
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				for (int x = 0; x < sigObj.getWidth(); x++) {
					for (int y = 0; y < sigObj.getHeight(); y++) {
						int rgb = bi.getRGB(x, y);
						if (rgb == -16777216) {
							sb.append("{\"lx\":" + x + ",\"ly\":" + y
									+ ",\"mx\":" + (x + 1) + ",\"my\":" + y
									+ "},");
						}
					}
				}
				if (sb.length() > 2)
					sb.deleteCharAt(sb.length() - 1);
				sb.append("]");
				System.out.println(sb.toString());
				generate_SQLScript(sb.toString(), table_name, coulmn_name,
						rs.getString("FIELD0"));
			}
			save_UpdateScript();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generate_SQLScript(String new_data, String table_name,
			String coulmn_name, String item_id) {

		if (!new_data.equals("[]")) {
			update_Script.append(Constants.update + " " + Constants.table + " "
					+ table_name);
			update_Script.append(" " + Constants.set + " " + coulmn_name + " ="
					+ "'" + new_data + "' " + Constants.where);
			update_Script.append(" FIELD0='" + item_id + "';");
			update_Script.append("\n");
		}

	}

	public void save_UpdateScript() {
		JFileChooser filechooser = new JFileChooser();
		String file_path;
		FileNameExtensionFilter propertyfilter = new FileNameExtensionFilter(
				"sql files (*.sql)", "sql");
		filechooser.setDialogTitle("Save Sql file");
		filechooser.setFileFilter(propertyfilter);
		filechooser.setCurrentDirectory(new File(System
				.getProperty("user.home")));
		int result = filechooser.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			file_path = filechooser.getSelectedFile().getAbsolutePath();
			java.io.FileWriter fw;
			try {
				fw = new java.io.FileWriter(filechooser.getSelectedFile()
						+ Constants.sql_extenstion);
				fw.write(update_Script.toString());
				fw.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"Error in Saving Sql File!", "Error",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} finally {
				fw = null;
			}
			System.out.println("Save file: " + file_path
					+ Constants.sql_extenstion);
		}
	}

	public StringBuilder getUpdate_Script() {
		return update_Script;
	}

	public void setUpdate_Script(StringBuilder update_Script) {
		this.update_Script = update_Script;
	}

}
