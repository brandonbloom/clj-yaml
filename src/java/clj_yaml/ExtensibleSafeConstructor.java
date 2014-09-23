package clj_yaml;

import clojure.lang.AFn;
import clojure.lang.IPersistentMap;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;

/**
 * An safe yaml constructor with extension tags and optional marking.
 */
public class ExtensibleSafeConstructor extends SafeConstructor {

    /**
     * @see clj_yaml.Extensibility#initTags
     */
    public ExtensibleSafeConstructor(IPersistentMap tags, boolean mark) {
        super();
        Extensibility.initTags(this.yamlConstructors, tags, mark);
    }

}