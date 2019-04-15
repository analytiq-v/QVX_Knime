/* Author monica*/
package edu.njit.knime.adapter.nodes.qvx;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.knime.core.data.ConfigurableDataCellFactory;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellFactory.FromString;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.ExecutionContext;


public class QvxDataCellFactory {

 
    private String lastErrorMessage;

    private String formatParameter;

    private final Map<DataType, org.knime.core.data.DataCellFactory> cellFactoryMap = new HashMap<>();

    private final ExecutionContext execContext;


    public QvxDataCellFactory() {
        this(null);
    }


    public QvxDataCellFactory(final ExecutionContext execContext) {
        this.execContext = execContext;
    }

    public final DataCell createDataCellOfType(final DataType type, String data) {

        if (type == null || data == null) {
            throw new NullPointerException(
                    "DataType and the data can't be null.");
        }

        // clear any previous error message
        lastErrorMessage = null;

        org.knime.core.data.DataCellFactory cellFactory = cellFactoryMap.get(type);
        if (cellFactory == null) {
            cellFactory = type.getCellFactory(execContext)
                    .orElseThrow(() -> new IllegalArgumentException("No data cell factory for data type '" + type
                        + "' found"));
            cellFactoryMap.put(type, cellFactory);
        }

        if (cellFactory instanceof ConfigurableDataCellFactory) {
            ((ConfigurableDataCellFactory) cellFactory).configure(formatParameter);
        }



        try {
            return ((FromString) cellFactory).createCell(data);
        } catch (Throwable t) {
            lastErrorMessage = t.getMessage();
            if (lastErrorMessage == null) {
                lastErrorMessage = "No details.";
            }
            return null;
        }
    }

    public String getErrorMessage() {
        return lastErrorMessage;
    }
}
