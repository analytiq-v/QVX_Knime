package edu.njit.knime.adapter.nodes.qvx;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;

/**
 * <code>NodeDialog</code> for the "Qvx" Node.
 * Qvx Node
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Monica
 */
public class QvxNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring Qvx node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected QvxNodeDialog() {
        super();

    	addDialogComponent(new DialogComponentFileChooser(
    			new SettingsModelString(QvxNodeModel.CFGKEY_FILE_PATH,
    					QvxNodeModel.DEFAULT_PATH),
    			"Qvx file", JFileChooser.OPEN_DIALOG, ".qvx"));             
    }
}

