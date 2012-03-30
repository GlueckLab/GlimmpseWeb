package edu.ucdenver.bios.glimmpseweb.client.proxy;

import java.util.ArrayList;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Post;
import org.restlet.client.resource.Result;

import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.PowerResult;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;

public interface PowerResourceProxy extends ClientProxy {
    /**
     * Calculate power for the specified study design.
     *
     * @param studyDesign study design object
     * @return List of power objects for the study design
     */
    @Post
    public void getPower(StudyDesign studyDesign, 
            Result<ArrayList<PowerResult>> callback);

    /**
     * Calculate the total sample size for the specified study design.
     *
     * @param studyDesign study design object
     * @return List of power objects for the study design.  These will contain
     * the total sample size
     */
    @Post
    public void getSampleSize(StudyDesign studyDesign,
            Result<ArrayList<PowerResult>> callback);

    /**
     * Calculate the detectable difference for the specified study design.
     *
     * @param studyDesign study design object
     * @return List of power objects for the study design.  These will contain
     * the detectable difference.
     */
    @Post
    public void getDetectableDifference(StudyDesign studyDesign,
            Result<ArrayList<PowerResult>> callback);

    /**
     * Get matrices used in the power calculation for a "guided" study design
     */
    @Post
    public void getMatrices(StudyDesign studyDesign,
            Result<ArrayList<NamedMatrix>> callback);
}
