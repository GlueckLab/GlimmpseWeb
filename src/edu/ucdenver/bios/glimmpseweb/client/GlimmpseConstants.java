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
package edu.ucdenver.bios.glimmpseweb.client;

import com.google.gwt.i18n.client.Constants;

/**
 * Container class for constants used throughout the GLIMMPSE interface.
 * Also used by Google Web Toolkit for internationalization
 */
public interface GlimmpseConstants extends Constants
{
    // shared style names for input wizard steps
	public static final String STYLE_LEFT_PANEL = "leftPanel";
	public static final String STYLE_RIGHT_PANEL = "rightPanel";
	public static final String STYLE_TOOLS_MENU_PANEL = "toolsMenuPanel";
	public static final String STYLE_TOOLS_MENU_HEADER = "toolsMenuHeader";
	public static final String STYLE_TOOLS_MENU_ITEM = "toolsMenuItem";
	public static final String STYLE_GLIMMPSE_PANEL = "glimmpsePanel";
    public static final String STYLE_WIZARD_STEP_PANEL = "wizardStepPanel";
    public static final String STYLE_WIZARD_STEP_HEADER = "wizardStepHeader";
    public static final String STYLE_WIZARD_STEP_DESCRIPTION = "wizardStepDescription";
    public static final String STYLE_WIZARD_STEP_SUBPANEL = "subpanel";
    public static final String STYLE_WIZARD_STEP_BUTTON = "wizardStepButton";
    public static final String STYLE_WIZARD_STEP_LINK = "wizardStepLink";
    public static final String STYLE_WIZARD_STEP_TABLE_PANEL = "wizardStepTablePanel";
    public static final String STYLE_WIZARD_STEP_TABLE = "wizardStepTable";
    public static final String STYLE_WIZARD_STEP_TABLE_HEADER = "wizardStepTableHeader";
    public static final String STYLE_WIZARD_STEP_TABLE_ROW = "wizardStepTableRow";
    public static final String STYLE_WIZARD_CONTENT = "wizardStepContent";
    public static final String STYLE_WIZARD_PARAGRAPH = "wizardStepParagraph";
    public static final String STYLE_WIZARD_INDENTED_CONTENT = "wizardStepIndentedContent";
    public static final String STYLE_WIZARD_RESPONSES_PANEL_HORIZONTAL_PANEL = "responsesPanelHorizontalPanel";
    public static final String STYLE_WIZARD_TOOL_PANEL = "wizardToolsPanel";
    public static final String STYLE_WIZARD_TOOL_BUTTON = "wizardToolsPanelButton";
    public static final String STYLE_WIZARD_STEP_LIST_PANEL = "wizardStepListPanel";
    public static final String STYLE_WIZARD_STEP_LIST_BUTTON = "wizardStepListButton";
    public static final String STYLE_WIZARD_STEP_LIST_TEXTBOX = "wizardStepListTextBox";
    public static final String STYLE_WIZARD_STEP_LIST_HEADER = "wizardStepListHeader";
    public static final String STYLE_WIZARD_LEFT_NAV_LINK = "wizardLeftNavLink";
    public static final String STYLE_WIZARD_LEFT_NAV_PANEL = "wizardLeftNavPanel";
    public static final String STYLE_WIZARD_LEFT_NAV_CONTENT = "wizardLeftNavContent";
    public static final String STYLE_WIZARD_STEP_TREE = "wizardStepTree";
    public static final String STYLE_WIZARD_STEP_TREE_NODE = "wizardStepTreeNode";
    public static final String STYLE_WIZARD_STEP_TEXTBOX = "wizardStepTextBox";
    public static final String STYLE_WIZARD_STEP_TAB_PANEL = "wizardStepTabPanel";
    public static final String STYLE_WIZARD_STEP_TAB_HEADER = "wizardStepTabPanelHeader";
    public static final String STYLE_WIZARD_STEP_TAB = "wizardStepTabPanelTab";
    public static final String STYLE_WIZARD_STEP_TAB_CONTENT = "wizardStepTabPanelContent";
    // Begin : Added for Power Curve Screen
    public static final String STYLE_WIZARD_STEP_LIST_BOX = "wizardStepListBox";
    public static final String STYLE_WIZARD_STEP_CHECK_BOX = "wizardStepCheckBox";    
    // End : Added for Power Curve Screen
    public static final String STYLE_MATRIX_PANEL = "matrixPanel";
    public static final String STYLE_MATRIX_DIMENSION = "matrixDimensions";
    public static final String STYLE_MATRIX_DATA = "matrixData";
    public static final String STYLE_MATRIX_CELL= "matrixCell";  
    // styles for feedback page
    public static final String STYLE_FEEDBACK_HEADER = "feedbackHeader";
    public static final String STYLE_FEEDBACK_DESCRIPTION = "feedbackDescription";
    public static final String STYLE_FEEDBACK_QUESTION = "feedbackQuestion";
    public static final String STYLE_FEEDBACK_RESPONSE = "feedbackResponse";
    public static final String STYLE_FEEDBACK_SUBMIT = "feedbackSubmit";
    public static final String STYLE_FEEDBACK_MESSAGE = "feedbackMessage";
    
    public static final String STYLE_MESSAGE = "message";
    public static final String STYLE_MESSAGE_ERROR = "error";
    public static final String STYLE_MESSAGE_OKAY = "okay";
    // generic dependent style names
    public static final String STYLE_EVEN = "even";
    public static final String STYLE_ODD = "odd";
    public static final String STYLE_SELECTED = "selected";
    public static final String STYLE_DESELECTED = "deselected";
    public static final String STYLE_DISABLED = "disabled";
    public static final String STYLE_SHORT = "short";
    public static final String STYLE_MED = "medium";
    public static final String STYLE_LONG = "long";
    public static final String STYLE_LEFT = "left";
    public static final String STYLE_LEFT_ACTIVE = "leftActive";
    public static final String STYLE_LEFT_NEXT_TO_ACTIVE = "leftNextToActive";
    public static final String STYLE_MIDDLE = "middle";
    public static final String STYLE_MIDDLE_ACTIVE = "middleActive";
    public static final String STYLE_MIDDLE_NEXT_TO_ACTIVE = "middleNextToActive";
    public static final String STYLE_RIGHT = "right";
    public static final String STYLE_RIGHT_ACTIVE = "rightActive";
    public static final String STYLE_SINGLE = "single";
    public static final String STYLE_INDENT = "indent";
    // mode names
    public static final String MODE_MATRIX = "matrix";
    public static final String MODE_GUIDED = "guided";
    
    // matrix names
    public static final String MATRIX_FIXED = "fixed";
    public static final String MATRIX_RANDOM = "random";
    public static final String MATRIX_DESIGN = "design";
    public static final String MATRIX_DESIGN_RANDOM = "designRandom";
    public static final String MATRIX_BETA = "beta";
    public static final String MATRIX_BETA_RANDOM = "betaRandom";
    public static final String MATRIX_BETWEEN_CONTRAST = "betweenSubjectContrast";
    public static final String MATRIX_BETWEEN_CONTRAST_RANDOM = "betweenSubjectContrastRandom";
    public static final String MATRIX_WITHIN_CONTRAST = "withinSubjectContrast";
    public static final String MATRIX_SIGMA_ERROR = "sigmaError";
    public static final String MATRIX_SIGMA_OUTCOME = "sigmaOutcome";
    public static final String MATRIX_SIGMA_OUTCOME_COVARIATE = "sigmaOutcomeGaussianRandom";
    public static final String MATRIX_SIGMA_COVARIATE = "sigmaGaussianRandom";
    public static final String MATRIX_THETA = "thetaNull";

    // data table column names for power results
    public static final String COLUMN_NAME_TEST = "test";
    public static final String COLUMN_NAME_ALPHA = "alpha";
    public static final String COLUMN_NAME_ACTUAL_POWER = "actualPower";
    public static final String COLUMN_NAME_SAMPLE_SIZE = "sampleSize";
    public static final String COLUMN_NAME_BETA_SCALE = "betaScale";
    public static final String COLUMN_NAME_SIGMA_SCALE = "sigmaScale";
    public static final String COLUMN_NAME_NOMINAL_POWER = "nominalPower";
    public static final String COLUMN_NAME_POWER_METHOD = "powerMethod";
    public static final String COLUMN_NAME_QUANTILE = "quantile";
    public static final String COLUMN_NAME_CI_LOWER = "ciLower";
    public static final String COLUMN_NAME_CI_UPPER = "ciUpper";
    
    // 
    public static final String RESPONSES_COVARIANCE_LABEL = "__RESPONSE_COVARIANCE__";
    
	// dimension names derived from linear model theory.
	// ensures that default matrix dimensions conform properly
    public static final int DEFAULT_N = 3;
    public static final int DEFAULT_Q = 3;
    public static final int DEFAULT_P = 1;
    public static final int DEFAULT_A = 2;
    public static final int DEFAULT_B = 1;
        
    // toolbar separator keyword
    public static final String TOOLBAR_SEPARATOR = "_SEPARATOR_";
    
    //inavalid string
    public String invalidString();
    // footer text
    public String fundingStatement();
    
    // left navigation / steps left panel
    // navigation group headers shared for matrix and guided mode
    public String navGroupStart();
    public String navGroupOptions();
    public String navGroupVariability();
    // navigation group headers for matrix mode
    public String navGroupDesign();
    public String navGroupCoefficients();
    public String navGroupContrasts();
    public String navGroupNullHypothesis();
    // navigation group headers for guided mode
    public String navGroupPredictors();
    public String navGroupClustering();
    public String navGroupResponses();
    public String navGroupHypothesis();
    public String navGroupMeans();
    // navigation panel labels shared in both matrix/guided
    public String navItemIntro();
    public String navItemSolutionType();
    public String navItemNominalPower();
    public String navItemTypeIError();
    public String navItemGaussianCovariate();
    public String navItemPerGroupSampleSize();
    public String navItemStatisticalTest();
    public String navItemConfidenceIntervals();
    public String navItemPowerCurve();
    public String navItemPowerMethod();
    
    // navigation panel labels for matrix mode
    public String navItemDesignEssenceMatrix();
    public String navItemBetaMatrix();
    public String navItemBetaScale();
    public String navItemBetweenParticipantContrast();
    public String navItemWithinParticipantContrast();
    public String navItemThetaNullMatrix();
    public String navItemSigmaErrorMatrix();
    public String navItemSigmaOutcomesMatrix();
    public String navItemSigmaCovariateOutcomesMatrix();
    public String navItemSigmaCovariateMatrix();
    public String navItemSigmaScale();
    // navigation panel labels for guided mode
    public String navItemFixedPredictors();
    public String navItemRelativeGroupSize();
    public String navItemResponses();
    public String navItemClustering();
    public String navItemRepeatedMeasure();
    public String navItemHypothesis();
    public String navItemNullHypothesis();
    public String navItemMeans();
    public String navItemMeansScaleFactors();
    public String navItemVariabilityWithinParticipant();
    public String navItemVariabilityGaussianCovariate();
    public String navItemVariabilityScaleFactors();
    public String navItemFinish();
    
    // tools menu
    public String toolsMenu();
    public String toolsMenuSave();
    public String toolsMenuHelp();
    public String toolsMenuAboutGlimmpse();
    public String toolsMenuCancel();
    public String toolsMenuSaveCSV();
    public String toolsMenuSaveCurve();
    public String toolsMenuSaveLegend();
    public String toolsMenuViewMatrices();
    
    // navigation buttons
    public String buttonNext();
    public String buttonPrevious();
    public String buttonHelp();
    public String buttonSave();
    public String buttonCancel();
    public String buttonFinish();
    // other buttons
    public String buttonDelete();
    public String buttonAdd();
    public String buttonClose();
    public String buttonExplain();
    
    // miscellaneous words
    public String and();
    public String yes();
    public String no();
    public String other();
    public String notSure();
    public String submit();
    
    // mode selection panel constants
    public String modeSelectionTitle();
    public String modeSelectionDescription();
    public String modeSelectionGoButton();
    public String modeSelectionMatrixTitle();
    public String modeSelectionMatrixDescription();
    public String modeSelectionGuidedTitle();
    public String modeSelectionGuidedDescription();
    public String modeSelectionUploadTitle();
    public String modeSelectionUploadDescription();
    
    /*  start group - navigation intro screen and solving for selection */
    // start panel constants
    public String startTitle();
    public String startDescription();
    // "solving for" panel constants
    public String solvingForLink();
    public String solvingForTitle();
    public String solvingForDescription();
    public String solvingForPowerLabel();
    public String solvingForSampleSizeLabel();
    public String solvingForEffectSizeLabel();
    public String solvingForNominalPowerTitle();
    public String solvingForNominalPowerDescription();
    public String solvingForNominalPowerInstructions();
    public String solvingForNominalPowerTableColumn();
    
    /* type I error section */
    public String alphaIntroTitle();
    public String alphaIntroDescription();
    // alpha panel constants
    public String alphaTitle();
    public String alphaDescription();
    public String alphaTableColumn();
    public String simpleAlphaTitle();
    public String simpleAlphaDescription();
    /* predictors section */
    public String participantsIntroTitle();
    public String participantsIntroDescription();
    // categorical predictors panel constants
    public String predictorsTitle();
    public String predictorsDescription();
    public String predictorsOneSampleButton();
    public String predictorsMultiSampleButton();
    public String predictorsTableColumn();
    public String categoriesTableColumn();
    // covariate panel constants
    public String covariateTitle();
    public String covariateDescription();
    public String predictorsCovariateDescription();
    public String matrixCovariateDescription();
    public String covariateCheckBoxLabel();
    public String covariateMeanLabel();
    public String covariateStandardDeviationLabel();
    // relative group size constants
    public String relativeGroupSizeTitle();
    public String relativeGroupSizeDescription();
    public String relativeGroupSizeTableColumn();
    public String perGroupSampleSizeTitle();
    public String perGroupSampleSizeDescription();
    public String perGroupSampleSizeTableColumn();
    
    /* outcomes section */
    public String responsesIntroTitle();
    public String responsesIntroDescription();
    // outcome variables panel
    public String responseVariablesTitle();
    public String responseVariablesDescription();
    public String responseVariablesTableColumn();
    // repeated measures panel
    public String repeatedMeasuresTitle();
    public String repeatedMeasuresDescription();
    public String repeatedMeasuresPanelAddRMButton();
    public String repeatedMeasuresPanelRemoveRMButton();
    public String repeatedMeasuresPanelAddSubDimensionButton();
    public String repeatedMeasuresPanelRemoveSubDimensionButton();
    // repeated measures subpanel
    public String repeatedMeasuresNodePanelDimensionLabel();
    public String repeatedMeasuresNodePanelTypeLabel();
    public String repeatedMeasuresNodePanelSpacingLabel();
    public String repeatedMeasuresNodePanelNumericLabel();
    public String repeatedMeasuresNodePanelOrdinalLabel();
    public String repeatedMeasuresNodePanelCategoricalLabel();
    public String repeatedMeasuresNodePanelNumMeasurementsLabel();
    public String repeatedMeasuresNodePanelEqualSpacingButton();
    
    /* hypothesis section */
    // hypotheses panel constants
    public String hypothesisIntroTitle();
    public String hypothesisIntroDescription();
    public String hypothesisTitle();
    public String hypothesisDescription();
    //hypothesis Panel
    public String hypothesisPanelMainEffect();
    public String hypothesisPanelMainEffectExplanation();
    public String hypothesisPanelInteraction();
    public String hypothesisPanelInteractionExplanation();
    public String hypothesisPanelTrend();
    public String hypothesisPanelTrendExplanation();
    public String hypothesisPanelGrandMean();
    public String hypothesisPanelGrandMeanExplanation();
    public String hypothesisPanelSelectTypeOfTrend();
    public String hypothesisPanelBetweenParticipantFactorsLabel();
    public String hypothesisPanelWithinParticipantFactorsLabel();

    public String hypothesisTrendPanelNoTrend();
    public String hypothesisTrendPanelLinear();
    public String hypothesisTrendPanelQuadratic();
    public String hypothesisTrendPanelCubic();
    public String hypothesisTrendPanelAllTrends();
    public String hypothesisTrendPanelChangeFromBaseline();

    //main effects Panel
    public String mainEffectPanelText();    
    //interaction hypothesis Panel
    public String interactionHypothesisPanelText();
    //trend hypothesis Panel
    public String trendHypothesisPanelText();
    // one sample hypothesis panel
    public String grandMeanHypothesisPanelText();
    
    //edit trend panel
    public String editTrendLabel();
    public String editTrendSelectedTrendPrefix();
    public String editTrendNoTrend();
    public String editTrendChangeFromBaseline();
    public String editTrendAllPolynomialTrends();
    public String editTrendLinearTrendOnly();
    public String editTrendQudraticTrendOnly(); 
    public String editTrendCubicTrendOnly();    
    
    /* mean differences section */
    public String meanDifferenceIntroTitle();
    public String meanDifferenceIntroDescription();
    public String meanDifferenceTitle();
    public String meanDifferenceDescription();
    public String meanDifferenceMainEffectHypothesis();
    public String meanDifferenceMainEffectQuestion();
    public String meanDifferenceInteractionEffectHypothesis();
    public String meanDifferenceInteractionEffectQuestion();
    public String meanDifferenceInteractionEffectQuestionMiddle();
    public String meanDifferencePatternTitle();
    public String meanDifferencePatternDescription();
    public String meanDifferenceScaleTitle();
    public String meanDifferenceScaleDescription();
    public String meanDifferenceScaleQuestion();
    public String meanDifferenceScaleAnswer();
    public String meanDifferenceRepeatedMeasuresInstructions();
    /* Variability section */
    // intro panel
    public String variabilityIntroTitle();
    public String variabilityIntroDescription();
    // sigma error panel
    public String variabilityErrorTitle();
    public String variabilityErrorDescription();  
    // sigma covariate panel
    public String variabilityCovariateTitle();
    public String variabilityCovariateDescription();
    // sigma outcome panel
    public String variabilityOutcomeTitle();
    public String variabilityOutcomeDescription();
    public String variabilityOutcomeQuestion();
    public String correlationOutcomeQuestion();
    // sigma covariate outcome panel
    public String variabilityCovariateOutcomeTitle();
    public String variabilityCovariateOutcomeDescription();
    public String variabilityCovariateOutcomeQuestion();
    // sigma scale panel
    public String variabilityScaleTitle();
    public String variabilityScaleDescription();
    public String variabilityScaleQuestion();
    public String variabilityScaleAnswer();
    
    // options panel constants
    public String optionsIntroTitle();
    public String optionsIntroDescription();
    public String optionsTitle();
    public String optionsDescription();
    public String testTitle();
    public String testDescription();
    public String testUnirepLabel();
    public String testUnirepGeisserGreenhouseLabel();
    public String testUnirepHuynhFeldtLabel();
    public String testUnirepBoxLabel();
    public String testWilksLambdaLabel();
    public String testPillaiBartlettTraceLabel();
    public String testHotellingLawleyTraceLabel();
    // power method constants
    public String powerMethodTitle();
    public String powerMethodDescription();
    public String powerMethodConditionalLabel();
    public String powerMethodQuantileLabel();
    public String powerMethodUnconditionalLabel();
    public String quantilesTableColumn();
    // display constants
    public String curveOptionsTitle();
    public String curveOptionsDescription();
    public String curveOptionsNone();
    public String curveOptionsXAxisLabel();
    public String curveOptionsStratifyLabel();
    public String curveOptionsDataSeriesLabel();
    public String curveOptionsSampleSizeLabel();
    public String curveOptionsBetaScaleLabel();
    public String curveOptionsSigmaScaleLabel();
    public String curveOptionsTestLabel();
    public String curveOptionsAlphaLabel();
    public String curveOptionsNominalPowerLabel();
    public String curveOptionsPowerLabel();
    public String curveOptionsPowerMethodLabel();
    public String curveOptionsQuantileLabel();   
    public String curveOptionsNoneLabel();
    // Begin Change : Added a new label for checkbox option
    public String curveOptionsConfidenceLimitsLabel();
    // End Change : Added a new label for checkbox option

    
    // confidence interval constants
    public String confidenceIntervalOptionsTitle();
    public String confidenceIntervalOptionsDescription();
    public String confidenceIntervalOptionsNone();
    public String confidenceIntervalOptionsTypeQuestion();
    public String confidenceIntervalOptionsTypeSigma();
    public String confidenceIntervalOptionsTypeBetaSigma();
    public String confidenceIntervalOptionsAlphaQuestion();
    public String confidenceIntervalOptionsAlphaLower();
    public String confidenceIntervalOptionsAlphaUpper();
    public String confidenceIntervalOptionsEstimatedDataQuestion();
    public String confidenceIntervalOptionsSampleSize();
    public String confidenceIntervalOptionsRank();
    
    // matrix constants
    public String matrixDimensionSeparator();
    // matrix intro
    public String matrixIntroTitle();
    public String matrixIntroDescription();
    // design matrix screen
    public String designIntroTitle();
    public String designIntroDescription();
    public String matrixDesignTitle();
    public String matrixDesignDescription();
    public String matrixCategoricalEffectsLabel();
    public String matrixCovariateEffectsLabel();
    
    // beta matrix screens
    public String parameterIntroTitle();
    public String parameterIntroDescription();
    public String betaTitle();
    public String betaDescription();
    public String betaScaleTitle();
    public String betaScaleDescription();
    public String betaScaleTableColumn();
    public String betaFixedMatrixName();
    public String betaGaussianMatrixName();

    // sigma matrix screen
    public String sigmaScaleTitle();
    public String sigmaScaleDescription();
    public String sigmaScaleTableColumn();
    
    // contrasts
    public String contrastIntroTitle();
    public String contrastIntroDescription();
    public String betweenSubjectContrastTitle();
    public String betweenSubjectContrastDescription();
    public String betweenSubjectContrastMatrixName();
    public String withinSubjectContrastTitle();
    public String withinSubjectContrastDescription();
    public String withinSubjectContrastMatrixName();
    
    // theta null
    public String thetaNullTitle();
    public String thetaNullDescription();
    public String thetaNullMatrixName();
    
    // sigma matrices
    public String sigmaErrorTitle();
    public String sigmaErrorDescription();
    public String sigmaErrorMatrixName();
    public String sigmaCovariateTitle();
    public String sigmaCovariateDescription();
    public String sigmaCovariateMatrixName();
    public String sigmaOutcomeTitle();
    public String sigmaOutcomeDescription();
    public String sigmaOutcomeMatrixName();
    public String sigmaOutcomeCovariateTitle();
    public String sigmaOutcomeCovariateDescription();
    public String sigmaOutcomeCovariateMatrixName();
    
    // error messages
    public String errorUploadFailed();
    public String errorUploadInvalidStudyFile();
    public String errorInvalidAlpha();
    public String errorInvalidMean();
    public String errorInvalidStandardDeviation();
    public String errorInvalidSampleSize();
    public String errorInvalidQuantile();
    public String errorInvalidPower();
    public String errorInvalidString();
    public String errorInvalidNumber();
    public String errorInvalidNonNegativeNumber();
    public String errorInvalidPositiveNumber();
    public String errorMaxRows();
    public String errorInvalidMatrixDimension();
    public String errorInvalidTailProbability();
    public String errorSampleSizeLessThanRank();
    public String errorInvalidClusterSize();
    public String errorInvalidCorrelation();
    public String errorInvalidNumRepeatedMeasures();
    public String errorInvalidEmail();
    public String errorNonUniqueValue();
    // confirm messages
    public String confirmClearScreen();
    public String confirmClearAll();
    
    // web services urls
    public String helpManualURI();
    public String formmailURI();
    public String chartSvcHostScatter();
    public String chartSvcHostLegend();
    public String powerSvcHostPower();
    public String powerSvcHostSampleSize();
    public String powerSvcHostDetectableDifference();
    public String powerSvcHostMatrices();
    public String powerSvcHostMatricesAsHTML();
    public String matrixSvcHostMatrices();
    public String fileSvcHostUpload();
    public String fileSvcHostSaveAs();

    //response panel constants
     public String responsesPanelTitle();
     public String responsesPanelDescription();
     public String responsesPanelPrefix();
     public String responsesPanelSufix();
     public String responsesPanelInstructions();
     public String responsesTableColumn();
     
     //clustering panel constants
     public String clusteringPanelTitle();
     public String clusteringPanelDescription();
     public String clusteringPanelAddClusteringButton();
     public String clusteringPanelRemoveClusteringButton();
     public String clusteringPanelAddSubgroupButton();
     public String clusteringPanelRemoveSubgroupButton();
     
     // subpanels for individual clustering nodes
     public String clusteringNodePanelNameLabel();
     public String clusteringNodePanelNumberOfGroupsLabel();
     public String clusteringNodePanelIntraclassCorrelationLabel();
     //cancel Dialog Box
     public String cancelDialogBoxQuery();

     //structured correlation panel
     public String structuredCorrelationPanelHeader();
     public String structuredCorrelationPanelText();
     public String standardDeviationLabel();
     public String standardDeviationExplinationHeader();
     public String standardDeviationExplinationText();
     public String strongestCorrelationLabel();
     public String strongestCorrelationExplinationHeader();
     public String strongestCorrelationExplinationText();
     public String rateOfDecayOfCorrelationLabel();
     public String rateOfDecayOfCorrelationExplinationHeader();
     public String rateOfDecayOfCorrelationExplinationText();

     //
     public String useCustomVariablity();
     public String useCustomCorrelation();
     public String uploadCorrelationMatrix();
     public String useStructuredVariability();
     public String useCovarianceView();
     public String uploadCovarianceMatrix();
     public String useCorrelationView();
     
     public String explainButtonText();
     
     public String useCustomVariablityAlertHeader();
     public String useCustomVariabilityAlertText();
     public String useCustomCorrelationAlertHeader();
     public String useCustomCorrelationAlertText();
     public String useStructuredVariabilityAlertHeader();
     public String useStructuredVariabilityAlertText();
     public String useCovarianceViewAlertHeader();
     public String useCovarianceViewAlertText();
     
     public String correlationMatrixAlertHeader();
     public String correlationMatrixAlertText();
     public String covarianceMatrixAlertHeader();
     public String covarianceMatrixAlertText();
     public String covarianceResponsesLabel();
     
     public String unstructuredCorrelationTitle();
     public String unstructuredCorrelationDescription();
     public String unstructuredCorrelationExpectedStandardDeviation();
     public String unstructuredCorrelationExpectedCorrelationMultivariate();
     public String unstructuredCorrelationExpectedCorrelationRepeatedMeasures();
     
     //unstructured covariance panel
     public String unstructuredCovarianceInstructions();
     public String unstructuredCovarianceHeader();
     
     
     //Within Subject Covariance Header
     public String withinSubjectCovarianceHeader();
     public String withinSubjectCovarianceInstructions();
     public String uploadFullCovarianceMatrix();
     public String fullCovarianceMatrixHeader();
     public String fullCovarianceMatrixText();
     
     //random Covariate Covariance Panel
     public String randomCovariateCovarianceHeader();
     public String randomCovariateCovarianceDescription();
     public String randomCovariateCovarianceRepeatedMeasuresInstructions();
     public String randomCovariateCovarianceStandardDeviationInstructions();
     public String randomCovariateCovarianceCorrelationInstructions();
     public String randomCovariateCovarianceSameCorrelationInstructions();
     public String randomCovariateCovarianceOutcomesColumnLabel();
     public String randomCovariateCovarianceCorrelationColumnLabel();
     // results screen
     public String resultsPowerResultsLabel();
     public String resultsPowerCurveLabel();
     public String resultsSaveToCSVLabel();
     public String resultsViewMatricesLabel();
     
     // feedback screen
     public String feedbackHeader();
     public String feedbackDescription();
     public String feedbackTypeLabel();
     public String feedbackTypeGeneral();
     public String feedbackTypeFeature();
     public String feedbackTypeBug();
     public String feedbackNameLabel();
     public String feedbackEmailLabel();
     public String feedbackMayWeContactLabel();
     public String feedbackBrowserLabel();
     public String feedbackOtherLabel();
     public String feedbackVersionLabel();
     public String feedbackContentsLabel();
     
     // browser labels
     public String browserIELabel();
     public String browserFirefoxLabel();
     public String browserChromeLabel();
     public String browserSafariLabel();
     public String browserOperaLabel();
}
