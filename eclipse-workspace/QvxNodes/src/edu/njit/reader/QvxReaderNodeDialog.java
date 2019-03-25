package edu.njit.reader;

import edu.njit.util.Util;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "QvxReader" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class QvxReaderNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring QvxReader node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected QvxReaderNodeDialog() {
        super();
        
        /* Test of QvxReader class */
        Object[][] results = QvxReader.readFromQvx(Util.PROJECT_DIR + "products.qvx");
        for(int i = 0; i < results.length; i++) {
        	for(int j = 0; j < results.length; j++) {
        		System.out.print(results[i][j]);
        		System.out.print(" ");
        	}
        	System.out.println();
        }
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    QvxReaderNodeModel.CFGKEY_COUNT,
                    QvxReaderNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

