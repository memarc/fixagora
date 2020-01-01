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
package net.sourceforge.fixagora.excel.shared.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import net.sourceforge.fixagora.basis.shared.model.persistence.FUser;

/**
 * The Class AssignedUserSettings.
 */
@Entity
public class AssignedUserSettings implements Serializable {

	private static final long serialVersionUID = 1L;
		
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "exceltradecapture_id", insertable = false, updatable = false, nullable = false)
	private ExcelTradeCapture excelTradeCapture = null;
	
	@ManyToOne(fetch = FetchType.LAZY,optional=false)
	private FUser fUser = null;
	
	@Column(nullable = true, unique = false, length=2000)
	private String path = null;
	
	@Column(nullable = true, unique = false)
	private String sheet = null;
	
	@Column(nullable = true, unique = false)
	private Boolean deleteAfterExport = null;

	@Column(nullable = true, unique = false)
	private Boolean replaceOldSheet = null;
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
	
		return id;
	}




	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
	
		this.id = id;
	}





	
	/**
	 * Gets the excel trade capture.
	 *
	 * @return the excel trade capture
	 */
	public ExcelTradeCapture getExcelTradeCapture() {
	
		return excelTradeCapture;
	}





	
	/**
	 * Sets the excel trade capture.
	 *
	 * @param excelTradeCapture the new excel trade capture
	 */
	public void setExcelTradeCapture(ExcelTradeCapture excelTradeCapture) {
	
		this.excelTradeCapture = excelTradeCapture;
	}





	
	
	/**
	 * Gets the replace old sheet.
	 *
	 * @return the replace old sheet
	 */
	public Boolean getReplaceOldSheet() {
	
		return replaceOldSheet;
	}





	
	/**
	 * Sets the replace old sheet.
	 *
	 * @param replaceOldSheet the new replace old sheet
	 */
	public void setReplaceOldSheet(Boolean replaceOldSheet) {
	
		this.replaceOldSheet = replaceOldSheet;
	}





	/**
	 * Gets the f user.
	 *
	 * @return the f user
	 */
	public FUser getfUser() {
	
		return fUser;
	}





	
	/**
	 * Sets the f user.
	 *
	 * @param fUser the new f user
	 */
	public void setfUser(FUser fUser) {
	
		this.fUser = fUser;
	}





	
	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
	
		return path;
	}





	
	/**
	 * Sets the path.
	 *
	 * @param path the new path
	 */
	public void setPath(String path) {
	
		this.path = path;
	}





	
	/**
	 * Gets the sheet.
	 *
	 * @return the sheet
	 */
	public String getSheet() {
	
		return sheet;
	}





	
	/**
	 * Sets the sheet.
	 *
	 * @param sheet the new sheet
	 */
	public void setSheet(String sheet) {
	
		this.sheet = sheet;
	}





	
	/**
	 * Gets the delete after export.
	 *
	 * @return the delete after export
	 */
	public Boolean getDeleteAfterExport() {
	
		return deleteAfterExport;
	}





	
	/**
	 * Sets the delete after export.
	 *
	 * @param deleteAfterExport the new delete after export
	 */
	public void setDeleteAfterExport(Boolean deleteAfterExport) {
	
		this.deleteAfterExport = deleteAfterExport;
	}
	
	
	/**
	 * Make eager.
	 */
	public void makeEager() {
		
		if(fUser!=null)
			fUser.getName();
		
	}

	
}
