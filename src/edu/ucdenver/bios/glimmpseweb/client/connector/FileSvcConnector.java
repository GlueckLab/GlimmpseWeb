package edu.ucdenver.bios.glimmpseweb.client.connector;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;

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
        saveForm.reset();
    }
    
    /**
     * Send a save request to the File web service.  A service is used to force
     * the browser to pop-up the save as dialog box.
     * 
     * @param data the data to be saved
     * @param filename default filename to use
     */
    public void saveStringToFile(String str, String filename)
    {
        if (str != null && !str.isEmpty()) {
            dataHidden.setValue(str);
            String saveFilename = DATA_FILENAME;
            if (filename != null && !filename.isEmpty()) {
                saveFilename = filename;
            }
            filenameHidden.setValue(saveFilename);
            saveForm.submit();
        }
        saveForm.reset();
    }
     
}
