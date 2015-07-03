/**
 * Copyright 2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.qbusict.cupboard.processor.internal;

import com.google.common.collect.ImmutableList;
import nl.qbusict.cupboard.annotation.Column;
import org.androidtransfuse.adapter.ASTField;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class CupboardAnalysis {
    public CupboardDescriptor analyze(ASTType value) {

        ImmutableList.Builder<FieldColumn> columns = ImmutableList.builder();
        FieldColumn idColumn = null;

        for (ASTType hierarchy = value; hierarchy != null; hierarchy = hierarchy.getSuperClass()) {
            for (ASTField field : hierarchy.getFields()) {
                String columnName = field.getName();
                if(field.isAnnotated(Column.class)){
                    columnName = field.getAnnotation(Column.class).value();
                }

                FieldColumn fieldColumn = new FieldColumn(hierarchy, field, columnName);
                if(columnName.equals("_id")){
                    idColumn = fieldColumn;
                }
                columns.add(fieldColumn);
            }
        }


        return new CupboardDescriptor(value, idColumn, columns.build());
    }
}
