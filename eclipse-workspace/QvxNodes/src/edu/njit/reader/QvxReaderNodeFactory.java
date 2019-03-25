package edu.njit.reader;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "QvxReader" Node.
 * 
 *
 * @author 
 */
public class QvxReaderNodeFactory 
        extends NodeFactory<QvxReaderNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public QvxReaderNodeModel createNodeModel() {
        return new QvxReaderNodeModel();
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
    public NodeView<QvxReaderNodeModel> createNodeView(final int viewIndex,
            final QvxReaderNodeModel nodeModel) {
        return new QvxReaderNodeView(nodeModel);
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
        return new QvxReaderNodeDialog();
    }

}

