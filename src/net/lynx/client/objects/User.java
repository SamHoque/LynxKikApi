package net.lynx.client.objects;

public class User {
    String node;
    String username;
    String email;
    String first;
    String last;

    User(){

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public String getNode() {
        return node;
    }

    public String getUsername() {
        return username;
    }
}
