package net.lynx.client.objects;

public class User {
    private String node;
    private String username;
    private String email;
    private String first;
    private String last;

    public User() {

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJid() {
        return node + "@talk.kik.com";
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setNode(Node node) {
        this.node = node.getText();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(Node email) {
        this.email = email.getText();
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(Node first) {
        this.first = first.getText();
    }

    public String getLast() {
        return last;
    }

    public void setLast(Node last) {
        this.last = last.getText();
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUsername(Node username) {
        this.username = username.getText();
    }
}
