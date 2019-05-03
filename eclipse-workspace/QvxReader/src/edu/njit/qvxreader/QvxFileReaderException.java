
package edu.njit.qvxreader;

import org.knime.core.data.DataRow;

public class QvxFileReaderException extends RuntimeException {

    private final DataRow row;

    QvxFileReaderException(final String msg) {
        super(msg);
        row = null;
    }

    public QvxFileReaderException(final String msg, final DataRow faultyRow) {
        super(msg);
        row = faultyRow;
    }

    public DataRow getErrorRow() {
        return row;
    }

}

