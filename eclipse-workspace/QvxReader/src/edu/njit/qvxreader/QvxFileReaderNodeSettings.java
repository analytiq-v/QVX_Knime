package edu.njit.qvxreader;

import java.net.MalformedURLException;
import java.net.URL;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.util.tokenizer.TokenizerSettings;

import edu.njit.qvx.QVXReader;
import edu.njit.qvx.QvxTableHeader;


public class QvxFileReaderNodeSettings extends TokenizerSettings {
    private static final NodeLogger LOGGER = NodeLogger.getLogger(QvxFileReaderNodeSettings.class);

    private QVXReader qvxReader = null;
    private QvxTableHeader qvxTableHeader =  null;
    
    private URL dataFileLocation = null;

    private String rowHeaderPrefix;
    private boolean uniquifyRowIDs;

    
    public static final String DEF_ROWPREFIX = "Row";
    public static final String CFGKEY_DATAURL = "DataURL";
    


    public QvxFileReaderNodeSettings() throws MalformedURLException {
    }

    public QvxFileReaderNodeSettings(final QvxFileReaderNodeSettings clonee) {
        super(clonee);
        dataFileLocation = clonee.dataFileLocation;
        rowHeaderPrefix = clonee.rowHeaderPrefix;
        uniquifyRowIDs = clonee.uniquifyRowIDs;
    }

    public void init() throws MalformedURLException {
        dataFileLocation = null;
        rowHeaderPrefix = null;
        uniquifyRowIDs = false;
    }


    public QvxFileReaderNodeSettings(final NodeSettingsRO cfg)
            throws InvalidSettingsException, MalformedURLException {

        super(cfg);
        if (cfg != null) {
            try {
                URL dataFileLocation = new URL(cfg.getString(CFGKEY_DATAURL));
                setDataFileLocationAndUpdateTableName(dataFileLocation);
            } catch (MalformedURLException mfue) {
                throw new IllegalArgumentException(
                        "Cannot create URL of data file" + " from '"
                                + cfg.getString(CFGKEY_DATAURL)
                                + "' in filereader config", mfue);
            } catch (InvalidSettingsException ice) {
                throw new InvalidSettingsException("Illegal config object for "
                        + "file reader settings! Key '" + CFGKEY_DATAURL
                        + "' missing!", ice);
            }

        }
    }


    @Override
    public void saveToConfiguration(final NodeSettingsWO cfg) {
        if (cfg == null) {
            throw new NullPointerException("Can't save 'file "
                    + "reader settings' to null config!");
        }

        if (dataFileLocation != null) {
            cfg.addString(CFGKEY_DATAURL, dataFileLocation.toString());
        }

        super.saveToConfiguration(cfg);

    }


    public void setDataFileLocationAndUpdateTableName(
            final URL dataFileLocation) {
        this.dataFileLocation = dataFileLocation;
    }


    public URL getDataFileLocation() {
        return dataFileLocation;
    }

	public void setQvxReader(QVXReader qvxReader) {
		this.qvxReader = qvxReader;		
	}
	
	public QVXReader getQvxReader() {
		return this.qvxReader;		
	}

	public void setQvxTableHeader(QvxTableHeader tableHeader) {
		this.qvxTableHeader = tableHeader;
	}

	public QvxTableHeader getQvxTableHeader() {
		return this.qvxTableHeader;
	}


}
