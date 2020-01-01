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
package net.sourceforge.fixagora.basis.client.model;


/**
 * The Class PluginInfo.
 */
public class PluginInfo {
	
	private String pluginName = null;
	
	private String copyRight  = null;
	
	private String contact = null;
	
	private String licenseText = null;

	/**
	 * Instantiates a new plugin info.
	 *
	 * @param pluginName the plugin name
	 * @param copyRight the copy right
	 * @param contact the contact
	 * @param licenseText the license text
	 */
	public PluginInfo(String pluginName, String copyRight, String contact, String licenseText) {

		super();
		this.pluginName = pluginName;
		this.copyRight = copyRight;
		this.contact = contact;
		this.licenseText = licenseText;
	}

	/**
	 * Instantiates a new plugin info.
	 *
	 * @param pluginName the plugin name
	 * @param copyRight the copy right
	 * @param contact the contact
	 */
	public PluginInfo(String pluginName, String copyRight, String contact) {

		super();
		this.pluginName = pluginName;
		this.copyRight = copyRight;
		this.contact = contact;
		
		final StringBuffer stringBuffer = new StringBuffer(pluginName);
		stringBuffer
				.append(" is free software; you can redistribute it and/or modify it under the terms of the GNU Library General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.");
		stringBuffer.append("\n\n");
		stringBuffer.append(pluginName);
		stringBuffer
				.append(" is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more details.");
		stringBuffer.append("\n\n");
		stringBuffer
				.append("You should have received a copy of the GNU Library General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.");

		licenseText = stringBuffer.toString();
	}

	
	/**
	 * Gets the plugin name.
	 *
	 * @return the plugin name
	 */
	public String getPluginName() {
	
		return pluginName;
	}

	
	/**
	 * Gets the copy right.
	 *
	 * @return the copy right
	 */
	public String getCopyRight() {
	
		return copyRight;
	}

	
	/**
	 * Gets the contact.
	 *
	 * @return the contact
	 */
	public String getContact() {
	
		return contact;
	}

	
	/**
	 * Gets the license text.
	 *
	 * @return the license text
	 */
	public String getLicenseText() {
	
		return licenseText;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return pluginName;
	}
	
	

}
