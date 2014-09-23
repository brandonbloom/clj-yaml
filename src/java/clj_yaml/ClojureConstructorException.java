package clj_yaml;

import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.Mark;

/**
 * Simply wraps a ConstructorException so that we can create one for custom errors with marking.
 */
public class ClojureConstructorException extends ConstructorException {

    protected ClojureConstructorException(String context, Mark contextMark, String problem, Mark problemMark, Throwable cause) {
        super(context, contextMark, problem, problemMark, cause);
    }

    protected ClojureConstructorException(String context, Mark contextMark, String problem, Mark problemMark) {
        super(context, contextMark, problem, problemMark);
    }

}
