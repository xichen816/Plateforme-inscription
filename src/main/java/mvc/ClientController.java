package mvc;

public class ClientController {
    private ClientModel model;
    private ClientView view;

    public ClientController(ClientModel cm, ClientView cv){
        this.model = cm;
        this.view = cv;
    }
}

