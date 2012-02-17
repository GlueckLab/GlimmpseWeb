package edu.ucdenver.bios.glimmpseweb.client.guided;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CovarianceCorrelationDeckPanel extends Composite
{

	public CovarianceCorrelationDeckPanel(int i)
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		
		DeckPanel deckPanel = new DeckPanel();
		
		StructuredCorrelationPanel structuredCovariancePanelInstance = new StructuredCorrelationPanel();
		UnStructuredCovariancePanel unstructuredCovariancePanelInstance = new UnStructuredCovariancePanel();
		UnstructuredCorrelationPanel unstructuredCorrelationPanelInstance = new UnstructuredCorrelationPanel();
		
		deckPanel.add(structuredCovariancePanelInstance);
		deckPanel.add(unstructuredCovariancePanelInstance);
		deckPanel.add(unstructuredCorrelationPanelInstance);
		
		deckPanel.showWidget(i);
		
		verticalPanel.add(deckPanel);
		initWidget(verticalPanel);
		
	}
}
