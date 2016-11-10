package com.asset.signature.main;

import java.awt.Button;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.Beans;
import java.io.File;
import java.io.IOException;

import javax.comm.CommDriver;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.asset.utilities.Constants;
import com.topaz.sigplus.SigPlus;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConvertJSONtoJPG extends JFrame {

	private class SignatureListener implements MouseListener, KeyListener {
		public void keyPressed(KeyEvent e) {
			setDataChanged(true);
		}

		public void keyReleased(KeyEvent e) {
			// Listen for delete key events
			if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				Object source = e.getSource();
				if (source instanceof SigPlus && ((SigPlus) source).isEnabled()) {
					((SigPlus) source).clearTablet();
					e.consume();
				}
			}
		}

		public void keyTyped(KeyEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
			Object source = e.getSource();
			if (source instanceof SigPlus && ((SigPlus) source).isEnabled()) {

				if (activeTablet != null) {
					activeTablet.setTabletState(0);
					JPanel panel = (JPanel) (activeTablet).getParent();
					panel.setBorder(BorderFactory
							.createEtchedBorder(EtchedBorder.LOWERED));
				}
				// turn capture on for the source of the click
				// Grab the focus so this control can recieve key events
				((SigPlus) source).requestFocus();
				((SigPlus) source).setTabletState(1);
				JPanel panel = (JPanel) ((SigPlus) source).getParent();
				panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				activeTablet = (SigPlus) source;
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

	}
	public static SigPlus activeTablet = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1755311195480977181L;
	public static String TABLETMODEL = "SignatureGemLCD4X3";
	public static String TABLETPORT = "HID1";
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConvertJSONtoJPG frame = new ConvertJSONtoJPG();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private Panel contentPane;
	private boolean dataChanged = false;
	BufferedImage image;

	SigPlus sigObj = null;

	final TextArea textField;

	/**
	 * Create the frame.
	 */
	public ConvertJSONtoJPG() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(EXIT_ON_CLOSE);
			}
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(EXIT_ON_CLOSE);
			}
		});
		/* Initialize frame */
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 442);
		contentPane = new Panel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setLayout(null);

		/* Initialize components */

		// textField
		textField = new TextArea();
		textField.setBounds(134, 10, 623, 123);

		// label
		Label label = new Label("Json Input:");
		label.setBounds(9, 9, 119, 23);

		// drawJson_button
		Button drawJson_button = new Button("Draw Json");
		drawJson_button.setBounds(763, 38, 111, 22);

		// drowSig_button
		Button drowSig_button = new Button("Draw Hexadecimal");
		drowSig_button.setBounds(763, 10, 111, 22);

		// json_output
		final TextArea json_output = new TextArea();
		json_output.setBounds(810, 160, 500, 200);
		json_output.setEditable(false);

		// copy_button
		Button copy_button = new Button("Copy");
		copy_button.setBounds(810, 370, 111, 22);

		// saveAsjpg_button
		Button saveAsjpg_button = new Button("Save as jpg Image");
		saveAsjpg_button.setBounds(810, 400, 111, 22);

		// convert_button
		Button convert_button = new Button("Convert To JSON");
		convert_button.setBounds(763, 70, 111, 22);

		// sigObj
		String drivername = "com.sun.comm.Win32Driver";
		try {
			CommDriver driver = (CommDriver) Class.forName(drivername)
					.newInstance();
			driver.initialize();
		} catch (Throwable th) {
		}
		try {
			ClassLoader cl = (com.topaz.sigplus.SigPlus.class).getClassLoader();
			sigObj = (SigPlus) Beans.instantiate(cl,
					"com.topaz.sigplus.SigPlus");
			sigObj.setVisible(true);
			sigObj.setTabletClippingMode(true);
			SignatureListener sigListener = new SignatureListener();
			sigObj.addMouseListener(sigListener);
			sigObj.enableInputMethods(true);
			sigObj.addKeyListener(sigListener);

			sigObj.setTabletModel(TABLETMODEL);
			sigObj.setTabletComPort(TABLETPORT);

			sigObj.setImageTransparentMode(false);
			sigObj.clearTablet();
			sigObj.setEnabled(true);
			int width = 500;
			int height = 500;
			sigObj.setBounds(9, 160, 800, 500);
			sigObj.setImageXSize(width);
			sigObj.setImageYSize(height);
			sigObj.setTabletLogicalXSize(width * 4);
			sigObj.setTabletLogicalYSize(height * 4);
		} catch (Exception e) {
		}

		contentPane.add(textField);
		contentPane.add(label);
		contentPane.add(drawJson_button);
		contentPane.add(sigObj);
		// contentPane.add(drowSig_button);
		// contentPane.add(convert_button);
		// contentPane.add(json_output);
		// contentPane.add(copy_button);
		// contentPane.add(saveAsjpg_button);

		/* buttons actions */
		drawJson_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawJsonData();
			}
		});

		drowSig_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSignature(textField.getText());
			}
		});

		copy_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection stringSelection = new StringSelection(
						json_output.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
			}
		});

		saveAsjpg_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsJPG();
			}
		});

		convert_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				json_output.setText(convertHexatoJSON());

			}
		});
	}

	public String convertHexatoJSON() {
		BufferedImage bi = new BufferedImage(sigObj.getWidth(),
				sigObj.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		sigObj.paint(g);
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("[");

			for (int x = 0; x < sigObj.getWidth(); x++) {
				for (int y = 0; y < sigObj.getHeight(); y++) {
					int rgb = bi.getRGB(x, y);
					if (rgb == -16777216) {
						sb.append("{\"lx\":" + x + ",\"ly\":" + y + ",\"mx\":"
								+ (x + 1) + ",\"my\":" + y + "},");
					}
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]");

			System.out.println(sb.toString());
			image = bi;
			return sb.toString();
		} catch (Exception e2) {
			e2.printStackTrace();
			JOptionPane.showMessageDialog(null, e2.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	public void drawJsonData() {
		BufferedImage bi_new = new BufferedImage(sigObj.getWidth(),
				sigObj.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi_new.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, sigObj.getWidth(), sigObj.getHeight());
		g.setColor(Color.BLACK);
		try {
			JSONArray jsonArray = new JSONArray(textField.getText());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject rec = jsonArray.getJSONObject(i);
				int lx = rec.getInt("lx");
				int ly = rec.getInt("ly");
				int mx = rec.getInt("mx");
				int my = rec.getInt("my");
				System.out.println("Point[" + i + "] " + lx + " " + ly + " "
						+ mx + " " + my);
				g.drawLine(lx, ly, mx, my);
			}
			JFileChooser filechooser = new JFileChooser();
			String file_path;
			FileNameExtensionFilter propertyfilter = new FileNameExtensionFilter(
					"jpg files (*.jpg)", "jpg");
			filechooser.setDialogTitle("Save jgp file");
			filechooser.setFileFilter(propertyfilter);
			filechooser.setCurrentDirectory(new File(System
					.getProperty("user.home")));
			int result = filechooser.showSaveDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				file_path = filechooser.getSelectedFile().getAbsolutePath();
				java.io.FileWriter fw;
				try {
					ImageIO.write(bi_new, "PNG", new File(file_path
							+ Constants.jpg_extention));
					JOptionPane.showMessageDialog(null, "Image Saved Success",
							"Success", JOptionPane.DEFAULT_OPTION);
				} catch (IOException ee) {
					JOptionPane.showMessageDialog(null,
							"Error in Saving jpg Image!", "Error",
							JOptionPane.ERROR_MESSAGE);
					ee.printStackTrace();
				} finally {
					fw = null;
				}
				System.out.println("Save file: " + file_path
						+ Constants.jpg_extention);
			}

		} catch (JSONException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);

		}
		setSignature(textField.getText());
	}

	public void saveAsJPG() {
		if (image != null) {
			JFileChooser filechooser = new JFileChooser();
			String file_path;
			FileNameExtensionFilter propertyfilter = new FileNameExtensionFilter(
					"jpg files (*.jpg)", "jpg");
			filechooser.setDialogTitle("Save jgp file");
			filechooser.setFileFilter(propertyfilter);
			filechooser.setCurrentDirectory(new File(System
					.getProperty("user.home")));
			int result = filechooser.showSaveDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				file_path = filechooser.getSelectedFile().getAbsolutePath();
				java.io.FileWriter fw;
				try {
					ImageIO.write(image, "PNG", new File(file_path
							+ Constants.jpg_extention));
					JOptionPane.showMessageDialog(null, "Image Saved Success",
							"Success", JOptionPane.DEFAULT_OPTION);
				} catch (IOException ee) {
					JOptionPane.showMessageDialog(null,
							"Error in Saving jpg Image!", "Error",
							JOptionPane.ERROR_MESSAGE);
					ee.printStackTrace();
				} finally {
					fw = null;
				}
				System.out.println("Save file: " + file_path
						+ Constants.jpg_extention);
			}
		} else {
			JOptionPane.showMessageDialog(null,
					"Convert the Hexadecimal to Json First");
		}
	}

	public void setDataChanged(boolean dataChanged) {
		this.dataChanged = dataChanged;
	}

	public void setSignature(String signature) {
		sigObj.setSigString(textField.getText());
	}
}
