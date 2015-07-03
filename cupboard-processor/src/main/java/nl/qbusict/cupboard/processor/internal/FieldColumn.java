package nl.qbusict.cupboard.processor.internal;

import org.androidtransfuse.adapter.ASTField;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class FieldColumn {

    private final ASTType type;
    private final ASTField field;
    private final String column;

    public FieldColumn(ASTType type, ASTField field, String column) {
        this.type = type;
        this.field = field;
        this.column = column;
    }
}
