package edu.njit.knime.adapter.nodes.qvx;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "Qvx" Node.
 * Qvx Node
 *
 * @author Monica
 */
public class QvxNodeFactory 
        extends NodeFactory<QvxNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public QvxNodeModel createNodeModel() {
        return new QvxNodeModel();
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
    public NodeView<QvxNodeModel> createNodeView(final int viewIndex,
            final QvxNodeModel nodeModel) {
        return new QvxNodeView(nodeModel);
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
        return new QvxNodeDialog();
    }

}

