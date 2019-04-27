package edu.njit.knime.adapter.nodes.qvx;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class QvxReaderNodeSettings {

	static final String CFGKEY_FILE_NAME = "fileName";
	
	private String fileName;
	
	QvxReaderNodeSettings(){
		fileName = null;
	}
	
	QvxReaderNodeSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		fileName = settings.getString(CFGKEY_FILE_NAME);
	}
	
	void saveSettingsTo(NodeSettingsWO settings) {
		settings.addString(CFGKEY_FILE_NAME, fileName);
	}
	
	public String getFileName() {
		return fileName;
	}
	
	void setFileName(String fileName) {
		this.fileName = fileName;
	}
}