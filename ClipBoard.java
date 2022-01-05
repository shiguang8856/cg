package excelbb;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;

public class ClipBoard {

	public static Toolkit toolkit = Toolkit.getDefaultToolkit();
	public static Clipboard clipboard = toolkit.getSystemClipboard();

	public static void addFlavorListener(FlavorListener l) {
		clipboard.addFlavorListener(l);
	}

	public static void setStringUtilSuccess(String srcData) {
		boolean b = false;
		do {
			try {
				StringSelection contents = new StringSelection(srcData);
				clipboard.setContents(contents, null);
				b = true;
			} catch (Exception e) {
			}
		} while (!b);
	}

	public static String getString() {
		String str = null;
		try {
			str = (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			System.out.println("cannot get clipboard string");
//			e.printStackTrace();
		}
		return str;
	}

	public static Image getImage() {
		Image img = null;
		try {
			img = (Image) clipboard.getData(DataFlavor.imageFlavor);
		} catch (Exception e) {
			System.out.println("cannot get clipboard image");
		}
		return img;
	}

	public static Toolkit getToolkit() {
		return toolkit;
	}

	public static void setToolkit(Toolkit toolkit) {
		ClipBoard.toolkit = toolkit;
	}

	public static Clipboard getClipboard() {
		return clipboard;
	}

	public static void setClipboard(Clipboard clipboard) {
		ClipBoard.clipboard = clipboard;
	}

}
