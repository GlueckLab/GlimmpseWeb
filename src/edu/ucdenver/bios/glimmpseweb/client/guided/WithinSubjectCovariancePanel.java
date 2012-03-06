package edu.ucdenver.bios.glimmpseweb.client.guided;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ButtonWithExplainationPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;

public class WithinSubjectCovariancePanel extends WizardStepPanel
{
	// context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;

	TabLayoutPanel tabPanel = new TabLayoutPanel(2.5,Unit.EM);
	
	VerticalPanel verticalPanel = new VerticalPanel();
	

	WithinSubjectCovariancePanel(WizardContext context) 
	{
		super(context, "Within Subject Covariance");
		
		/******REMOVE THIS LATER*/
		ArrayList<RepeatedMeasuresNode> repeatedMeasuresNodeArrayList = new ArrayList<RepeatedMeasuresNode>();
		List<Integer> rootSpacingList = new ArrayList<Integer>();
		rootSpacingList.add(1);
		rootSpacingList.add(5);
		rootSpacingList.add(9);
		RepeatedMeasuresNode root = new RepeatedMeasuresNode();
        root.setDimension("Week");
        root.setNode(0);
        root.setNumberOfMeasurements(3);
        root.setParent(0);
        root.setType("Numeric");
        root.setSpacingList(rootSpacingList);
        repeatedMeasuresNodeArrayList.add(root);
        RepeatedMeasuresNode child = new RepeatedMeasuresNode();
        List<Integer> childSpacingList = new ArrayList<Integer>();
        childSpacingList.add(1);
        childSpacingList.add(2);
        childSpacingList.add(3);
        childSpacingList.add(4);
        childSpacingList.add(5);
        childSpacingList.add(6);
        childSpacingList.add(7);
        child.setSpacingList(childSpacingList);
        child.setDimension("Day");
		child.setType("Ordinal");
        child.setNode(1);
        child.setNumberOfMeasurements(7);
        child.setParent(0);
        repeatedMeasuresNodeArrayList.add(child);
        RepeatedMeasuresNode child1 = new RepeatedMeasuresNode();
        List<Integer> childSpacingList1 = new ArrayList<Integer>();
        childSpacingList1.add(1);
        childSpacingList1.add(5);
        childSpacingList1.add(9);
		child1.setDimension("Time");
		child1.setType("Numeric");
        child1.setNode(2);
        child1.setNumberOfMeasurements(3);
        child1.setParent(1);
        child1.setSpacingList(childSpacingList1);
        repeatedMeasuresNodeArrayList.add(child1);
        studyDesignContext.setRepeatedMeasuresNodes(this, repeatedMeasuresNodeArrayList);
        
        /*******END OF REMOVE THIS*/
        
        
        
		HTML header = new HTML();
		HTML instructions = new HTML();
		
		header.setText(GlimmpseWeb.constants.withinSubjectCovarianceHeader());
		instructions.setText(GlimmpseWeb.constants.withinSubjectCovarianceInstructions());
		
		constructTabPanel();
				
		
		ButtonWithExplainationPanel uploadFullCovarianceMatrixButton = new ButtonWithExplainationPanel(GlimmpseWeb.constants.uploadFullCovarianceMatrix(), 
				GlimmpseWeb.constants.explainButtonText(), 
				GlimmpseWeb.constants.fullCovarianceMatrixHeader(), 
				GlimmpseWeb.constants.fullCovarianceMatrixText());
		
		uploadFullCovarianceMatrixButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				
			}
			
		});
				
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		instructions.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		verticalPanel.add(header);
		verticalPanel.add(instructions);
		verticalPanel.add(tabPanel);
		verticalPanel.add(uploadFullCovarianceMatrixButton);
			
		initWidget(verticalPanel);
		
		
	}
	
	public TabLayoutPanel constructTabPanel()
	{
		
		List<RepeatedMeasuresNode> repeatedMeasuresNodeList = studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
		int size = repeatedMeasuresNodeList.size();
		for(int i = 0; i < size; i++)
		{
			
		RepeatedMeasuresNode obj = repeatedMeasuresNodeList.get(i);
		tabPanel.add(new CovarianceCorrelationDeckPanel(obj), obj.getDimension());
		if(i == size-1)
		{
			tabPanel.add(new CovarianceCorrelationDeckPanel(obj),"Responses");
		}
		}
		
		tabPanel.setHeight("525");
		tabPanel.setWidth("600");
		
		tabPanel.setAnimationDuration(700);
		return tabPanel;
	}
	

	@Override
	public void reset() 
	{
		
		
	}

}


