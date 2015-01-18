package org.shunya.serverwatcher;

public class ServerComponentBuilder extends ServerComponent {

    public ServerComponentBuilder() {
        setResponse("n/a");
        setStatus(ServerStatus.UNKNOWN);
    }

    public ServerComponentBuilder withExpectedResponseCode(int... expectedResponseCode) {
        setExpectedResponseCode(expectedResponseCode);
        return this;
    }

    public ServerComponentBuilder withExpectedTokenString(String expectedTokenString) {
        setExpectedTokenString(expectedTokenString);
        return this;
    }

    public ServerComponentBuilder withName(String name) {
        setName(name);
        return this;
    }

    public ServerComponentBuilder withUrl(String url) {
        setUrl(url);
        return this;
    }

    public ServerComponentBuilder withServerStatus(ServerStatus status) {
        setStatus(status);
        return this;
    }

    public ServerComponentBuilder withServerType(ServerType type) {
        setServerType(type);
        return this;
    }

    public ServerComponentBuilder withUserName(String userName){
        setUsername(userName);
        return this;
    }

    public ServerComponentBuilder withPassword(String password){
        setPassword(password);
        return this;
    }

    public ServerComponent build() {
        return this;
    }
}
