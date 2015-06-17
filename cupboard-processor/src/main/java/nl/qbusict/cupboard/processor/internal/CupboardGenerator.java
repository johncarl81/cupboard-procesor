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

import com.sun.codemodel.*;
import nl.qbusict.cupboard.annotation.Column;
import nl.qbusict.cupboard.convert.EntityConverter;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.ClassNamer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shannon Kinkead
 */
public class CupboardGenerator {

    private final JCodeModel codeModel;
    private final ClassGenerationUtil classGenerationUtil;

    @Inject
    public CupboardGenerator(JCodeModel codeModel, ClassGenerationUtil classGenerationUtil) {
        this.codeModel = codeModel;
        this.classGenerationUtil = classGenerationUtil;
    }

    public JDefinedClass generateEntityConverter(ASTType type, CupboardDescriptor descriptor) {
        try {
            JType jType = classGenerationUtil.ref(type);
            JDefinedClass converterClass = classGenerationUtil.defineClass(ClassNamer.className(type).append("EntityConverter").build());
            converterClass._implements(classGenerationUtil.ref(EntityConverter.class).narrow(jType));

            JMethod fromCursor = converterClass.method(JMod.PUBLIC, jType, "fromCursor");
            fromCursor.param(classGenerationUtil.ref("android.database.Cursor"), "cursor");
            fromCursor.annotate(Override.class);
            JBlock fromCursorBlock = fromCursor.body();
            fromCursorBlock._return(JExpr._new(jType));

            JMethod toValues = converterClass.method(JMod.PUBLIC, codeModel.VOID, "toValues");
            toValues.param(jType, "object");
            toValues.param(classGenerationUtil.ref("android.content.ContentValues"), "values");
            toValues.annotate(Override.class);

            JMethod getColumns = converterClass.method(JMod.PUBLIC, classGenerationUtil.ref(List.class).narrow(Column.class), "getColumns");
            getColumns.annotate(Override.class);
            JBlock getColumnsBlock = getColumns.body();
            getColumnsBlock._return(JExpr._new(classGenerationUtil.ref(ArrayList.class).narrow(Column.class)));

            JMethod setId = converterClass.method(JMod.PUBLIC, codeModel.VOID, "setId");
            setId.param(Long.class, "id");
            setId.param(jType, "instance");
            setId.annotate(Override.class);

            JMethod getId = converterClass.method(JMod.PUBLIC, classGenerationUtil.ref(Long.class), "getId");
            getId.param(jType, "instance");
            getId.annotate(Override.class);
            JBlock getIdBlock = getId.body();
            getIdBlock._return(JExpr.lit(4l));

            JMethod getTable = converterClass.method(JMod.PUBLIC, classGenerationUtil.ref(String.class), "getTable");
            getTable.annotate(Override.class);
            JBlock getTableBlock = getTable.body();
            getTableBlock._return(classGenerationUtil.ref(type).dotclass().invoke("getSimpleName"));

            return converterClass;
        } catch (JClassAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }
}
