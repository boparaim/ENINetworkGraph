package ca.empowered.nms.graph.config;

import org.springframework.stereotype.Component;

/**
 * This entity reads application properties. It acts as a utility class for accessing all the settings.
 * 
 * @author mboparai
 *
 */
@Component
public final class Settings {

	//private static final Logger log = LogManager.getLogger(Settings.class.getName());
	
	private static String appName;
	private static String sourceConfigFile;
	private static boolean sourceReadContinuously;
	private static int sourceReadInterval;
	private static String outputInitialGraphFile;
	private static String graphvizPath;
	private static String graphvizLayout;
	private static String graphvizFormat;
	private static String graphvizOutputFile;
	private static String restServerIP;
	private static String restServerPort;
	private static String restServerPath;

	public static String getAppName() {
		return appName;
	}
	public static void setAppName(String appName) {
		Settings.appName = appName;
	}
	public static String getSourceConfigFile() {
		return sourceConfigFile;
	}
	public static void setSourceConfigFile(String sourceConfigFile) {
		Settings.sourceConfigFile = sourceConfigFile;
	}
	public static boolean isSourceReadContinuously() {
		return sourceReadContinuously;
	}
	public static void setSourceReadContinuously(boolean sourceReadContinuously) {
		Settings.sourceReadContinuously = sourceReadContinuously;
	}
	public static int getSourceReadInterval() {
		return sourceReadInterval;
	}
	public static void setSourceReadInterval(int sourceReadInterval) {
		Settings.sourceReadInterval = sourceReadInterval * 1000 * 60;
	}
	public static String getOutputInitialGraphFile() {
		return outputInitialGraphFile;
	}
	public static void setOutputInitialGraphFile(String outputInitialGraphFile) {
		Settings.outputInitialGraphFile = outputInitialGraphFile;
	}
	public static String getGraphvizPath() {
		return graphvizPath;
	}
	public static void setGraphvizPath(String graphvizPath) {
		Settings.graphvizPath = graphvizPath;
	}
	public static String getGraphvizLayout() {
		return graphvizLayout;
	}
	public static void setGraphvizLayout(String graphvizLayout) {
		Settings.graphvizLayout = graphvizLayout;
	}
	public static String getGraphvizFormat() {
		return graphvizFormat;
	}
	public static void setGraphvizFormat(String graphvizFormat) {
		Settings.graphvizFormat = graphvizFormat;
	}
	public static String getGraphvizOutputFile() {
		return graphvizOutputFile;
	}
	public static void setGraphvizOutputFile(String graphvizOutputFile) {
		Settings.graphvizOutputFile = graphvizOutputFile;
	}
	public static String getRestServerIP() {
		return restServerIP;
	}
	public static void setRestServerIP(String restServerIP) {
		Settings.restServerIP = restServerIP;
	}
	public static String getRestServerPort() {
		return restServerPort;
	}
	public static void setRestServerPort(String restServerPort) {
		Settings.restServerPort = restServerPort;
	}
	public static String getRestServerPath() {
		return restServerPath;
	}
	public static void setRestServerPath(String restServerPath) {
		Settings.restServerPath = restServerPath;
	}
	private static String nodeNameSuffix;
	private static Boolean evenlyDistributeRelationships;
	private static Boolean randomizeEventGeneration;
	private static Integer randomizeEventGenerationInterval;
	private static String restDestinationURL;
	private static Boolean restClientEnabled;
	private static String cssStyleSheet;
	private static boolean displayGUI;
	private static boolean guiClosesApp;
	private static boolean uiAntiAlias;
	private static boolean uiQuality;
	private static String uiLayoutAlgorithm;
	private static String uiRenderer;
	private static boolean uiUseOpenGL;
	private static boolean uiUseDirectX;
	private static double uiZoomFactor;
	private static boolean uiMaximize;
	private static boolean uiZoomWithMouse;
	private static boolean exportTopology;
	private static String topologyFormat;
	private static boolean noisyEventGeneration;
	private static Integer noisyEventGenerationInterval;
	private static String noisyEventGenerationEvents;
	private static boolean uiShowRelatedNodesOnClick;
	
	public static String getNodeNameSuffix() {
		return nodeNameSuffix;
	}
	public static void setNodeNameSuffix(String nodeNameSuffix) {
		Settings.nodeNameSuffix = nodeNameSuffix;
	}
	public static Boolean getEvenlyDistributeRelationships() {
		return evenlyDistributeRelationships;
	}
	public static void setEvenlyDistributeRelationships(Boolean evenlyDistributeRelationships) {
		Settings.evenlyDistributeRelationships = evenlyDistributeRelationships;
	}
	public static Boolean isRandomizeEventGeneration() {
		return randomizeEventGeneration;
	}
	public static void setRandomizeEventGeneration(Boolean randomizeEventGeneration) {
		Settings.randomizeEventGeneration = randomizeEventGeneration;
	}
	public static String getRestDestinationURL() {
		return restDestinationURL;
	}
	public static void setRestDestinationURL(String restDestinationURL) {
		Settings.restDestinationURL = restDestinationURL;
	}
	public static Boolean getRestClientEnabled() {
		return restClientEnabled;
	}
	public static void setRestClientEnabled(Boolean restClientEnabled) {
		Settings.restClientEnabled = restClientEnabled;
	}
	public static String getCssStyleSheet() {
		return cssStyleSheet;
	}
	public static void setCssStyleSheet(String cssStyleSheet) {
		Settings.cssStyleSheet = cssStyleSheet;
	}
	public static boolean isDisplayGUI() {
		return displayGUI;
	}
	public static void setDisplayGUI(boolean displayGUI) {
		Settings.displayGUI = displayGUI;
	}
	public static boolean isGuiClosesApp() {
		return guiClosesApp;
	}
	public static void setGuiClosesApp(boolean guiClosesApp) {
		Settings.guiClosesApp = guiClosesApp;
	}
	public static boolean isUiAntiAlias() {
		return uiAntiAlias;
	}
	public static void setUiAntiAlias(boolean uiAntiAlias) {
		Settings.uiAntiAlias = uiAntiAlias;
	}
	public static boolean isUiQuality() {
		return uiQuality;
	}
	public static void setUiQuality(boolean uiQuality) {
		Settings.uiQuality = uiQuality;
	}
	public static String getUiLayoutAlgorithm() {
		return uiLayoutAlgorithm;
	}
	public static void setUiLayoutAlgorithm(String uiLayoutAlgorithm) {
		Settings.uiLayoutAlgorithm = "org.graphstream.ui.layout.springbox.implementations." + uiLayoutAlgorithm;
		System.out.println("class is "+Settings.uiLayoutAlgorithm);
		System.setProperty("org.graphstream.ui.layout", Settings.uiLayoutAlgorithm);
	}
	public static String getUiRenderer() {
		return uiRenderer;
	}
	public static void setUiRenderer(String uiRenderer) {
		Settings.uiRenderer = uiRenderer;
		System.setProperty("org.graphstream.ui.renderer", Settings.uiRenderer);
	}
	public static boolean isUiUseOpenGL() {
		return uiUseOpenGL;
	}
	public static void setUiUseOpenGL(boolean uiUseOpenGL) {
		Settings.uiUseOpenGL = uiUseOpenGL;
		System.setProperty("sun.java2d.opengl", String.valueOf(Settings.uiUseOpenGL));
	}
	public static boolean isUiUseDirectX() {
		return uiUseDirectX;
	}
	public static void setUiUseDirectX(boolean uiUseDirectX) {
		Settings.uiUseDirectX = uiUseDirectX;
		System.setProperty("sun.java2d.directx", String.valueOf(Settings.uiUseDirectX));
	}
	public static double getUiZoomFactor() {
		return uiZoomFactor;
	}
	public static void setUiZoomFactor(double uiZoomFactor) {
		Settings.uiZoomFactor = uiZoomFactor;
	}
	public static boolean isUiMaximize() {
		return uiMaximize;
	}
	public static void setUiMaximize(boolean uiMaximize) {
		Settings.uiMaximize = uiMaximize;
	}
	public static boolean isUiZoomWithMouse() {
		return uiZoomWithMouse;
	}
	public static void setUiZoomWithMouse(boolean uiZoomWithMouse) {
		Settings.uiZoomWithMouse = uiZoomWithMouse;
	}
	public static boolean isExportTopology() {
		return exportTopology;
	}
	public static void setExportTopology(boolean exportTopology) {
		Settings.exportTopology = exportTopology;
	}
	public static String getTopologyFormat() {
		return topologyFormat;
	}
	public static void setTopologyFormat(String topologyFormat) {
		Settings.topologyFormat = topologyFormat;
	}
	public static boolean isNoisyEventGeneration() {
		return noisyEventGeneration;
	}
	public static void setNoisyEventGeneration(boolean noisyEventGeneration) {
		Settings.noisyEventGeneration = noisyEventGeneration;
	}
	public static boolean isUiShowRelatedNodesOnClick() {
		return uiShowRelatedNodesOnClick;
	}
	public static void setUiShowRelatedNodesOnClick(boolean uiShowRelatedNodesOnClick) {
		Settings.uiShowRelatedNodesOnClick = uiShowRelatedNodesOnClick;
	}
	public static Integer getRandomizeEventGenerationInterval() {
		return randomizeEventGenerationInterval;
	}
	public static void setRandomizeEventGenerationInterval(Integer randomizeEventGenerationInterval) {
		Settings.randomizeEventGenerationInterval = randomizeEventGenerationInterval;
	}
	public static Integer getNoisyEventGenerationInterval() {
		return noisyEventGenerationInterval;
	}
	public static void setNoisyEventGenerationInterval(Integer noisyEventGenerationInterval) {
		Settings.noisyEventGenerationInterval = noisyEventGenerationInterval;
	}
	public static String getNoisyEventGenerationEvents() {
		return noisyEventGenerationEvents;
	}
	public static void setNoisyEventGenerationEvents(String noisyEventGenerationEvents) {
		Settings.noisyEventGenerationEvents = noisyEventGenerationEvents;
	}
}
