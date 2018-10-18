package net.lynx.client.objects;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Self Explanatory
 */
public class Node {
    private LinkedHashMap<String, String> attributes;
    private List<Node> children;
    private String name;
    private Node parent;
    private String text;

    public Node(String name) {
        this(name, null, null, null);
    }

    public Node(String name, String text) {
        this(name, text, null, null);
    }

    public Node(String name, List<Node> children) {
        this(name, null, children, null);
    }

    public Node(String name, Map<String, String> attributes) {
        this(name, null, null, attributes);
    }

    public Node(String name, String text, List<Node> children) {
        this(name, text, children, null);
    }

    public Node(String name, String text, Map<String, String> attributes) {
        this(name, text, null, attributes);
    }

    public Node(String name, List<Node> children, Map<String, String> attributes) {
        this(name, null, children, attributes);
    }

    private Node(String name, String text, List<Node> children, Map<String, String> attributes) {
        this.name = name;
        this.text = text;
        this.children = children;
        this.attributes = attributes == null ? null : new LinkedHashMap<>(attributes);
    }

    public Node(String name, XmlPullParser parser) throws IOException, XmlPullParserException {
        this(name);
        read(parser, false);
    }

    public Node addChild(Node node) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(node);
        node.parent = this;
        return this;
    }

    public Node removeChild(Node node) {
        if (this.children != null) {
            this.children.remove(node);
            node.parent = null;
            if (this.children.isEmpty()) {
                this.children = null;
            }
        }
        return this;
    }

    public Node addTextNode(String name, String text) {
        return addChild(new Node(name, text));
    }

    public Node addEmptyNode(String name) {
        return addTextNode(name, null);
    }

    public Node addAttribute(String key, String value) {
        if (this.attributes == null) {
            this.attributes = new LinkedHashMap<>();
        }
        this.attributes.put(key, value);
        return this;
    }

    public Node addAttribute(String key, int value) {
        if (this.attributes == null) {
            this.attributes = new LinkedHashMap<>();
        }
        this.attributes.put(key, String.valueOf(value));
        return this;
    }


    public Node addAttribute(String key, boolean value) {
        if (this.attributes == null) {
            this.attributes = new LinkedHashMap<>();
        }
        this.attributes.put(key, String.valueOf(value));
        return this;
    }

    public Node removeAttribute(String key) {
        if (this.attributes != null) {
            this.attributes.remove(key);
            if (this.attributes.isEmpty()) {
                this.attributes = null;
            }
        }
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Node setName(String name) {
        this.name = name;
        return this;
    }

    public Node getParent() {
        return this.parent;
    }

    public String getText() {
        return this.text;
    }

    public String getAttribute(String key) {
        return this.attributes == null ? null : this.attributes.get(key);
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public Node read(XmlPullParser parser) throws XmlPullParserException, IOException {
        return read(parser, true);
    }

    private Node read(XmlPullParser parser, boolean returnClass) throws XmlPullParserException, IOException {
        Node Node = returnClass ? this : null;
        while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
            switch (parser.getEventType()) {
                case XmlPullParser.START_TAG:
                    Node childNode = Node == null ? this.setName(parser.getName()) : new Node(parser.getName());
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        childNode.addAttribute(parser.getAttributeName(i), parser.getAttributeValue(i));
                    }
                    if (Node != null) {
                        Node.addChild(childNode);
                    }
                    Node = childNode;
                    break;
                case XmlPullParser.END_TAG:
                    if (Node != null) {
                        Node parent = Node.parent;
                        if (parent != null) {
                            Node = parent;
                            break;
                        }
                    }
                    return returnClass ? this : Node;
                case XmlPullParser.TEXT:
                    if (Node != null) {
                        Node.text = parser.getText();
                    }
                    break;
            }
            parser.next();
        }
        return returnClass ? this : Node;
    }

    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass() && toString().equals(o.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("<");
        if (this.text == null && this.children == null && this.attributes == null) {
            return sb.append(this.name).append("/").append(">").toString();
        }
        sb.append(this.name);
        if (this.attributes != null) {
            for (Map.Entry<String, String> entry : this.attributes.entrySet()) {
                sb.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
            }
        }
        if (this.text != null) {
            sb.append(">").append(this.text).append("</").append(this.name);
        } else if (this.children != null) {
            sb.append(">");
            for (Node child : this.children) {
                sb.append(child);
            }
            sb.append("</").append(this.name);
        } else {
            sb.append("/");
        }
        return sb.append(">").toString();
    }

    public Node[] getChildrensByName(String name){
        List<Node> childrens = new ArrayList<>();
        if (this.children == null) return null;
        for (Node child : this.children) {
            if (child.name.equals(name)) {
                childrens.add(child);
            }
        }
        return childrens.toArray(new Node[0]);
    }

    public Node getFirstChildByName(String name) {
        if (this.children == null) return null;
        for (Node child : this.children) {
            if (child.name.equals(name)) {
                return child;
            }
        }
        return null;
    }

    public void setNamespace(String namespace) {
        addAttribute("xmlns", namespace);
    }

    public Node getFirstChildByXmlns(String xmlns) {
        if (this.children == null) return null;
        for (Node child : this.children) {
            if (xmlns.equals(child.attributes.get("xmlns"))) {
                return child;
            }
        }
        return null;
    }
}

