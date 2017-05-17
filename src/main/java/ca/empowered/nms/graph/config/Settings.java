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
	private static String nodeNameSuffix;

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
	public static String getNodeNameSuffix() {
		return nodeNameSuffix;
	}
	public static void setNodeNameSuffix(String nodeNameSuffix) {
		Settings.nodeNameSuffix = nodeNameSuffix;
	}
}
