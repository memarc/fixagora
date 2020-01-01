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
package net.sourceforge.fixagora.basis.client.view.dialog.fix;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import net.sourceforge.fixagora.basis.client.model.message.AbstractFIXElement;
import net.sourceforge.fixagora.basis.client.model.message.FIXComponent;
import net.sourceforge.fixagora.basis.client.model.message.FIXField;
import net.sourceforge.fixagora.basis.client.model.message.FIXGroup;
import net.sourceforge.fixagora.basis.client.model.message.FIXMessageFilter;
import net.sourceforge.fixagora.basis.client.model.message.AbstractFIXElement.FieldType;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXFieldMap;
import net.sourceforge.fixagora.basis.shared.model.persistence.SpreadSheetFIXGroup;

/**
 * The Class AbstractFIXPanel.
 */
public abstract class AbstractFIXPanel extends JPanel {

	private class Highlighter extends TimerTask {

		private int color = 250;

		private JComponent component = null;

		private int colorShift = -10;

		private int i = 0;

		private Font originalFont = null;

		private Timer timer = null;

		public Highlighter(final JComponent component, final Timer timer) {

			super();

			found = true;
			originalFont = component.getFont();

			this.component = component;
			this.component.setFont(new Font(originalFont.getName(), Font.BOLD, originalFont.getSize()));
			this.timer = timer;

		}

		@Override
		public void run() {

			if (color == 0)

				if (i == 10) {

					component.setFont(originalFont);
					found = false;
					timer.cancel();

					return;

				}
				else
					colorShift = 10;

			if (color == 250) {

				colorShift = -10;
				i++;
			}

			component.setForeground(new Color(color, 0, 0));
			color = color + colorShift;

		}

	}

	private static final long serialVersionUID = 1L;

	/** The abstract fix element. */
	protected AbstractFIXElement abstractFIXElement = null;

	/** The add button. */
	protected JButton addButton = null;

	/** The child panel. */
	protected JPanel childPanel = null;

	/** The children. */
	protected List<AbstractFIXPanel> children = new ArrayList<AbstractFIXPanel>();

	/** The data type label. */
	protected JLabel dataTypeLabel = null;

	private final DecimalFormat decimalFormat = new DecimalFormat("####");

	/** The depth. */
	protected int depth = 0;

	/** The enabled. */
	protected boolean enabled = false;

	/** The field map. */
	protected SpreadSheetFIXFieldMap fieldMap = null;

	/** The fix message filter. */
	protected FIXMessageFilter fixMessageFilter = null;

	private boolean found = false;

	/** The gbc_add button. */
	protected GridBagConstraints gbc_addButton = null;

	/** The gbc_remove button. */
	protected GridBagConstraints gbc_removeButton = null;

	/** The last panel. */
	protected JPanel lastPanel = null;

	/** The left panel. */
	protected JPanel leftPanel = null;

	/** The name label. */
	protected JLabel nameLabel = null;

	/** The number label. */
	protected JLabel numberLabel = null;

	/** The parent. */
	protected AbstractFIXPanel parent = null;

	/** The remove button. */
	protected JButton removeButton = null;

	/** The required label. */
	protected JLabel requiredLabel = null;

	/** The top panel. */
	protected JPanel topPanel = null;

	/** The value component. */
	protected JComponent valueComponent = null;

	/** The warning icon label. */
	protected JLabel warningIconLabel = null;

	/** The input. */
	protected boolean input = false;

	/** The iterate button. */
	protected JButton iterateButton = null;

	/** The gbc_iterate button. */
	protected GridBagConstraints gbc_iterateButton = null;

	/** The field check result. */
	protected FieldCheckResult fieldCheckResult = new FieldCheckResult(0, "Component is ok.", this);

	/**
	 * Instantiates a new abstract fix panel.
	 *
	 * @param parent the parent
	 * @param abstractFIXElement the abstract fix element
	 * @param fieldMap the field map
	 * @param enabled the enabled
	 * @param depth the depth
	 * @param fixMessageFilter the fix message filter
	 * @param input the input
	 */
	public AbstractFIXPanel(final AbstractFIXPanel parent, final AbstractFIXElement abstractFIXElement, final SpreadSheetFIXFieldMap fieldMap,
			final boolean enabled, final int depth, final FIXMessageFilter fixMessageFilter, boolean input) {

		super();

		this.input = input;
		this.parent = parent;
		this.depth = depth;
		this.fieldMap = fieldMap;
		this.enabled = enabled;
		this.fixMessageFilter = fixMessageFilter;
		this.abstractFIXElement = abstractFIXElement;

		setBorder(new MatteBorder(0, 0, 0, 0, new Color(0, 0, 0)));
		setLayout(new BorderLayout(0, 0));

		Color foreground = Color.BLACK;
		if (enabled)
			setBackground(getPanelBackground());
		else {

			setBackground(Color.LIGHT_GRAY);
			foreground = Color.GRAY;
		}

		topPanel = new JPanel();
		topPanel.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(0, 0, 0)), new EmptyBorder(5, 25 * depth, 5, 10)));
		topPanel.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(0, 0, 0)), new EmptyBorder(5, 25 * depth, 5, 10)));
		topPanel.setOpaque(false);
		final GridBagLayout gbl_topPanel = new GridBagLayout();
		gbl_topPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
		gbl_topPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		topPanel.setLayout(gbl_topPanel);
		add(topPanel, BorderLayout.NORTH);

		requiredLabel = new JLabel();
		requiredLabel.setVerticalTextPosition(SwingConstants.CENTER);
		requiredLabel.setVerticalAlignment(SwingConstants.CENTER);
		requiredLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		requiredLabel.setHorizontalAlignment(SwingConstants.LEFT);
		requiredLabel.setPreferredSize(new Dimension(abstractFIXElement.getFontProperties().getFieldRequiredWidth() + 10, 25));
		requiredLabel.setMinimumSize(new Dimension(abstractFIXElement.getFontProperties().getFieldRequiredWidth() + 10, 25));
		requiredLabel.setMaximumSize(new Dimension(abstractFIXElement.getFontProperties().getFieldRequiredWidth() + 10, 25));
		requiredLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		requiredLabel.setForeground(foreground);
		final GridBagConstraints gbc_requiredLabel = new GridBagConstraints();
		gbc_requiredLabel.insets = new Insets(0, 0, 0, 5);
		gbc_requiredLabel.anchor = GridBagConstraints.WEST;
		gbc_requiredLabel.gridx = 1;
		gbc_requiredLabel.gridy = 0;
		topPanel.add(requiredLabel, gbc_requiredLabel);

		switch (abstractFIXElement.getFieldType()) {

			case CONDITIONALLY:
				requiredLabel.setText("Conditionally");
				break;

			case OPTIONAL:
				requiredLabel.setText("Optional");
				break;

			case REQUIRED:
				requiredLabel.setText("Required");
				break;
		}

		dataTypeLabel = new JLabel();
		dataTypeLabel.setMinimumSize(new Dimension(abstractFIXElement.getFontProperties().getFieldDataTypeWidth() + 10, 25));
		dataTypeLabel.setPreferredSize(new Dimension(abstractFIXElement.getFontProperties().getFieldDataTypeWidth() + 10, 25));
		dataTypeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		dataTypeLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		dataTypeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		dataTypeLabel.setForeground(foreground);
		final GridBagConstraints gbc_dataTypeLabel = new GridBagConstraints();
		gbc_dataTypeLabel.anchor = GridBagConstraints.WEST;
		gbc_dataTypeLabel.insets = new Insets(0, 0, 0, 5);
		gbc_dataTypeLabel.gridx = 2;
		gbc_dataTypeLabel.gridy = 0;
		topPanel.add(dataTypeLabel, gbc_dataTypeLabel);

		final JLabel typeLabel = new JLabel(this.toString());
		typeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		typeLabel.setPreferredSize(new Dimension(abstractFIXElement.getFontProperties().getFieldTypeWidth() + 10, 25));
		typeLabel.setMinimumSize(new Dimension(abstractFIXElement.getFontProperties().getFieldTypeWidth() + 10, 25));
		typeLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		typeLabel.setForeground(foreground);
		final GridBagConstraints gbc_typeLabel = new GridBagConstraints();
		gbc_typeLabel.anchor = GridBagConstraints.WEST;
		gbc_typeLabel.insets = new Insets(0, 0, 0, 5);
		gbc_typeLabel.gridx = 3;
		gbc_typeLabel.gridy = 0;
		topPanel.add(typeLabel, gbc_typeLabel);

		numberLabel = new JLabel(Integer.toString(abstractFIXElement.getNumber()));
		numberLabel.setMinimumSize(new Dimension(abstractFIXElement.getFontProperties().getFieldNumberWidth() + 10, 25));
		numberLabel.setPreferredSize(new Dimension(abstractFIXElement.getFontProperties().getFieldNumberWidth() + 10, 25));
		numberLabel.setHorizontalAlignment(SwingConstants.LEFT);
		numberLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		numberLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		numberLabel.setForeground(foreground);
		final GridBagConstraints gbc_numberLabel = new GridBagConstraints();
		gbc_numberLabel.anchor = GridBagConstraints.WEST;
		gbc_numberLabel.insets = new Insets(0, 0, 0, 5);
		gbc_numberLabel.gridx = 4;
		gbc_numberLabel.gridy = 0;
		topPanel.add(numberLabel, gbc_numberLabel);

		warningIconLabel = new JLabel("");
		warningIconLabel.setPreferredSize(new Dimension(21, 16));
		final GridBagConstraints gbc_warningIconLabel = new GridBagConstraints();
		gbc_warningIconLabel.insets = new Insets(0, 0, 0, 5);
		gbc_warningIconLabel.gridx = 5;
		gbc_warningIconLabel.gridy = 0;
		topPanel.add(warningIconLabel, gbc_warningIconLabel);

		nameLabel = new JLabel(abstractFIXElement.getName());
		nameLabel.setMaximumSize(new Dimension(abstractFIXElement.getFontProperties().getFieldNameWidth() + 10, 25));
		nameLabel.setVerticalTextPosition(SwingConstants.CENTER);
		nameLabel.setVerticalAlignment(SwingConstants.CENTER);
		nameLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		nameLabel.setPreferredSize(new Dimension(new Dimension(abstractFIXElement.getFontProperties().getFieldNameWidth() + 10, 25)));
		nameLabel.setMinimumSize(new Dimension(new Dimension(abstractFIXElement.getFontProperties().getFieldNameWidth() + 10, 25)));
		nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		nameLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		nameLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		nameLabel.setForeground(foreground);
		final GridBagConstraints gbc_name = new GridBagConstraints();
		gbc_name.anchor = GridBagConstraints.WEST;
		gbc_name.insets = new Insets(0, 0, 0, 5);
		gbc_name.gridx = 6;
		gbc_name.gridy = 0;
		topPanel.add(nameLabel, gbc_name);

		lastPanel = new JPanel();
		final GridBagConstraints gbc_lastPanel = new GridBagConstraints();
		gbc_lastPanel.anchor = GridBagConstraints.WEST;
		gbc_lastPanel.gridx = 7;
		gbc_lastPanel.gridy = 0;
		final GridBagLayout gbl_lastPanel = new GridBagLayout();
		gbl_lastPanel.columnWeights = new double[] { 0.0, 1.0 };
		lastPanel.setLayout(gbl_lastPanel);
		topPanel.add(lastPanel, gbc_lastPanel);

		if (enabled)
			lastPanel.setBackground(getPanelBackground());

		else
			lastPanel.setBackground(Color.LIGHT_GRAY);

		iterateButton = new JButton("Iterate");
		iterateButton.setFocusable(false);
		iterateButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		iterateButton.setEnabled(false);
		iterateButton.setMinimumSize(new Dimension(100, 25));
		iterateButton.setMaximumSize(new Dimension(100, 25));
		iterateButton.setPreferredSize(new Dimension(100, 25));
		iterateButton.setIcon(new ImageIcon(AbstractFIXPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/arrow-refresh.png")));
		gbc_iterateButton = new GridBagConstraints();
		gbc_iterateButton.insets = new Insets(0, 0, 0, 10);
		gbc_iterateButton.gridx = 0;
		gbc_iterateButton.gridy = 0;

		addButton = new JButton("Add");
		addButton.setFocusable(false);
		addButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		addButton.setEnabled(false);
		addButton.setMinimumSize(new Dimension(100, 25));
		addButton.setMaximumSize(new Dimension(100, 25));
		addButton.setPreferredSize(new Dimension(100, 25));
		addButton.setIcon(new ImageIcon(AbstractFIXPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-add.png")));
		gbc_addButton = new GridBagConstraints();
		gbc_addButton.insets = new Insets(0, 0, 0, 10);
		gbc_addButton.gridx = 1;
		gbc_addButton.gridy = 0;

		removeButton = new JButton("Remove");
		removeButton.setFocusable(false);
		removeButton.setEnabled(false);
		removeButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		removeButton.setMinimumSize(new Dimension(100, 25));
		removeButton.setMaximumSize(new Dimension(100, 25));
		removeButton.setPreferredSize(new Dimension(100, 25));
		removeButton.setIcon(new ImageIcon(AbstractFIXPanel.class.getResource("/net/sourceforge/fixagora/basis/client/view/images/16x16/edit-delete.png")));
		gbc_removeButton = new GridBagConstraints();
		gbc_removeButton.anchor = GridBagConstraints.WEST;
		gbc_removeButton.gridx = 2;
		gbc_removeButton.gridy = 0;

		childPanel = new JPanel();
		childPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(0, 0, 0)));
		childPanel.setOpaque(false);
		final GridBagLayout gbl_childPanel = new GridBagLayout();
		gbl_childPanel.columnWeights = new double[] { 1.0 };
		childPanel.setLayout(gbl_childPanel);

		leftPanel = new JPanel();
		leftPanel.setBorder(new MatteBorder(0, 0, 1, 1, new Color(0, 0, 0)));
		leftPanel.setOpaque(false);
		leftPanel.setMinimumSize(new Dimension(25, 10));
		leftPanel.setPreferredSize(new Dimension(25, 10));
	}
	
	/**
	 * Browse field check result.
	 *
	 * @param fieldCheckResult2 the field check result2
	 * @return the abstract fix panel
	 */
	public AbstractFIXPanel browseFieldCheckResult(final FieldCheckResult fieldCheckResult2) {

		for (final AbstractFIXPanel abstractFIXPanel : children) {

			final AbstractFIXPanel abstractFIXPanel2 = abstractFIXPanel.browseFieldCheckResult(fieldCheckResult2);

			if (abstractFIXPanel2 != null)
				return abstractFIXPanel2;
		}

		if (fieldCheckResult2.equals(fieldCheckResult))
			return this;

		return null;

	}


	/**
	 * Field value changed.
	 */
	public void fieldValueChanged() {

		if (input)
			inputCheck();
		else
			fieldValueCheck();
		parent.fieldValueChanged();
	}

	/**
	 * Input check.
	 */
	protected void inputCheck() {

		if (!enabled)
			return;
		fieldCheckResult = checkInputFieldMap(abstractFIXElement, fieldMap);
	}

	/**
	 * Field value check.
	 */
	protected void fieldValueCheck() {

		if (fieldMap.isHeaderOrTrailer()) {

			parent.fieldValueChanged();
			return;
		}

		if (!enabled)
			return;

		fieldCheckResult = checkFieldMap(abstractFIXElement, fieldMap);
		warningIconLabel.setIcon(fieldCheckResult.getImageIcon());
		warningIconLabel.setToolTipText(fieldCheckResult.getToolTipText());
	}

	/**
	 * Check input field map.
	 *
	 * @param abstractFIXElement the abstract fix element
	 * @param fieldMap the field map
	 * @return the field check result
	 */
	protected FieldCheckResult checkInputFieldMap(final AbstractFIXElement abstractFIXElement, final SpreadSheetFIXFieldMap fieldMap) {
		
		String text = " trigger ";
		if(input)
			text = " key ";

		if (abstractFIXElement instanceof FIXField) {

			if (!fieldMap.isSetField(abstractFIXElement.getNumber()))
				return new FieldCheckResult(2, "No"+text+"is assigned to a column", this);
			
			if(!fieldMap.getKeyValue(abstractFIXElement.getNumber()))
				return new FieldCheckResult(2, "No"+text+"is assigned to a column", this);

			return new FieldCheckResult(0, "Field " + abstractFIXElement.getName() + " is ok.", this);

		}
		else if (abstractFIXElement instanceof FIXComponent) {

			final FIXComponent fixComponent = (FIXComponent) abstractFIXElement;

			for (final AbstractFIXElement abstractFIXElement2 : fixComponent.getAbstractFIXElements()) {

				final FieldCheckResult j = checkInputFieldMap(abstractFIXElement2, fieldMap);

				if (j.getBugLevel() == 0)
					return j;
			}

			return new FieldCheckResult(2, "No"+text+"is assigned to a column", this);
		}
		else if (abstractFIXElement instanceof FIXGroup) {

			final FIXGroup fixGroup = (FIXGroup) abstractFIXElement;
			for (final SpreadSheetFIXGroup group : fieldMap.getGroups(fixGroup.getNumber()))
				for (final AbstractFIXElement abstractFIXElement2 : fixGroup.getAbstractFIXElements()) {

					final FieldCheckResult j = checkInputFieldMap(abstractFIXElement2, group);

					if (j.getBugLevel() == 0)
						return j;
				}

			return new FieldCheckResult(2, "No"+text+"is assigned to a column", this);

		}
		else
			return new FieldCheckResult(0, "Group " + abstractFIXElement.getName() + " is ok.", this);
	}

	/**
	 * Check field map.
	 *
	 * @param abstractFIXElement the abstract fix element
	 * @param fieldMap the field map
	 * @return the field check result
	 */
	protected FieldCheckResult checkFieldMap(final AbstractFIXElement abstractFIXElement, final SpreadSheetFIXFieldMap fieldMap) {

		if (abstractFIXElement instanceof FIXField) {

			if (abstractFIXElement.getFieldType() == FieldType.REQUIRED && !fieldMap.isSetField(abstractFIXElement.getNumber()))
				return new FieldCheckResult(2, "Required field " + abstractFIXElement.getName() + " is not initialized.", this);

			else if (abstractFIXElement.getFieldType() == FieldType.CONDITIONALLY && !fieldMap.isSetField(abstractFIXElement.getNumber()))
				return new FieldCheckResult(2, "Conditionally field " + abstractFIXElement.getName() + " is not initialized.", this);

			return new FieldCheckResult(0, "Required field " + abstractFIXElement.getName() + " is ok.", this);

		}
		else if (abstractFIXElement instanceof FIXComponent) {

			if (abstractFIXElement.getNumber() < 0)
				return new FieldCheckResult(0, "Header " + abstractFIXElement.getName() + " is ok.", this);

			final FIXComponent fixComponent = (FIXComponent) abstractFIXElement;
			FieldCheckResult i = new FieldCheckResult(0, "Component " + abstractFIXElement.getName() + " is ok.", this);

			final boolean atLeastOneSet = fixComponent.atLeastOneSet(fieldMap);

			if (fixComponent.getFieldType() != FieldType.OPTIONAL)
				if (!atLeastOneSet)
					i = new FieldCheckResult(1, "Component " + abstractFIXElement.getName() + " is empty.", this);

			if (fixComponent.getFieldType() != FieldType.OPTIONAL || atLeastOneSet)
				for (final AbstractFIXElement abstractFIXElement2 : fixComponent.getAbstractFIXElements()) {

					final FieldCheckResult j = checkFieldMap(abstractFIXElement2, fieldMap);

					if (j.getBugLevel() > i.getBugLevel())
						i = j;
				}

			return i;

		}
		else if (abstractFIXElement instanceof FIXGroup) {

			final FIXGroup fixGroup = (FIXGroup) abstractFIXElement;

			if (fixGroup.getFieldType() == FieldType.REQUIRED
					&& (fieldMap.getGroupCount(fixGroup.getNumber()) == 0 || !fieldMap.hasGroup(fixGroup.getNumber())))
				return new FieldCheckResult(2, "Required group " + abstractFIXElement.getName() + " has no entries.", this);

			else if (fixGroup.getFieldType() == FieldType.CONDITIONALLY
					&& (fieldMap.getGroupCount(fixGroup.getNumber()) == 0 || !fieldMap.hasGroup(fixGroup.getNumber())))
				return new FieldCheckResult(2, "Required group " + abstractFIXElement.getName() + " has no entries.", this);

			FieldCheckResult i = new FieldCheckResult(0, "Group " + abstractFIXElement.getName() + " is ok.", this);

			for (final SpreadSheetFIXGroup group : fieldMap.getGroups(fixGroup.getNumber())) {

				final boolean atLeastOneSet = fixGroup.atLeastOneSet(group);

				if (!atLeastOneSet)
					i = new FieldCheckResult(1, "Group " + abstractFIXElement.getName() + " is empty.", this);

				for (final AbstractFIXElement abstractFIXElement2 : fixGroup.getAbstractFIXElements()) {

					final FieldCheckResult j = checkFieldMap(abstractFIXElement2, group);

					if (j.getBugLevel() > i.getBugLevel())
						i = j;
				}
			}

			return i;

		}
		else
			return new FieldCheckResult(0, "Group " + abstractFIXElement.getName() + " is ok.", this);
	}

	/**
	 * Find.
	 *
	 * @param text the text
	 * @param lastHit the last hit
	 * @return the abstract fix panel
	 */
	public AbstractFIXPanel find(final String text, final LastHit lastHit) {

		if (lastHit.getAbstractFIXPanel() == null && abstractFIXElement.getName().toUpperCase().contains(text.toUpperCase())) {

			if (!found) {

				final Timer timer = new Timer();
				timer.schedule(new Highlighter(nameLabel, timer), 10, 10);
			}

			return this;
		}
		if (lastHit.getAbstractFIXPanel() == null && text.trim().equals(decimalFormat.format(abstractFIXElement.getNumber()))) {

			if (!found) {

				final Timer timer = new Timer();
				timer.schedule(new Highlighter(numberLabel, timer), 10, 10);
			}

			return this;
		}

		if (lastHit.getAbstractFIXPanel() == this)
			lastHit.setAbstractFIXPanel(null);

		for (final AbstractFIXPanel abstractFIXPanel : children) {

			final AbstractFIXPanel abstractFIXPanel2 = abstractFIXPanel.find(text, lastHit);
			if (abstractFIXPanel2 != null)
				return abstractFIXPanel2;
		}
		return null;

	}

	/**
	 * Gets the panel background.
	 *
	 * @return the panel background
	 */
	protected abstract Color getPanelBackground();

	/**
	 * Dispose.
	 */
	public void dispose() {

		for (AbstractFIXPanel abstractFIXPanel : children) {
			if (abstractFIXPanel.getParent() != null)
				abstractFIXPanel.getParent().remove(abstractFIXPanel);
			abstractFIXPanel.dispose();
		}
		children.clear();
	}

}
