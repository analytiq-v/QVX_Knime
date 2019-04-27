
package edu.njit.knime.adapter.nodes.qvx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Vector;

import org.knime.base.node.util.BufferedFileReader;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.util.tokenizer.Delimiter;
import org.knime.core.util.tokenizer.SettingsStatus;
import org.knime.core.util.tokenizer.TokenizerSettings;

import edu.njit.knime.adapter.qvx.QVXReader;
import edu.njit.knime.adapter.qvx.QvxTableHeader;


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

        } // if (cfg != null)
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
