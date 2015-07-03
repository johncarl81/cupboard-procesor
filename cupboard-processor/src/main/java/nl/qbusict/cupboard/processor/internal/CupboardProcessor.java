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

import com.sun.codemodel.JDefinedClass;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.transaction.ScopedTransactionBuilder;
import org.androidtransfuse.transaction.TransactionProcessorPool;
import org.androidtransfuse.util.Logger;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * @author John Ericksen
 */
public class CupboardProcessor {

    private final Logger log;
    private final TransactionProcessorPool<Provider<ASTType>, JDefinedClass> cupboardProcessor;
    private final Provider<CupboardProcessorWorker> cupboardTransactionFactory;
    private final ScopedTransactionBuilder scopedTransactionBuilder;

    public CupboardProcessor(Logger log,
                             TransactionProcessorPool<Provider<ASTType>, JDefinedClass> cupboardProcessor,
                             Provider<CupboardProcessorWorker> cupboardTransactionFactory,
                             ScopedTransactionBuilder scopedTransactionBuilder) {
        this.log = log;
        this.cupboardProcessor = cupboardProcessor;
        this.cupboardTransactionFactory = cupboardTransactionFactory;
        this.scopedTransactionBuilder = scopedTransactionBuilder;
    }

    public void submit(Class<? extends Annotation> annotation, Collection<Provider<ASTType>> astProviders) {
        for (Provider<ASTType> astProvider : astProviders) {
            cupboardProcessor.submit(scopedTransactionBuilder.build(astProvider, cupboardTransactionFactory));
        }
    }

    public void execute() {
        cupboardProcessor.execute();
    }

    public void checkForErrors() {
        if (!cupboardProcessor.isComplete()) {
            log.error("Code generation did not complete successfully.");
        }
    }
}
