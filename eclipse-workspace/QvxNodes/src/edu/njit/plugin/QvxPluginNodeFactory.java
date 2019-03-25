package edu.njit.plugin;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "QvxPlugin" Node.
 * 
 *
 * @author 
 */
public class QvxPluginNodeFactory 
        extends NodeFactory<QvxPluginNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public QvxPluginNodeModel createNodeModel() {
        return new QvxPluginNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<QvxPluginNodeModel> createNodeView(final int viewIndex,
            final QvxPluginNodeModel nodeModel) {
        return new QvxPluginNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new QvxPluginNodeDialog();
    }

}

