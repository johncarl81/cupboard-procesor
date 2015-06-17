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

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.ClassNamer;

import javax.inject.Inject;

/**
 * @author John Ericksen
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
            JDefinedClass converterClass = classGenerationUtil.defineClass(ClassNamer.className(type).append("$$EntityConverter").build());
            converterClass._implements(classGenerationUtil.ref("nl.qbusict.cupboard.convert.EntityConverter").narrow(jType));


            return converterClass;
        } catch (JClassAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }
}
