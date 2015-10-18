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
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.internal.convert.DefaultFieldConverterFactory;
import org.androidtransfuse.adapter.ASTPrimitiveType;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.ClassNamer;
import org.androidtransfuse.gen.UniqueVariableNamer;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shannon Kinkead
 */
public class CupboardGenerator {

    private static final Map<ASTType, EntityConverter.ColumnType> TYPE_MAP = new HashMap<ASTType, EntityConverter.ColumnType>();
    private static final Map<ASTType, String> FROM_CURSOR_METHOD_MAP = new HashMap<ASTType, String>();

    private final JCodeModel codeModel;
    private final ClassGenerationUtil classGenerationUtil;
    private final UniqueVariableNamer variableNamer;

    static {
        TYPE_MAP.put(ASTPrimitiveType.BOOLEAN, EntityConverter.ColumnType.INTEGER);
        TYPE_MAP.put(ASTPrimitiveType.BYTE, EntityConverter.ColumnType.INTEGER);
        TYPE_MAP.put(ASTPrimitiveType.CHAR, EntityConverter.ColumnType.INTEGER);
        TYPE_MAP.put(ASTPrimitiveType.DOUBLE, EntityConverter.ColumnType.REAL);
        TYPE_MAP.put(ASTPrimitiveType.FLOAT, EntityConverter.ColumnType.REAL);
        TYPE_MAP.put(ASTPrimitiveType.INT, EntityConverter.ColumnType.INTEGER);
        TYPE_MAP.put(ASTPrimitiveType.LONG, EntityConverter.ColumnType.INTEGER);
        TYPE_MAP.put(ASTPrimitiveType.SHORT, EntityConverter.ColumnType.INTEGER);
        TYPE_MAP.put(new ASTStringType(String.class.getCanonicalName()), EntityConverter.ColumnType.TEXT);

        FROM_CURSOR_METHOD_MAP.put(ASTPrimitiveType.BOOLEAN, "getInt");
        FROM_CURSOR_METHOD_MAP.put(ASTPrimitiveType.BYTE, "getInt");
        FROM_CURSOR_METHOD_MAP.put(ASTPrimitiveType.CHAR, "getInt");
        FROM_CURSOR_METHOD_MAP.put(ASTPrimitiveType.DOUBLE, "getDouble");
        FROM_CURSOR_METHOD_MAP.put(ASTPrimitiveType.FLOAT, "getFloat");
        FROM_CURSOR_METHOD_MAP.put(ASTPrimitiveType.INT, "getInt");
        FROM_CURSOR_METHOD_MAP.put(ASTPrimitiveType.LONG, "getLong");
        FROM_CURSOR_METHOD_MAP.put(ASTPrimitiveType.SHORT, "getShort");
        FROM_CURSOR_METHOD_MAP.put(new ASTStringType(String.class.getCanonicalName()), "getString");
    }

    @Inject
    public CupboardGenerator(JCodeModel codeModel, ClassGenerationUtil classGenerationUtil, UniqueVariableNamer variableNamer) {
        this.codeModel = codeModel;
        this.classGenerationUtil = classGenerationUtil;
        this.variableNamer = variableNamer;
    }

    public JDefinedClass generateEntityConverter(ASTType type, CupboardDescriptor descriptor) {
        try {
            JType jType = classGenerationUtil.ref(type);
            JDefinedClass converterClass = classGenerationUtil.defineClass(ClassNamer.className(type).append("EntityConverter").build());
            converterClass._implements(classGenerationUtil.ref(EntityConverter.class).narrow(jType));

            JFieldVar cupboard = converterClass.field(JMod.PRIVATE, Cupboard.class, variableNamer.generateName(Cupboard.class));
            JFieldVar columns = converterClass.field(JMod.PRIVATE, classGenerationUtil.ref(List.class).narrow(EntityConverter.Column.class), variableNamer.generateName(classGenerationUtil.ref(List.class).narrow(EntityConverter.Column.class)));

            JMethod constructor = converterClass.constructor(JMod.PUBLIC);
            JVar constructorCupboard = constructor.param(Cupboard.class, variableNamer.generateName(Cupboard.class));
            JBlock constructorBlock = constructor.body();
            constructorBlock.assign(cupboard, constructorCupboard);
            for (FieldColumn fieldColumn : descriptor.getColumns()) {
                JVar column = constructorBlock.decl(classGenerationUtil.ref(EntityConverter.Column.class), variableNamer.generateName(EntityConverter.Column.class));
                column.assign(JExpr._new(classGenerationUtil.ref(EntityConverter.Column.class)).arg(JExpr.lit(fieldColumn.getColumn())).arg(codeModel.ref(EntityConverter.ColumnType.class).staticRef(TYPE_MAP.get(fieldColumn.getType()).name())));
                constructorBlock.invoke(columns, "add").arg(column);
            }

            JMethod fromCursor = converterClass.method(JMod.PUBLIC, jType, "fromCursor");
            JVar cursor = fromCursor.param(classGenerationUtil.ref("android.database.Cursor"), "cursor");
            fromCursor.annotate(Override.class);
            JBlock fromCursorBlock = fromCursor.body();
            JVar result = fromCursorBlock.decl(jType, variableNamer.generateName(jType), JExpr._new(jType));
            for (int i = 0; i < descriptor.getColumns().size(); i++) {
                FieldColumn fieldColumn = descriptor.getColumns().get(i);
                JInvocation expr = codeModel.ref(DefaultFieldConverterFactory.class).staticInvoke("create").arg(cupboard).arg(codeModel._getClass(String.class.getCanonicalName()).staticRef("class"));
                JVar fieldConverter = fromCursorBlock.decl(codeModel.ref(FieldConverter.class), variableNamer.generateName(FieldConverter.class), expr);
                JConditional fieldConverterCheck = fromCursorBlock._if(fieldConverter.eq(JExpr._null()));
                JBlock fieldConverterCheckBody = fieldConverterCheck._then();
                fieldConverterCheckBody.assign(result.ref(fieldColumn.getField().getName()), fieldConverter.invoke("fromCursorValue").arg(cursor.invoke(FROM_CURSOR_METHOD_MAP.get(fieldColumn.getType())).arg(JExpr.lit(i))));
            }

            fromCursorBlock._return(result);

            JMethod toValues = converterClass.method(JMod.PUBLIC, codeModel.VOID, "toValues");
            JVar objectVar = toValues.param(jType, "object");
            JVar contentValuesVar = toValues.param(classGenerationUtil.ref("android.content.ContentValues"), "values");
            toValues.annotate(Override.class);
            JBlock toValuesBlock = toValues.body();
            for (FieldColumn column : descriptor.getColumns()) {
                toValuesBlock.add(contentValuesVar.invoke("put").arg(JExpr.lit(column.getColumn())).arg(objectVar.ref(column.getField().getName())));
            }

            JMethod getColumns = converterClass.method(JMod.PUBLIC, classGenerationUtil.ref(List.class).narrow(EntityConverter.Column.class), "getColumns");
            getColumns.annotate(Override.class);
            JBlock getColumnsBlock = getColumns.body();
            getColumnsBlock._return(columns);

            JMethod setId = converterClass.method(JMod.PUBLIC, codeModel.VOID, "setId");
            setId.param(Long.class, "id");
            setId.param(jType, "instance");
            setId.annotate(Override.class);

            JMethod getId = converterClass.method(JMod.PUBLIC, classGenerationUtil.ref(Long.class), "getId");
            JVar instanceVar = getId.param(jType, "instance");
            getId.annotate(Override.class);
            JBlock getIdBlock = getId.body();
            getIdBlock._return(instanceVar.ref(descriptor.getId().getField().getName()));

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
