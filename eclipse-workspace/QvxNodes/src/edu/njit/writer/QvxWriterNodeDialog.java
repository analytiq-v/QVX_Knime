package edu.njit.writer;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

import edu.njit.qvx.QvxTableHeader;
import edu.njit.util.Util;
import edu.njit.writer.QvxWriter;

/**
 * <code>NodeDialog</code> for the "QvxWriter" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class QvxWriterNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring QvxWriter node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected QvxWriterNodeDialog() {
        super();
        
        /* Testing of the "QvxWriter" class */
        QvxTableHeader tableHeader = QvxWriter.defaultTableHeader();
		QvxWriter.writeQvxFromCsv("products.csv", Util.PROJECT_DIR + "products.qvx", tableHeader);
		
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    QvxWriterNodeModel.CFGKEY_COUNT,
                    QvxWriterNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

