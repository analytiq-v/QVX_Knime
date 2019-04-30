package edu.njit.qvxwriter;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "QvxWriter" Node.
 * 
 *
 * @author 
 */
public class QvxWriterNodeView extends NodeView<QvxWriterNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link QvxWriterNodeModel})
     */
    protected QvxWriterNodeView(final QvxWriterNodeModel nodeModel) {
        super(nodeModel);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {

        QvxWriterNodeModel nodeModel = 
            (QvxWriterNodeModel)getNodeModel();
        assert nodeModel != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {

    }

}

