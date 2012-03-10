package edu.ucdenver.bios.glimmpseweb.client.guided;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ButtonWithExplainationPanel;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;

public class CovarianceCorrelationDeckPanel extends Composite
{
	protected DeckPanel deckPanel = new DeckPanel();
	protected VerticalPanel controlButtonPanel = new VerticalPanel();
	
	protected ButtonWithExplainationPanel useCustomVariability = new ButtonWithExplainationPanel(GlimmpseWeb.constants.useCustomVariablity(),
			GlimmpseWeb.constants.explainButtonText(), 
			GlimmpseWeb.constants.useCustomVariablityAlertHeader(), 
			GlimmpseWeb.constants.useCustomVariabilityAlertText());
	
	protected ButtonWithExplainationPanel useCustomCorrelation = new ButtonWithExplainationPanel(GlimmpseWeb.constants.useCustomCorrelation(),
			GlimmpseWeb.constants.explainButtonText(), 
			GlimmpseWeb.constants.useCustomCorrelationAlertHeader(), 
			GlimmpseWeb.constants.useCustomCorrelationAlertText());
	
	protected ButtonWithExplainationPanel uploadCorrelationMatrix = new ButtonWithExplainationPanel(GlimmpseWeb.constants.uploadCorrelationMatrix(),
			GlimmpseWeb.constants.explainButtonText(), 
			GlimmpseWeb.constants.correlationMatrixAlertHeader(), 
			GlimmpseWeb.constants.correlationMatrixAlertText());
	
	protected ButtonWithExplainationPanel useStructuredVariability = new ButtonWithExplainationPanel(GlimmpseWeb.constants.useStructuredVariability(),
			GlimmpseWeb.constants.explainButtonText(), 
			GlimmpseWeb.constants.useStructuredVariabilityAlertHeader(), 
			GlimmpseWeb.constants.useStructuredVariabilityAlertText());
	
	
	protected ButtonWithExplainationPanel uploadCovarianceMatrix = new ButtonWithExplainationPanel(GlimmpseWeb.constants.uploadCovarianceMatrix(),
			GlimmpseWeb.constants.explainButtonText(), 
			GlimmpseWeb.constants.covarinceMatrixAlertHeader(), 
			GlimmpseWeb.constants.covarinceMatrixAlertText());
	
	/*public CovarianceCorrelationDeckPanel(RepeatedMeasuresNode obj)
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		
		StructuredCorrelationPanel structuredCorrelationPanelInstance = new StructuredCorrelationPanel(labelList, spacingList);
		UnStructuredCovariancePanel unstructuredCorrelationPanelInstance = new UnStructuredCovariancePanel(obj);
		UnStructuredCorrelationPanel unstructuredCovariancePanelInstance = new UnStructuredCorrelationPanel(obj);
		
		deckPanel.add(structuredCorrelationPanelInstance);
		deckPanel.add(unstructuredCovariancePanelInstance);
		deckPanel.add(unstructuredCorrelationPanelInstance);
		
		
		deckPanel.showWidget(0);
		
		createControlButtonPanel();
		useCustomVariability.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				useCustomVariabilityEvent();				
			}
			
		});
			
		useCustomCorrelation.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				useCustomCorrelationEvent();
							
			}
			
		});
		
		uploadCorrelationMatrix.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				uploadCorrelationMatrixEvent();
 
				
			}
			
		});
		
		useStructuredVariability.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				useStructuredVariabilityEvent();
			}
			
		});
		
		uploadCovarianceMatrix.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				
				uploadCovarianceMatrixEvent();
			}
			
		});
		
		uploadCorrelationMatrix.setVisible(false);
		useStructuredVariability.setVisible(false);
		uploadCovarianceMatrix.setVisible(false);
		
		
		controlButtonPanel.add(useCustomVariability);
		controlButtonPanel.add(useCustomCorrelation);
		controlButtonPanel.add(uploadCorrelationMatrix);
		controlButtonPanel.add(useStructuredVariability);
		controlButtonPanel.add(uploadCovarianceMatrix);
		
		
		controlButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		controlButtonPanel.setWidth("10");
				
		verticalPanel.add(deckPanel);
		verticalPanel.add(controlButtonPanel);
		
		
		initWidget(verticalPanel);
	}
	
*/	public CovarianceCorrelationDeckPanel(RepeatedMeasuresNode repeatedMeasuresNode)
	{
		String dimension = repeatedMeasuresNode.getDimension();
//		List<Integer> spacingList = repeatedMeasuresNode.getSpacingList();
//		List<String> labelList = new ArrayList<String>(repeatedMeasuresNode.getNumberOfMeasurements());
//		int size = spacingList.size();
//		for(int i = 0; i < size; i++)
//		{
//			labelList.add(i, dimension+""+spacingList.get(i).toString());
//		}
//		buildDeckPanel(labelList, spacingList);
	}
	
	public CovarianceCorrelationDeckPanel(List<String> responseList)
	{
		List<String> labels = responseList;
		List<Integer> spacingList = new ArrayList<Integer>(labels.size());
		for(int i = 0; i < labels.size(); i++)
		{
			spacingList.add(new Integer(i));
		}
		buildDeckPanel(labels, spacingList);
	}
	
	
	
	private void buildDeckPanel(List<String> labelList, List<Integer>spacingList)
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		
		StructuredCorrelationPanel structuredCorrelationPanelInstance = 
				new StructuredCorrelationPanel(labelList, spacingList);
		UnStructuredCovariancePanel unstructuredCorrelationPanelInstance = 
				new UnStructuredCovariancePanel(labelList, spacingList);
		UnStructuredCorrelationPanel unstructuredCovariancePanelInstance = 
				new UnStructuredCorrelationPanel(labelList, spacingList);
		
		deckPanel.add(structuredCorrelationPanelInstance);
		deckPanel.add(unstructuredCovariancePanelInstance);
		deckPanel.add(unstructuredCorrelationPanelInstance);
		
		
		deckPanel.showWidget(0);
		
		useCustomVariability.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				useCustomVariabilityEvent();				
			}
			
		});
			
		useCustomCorrelation.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				useCustomCorrelationEvent();
							
			}
			
		});
		
		uploadCorrelationMatrix.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				uploadCorrelationMatrixEvent();
 
				
			}
			
		});
		
		useStructuredVariability.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				useStructuredVariabilityEvent();
			}
			
		});
		
		uploadCovarianceMatrix.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				
				uploadCovarianceMatrixEvent();
			}
			
		});
		
		uploadCorrelationMatrix.setVisible(false);
		useStructuredVariability.setVisible(false);
		uploadCovarianceMatrix.setVisible(false);
		
		
		controlButtonPanel.add(useCustomVariability);
		controlButtonPanel.add(useCustomCorrelation);
		controlButtonPanel.add(uploadCorrelationMatrix);
		controlButtonPanel.add(useStructuredVariability);
		controlButtonPanel.add(uploadCovarianceMatrix);
		
		
		controlButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		/*controlButtonPanel.setWidth("10");*/
				
		verticalPanel.add(deckPanel);
		verticalPanel.add(controlButtonPanel);
		
		
		initWidget(verticalPanel);

	}
	
	
	
	
	public VerticalPanel createControlButtonPanel()
	{
		
		
		return createControlButtonPanel();		
	}
	
	public void useCustomVariabilityEvent()
	{
		deckPanel.showWidget(2);
		useCustomVariability.setVisible(false);
		useCustomCorrelation.setVisible(true);
		uploadCorrelationMatrix.setVisible(false);
		useStructuredVariability.setVisible(true);
		uploadCovarianceMatrix.setVisible(true);
	}
	public void useCustomCorrelationEvent()
	{
		deckPanel.showWidget(1);
		useCustomVariability.setVisible(true);
		useCustomCorrelation.setVisible(false);
		uploadCorrelationMatrix.setVisible(true);
		useStructuredVariability.setVisible(true);
		uploadCovarianceMatrix.setVisible(false);
	}
	public void useStructuredVariabilityEvent()
	{
		deckPanel.showWidget(0);
		useCustomVariability.setVisible(true);
		useCustomCorrelation.setVisible(true);
		uploadCorrelationMatrix.setVisible(false);
		useStructuredVariability.setVisible(false);
		uploadCovarianceMatrix.setVisible(false);
	}
	
	public void uploadCorrelationMatrixEvent()
	{
		
	}
	public void uploadCovarianceMatrixEvent()
	{
		
	}
}

