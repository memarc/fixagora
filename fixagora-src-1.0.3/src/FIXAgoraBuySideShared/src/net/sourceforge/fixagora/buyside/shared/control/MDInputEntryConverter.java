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
package net.sourceforge.fixagora.buyside.shared.control;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.sourceforge.fixagora.buyside.shared.communication.AbstractBuySideMDInputEntry;

/**
 * The Class MDInputEntryConverter.
 */
public class MDInputEntryConverter {


	/**
	 * Gets the bytes.
	 *
	 * @param abstractBuySideMDInpuEntries the abstract buy side md inpu entries
	 * @return the bytes
	 */
	public static byte[] getBytes(List<AbstractBuySideMDInputEntry> abstractBuySideMDInpuEntries)
	{
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(abstractBuySideMDInpuEntries);
			objectOutputStream.close();
			byteArrayOutputStream.close();

			ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream2);
			gzip.write(byteArrayOutputStream.toByteArray());
			gzip.finish();
			gzip.close();

			return byteArrayOutputStream2.toByteArray();
		}
		catch (IOException e) {
			return new byte[0];
		}

	}
	
	/**
	 * Gets the buy side md input entries.
	 *
	 * @param bytes the bytes
	 * @return the buy side md input entries
	 */
	public static List<AbstractBuySideMDInputEntry> getBuySideMDInputEntries(byte[] bytes)
	{
		try {

			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
			GZIPInputStream gzip = new GZIPInputStream(byteArrayInputStream);
			ObjectInputStream objectInputStream = new ObjectInputStream(gzip);
			@SuppressWarnings("unchecked")
			List<AbstractBuySideMDInputEntry> abstractBuySideMDInpuEntries = (List<AbstractBuySideMDInputEntry>) objectInputStream.readObject();

			return abstractBuySideMDInpuEntries;
		}
		catch (Exception e) {
			return new ArrayList<AbstractBuySideMDInputEntry>();
		}

	}
	
}
