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
package net.sourceforge.fixagora.basis.server.control;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * The Class JarFileLoader.
 */
public class JarFileLoader extends URLClassLoader {

	/**
	 * Instantiates a new jar file loader.
	 *
	 * @param urls the urls
	 */
	public JarFileLoader(URL[] urls) {

		super(urls);
	}

	/**
	 * Adds the file.
	 *
	 * @param path the path
	 * @throws MalformedURLException the malformed url exception
	 */
	public void addFile(String path) throws MalformedURLException {

		String urlPath = "jar:file://" + path + "!/";
		addURL(new URL(urlPath));
	}

}