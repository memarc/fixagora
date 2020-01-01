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
package net.sourceforge.fixagora.basis.client.model.message;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.List;

import javax.swing.JPanel;

import net.sourceforge.fixagora.basis.client.control.DictionaryParser;

/**
 * The Class FIXMessage.
 */
public class FIXMessage extends AbstractFIXElement implements Comparable<FIXMessage> {

	private DictionaryParser dictionaryParser = null;

	private FIXComponent header = null;

	private String messageCat = null;

	private String messageType = null;

	private FIXComponent trailer = null;

	/**
	 * Instantiates a new fIX message.
	 *
	 * @param name the name
	 * @param messageType the message type
	 * @param messageCat the message cat
	 * @param fixMessageEntries the fix message entries
	 * @param dictionaryParser the dictionary parser
	 */
	public FIXMessage(final String name, final String messageType, final String messageCat, final List<AbstractFIXElement> fixMessageEntries,
			final DictionaryParser dictionaryParser) {

		super(name, 0, FieldType.REQUIRED, fixMessageEntries);

		this.messageType = messageType;
		this.messageCat = messageCat;
		this.dictionaryParser = dictionaryParser;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final FIXMessage o) {

		return this.getName().compareTo(o.getName());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixpusher.model.message.AbstractFIXElement#getDepth()
	 */
	@Override
	public int getDepth() {

		int max = dictionaryParser.getHeader().getDepth();
		for (final AbstractFIXElement abstractFIXElement : fixFields)
			if (abstractFIXElement.getDepth() > max)
				max = abstractFIXElement.getDepth();
		return max + 1;
	}


	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public FIXComponent getHeader() {

		if (header == null)
			header = dictionaryParser.getHeader();

		return header;
	}

	/**
	 * Gets the message cat.
	 *
	 * @return the message cat
	 */
	public String getMessageCat() {

		return messageCat;
	}

	/**
	 * Gets the message type.
	 *
	 * @return the message type
	 */
	public String getMessageType() {

		return messageType;
	}

	/**
	 * Gets the trailer.
	 *
	 * @return the trailer
	 */
	public FIXComponent getTrailer() {

		if (trailer == null)
			trailer = dictionaryParser.getTrailer();

		return trailer;
	}

	private void initFontProperties(final AbstractFIXElement abstractFIXElement, final FontMetrics fontMetrics, final FontMetrics fontMetrics2) {

		final int fieldRequiredWidth = fontMetrics.stringWidth("Conditionally");
		final int fieldTypeWidth = fontMetrics.stringWidth("Component");
		final int fieldNumberWidth = fontMetrics.stringWidth(Integer.toString(abstractFIXElement.getNumber()));
		final int fieldNameWidth = fontMetrics2.stringWidth(abstractFIXElement.getName());
		final FontProperties fontProperties = abstractFIXElement.getFontProperties();

		int fieldDataTypeWidth = 0;
		if (abstractFIXElement instanceof FIXField)
			fieldDataTypeWidth = fontMetrics.stringWidth(((FIXField) abstractFIXElement).getType().toString());

		if (fontProperties.getFieldRequiredWidth() < fieldRequiredWidth)
			fontProperties.setFieldRequiredWidth(fieldRequiredWidth);

		if (fontProperties.getFieldDataTypeWidth() < fieldDataTypeWidth)
			fontProperties.setFieldDataTypeWidth(fieldDataTypeWidth);

		if (fontProperties.getFieldTypeWidth() < fieldTypeWidth)
			fontProperties.setFieldTypeWidth(fieldTypeWidth);

		if (fontProperties.getFieldNumberWidth() < fieldNumberWidth)
			fontProperties.setFieldNumberWidth(fieldNumberWidth);

		if (fontProperties.getFieldNameWidth() < fieldNameWidth)
			fontProperties.setFieldNameWidth(fieldNameWidth);

		for (final AbstractFIXElement abstractFIXElement2 : abstractFIXElement.getAbstractFIXElements()) {

			abstractFIXElement2.setFontProperties(fontProperties);
			initFontProperties(abstractFIXElement2, fontMetrics, fontMetrics2);
		}

	}

	/**
	 * Inits the font properties.
	 *
	 * @param fixMainMessagePanel the fix main message panel
	 */
	public void initFontProperties(final JPanel fixMainMessagePanel) {

		if (getFontProperties() == null) {

			setFontProperties(new FontProperties());
			getHeader().setFontProperties(getFontProperties());
			getTrailer().setFontProperties(getFontProperties());

			initFontProperties(this, fixMainMessagePanel.getFontMetrics(new Font("Dialog", Font.PLAIN, 12)),
					fixMainMessagePanel.getFontMetrics(new Font("Dialog", Font.BOLD, 12)));

			initFontProperties(header, fixMainMessagePanel.getFontMetrics(new Font("Dialog", Font.PLAIN, 12)),
					fixMainMessagePanel.getFontMetrics(new Font("Dialog", Font.BOLD, 12)));

			initFontProperties(trailer, fixMainMessagePanel.getFontMetrics(new Font("Dialog", Font.PLAIN, 12)),
					fixMainMessagePanel.getFontMetrics(new Font("Dialog", Font.BOLD, 12)));
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fixpusher.model.message.AbstractFIXElement#toString()
	 */
	@Override
	public String toString() {

		final StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append(getName());
		stringBuffer.append(" (");
		stringBuffer.append(messageType);
		stringBuffer.append(")");

		return stringBuffer.toString();
	}

}
