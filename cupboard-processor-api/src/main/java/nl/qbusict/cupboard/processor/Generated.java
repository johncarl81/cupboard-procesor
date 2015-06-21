package nl.qbusict.cupboard.processor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Copy of the Java Generated annotation (absent in Android implementation)
 *
 * @author John Ericksen
 */
@Documented
@Retention(SOURCE)
@Target({PACKAGE, TYPE, ANNOTATION_TYPE, METHOD, CONSTRUCTOR, FIELD, LOCAL_VARIABLE, PARAMETER})
public @interface Generated {

    /**
     * Identifies the Generator used to generate the annotated class.
     */
    java.lang.String[] value();

    /**
     * Specifies the date in ISO 8601 format when this class was generated.
     */
    java.lang.String date() default "";

    /**
     * Contains any relevant comments.
     */
    java.lang.String comments() default "";
}
