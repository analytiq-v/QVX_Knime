package edu.njit.qvxwriter;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class QvxWriterNodeSettings {

	 /** the settings key which is used to retrieve and 
    store the settings (from the dialog or from a settings file)    
   (package visibility to be usable from the dialog). */
	static enum Endianness {
		
		BIG_ENDIAN("Big-Endian"),
		LITTLE_ENDIAN("Little-Endian");
		
		public final String name;
		
		private Endianness(String _name) {
			name = _name;
		}
		
		public String toString() {
			return name;
		}
	}	

	static enum OverwritePolicy {
		ABORT("Abort"),
		OVERWRITE("Overwrite");
		
		public final String name;
		
		private OverwritePolicy(String _name) {
			name = _name;
		}
		
		public String toString() {
			return name;
		}		
	}
	
	static final String CFGKEY_FILE_NAME = "fileName";
	static final String CFGKEY_OVERWRITE_POLICY = "overwritePolicy";
	static final String CFGKEY_IS_BIG_ENDIAN = "isBigEndian";
	
	static final String CFGKEY_CUSTOM_TABLE_NAME = "customTableName";
	static final String CFGKEY_DEFAULT_TABLE_NAME = "defaultTableName";
	static final String CFGKEY_TABLE_NAME = "tableName";
	static final String CFGKEY_USE_DEFAULT_TABLE_NAME = "useDefaultTableName";
	
	static final String CFGKEY_USES_RECORD_SEPARATOR = "usesRecordSeparator";	
    
	private String fileName;
	private boolean isBigEndian;
	private String overwritePolicy;
	
	private String customTableName;
	private String defaultTableName;
	private String tableName;
	private boolean useDefaultTableName;
	
	private boolean usesRecordSeparator;
	
	QvxWriterNodeSettings() {
		fileName = null;
		isBigEndian = false;
		overwritePolicy = null;
		customTableName = null;
		defaultTableName = null;
		tableName = null;
		useDefaultTableName = false;
		usesRecordSeparator = false;
	}
	
	QvxWriterNodeSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		fileName = settings.getString(CFGKEY_FILE_NAME);
		isBigEndian = settings.getBoolean(CFGKEY_IS_BIG_ENDIAN);
		overwritePolicy = settings.getString(CFGKEY_OVERWRITE_POLICY);
		customTableName = settings.getString(CFGKEY_CUSTOM_TABLE_NAME);
		defaultTableName = settings.getString(CFGKEY_DEFAULT_TABLE_NAME);
		tableName = settings.getString(CFGKEY_TABLE_NAME);
		useDefaultTableName = settings.getBoolean(CFGKEY_USE_DEFAULT_TABLE_NAME);
		usesRecordSeparator = settings.getBoolean(CFGKEY_USES_RECORD_SEPARATOR);
	}
	
	void saveSettingsTo(NodeSettingsWO settings) {
		settings.addString(CFGKEY_FILE_NAME, fileName);
		settings.addBoolean(CFGKEY_IS_BIG_ENDIAN, isBigEndian);
		settings.addString(CFGKEY_OVERWRITE_POLICY, overwritePolicy);
		settings.addString(CFGKEY_CUSTOM_TABLE_NAME, customTableName);
		settings.addString(CFGKEY_DEFAULT_TABLE_NAME, defaultTableName);
		settings.addString(CFGKEY_TABLE_NAME, tableName);
		settings.addBoolean(CFGKEY_USE_DEFAULT_TABLE_NAME, useDefaultTableName);
		settings.addBoolean(CFGKEY_USES_RECORD_SEPARATOR, usesRecordSeparator);
	}
	
	String getFileName() {
		return fileName;
	}
	
	boolean getIsBigEndian() {
		return isBigEndian;
	}
	
	String getOverwritePolicy() {
		return overwritePolicy;
	}
	
	String getCustomTableName() {
		return customTableName;
	}
	
	String getDefaultTableName() {
		return defaultTableName;
	}
	
	String getTableName() {
		return tableName;
	}
	
	boolean getUseDefaultTableName() {
		return useDefaultTableName;
	}
	
	boolean getUsesSeparatorByte() {
		return usesRecordSeparator;
	}
	
	void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	void setIsBigEndian(boolean isBigEndian) {
		this.isBigEndian = isBigEndian;
	}
	
	void setOverwritePolicy(OverwritePolicy overwritePolicy) {
		this.overwritePolicy = overwritePolicy.toString();
	}
	
	void setCustomTableName(String customTableName) {
		this.customTableName = customTableName;
	}
	
	void setDefaultTableName(String defaultTableName) {
		this.defaultTableName = defaultTableName;
	}
	
	void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	void setUseDefaultTableName(boolean useDefaultTableName) {
		this.useDefaultTableName = useDefaultTableName;
	}
	
	void setUsesSeparatorByte(boolean usesSeparatorByte) {
		this.usesRecordSeparator = usesSeparatorByte;
	}
}
