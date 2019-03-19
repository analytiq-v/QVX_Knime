package edu.njit.knime.adapter.nodes.qvx;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "QvxAdapter" Node.
 * Reading and writing different qvx file formats and figuring out how to convert them into table format
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Simple Qvx Adapter to Read and Write 
 */
public class QvxAdapterNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring QvxAdapter node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected QvxAdapterNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    QvxAdapterNodeModel.CFGKEY_COUNT,
                    QvxAdapterNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

