package edu.njit.qvxreader;

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
public class QvxReaderNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring Qvx node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected QvxReaderNodeDialog() {
        super();

    	addDialogComponent(new DialogComponentFileChooser(
    			new SettingsModelString(QvxReaderNodeModel.CFGKEY_FILE_PATH,
    					QvxReaderNodeModel.DEFAULT_PATH),
    			"Qvx file", JFileChooser.OPEN_DIALOG, ".qvx"));             
    }
}

