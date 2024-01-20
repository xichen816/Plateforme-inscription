package mvc;

import client.Client;
import javafx.application.Application;

public abstract class ClientModel extends Application {
    Client client = new Client();
    private String session;
    private String code;
    private String name;

    public ClientModel() {
        this.session = null;
        this.code = null;
        this.name = null;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return this.session;
    }



    }



