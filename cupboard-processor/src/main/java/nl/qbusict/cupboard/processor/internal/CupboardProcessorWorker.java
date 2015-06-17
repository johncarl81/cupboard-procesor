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

import com.sun.codemodel.JDefinedClass;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.transaction.AbstractCompletionTransactionWorker;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author John Ericksen
 */
public class CupboardProcessorWorker extends AbstractCompletionTransactionWorker<Provider<ASTType>, JDefinedClass> {

    private final CupboardAnalysis cupboardAnalysis;
    private final CupboardGenerator cupboardGenerator;

    @Inject
    public CupboardProcessorWorker(CupboardAnalysis cupboardAnalysis, CupboardGenerator cupboardGenerator) {
        this.cupboardAnalysis = cupboardAnalysis;
        this.cupboardGenerator = cupboardGenerator;
    }

    @Override
    public JDefinedClass innerRun(Provider<ASTType> astTypeProvider) {
        ASTType value = astTypeProvider.get();

        CupboardDescriptor analysis = cupboardAnalysis.analyze(value);

        if(analysis != null) {
            return cupboardGenerator.generateEntityConverter(value, analysis);
        }
        return null;
    }
}
