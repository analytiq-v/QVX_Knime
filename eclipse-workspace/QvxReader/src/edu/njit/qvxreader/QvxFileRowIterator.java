
package edu.njit.qvxreader;

import java.io.IOException;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;


class QvxFileRowIterator extends CloseableRowIterator {


    private final QvxFileReaderNodeSettings qvxSettings;

    private final DataTableSpec tableSpec;

    private String rowHeaderPrefix = "";

    private ExecutionContext exec;

    QvxFileRowIterator(final QvxFileReaderNodeSettings frSettings,
            final DataTableSpec tableSpec, final boolean[] skipColumns,
            final ExecutionContext exec) throws IOException {

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

        this.tableSpec = tableSpec;
        this.qvxSettings = frSettings;

        this.exec = exec;
        rowHeaderPrefix = QvxFileReaderNodeSettings.DEF_ROWPREFIX;

    } 

     @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void dispose() {

    }

    @Override
    public void close() {
        dispose();
    }

    private int rowCount = 0;
 
    @Override
    public boolean hasNext() {

        if(qvxSettings.getQvxReader().getTableData()[0].iterator().hasNext())
        	return true;
		else
			return false;

    }

    @Override
    public DataRow next() {

        String rowHeader;

        rowHeader = createRowHeader(rowCount);     
        
        return  qvxSettings.getQvxReader().getTableData()[0].iterator().next();

    } 

    private String createRowHeader(final int rowNumber) {

        String fileHeader = null;
 
        if (rowHeaderPrefix == null) 

        	rowHeaderPrefix =  QvxFileReaderNodeSettings.DEF_ROWPREFIX;


        return rowHeaderPrefix + rowNumber;
    }


    private QvxFileReaderException prepareForException(final String msg, final String rowHeader, final DataCell[] cellsRead) {

        DataCell[] errCells = new DataCell[cellsRead.length];
        System.arraycopy(cellsRead, 0, errCells, 0, errCells.length);

        for (int c = 0; c < errCells.length; c++) {
            if (errCells[c] == null) {
                errCells[c] = DataType.getMissingCell();
            }
        }

        String errRowHeader = "ERROR_ROW (" + rowHeader.toString() + ")";

        DataRow errRow = new DefaultRow(errRowHeader, errCells);

        return new QvxFileReaderException(msg, errRow);

    }




}
