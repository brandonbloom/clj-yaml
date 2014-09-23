package clj_yaml;

import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
import clojure.lang.ISeq;
import clojure.lang.RT;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.*;

import java.util.Map;


/**
 * Support code for tag-extensible yaml constructor subclasses.
 */
class Extensibility {

    /**
     * Mutates yamlConstructors by adding extension tags and potentially wrapping with marking.
     * @param yamlConstructors Correspoinding protected field map from a BaseConstructor.
     * @param tags A map of strings to construction functions.
     * @param mark True if constructors should be wrapped with Marking data.
     */
    static void initTags(final Map<Tag, Construct> yamlConstructors,
                         final IPersistentMap tags,
                         final boolean mark) {

        for (ISeq xs = RT.seq(tags); xs != null; xs = xs.next()) {

            final Map.Entry entry = (Map.Entry) xs.first();
            final Tag tag = new Tag((String) entry.getKey());
            final IFn f = (IFn) entry.getValue();

            Construct construct = new Construct() {
                @Override
                public Object construct(Node node) {
                    return f.invoke(nodeValue(node));
                }
                @Override
                public void construct2ndStep(Node node, Object o) {
                    throw new UnsupportedOperationException("cyclic references in extension tags are not supported");
                }
            };

            if (mark) {
                construct = new Marking(construct);
            }

            yamlConstructors.put(tag, construct);

        }

        if (mark) {
            for (Tag tag : MARKED_TAGS) {
                Construct old = yamlConstructors.get(tag);
                yamlConstructors.put(tag, new Marking(old));
            }
        }

    }

    /**
     * Returns the inner value of a extension-tagged node.
     */
    private static Object nodeValue(Node node) {
        switch (node.getNodeId()) {
            case scalar:
                return ((ScalarNode)node).getValue();
            case mapping:
            case anchor:
            case sequence:
            default:
                throw new ClojureConstructorException(null, null,
                        "extension tags are currently only supported on scalar values",
                        node.getStartMark());
        }
    }

    /**
     * The types we want to wrap when marking.
     */
    static Tag[] MARKED_TAGS = {
        Tag.NULL, Tag.BOOL, Tag.INT, Tag.FLOAT, Tag.BINARY, Tag.TIMESTAMP,
        Tag.OMAP, Tag.PAIRS, Tag.SET, Tag.STR, Tag.SEQ, Tag.MAP
    };

    /**
     * An intermediate representation of data marked with start and
     * end positions before we turn it into the nice clojure thing.
     */
    public static class Marked {

        /* An object paired with start and end Marks. */
        public final Mark start;
        public final Mark end;
        public final Object marked;

        public Marked(Mark start, Mark end, Object marked) {
            this.start = start;
            this.end = end;
            this.marked = marked;
        }

    }

    /**
     * A wrapper around a Construct that marks source positions before calling the original.
     */
    public static class Marking extends AbstractConstruct {

        public final Construct constructor;

        public Marking(Construct constructor) {
            this.constructor = constructor;
        }

        public Object construct(Node node) {
            return new Marked(node.getStartMark(),
                    node.getEndMark(),
                    constructor.construct(node));
        }
    }

}
