package edu.ucdenver.bios.glimmpseweb.client.connector;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;

/**
 * Connector to file service.  Note that this class must extend composite
 * so we have a form
 * 
 * @author Sarah Kreidler
 *
 */
public class FileSvcConnector extends Composite {
    
    // default filename for save
    protected static final String STUDY_FILENAME = "study.json";
    // default filename for save
    protected static final String DATA_FILENAME = "powerResults.csv";
    // url for file save web service
    protected static final String SAVEAS_URL = GlimmpseWeb.constants.fileSvcHostSaveAs(); 
    // form for saving the study design
    protected FormPanel saveForm = new FormPanel("_blank");
    protected Hidden dataHidden = new Hidden("data");
    protected Hidden filenameHidden = new Hidden("filename");

    // JSON encoder/decoder class
    private static final DomainObjectSerializer serializer = 
        DomainObjectSerializer.getInstance();
    
    public FileSvcConnector() {
        VerticalPanel panel = new VerticalPanel();
        
        // set up the form for saving study designs
        VerticalPanel saveFormContainer = new VerticalPanel();
        saveFormContainer.add(dataHidden);
        saveFormContainer.add(filenameHidden);
        saveForm.setAction(SAVEAS_URL);
        saveForm.setMethod(FormPanel.METHOD_POST);
        saveForm.add(saveFormContainer);
        panel.add(saveForm);
        
        initWidget(panel);
    }
    
    /**
     * Send a save request to the File web service.  A service is used to force
     * the browser to pop-up the save as dialog box.
     * 
     * @param data the data to be saved
     * @param filename default filename to use
     */
    public void saveStudyDesign(StudyDesign studyDesign, String filename)
    {
        if (studyDesign != null) {
            String jsonEncoded = serializer.toJSON(studyDesign);
            dataHidden.setValue(jsonEncoded);
            String saveFilename = STUDY_FILENAME;
            if (filename != null && !filename.isEmpty()) {
                saveFilename = filename;
            }
            filenameHidden.setValue(saveFilename);
            saveForm.submit();
        }
    }
    
    /**
     * Send a save request to the File web service.  A service is used to force
     * the browser to pop-up the save as dialog box.
     * 
     * @param data the data to be saved
     * @param filename default filename to use
     */
    public void saveDataTableAsCSV(DataTable dataTable, String filename)
    {
        if (dataTable != null) {
            dataHidden.setValue(dataTableToCSV(dataTable));
            String saveFilename = DATA_FILENAME;
            if (filename != null && !filename.isEmpty()) {
                saveFilename = filename;
            }
            filenameHidden.setValue(saveFilename);
            saveForm.submit();
        }
    }
    
    /**
     * Output the data table in CSV format
     * @return CSV formatted data
     */
    private String dataTableToCSV(DataTable dataTable)
    {
        StringBuffer buffer = new StringBuffer();
        
        if (dataTable.getNumberOfRows() > 0)
        {
            // add the column headers
            for(int col = 0; col < dataTable.getNumberOfColumns(); col++)
            {
                if (col > 0) buffer.append(",");
                buffer.append(dataTable.getColumnId(col));
            }
            buffer.append("\n");
            // now add the data
            for(int row = 0; row < dataTable.getNumberOfRows(); row++)
            {
                for(int col = 0; col < dataTable.getNumberOfColumns(); col++)
                {
                    if (col > 0) buffer.append(",");
                    if (dataTable.getColumnType(col) == ColumnType.STRING)
                        buffer.append(dataTable.getValueString(row, col));
                    else    
                        buffer.append(dataTable.getValueDouble(row, col));    
                }
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }
    
    
}
