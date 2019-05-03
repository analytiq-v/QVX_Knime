package edu.njit.qvxreader;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowIterator;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.core.util.tokenizer.SettingsStatus;

public class QvxFileTable implements DataTable {

    private static final NodeLogger LOGGER =NodeLogger.getLogger(QvxFileTable.class);

    private final DataTableSpec tableSpec;

    public final QvxFileReaderNodeSettings qvxSettings;

    private final ExecutionContext exec;

    private final boolean[] skipColums;

    private final LinkedList<WeakReference<QvxFileRowIterator>> iterators;

    public QvxFileTable(final DataTableSpec tableSpec,
            final QvxFileReaderNodeSettings frSettings, final ExecutionContext exec) {
        this(tableSpec, frSettings,
                createFalseArray(tableSpec.getNumColumns()), exec);
    }

    public QvxFileTable(final DataTableSpec tableSpec,
            final QvxFileReaderNodeSettings frSettings, final boolean[] skipColumns,
            final ExecutionContext exec) {

        if ((tableSpec == null) || (frSettings == null)) {
            throw new NullPointerException("Must specify non-null table spec"
                    + " and file reader settings for file table.");
        }
        if (skipColumns.length < tableSpec.getNumColumns()) {
            throw new IllegalArgumentException("The number of columns can't"
                    + " be larger than the spec for columns to skip");
        }
        int cols = 0;
        for (boolean b : skipColumns) {
            if (!b) {
                cols++;
            }
        }
        if (cols != tableSpec.getNumColumns()) {
            throw new IllegalArgumentException("The number of columns to "
                    + "include is different from the number of columns in the"
                    + " table spec.");
        }
        iterators = new LinkedList<WeakReference<QvxFileRowIterator>>();
        this.tableSpec = tableSpec;
        qvxSettings = frSettings;
        skipColums = skipColumns;
        this.exec = exec;

    }

    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    public void dispose() {
        synchronized (iterators) {
            for (WeakReference<QvxFileRowIterator> w : iterators) {
            	QvxFileRowIterator i = w.get();
                if (i != null) {
                    i.dispose();
                }
            }
            iterators.clear();
        }
    }

    private static boolean[] createFalseArray(final int length) {
        boolean[] result = new boolean[length];
        Arrays.fill(result, false);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public QvxFileRowIterator iterator() {
        try {
        	qvxSettings.init();
            synchronized (iterators) {
            	QvxFileRowIterator i =
                        new QvxFileRowIterator(qvxSettings, tableSpec,
                                skipColums, exec);
                iterators.add(new WeakReference<QvxFileRowIterator>(i));
                return i;

            }
        } catch (IOException ioe) {
            LOGGER.error("Row Iterator Exception" + ioe.getMessage());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public DataTableSpec getDataTableSpec() {
        return tableSpec;
    }

    public SettingsStatus getStatusOfSettings(final boolean openDataFile) {

        SettingsStatus status = new SettingsStatus();

        addStatusOfSettings(status, openDataFile);

        return status;
    }


    public void addStatusOfSettings(final SettingsStatus status,
            final boolean openDataFile) {

        if (tableSpec == null) {
            status.addError("DataTableSpec not set!");
        }
        if (qvxSettings == null) {
            status.addError("FileReader settings not set!");
        } else {
            // we do that here - still.
            addTableSpecStatusOfSettings(status, tableSpec);
        }

    }

    private void addTableSpecStatusOfSettings(final SettingsStatus status,
            final DataTableSpec tableSpec) {
        // check the number of columns. Must be set to a number > 0.
        if (tableSpec.getNumColumns() < 1) {
            status.addError("Number of columns must be greater than zero.");
        }

        // hash map for faster column name uniqueness checking
        HashMap<String, Integer> colNames = new HashMap<String, Integer>();

        // we need a column spec for each column - and if set we need types,
        // names, and if possible values are set they must not be null.
        for (int c = 0; c < tableSpec.getNumColumns(); c++) {

            if (tableSpec.getColumnSpec(c) == null) {
                status.addError("Column spec for column with index '" + c
                        + "' is not set.");
            } else {
                // check col type
                DataColumnSpec cSpec = tableSpec.getColumnSpec(c);
                DataType cType = cSpec.getType();
                if (cType == null) {
                    status.addError("Column type for column with index '" + c
                            + "' is not set.");
                } else {
                    if (!DataType.class.isAssignableFrom(cType.getClass())) {
                        status.addError("The type of the column with index '"
                                + c + "' is not derived from DataType.");
                    }
                }

                // check col name
                String cName = cSpec.getName();
                if (cName == null) {
                    status.addError("Column name for column with index '" + c
                            + "' is not set.");
                } else {
                    // make sure it's unique
                    Integer sameCol = colNames.put(cName, c);
                    if (sameCol != null) {
                        status.addError("Column with index " + c
                                + " has the same name as column " + sameCol
                                + " ('" + cName + "').");
                    }
                }

                // check the possible values, in case they are set.
                Set<DataCell> values = cSpec.getDomain().getValues();
                if (values == null) {
                    status.addInfo("No possible values set for column with"
                            + " index '" + c + "'.");
                } else {
                    if (values.size() == 0) {
                        status.addWarning("The container for the possible "
                                + " values of the column with index '" + c
                                + "' is empty!");
                    }
                    // if set they must be not null
                    for (DataCell v : values) {
                        if (v == null) {
                            status.addError("One of the possible values set"
                                    + " for the column with index '" + c
                                    + "' is" + " null.");
                            // adding this message once for each col is enough
                            break;
                        }
                    }
                }
            } // end of if column spec is not null
        } // end of for all columns

    } // addTableSpecStatusOfSettings(SettingsStatus, DataTableSpec)

     @Override
    public String toString() {
        // maximum number of chars to print
        final int colLength = 15;
        RowIterator rowIterator = iterator();
        DataRow row;
        StringBuffer result = new StringBuffer();

        // Create a column header
        // Cell (0,0)
        result.append(sprintDataCell(" ", colLength));
        // "<ColName>[Type]"
        for (int i = 0; i < tableSpec.getNumColumns(); i++) {
            if (tableSpec.getColumnSpec(i).getType().equals(
                    StringCell.TYPE)) {
                result.append(sprintDataCell(
                        tableSpec.getColumnSpec(i).getName().toString()
                        + "[Str]", colLength));
            } else if (tableSpec.getColumnSpec(i).getType().equals(
                    IntCell.TYPE)) {
                result.append(sprintDataCell(
                        tableSpec.getColumnSpec(i).getName().toString()
                        + "[Int]", colLength));
            } else if (tableSpec.getColumnSpec(i).getType().equals(
                    DoubleCell.TYPE)) {
                result.append(sprintDataCell(
                        tableSpec.getColumnSpec(i).getName().toString()
                        + "[Dbl]", colLength));
            } else {
                result.append(sprintDataCell(
                        tableSpec.getColumnSpec(i).getName().toString()
                        + "[UNKNOWN!!]", colLength));
            }
        }
        result.append("\n");
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            result.append(sprintDataCell(row.getKey().getString(), colLength));
            for (int i = 0; i < row.getNumCells(); i++) {
                result.append(
                        sprintDataCell(row.getCell(i).toString(), colLength));
            }
            result.append("\n");
        }
        return result.toString();
    } // toString()

     private static String sprintDataCell(final String dc, final int length) {
        assert (dc != null);
        // the final string, with all the spaces
        final StringBuffer result = new StringBuffer(dc);
        for (int i = result.length(); i < length; i++) {
            result.append(" ");
        }
        return result.toString();
    }
}
