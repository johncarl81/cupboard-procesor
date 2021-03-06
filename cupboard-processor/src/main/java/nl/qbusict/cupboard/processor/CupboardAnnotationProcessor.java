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
package nl.qbusict.cupboard.processor;

import com.google.auto.service.AutoService;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import nl.qbusict.cupboard.processor.internal.CupboardProcessor;
import nl.qbusict.cupboard.processor.internal.ProcessingScope;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.element.ReloadableASTElementFactory;
import org.androidtransfuse.annotations.ScopeReference;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.bootstrap.Bootstraps;
import org.androidtransfuse.config.EnterableScope;
import org.androidtransfuse.scope.ScopeKey;

import javax.annotation.processing.*;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

/**
 * @author John Ericksen
 */
@SupportedAnnotationTypes("*")
@Bootstrap
@AutoService(Processor.class)
public class CupboardAnnotationProcessor extends AbstractProcessor {

    @Inject
    private CupboardProcessor cupboardProcessor;
    @Inject
    private ReloadableASTElementFactory reloadableASTElementFactory;
    @Inject
    @ScopeReference(ProcessingScope.class)
    private EnterableScope processingScope;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Bootstraps.getInjector(CupboardAnnotationProcessor.class)
                .add(Singleton.class, ScopeKey.of(ProcessingEnvironment.class), processingEnv)
                .inject(this);
    }

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {

        processingScope.enter();

        processingScope.seed(ScopeKey.of(RoundEnvironment.class), roundEnvironment);

        cupboardProcessor.submit(EntityClass.class, buildASTCollection(roundEnvironment, EntityClass.class));
        cupboardProcessor.submit(EntityClasses.class, buildASTCollection(roundEnvironment, EntityClasses.class));
        cupboardProcessor.submit(Entity.class, buildASTCollection(roundEnvironment, Entity.class));

        cupboardProcessor.execute();

        if (roundEnvironment.processingOver()) {
            // Throws an exception if errors still exist.
            cupboardProcessor.checkForErrors();
        }

        processingScope.exit();

        return true;
    }

    private Collection<Provider<ASTType>> buildASTCollection(RoundEnvironment round, Class<? extends Annotation> annotation) {
        return reloadableASTElementFactory.buildProviders(
                FluentIterable.from(round.getElementsAnnotatedWith(annotation))
                        .filter(new Predicate<Element>() {
                            public boolean apply(Element element) {
                                //we're only dealing with TypeElements
                                return element instanceof TypeElement;
                            }
                        })
                        .transform(new Function<Element, TypeElement>() {
                            public TypeElement apply(Element element) {
                                return (TypeElement)element;
                            }
                        })
                        .toList());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
