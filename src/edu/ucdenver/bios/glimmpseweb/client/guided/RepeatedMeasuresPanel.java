/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.ucdenver.bios.glimmpseweb.client.guided;

/**
 * Repeated measures panel - for v2.0.x
 */
import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.XMLUtilities;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;


public class RepeatedMeasuresPanel extends WizardStepPanel
implements ClickHandler, ChangeHandler
{
	private static final String NON_REPEATED = "0";
	private static final String SINGLY_REPEATED = "1";
	private static final String DOUBLY_REPEATED = "2";
	private static final String REPEATED_MEAUSRES_RADIO_GROUP = "repeatedMeasuresGroup";
	protected RadioButton singleMeasureRadioButton = 
		new RadioButton(REPEATED_MEAUSRES_RADIO_GROUP, GlimmpseWeb.constants.singleMeasureLabel());
	protected RadioButton repeatedMeasures1DRadioButton = 
		new RadioButton(REPEATED_MEAUSRES_RADIO_GROUP, GlimmpseWeb.constants.repeatedMeasures1DLabel());
	protected RadioButton repeatedMeasures2DRadioButton = 
		new RadioButton(REPEATED_MEAUSRES_RADIO_GROUP, GlimmpseWeb.constants.repeatedMeasures2DLabel());
	
	// text boxes for each dimension
	protected TextBox repetitions1DTextBox = new TextBox();
	protected TextBox units1DTextBox = new TextBox();
	protected TextBox repetitions2DOuterTextBox = new TextBox();
	protected TextBox units2DOuterTextBox = new TextBox();
	protected TextBox repetitions2DInnerTextBox = new TextBox();
	protected TextBox units2DInnerTextBox = new TextBox();

    protected HTML errorHTML = new HTML();

	public RepeatedMeasuresPanel(WizardContext context)
	{
		super(context, "Repeated Measures");
		skip = true; // TODO: remove once we get repeated measures going.
    	VerticalPanel panel = new VerticalPanel();
    	
        // create the repeated measures header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.repeatedMeasuresTitle());
        HTML description = new HTML(GlimmpseWeb.constants.repeatedMeasuresDescription());
                
        panel.add(header);
        panel.add(description);
        panel.add(singleMeasureRadioButton);
        panel.add(repeatedMeasures1DRadioButton);
        panel.add(createSinglyRepeatedMeasuresPanel());
        panel.add(repeatedMeasures2DRadioButton);
        panel.add(createDoublyRepeatedMeasuresPanel());

        // add callbacks
        singleMeasureRadioButton.addClickHandler(this);
        repeatedMeasures1DRadioButton.addClickHandler(this);
        repeatedMeasures2DRadioButton.addClickHandler(this);
        
        repetitions1DTextBox.addChangeHandler(this);
        units1DTextBox.addChangeHandler(this);
        repetitions2DOuterTextBox.addChangeHandler(this);
        units2DOuterTextBox.addChangeHandler(this);
        repetitions2DInnerTextBox.addChangeHandler(this);
        units2DInnerTextBox.addChangeHandler(this);
        
        // select non-repeated measures by default
        reset();
        
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        initWidget(panel);
	}

	private VerticalPanel createSinglyRepeatedMeasuresPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		
		Grid grid = new Grid(2,2);
		grid.setWidget(0, 0, new HTML(GlimmpseWeb.constants.repeatedMeasuresRepeatsLabel()));
		grid.setWidget(0, 1, repetitions1DTextBox);
		grid.setWidget(1, 0, new HTML(GlimmpseWeb.constants.repeatedMeasuresUnitsLabel()));
		grid.setWidget(1, 1, units1DTextBox);
		panel.add(grid);
		
		return panel;
	}
	
	private VerticalPanel createDoublyRepeatedMeasuresPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		
		Grid grid = new Grid(3,4);
		grid.setWidget(0, 0, new HTML("1st Dimension"));
		grid.setWidget(1, 0, new HTML(GlimmpseWeb.constants.repeatedMeasuresRepeatsLabel()));
		grid.setWidget(1, 1, repetitions2DOuterTextBox);
		grid.setWidget(2, 0, new HTML(GlimmpseWeb.constants.repeatedMeasuresUnitsLabel()));
		grid.setWidget(2, 1, units2DOuterTextBox);
		
		grid.setWidget(0, 2, new HTML("2nd Dimension"));
		grid.setWidget(1, 2, new HTML(GlimmpseWeb.constants.repeatedMeasuresRepeatsLabel()));
		grid.setWidget(1, 3, repetitions2DInnerTextBox);
		grid.setWidget(2, 2, new HTML(GlimmpseWeb.constants.repeatedMeasuresUnitsLabel()));
		grid.setWidget(2, 3, units2DInnerTextBox);
		
		panel.add(grid);
		
		return panel;
	}

	
    private void notifyRepeatedMeasures()
    {
    	ArrayList<RepeatedMeasure> rmList = new ArrayList<RepeatedMeasure>();
    	
    	if (repeatedMeasures1DRadioButton.getValue())
    	{
    		rmList.add(new RepeatedMeasure(units1DTextBox.getText(), 
    				Integer.parseInt(repetitions1DTextBox.getText())));
    	}
    	else if (repeatedMeasures2DRadioButton.getValue())
    	{
    		rmList.add(new RepeatedMeasure(units2DOuterTextBox.getText(), 
    				Integer.parseInt(repetitions2DOuterTextBox.getText())));
    		rmList.add(new RepeatedMeasure(units2DInnerTextBox.getText(), 
    				Integer.parseInt(repetitions2DInnerTextBox.getText())));
    	}

    }
    
    @Override
    public void onExit()
    {
    	notifyRepeatedMeasures();
    }
    
	@Override
	public void reset()
	{
		singleMeasureRadioButton.setValue(true);
		repetitions1DTextBox.setEnabled(false);
		units1DTextBox.setEnabled(false);
		repetitions2DOuterTextBox.setEnabled(false);
		units2DOuterTextBox.setEnabled(false);
		repetitions2DInnerTextBox.setEnabled(false);
		units2DInnerTextBox.setEnabled(false);
		checkComplete();
		skip = true;
	}

//	@Override
//	public void loadFromNode(Node node)
//	{
//		if (GlimmpseConstants.TAG_REPEATED_MEASURES.equalsIgnoreCase(node.getNodeName()))
//		{
//			NamedNodeMap attrs = node.getAttributes();
//			Node typeNode = attrs.getNamedItem(GlimmpseConstants.ATTR_TYPE);
//			if (typeNode != null)
//			{
//				if (NON_REPEATED.equals(typeNode.getNodeValue()))
//				{
//					singleMeasureRadioButton.setValue(true);
//				}
//				else if (SINGLY_REPEATED.equals(typeNode.getNodeValue()))
//				{
//					repeatedMeasures1DRadioButton.setValue(true);
//					repetitions1DTextBox.setEnabled(true);
//					units1DTextBox.setEnabled(true);
//					Node dimensionNode = node.getFirstChild();
//					if (dimensionNode != null)
//					{
//						NamedNodeMap dimAttrs = dimensionNode.getAttributes();
//						Node timesNode = dimAttrs.getNamedItem(GlimmpseConstants.ATTR_TIMES);
//						if (timesNode != null)
//						{
//							repetitions1DTextBox.setText(timesNode.getNodeValue());
//						}
//						Node unitsNode = dimensionNode.getFirstChild();
//						if (unitsNode != null)
//						{
//							units1DTextBox.setText(unitsNode.getNodeValue());
//						}
//					}
//				}
//				else if (DOUBLY_REPEATED.equals(typeNode.getNodeValue()))
//				{
//					repeatedMeasures2DRadioButton.setValue(true);
//					repetitions2DOuterTextBox.setEnabled(true);
//					units2DOuterTextBox.setEnabled(true);
//					repetitions2DInnerTextBox.setEnabled(true);
//					units2DInnerTextBox.setEnabled(true);
//					NodeList children = node.getChildNodes();
//					Node outerDimensionNode = children.item(0);
//					if (outerDimensionNode != null)
//					{
//						NamedNodeMap dimAttrs = outerDimensionNode.getAttributes();
//						Node timesNode = dimAttrs.getNamedItem(GlimmpseConstants.ATTR_TIMES);
//						if (timesNode != null)
//						{
//							repetitions2DOuterTextBox.setText(timesNode.getNodeValue());
//						}
//						Node unitsNode = outerDimensionNode.getFirstChild();
//						if (unitsNode != null)
//						{
//							units2DOuterTextBox.setText(unitsNode.getNodeValue());
//						}
//					}
//					Node innerDimensionNode = children.item(1);
//					if (innerDimensionNode != null)
//					{
//						NamedNodeMap dimAttrs = innerDimensionNode.getAttributes();
//						Node timesNode = dimAttrs.getNamedItem(GlimmpseConstants.ATTR_TIMES);
//						if (timesNode != null)
//						{
//							repetitions2DInnerTextBox.setText(timesNode.getNodeValue());
//						}
//						Node unitsNode = innerDimensionNode.getFirstChild();
//						if (unitsNode != null)
//						{
//							units2DInnerTextBox.setText(unitsNode.getNodeValue());
//						}
//					}
//				}
//			}
//		}
//		checkComplete();
//	}

	@Override
	public void onClick(ClickEvent event)
	{
		repetitions1DTextBox.setEnabled(repeatedMeasures1DRadioButton.getValue());
		units1DTextBox.setEnabled(repeatedMeasures1DRadioButton.getValue());
		repetitions2DOuterTextBox.setEnabled(repeatedMeasures2DRadioButton.getValue());
		units2DOuterTextBox.setEnabled(repeatedMeasures2DRadioButton.getValue());
		repetitions2DInnerTextBox.setEnabled(repeatedMeasures2DRadioButton.getValue());
		units2DInnerTextBox.setEnabled(repeatedMeasures2DRadioButton.getValue());
		checkComplete();
	}
	
	private void checkComplete()
	{
		if (singleMeasureRadioButton.getValue())
		{
			notifyComplete();
		}
		else if (repeatedMeasures1DRadioButton.getValue())
		{		
			if (!repetitions1DTextBox.getText().isEmpty() && 
					!units1DTextBox.getText().isEmpty())
			{
				notifyComplete();
			}
			else
			{
				notifyInProgress();
			}
		}
		else if (repeatedMeasures2DRadioButton.getValue())
		{
			if (!repetitions2DOuterTextBox.getText().isEmpty() && 
					!units2DOuterTextBox.getText().isEmpty() &&
					!repetitions2DInnerTextBox.getText().isEmpty() &&
					!units2DInnerTextBox.getText().isEmpty())
			{
				notifyComplete();
			}
			else
			{
				notifyInProgress();
			}
		}
	}
	
	public String toStudyXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (singleMeasureRadioButton.getValue())
		{
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_REPEATED_MEASURES,
					GlimmpseConstants.ATTR_TYPE + "='0'");
		}
		else if (repeatedMeasures1DRadioButton.getValue())
		{
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_REPEATED_MEASURES,
					GlimmpseConstants.ATTR_TYPE + "='1'");
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_DIMENSION, 
					GlimmpseConstants.ATTR_TIMES + "='" + repetitions1DTextBox.getText() + "'");
			buffer.append(units1DTextBox.getText());
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_DIMENSION);
			
		}
		else if (repeatedMeasures2DRadioButton.getValue())
		{
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_REPEATED_MEASURES,
					GlimmpseConstants.ATTR_TYPE + "='2'");
			
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_DIMENSION, 
					GlimmpseConstants.ATTR_TIMES + "='" + repetitions2DOuterTextBox.getText() + "'");
			buffer.append(units2DOuterTextBox.getText());
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_DIMENSION);
			
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_DIMENSION, 
					GlimmpseConstants.ATTR_TIMES + "='" + repetitions2DInnerTextBox.getText() + "'");
			buffer.append(units2DInnerTextBox.getText());
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_DIMENSION);
		}
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_REPEATED_MEASURES);
		
		return buffer.toString();
	}

	@Override
	public void onChange(ChangeEvent event)
	{
		checkComplete();
	}

}
