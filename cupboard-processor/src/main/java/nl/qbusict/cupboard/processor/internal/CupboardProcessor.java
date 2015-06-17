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
import org.androidtransfuse.transaction.ScopedTransactionBuilder;
import org.androidtransfuse.transaction.TransactionProcessorPool;

import javax.inject.Provider;
import java.util.Collection;

/**
 * @author John Ericksen
 */
public class CupboardProcessor {

    private final TransactionProcessorPool<Provider<ASTType>, JDefinedClass> silverProcessor;
    private final Provider<CupboardProcessorWorker> silverTransactionFactory;
    private final ScopedTransactionBuilder scopedTransactionBuilder;

    public CupboardProcessor(TransactionProcessorPool<Provider<ASTType>, JDefinedClass> silverProcessor,
                             Provider<CupboardProcessorWorker> silverTransactionFactory,
                             ScopedTransactionBuilder scopedTransactionBuilder) {
        this.silverProcessor = silverProcessor;
        this.silverTransactionFactory = silverTransactionFactory;
        this.scopedTransactionBuilder = scopedTransactionBuilder;
    }

    public void submit(Collection<Provider<ASTType>> astProviders) {
        for (Provider<ASTType> astProvider : astProviders) {
            silverProcessor.submit(scopedTransactionBuilder.build(astProvider, silverTransactionFactory));
        }
    }

    public void execute() {
        silverProcessor.execute();
    }

    public void checkForErrors() {
        if (!silverProcessor.isComplete()) {
            //throw new TransfuseAnalysisException("@Silver code generation did not complete successfully.", processor.getErrors());
        }
    }
}
