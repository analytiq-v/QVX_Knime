package edu.njit.knime.adapter.nodes.qvx;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "QvxAdapter" Node.
 * Reading and writing different qvx file formats and figuring out how to convert them into table format
 *
 * @author Simple Qvx Adapter to Read and Write 
 */
public class QvxAdapterNodeFactory 
        extends NodeFactory<QvxAdapterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public QvxAdapterNodeModel createNodeModel() {
        return new QvxAdapterNodeModel();
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
    public NodeView<QvxAdapterNodeModel> createNodeView(final int viewIndex,
            final QvxAdapterNodeModel nodeModel) {
        return new QvxAdapterNodeView(nodeModel);
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
        return new QvxAdapterNodeDialog();
    }

}