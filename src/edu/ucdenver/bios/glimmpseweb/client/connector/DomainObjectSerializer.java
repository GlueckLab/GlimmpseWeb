package edu.ucdenver.bios.glimmpseweb.client.connector;

import java.util.List;

import name.pehl.piriti.json.client.JsonReader;
import name.pehl.piriti.json.client.JsonWriter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;

import edu.ucdenver.bios.webservice.common.domain.BetaScale;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.ClusterNode;
import edu.ucdenver.bios.webservice.common.domain.ConfidenceInterval;
import edu.ucdenver.bios.webservice.common.domain.ConfidenceIntervalDescription;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.NominalPower;
import edu.ucdenver.bios.webservice.common.domain.PowerCurveDescription;
import edu.ucdenver.bios.webservice.common.domain.PowerMethod;
import edu.ucdenver.bios.webservice.common.domain.PowerResult;
import edu.ucdenver.bios.webservice.common.domain.PowerResultList;
import edu.ucdenver.bios.webservice.common.domain.Quantile;
import edu.ucdenver.bios.webservice.common.domain.RelativeGroupSize;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
import edu.ucdenver.bios.webservice.common.domain.SampleSize;
import edu.ucdenver.bios.webservice.common.domain.SigmaScale;
import edu.ucdenver.bios.webservice.common.domain.StatisticalTest;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.domain.TypeIError;

/**
 * JSON encoding/decoding for the communication layer using the 
 * piriti library.
 * 
 * @author Sarah Kreidler
 *
 */
public class DomainObjectSerializer {
    
    // initialize all of the JSON reader/writer classes.
    
    // study design
    public interface StudyDesignReader extends JsonReader<StudyDesign> {}
    public static final StudyDesignReader studyDesignReader = GWT.create(StudyDesignReader.class);
    public interface StudyDesignWriter extends JsonWriter<StudyDesign> {}
    public static final StudyDesignWriter studyDesignWriter = GWT.create(StudyDesignWriter.class);
    
    // named matrix
    public interface NamedMatrixReader extends JsonReader<NamedMatrix> {}
    public static final NamedMatrixReader namedMatrixReader = GWT.create(NamedMatrixReader.class);
    public interface NamedMatrixWriter extends JsonWriter<NamedMatrix> {}
    public static final NamedMatrixWriter namedMatrixWriter = GWT.create(NamedMatrixWriter.class);    
    
    // covariance objects
    public interface CovarianceReader extends JsonReader<Covariance> {}
    public static final CovarianceReader covarianceReader = GWT.create(CovarianceReader.class);
    public interface CovarianceWriter extends JsonWriter<Covariance> {}
    public static final CovarianceWriter covarianceWriter = GWT.create(CovarianceWriter.class);    
    
    // hypothesis objects
    public interface HypothesisReader extends JsonReader<Hypothesis> {}
    public static final HypothesisReader hypothesisReader = GWT.create(HypothesisReader.class);
    public interface HypothesisWriter extends JsonWriter<Hypothesis> {}
    public static final HypothesisWriter hypothesisWriter = GWT.create(HypothesisWriter.class);    
    
    // clustering
    public interface ClusterNodeReader extends JsonReader<ClusterNode> {}
    public static final ClusterNodeReader clusterNodeReader = GWT.create(ClusterNodeReader.class);
    public interface ClusterNodeWriter extends JsonWriter<ClusterNode> {}
    public static final ClusterNodeWriter clusterNodeWriter = GWT.create(ClusterNodeWriter.class);    
    
    // repeated measures
    public interface RepeatedMeasuresNodeReader extends JsonReader<RepeatedMeasuresNode> {}
    public static final RepeatedMeasuresNodeReader repeatedMeasuresNodeReader = GWT.create(RepeatedMeasuresNodeReader.class);
    public interface RepeatedMeasuresNodeWriter extends JsonWriter<RepeatedMeasuresNode> {}
    public static final RepeatedMeasuresNodeWriter repeatedMeasuresNodeWriter = GWT.create(RepeatedMeasuresNodeWriter.class);    
    
    // between participant factors
    public interface BetweenParticipantFactorReader extends JsonReader<BetweenParticipantFactor> {}
    public static final BetweenParticipantFactorReader betweenParticipantFactorReader = GWT.create(BetweenParticipantFactorReader.class);
    public interface BetweenParticipantFactorWriter extends JsonWriter<BetweenParticipantFactor> {}
    public static final BetweenParticipantFactorWriter betweenParticipantFactorWriter = GWT.create(BetweenParticipantFactorWriter.class);    
    
    // confidence intervals
    public interface ConfidenceIntervalDescriptionReader extends JsonReader<ConfidenceIntervalDescription> {}
    public static final ConfidenceIntervalDescriptionReader confidenceIntervalDescriptionReader = GWT.create(ConfidenceIntervalDescriptionReader.class);
    public interface ConfidenceIntervalDescriptionWriter extends JsonWriter<ConfidenceIntervalDescription> {}
    public static final ConfidenceIntervalDescriptionWriter confidenceIntervalDescriptionWriter = GWT.create(ConfidenceIntervalDescriptionWriter.class);    
    
    // power curve description
    public interface PowerCurveDescriptionReader extends JsonReader<PowerCurveDescription> {}
    public static final PowerCurveDescriptionReader powerCurveDescriptionReader = GWT.create(PowerCurveDescriptionReader.class);
    public interface PowerCurveDescriptionWriter extends JsonWriter<PowerCurveDescription> {}
    public static final PowerCurveDescriptionWriter powerCurveDescriptionWriter = GWT.create(PowerCurveDescriptionWriter.class);    

    // type I error
    public interface TypeIErrorReader extends JsonReader<TypeIError> {}
    public static final TypeIErrorReader typeIErrorReader = GWT.create(TypeIErrorReader.class);
    public interface TypeIErrorWriter extends JsonWriter<TypeIError> {}
    public static final TypeIErrorWriter typeIErrorWriter = GWT.create(TypeIErrorWriter.class);    

    // beta scales
    public interface BetaScaleReader extends JsonReader<BetaScale> {}
    public static final BetaScaleReader betaScaleReader = GWT.create(BetaScaleReader.class);
    public interface BetaScaleWriter extends JsonWriter<BetaScale> {}
    public static final BetaScaleWriter betaScaleWriter = GWT.create(BetaScaleWriter.class);    

    // sigma scale
    public interface SigmaScaleReader extends JsonReader<SigmaScale> {}
    public static final SigmaScaleReader sigmaScaleReader = GWT.create(SigmaScaleReader.class);
    public interface SigmaScaleWriter extends JsonWriter<SigmaScale> {}
    public static final SigmaScaleWriter sigmaScaleWriter = GWT.create(SigmaScaleWriter.class);    

    // sigma scale
    public interface RelativeGroupSizeReader extends JsonReader<RelativeGroupSize> {}
    public static final RelativeGroupSizeReader relativeGroupSizeReader = GWT.create(RelativeGroupSizeReader.class);
    public interface RelativeGroupSizeWriter extends JsonWriter<RelativeGroupSize> {}
    public static final RelativeGroupSizeWriter relativeGroupSizeWriter = GWT.create(RelativeGroupSizeWriter.class);    

    // sample size list
    public interface SampleSizeReader extends JsonReader<SampleSize> {}
    public static final SampleSizeReader sampleSizeReader = GWT.create(SampleSizeReader.class);
    public interface SampleSizeWriter extends JsonWriter<SampleSize> {}
    public static final SampleSizeWriter sampleSizeWriter = GWT.create(SampleSizeWriter.class);    

    // statistical test
    public interface StatisticalTestReader extends JsonReader<StatisticalTest> {}
    public static final StatisticalTestReader statisticalTestReader = GWT.create(StatisticalTestReader.class);
    public interface StatisticalTestWriter extends JsonWriter<StatisticalTest> {}
    public static final StatisticalTestWriter statisticalTestWriter = GWT.create(StatisticalTestWriter.class);    

    // power methods
    public interface PowerMethodReader extends JsonReader<PowerMethod> {}
    public static final PowerMethodReader powerMethodReader = GWT.create(PowerMethodReader.class);
    public interface PowerMethodWriter extends JsonWriter<PowerMethod> {}
    public static final PowerMethodWriter powerMethodWriter = GWT.create(PowerMethodWriter.class);    

    // quantiles
    public interface QuantileReader extends JsonReader<Quantile> {}
    public static final QuantileReader quantileReader = GWT.create(QuantileReader.class);
    public interface QuantileWriter extends JsonWriter<Quantile> {}
    public static final QuantileWriter quantileWriter = GWT.create(QuantileWriter.class);    

    // nominal power
    public interface NominalPowerReader extends JsonReader<NominalPower> {}
    public static final NominalPowerReader nominalPowerReader = GWT.create(NominalPowerReader.class);
    public interface NominalPowerWriter extends JsonWriter<NominalPower> {}
    public static final NominalPowerWriter nominalPowerWriter = GWT.create(NominalPowerWriter.class);    

    // responses
    public interface ResponseNodeReader extends JsonReader<ResponseNode> {}
    public static final ResponseNodeReader responseNodeReader = GWT.create(ResponseNodeReader.class);
    public interface ResponseNodeWriter extends JsonWriter<ResponseNode> {}
    public static final ResponseNodeWriter responseNodeWriter = GWT.create(ResponseNodeWriter.class);    

    // power results
    public interface PowerResultReader extends JsonReader<PowerResult> {}
    public static final PowerResultReader powerResultReader = GWT.create(PowerResultReader.class);
    public interface PowerResultWriter extends JsonWriter<PowerResult> {}
    public static final PowerResultWriter powerResultWriter = GWT.create(PowerResultWriter.class);
    
    // confidence interval objects contained in power results
    public interface ConfidenceIntervalReader extends JsonReader<ConfidenceInterval> {}
    public static final ConfidenceIntervalReader confidenceIntervalReader = GWT.create(ConfidenceIntervalReader.class);
    
    // integer reader
    public interface IntegerReader extends JsonReader<Integer>{}
    public static final IntegerReader integerReader = GWT.create(IntegerReader.class);
    
    //boolean reader
    public interface BooleanReader extends JsonReader<Boolean>{}
    public static final BooleanReader booleanReader = GWT.create(BooleanReader.class);
    
    /** 
     * The piriti library does not support multidimensional arrays.  Thus, we had to write 
     * our own extension for handling the Blob2DArray class.  
     * !!!DO NOT instantiate these with GWT.create - they are not auto-generated!!! 
     */
    public static final Blob2DArrayReader blob2DArrayReader = new Blob2DArrayReaderImpl();    
    public static final Blob2DArrayWriter blob2DArrayWriter = new Blob2DArrayWriterImpl();    
    
    /** Singleton instance of the DomainObjectSerializer class. */
    private static DomainObjectSerializer instance = null;
    
    /**
     * Create a new domain object serializer.
     */
    private DomainObjectSerializer() {
    }

    /**
     * Create a single instance of a domain object serializer.
     * @return DomainObjectSerializer object
     */
    public static DomainObjectSerializer getInstance() {
        if (instance == null) {
            instance = new DomainObjectSerializer();
        }

        return instance;
    }

    /**
     * Convert a study design to JSON
     * @param design the study design object
     * @return JSON encoded string
     */
    public String toJSON(StudyDesign design) {
        
        String json = studyDesignWriter.toJson(design);
        return json;
    }
    
    public String toJSON(NamedMatrix matrix) {
        
        String json = namedMatrixWriter.toJson(matrix);
        return json;
    }
    
    /**
     * Parse a list of power results from a JSON string
     * @param jsonString entity body of HTTP request
     * @return list of power results
     */
    public List<PowerResult> powerResultListFromJSON(String jsonString) {
        List<PowerResult> results = null;
        try
        {
            JSONArray array = JSONParser.parseStrict(jsonString).isArray();
            results = powerResultReader.readList(array);
        } catch (Exception e) {
        }
        return results;
    }
    
    /**
     * Parse a matrix list from a JSON string
     * @param jsonString entity body of HTTP request
     * @return list of matrices
     */
    public List<NamedMatrix> matrixListFromJSON(String jsonString) {
        List<NamedMatrix> results = null;
        try
        {
            JSONArray array = JSONParser.parseStrict(jsonString).isArray();
            results = namedMatrixReader.readList(array);
        } catch (Exception e) {
        }
        return results;
    }
    
    public Integer integerFromJSON(String jsonString)
    {
        Integer value = null;
        try
        {
            /*JSONObject object = JSONParser.parseStrict(jsonString).isObject();*/
            value = integerReader.read(jsonString);
        }
        catch (Exception e){    
        }
        return value;
    }
    
    public boolean booleanFromJSON(String jsonString)
    {
        boolean value = false;
        try
        {
            value = booleanReader.read(jsonString);
        }
        catch (Exception e){  
        }
        return value;
    }
}
