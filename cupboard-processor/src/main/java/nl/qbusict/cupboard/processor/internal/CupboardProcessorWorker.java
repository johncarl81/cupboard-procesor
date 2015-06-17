/**
 * Copyright 2014-2015 John Ericksen
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
import org.androidtransfuse.adapter.*;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.adapter.element.ASTElementFactory;
import org.androidtransfuse.adapter.element.ElementVisitorAdaptor;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.ClassNamer;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.androidtransfuse.transaction.AbstractCompletionTransactionWorker;
import org.androidtransfuse.util.matcher.Matcher;
import org.androidtransfuse.validation.Validator;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.*;

/**
 * @author John Ericksen
 */
public class CupboardProcessorWorker extends AbstractCompletionTransactionWorker<Provider<ASTType>, JDefinedClass> {

    private final Provider<RoundEnvironment> roundEnvironmentProvider;
    private final ASTElementFactory astElementFactory;
    private final ASTClassFactory astClassFactory;
    private final ASTFactory astFactory;
    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;
    private final JCodeModel codeModel;
    private final Validator validator;

    @Inject
    public CupboardProcessorWorker(Provider<RoundEnvironment> roundEnvironmentProvider,
                                   ASTElementFactory astElementFactory,
                                   ASTClassFactory astClassFactory,
                                   ASTFactory astFactory,
                                   ClassGenerationUtil generationUtil,
                                   UniqueVariableNamer namer,
                                   JCodeModel codeModel,
                                   Validator validator) {
        this.roundEnvironmentProvider = roundEnvironmentProvider;
        this.astElementFactory = astElementFactory;
        this.astClassFactory = astClassFactory;
        this.astFactory = astFactory;
        this.generationUtil = generationUtil;
        this.namer = namer;
        this.codeModel = codeModel;
        this.validator = validator;
    }

    @Override
    public JDefinedClass innerRun(Provider<ASTType> astTypeProvider) {
        ASTType implementation = astTypeProvider.get();

        return null;
    }
}
