package clj_yaml;

import clojure.lang.AFn;
import clojure.lang.IPersistentMap;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;

/**
 * An unsafe yaml constructor with extension tags and optional marking.
 */
public class ExtensibleConstructor extends Constructor {

    /**
     * @see clj_yaml.Extensibility#initTags
     */
    public ExtensibleConstructor(IPersistentMap tags, boolean mark) {
        super();
        Extensibility.initTags(this.yamlConstructors, tags, mark);
    }

}