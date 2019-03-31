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
	static final String CFGKEY_OVERWRITE_POLICY = "fileOverwritePolicy";
	static final String CFGKEY_IS_BIG_ENDIAN = "isBigEndian";
	static final String CFGKEY_USES_RECORD_SEPARATOR = "usesRecordSeparator";	
	
	/** initial default count value. */
    static final int DEFAULT_COUNT = 100;
    
    
	private String fileName;
	private boolean isBigEndian;
	private String overwritePolicy;
	private boolean usesRecordSeparator;
	
	QvxWriterNodeSettings() {
		fileName = null;
		isBigEndian = false;
		overwritePolicy = null;
		usesRecordSeparator = false;
	}
	
	QvxWriterNodeSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		fileName = settings.getString(CFGKEY_FILE_NAME);
		isBigEndian = settings.getBoolean(CFGKEY_IS_BIG_ENDIAN);
		overwritePolicy = settings.getString(CFGKEY_OVERWRITE_POLICY);
		usesRecordSeparator = settings.getBoolean(CFGKEY_USES_RECORD_SEPARATOR);
	}
	
	void saveSettingsTo(NodeSettingsWO settings) {
		settings.addString(CFGKEY_FILE_NAME, fileName);
		settings.addBoolean(CFGKEY_IS_BIG_ENDIAN, isBigEndian);
		settings.addString(CFGKEY_OVERWRITE_POLICY, overwritePolicy);
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
	
	void setUsesSeparatorByte(boolean usesSeparatorByte) {
		this.usesRecordSeparator = usesSeparatorByte;
	}
}
