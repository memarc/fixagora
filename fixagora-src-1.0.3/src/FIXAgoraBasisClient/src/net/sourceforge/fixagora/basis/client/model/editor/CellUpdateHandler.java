/**
 * Copyright (C) 2012-2013 Alexander Pinnow
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 **/
package net.sourceforge.fixagora.basis.client.model.editor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.fixagora.basis.shared.model.communication.SpreadSheetCell;

import org.apache.poi.ss.util.CellReference;

/**
 * The Class CellUpdateHandler.
 */
public class CellUpdateHandler {

	private Map<String, Long> lastUpdateMap = Collections.synchronizedMap(new HashMap<String, Long>());

	private Map<String, Long> failedMap = Collections.synchronizedMap(new HashMap<String, Long>());

	private Color blue = new Color(82, 122, 255);

	/**
	 * Adds the spread sheet cells.
	 *
	 * @param list the list
	 */
	public void addSpreadSheetCells(List<SpreadSheetCell> list) {

		for (SpreadSheetCell spreadSheetCell : list) {

			lastUpdateMap.put(
					Long.toString(spreadSheetCell.getSheet()) + CellReference.convertNumToColString(spreadSheetCell.getColumn())
							+ Integer.toString(spreadSheetCell.getRow()), System.currentTimeMillis());
		}
	}

	/**
	 * Adds the failed sheet cell.
	 *
	 * @param spreadSheetCell the spread sheet cell
	 */
	public void addFailedSheetCell(SpreadSheetCell spreadSheetCell) {

		failedMap.put(
				Long.toString(spreadSheetCell.getSheet()) + CellReference.convertNumToColString(spreadSheetCell.getColumn())
						+ Integer.toString(spreadSheetCell.getRow()), System.currentTimeMillis());
	}

	/**
	 * Checks if is repaint required.
	 *
	 * @return true, if is repaint required
	 */
	public boolean isRepaintRequired() {

		List<String> remove = new ArrayList<String>();

		Set<String> keys = lastUpdateMap.keySet();

		synchronized (lastUpdateMap) {
			Iterator<String> iterator = keys.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();

				Long start = lastUpdateMap.get(key);

				if (start != null) {
					long dist = System.currentTimeMillis() - start;
					if (dist > 1000) {
						remove.add(key);
					}
				}
			}
		}

		for (String sheetCell : remove)
			lastUpdateMap.remove(sheetCell);
		if (remove.size() > 0)
			return true;
		
		remove = new ArrayList<String>();
		
		keys = failedMap.keySet();

		synchronized (failedMap) {
			Iterator<String> iterator = keys.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();

				Long start = failedMap.get(key);

				if (start != null) {
					long dist = System.currentTimeMillis() - start;
					if (dist > 1000) {
						remove.add(key);
					}
				}
			}
		}

		for (String sheetCell : remove)
			failedMap.remove(sheetCell);
		if (remove.size() > 0)
			return true;
		
		return lastUpdateMap.size() +failedMap.size() > 0;
	}

	/**
	 * Gets the highlight color.
	 *
	 * @param color the color
	 * @param spreadSheetCell the spread sheet cell
	 * @return the highlight color
	 */
	public Color getHighlightColor(Color color, SpreadSheetCell spreadSheetCell) {

		if (spreadSheetCell == null)
			return color;

		long dist = System.currentTimeMillis();

		Color color2 = blue;

		Long failed = failedMap.get(Long.toString(spreadSheetCell.getSheet()) + CellReference.convertNumToColString(spreadSheetCell.getColumn())
				+ Integer.toString(spreadSheetCell.getRow()));
		
		if (failed != null) {
			
			color2 = Color.RED;
			dist = dist - failed;

			if (dist > 1000) {
				failedMap.remove(Long.toString(spreadSheetCell.getSheet()) + CellReference.convertNumToColString(spreadSheetCell.getColumn())
						+ Integer.toString(spreadSheetCell.getRow()));
				return color;
			}
		}
		else {

			Long start = lastUpdateMap.get(Long.toString(spreadSheetCell.getSheet()) + CellReference.convertNumToColString(spreadSheetCell.getColumn())
					+ Integer.toString(spreadSheetCell.getRow()));

			if (start == null)
				return color;
			dist = dist - start;

			if (dist > 1000) {
				lastUpdateMap.remove(Long.toString(spreadSheetCell.getSheet()) + CellReference.convertNumToColString(spreadSheetCell.getColumn())
						+ Integer.toString(spreadSheetCell.getRow()));
				return color;
			}
		}

		double shift = (1000d - dist) / 1000d;

		double redShift = color2.getRed() - color.getRed();
		double greenShift = color2.getGreen() - color.getGreen();
		double blueShift = color2.getBlue() - color.getBlue();

		int newRed = color.getRed() + (int) (redShift * shift);
		int newGreen = color.getGreen() + (int) (greenShift * shift);
		int newBlue = color.getBlue() + (int) (blueShift * shift);

		if (newRed < 0)
			newRed = 0;
		if (newGreen < 0)
			newGreen = 0;
		if (newBlue < 0)
			newBlue = 0;

		if (newRed > 255)
			newRed = 255;
		if (newGreen > 255)
			newGreen = 255;
		if (newBlue > 255)
			newBlue = 255;

		return new Color(newRed, newGreen, newBlue);

	}

	/**
	 * Gets the fadeout color.
	 *
	 * @param color the color
	 * @param fadeOut the fade out
	 * @param spreadSheetCell the spread sheet cell
	 * @return the fadeout color
	 */
	public Color getFadeoutColor(Color color, Color fadeOut, SpreadSheetCell spreadSheetCell) {

		if (spreadSheetCell == null)
			return color;

		Long start = lastUpdateMap.get(Long.toString(spreadSheetCell.getSheet()) + CellReference.convertNumToColString(spreadSheetCell.getColumn())
				+ Integer.toString(spreadSheetCell.getRow()));
		if (start == null)
			return color;
		long dist = System.currentTimeMillis() - start;

		if (dist > 1000) {
			lastUpdateMap.remove(Long.toString(spreadSheetCell.getSheet()) + CellReference.convertNumToColString(spreadSheetCell.getColumn())
					+ Integer.toString(spreadSheetCell.getRow()));
			return color;
		}

		double shift = (1000d - dist) / 1000d;

		double redShift = fadeOut.getRed() - color.getRed();
		double greenShift = fadeOut.getGreen() - color.getGreen();
		double blueShift = fadeOut.getBlue() - color.getBlue();

		int newRed = color.getRed() + (int) (redShift * shift);
		int newGreen = color.getGreen() + (int) (greenShift * shift);
		int newBlue = color.getBlue() + (int) (blueShift * shift);

		if (newRed < 0)
			newRed = 0;
		if (newGreen < 0)
			newGreen = 0;
		if (newBlue < 0)
			newBlue = 0;

		if (newRed > 255)
			newRed = 255;
		if (newGreen > 255)
			newGreen = 255;
		if (newBlue > 255)
			newBlue = 255;

		return new Color(newRed, newGreen, newBlue);

	}

}
