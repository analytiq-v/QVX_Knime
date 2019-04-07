
package edu.njit.knime.adapter.nodes.qvx;

import org.knime.core.data.DataRow;

/**
 * The exception the {@link java.io.FileReader} (more specificaly the
 * {@link org.knime.base.node.io.filereader.FileRowIterator}) throws if
 * something goes wrong.
 *
 * This is a runtime exception for now.
 *
 * @author Peter Ohl, University of Konstanz
 */
public class QvxFileReaderException extends RuntimeException {

    private final DataRow m_row;

    private final int m_lineNumber;

    private String m_detailsMsg;

    /**
     * Always provide a good user message why things go wrong.
     *
     * @param msg the message to store in the exception
     */
    QvxFileReaderException(final String msg) {
        super(msg);
        m_row = null;
        m_lineNumber = -1;
        m_detailsMsg = null;
    }

    /**
     * Constructor for an exception that stores the last (partial) row where
     * things went wrong.
     *
     * @param msg the message what went wrong
     * @param faultyRow the row as far as it got read
     * @param lineNumber the lineNumber the error occurred
     * @since 2.11
     */
    public QvxFileReaderException(final String msg, final DataRow faultyRow,
            final int lineNumber) {
        super(msg);
        m_row = faultyRow;
        m_lineNumber = lineNumber;
    }

    /**
     * @return the row that was (possibly partially!) read before things went
     *         wrong. Could be <code>null</code>, if not set.
     * @since 2.11
     */
    public DataRow getErrorRow() {
        return m_row;
    }

    /**
     * @return the line number where the error occurred in the file. Could be -1
     *         if not set.
     * @since 2.11
     */
    public int getErrorLineNumber() {
        return m_lineNumber;
    }

    /**
     * Sets an additional message.
     *
     * @param msg the additional message
     */
    void setDetailsMessage(final String msg) {
        m_detailsMsg = msg;
    }

    /**
     * @return the previously set message, or null.
     * @since 2.11
     */
    public String getDetailedMessage() {
        return m_detailsMsg;
    }
}

