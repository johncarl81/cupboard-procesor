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
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class CupboardDescriptor {

    private final ASTType type;

    private FieldColumn id;
    private ImmutableList<FieldColumn> columns;

    public CupboardDescriptor(ASTType type, FieldColumn id, ImmutableList<FieldColumn> columns) {
        this.type = type;
        this.id = id;
        this.columns = columns;
    }

    public FieldColumn getId() {
        return id;
    }

    public ImmutableList<FieldColumn> getColumns() {
        return columns;
    }

    public ASTType getType() {
        return type;
    }
}
