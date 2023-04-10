package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Représenter un serveur sur un port donné.
 */
public class Server {

    /**
     * Désigne un bouton évènement, l'inscription aux cours
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";

    /**
     * Désigne un bouton évènement, chargement de la session souhaitée
     */
    public final static String LOAD_COMMAND = "CHARGER";

    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Créer le constructeur de l'objet serveur qui est une instance de 'ServerSocket' ,traitant au maximum 1 client.
     * L'objet contient une liste de gestionnaire d'évènement.
     * @param port la valeur de laquelle on définit le port dans la création du serveur
     * @exception IOException si une erreur se produit sur les entrées
     * @see IOException
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }


    /**
     * Ajouter la séquence d'élément dans la liste des éléments associés à cet objet.
     * @param h séquence d'élément de type ' EventHandler'
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * Associe une alerte à tous les éléments de la liste avec la commande et l'argument donnés.
     * @param cmd la commande à envoyer aux gestionnaires d'événements
     * @param arg la commande à envoyer aux gestionnaires d'événements
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }


    /**
     * Répond continuellement aux demandes de connexion des clients.
     * Gère la connection client-serveur en traitant les données envoyées par chaque client.
     * @throws Exception si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cette méthode répond aux types d'exceptions suivants par une alerte :
     * @exception IOException si une erreur se produit sur les entrées
     * @exception ClassNotFoundException si une erreur se produit sur une classe inexistante
     * @see IOException lorsqu'une erreur survient lors de la lecture du flux d'entrée
     * @exception NullPointerException si la ligne de commande est vide
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }


    /**
     * Cette méthode prend en entrée une ligne de commande et sépare la commande de ses arguments en deux parties.
     * Elle crée un nouvel objet, Pair.
     * @param line entrée à analyser
     * @return Pair, un objet qui contient la commande et les arguments
     * @exception ArrayIndexOutOfBoundsException si la ligne de commande est vide ou ne contient aucune commande.
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }


    /**
     Méthode qui ferme les flux de données entrantes et sortantes du client ainsi que sa connexion.
     @exception IOException si une erreur d'entrée/sortie se produit lors de la fermeture des flux ou de la connexion.
     @see IOException
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }


    /**
     La méthode suivante traite l'inscription ou le chargement des cours dépendant de la commande passée en argument.
     @param cmd la commande à envoyer aux gestionnaires d'événements
     @param arg la commande à envoyer aux gestionnaires d'événements
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     * Lire un fichier texte contenant des informations sur les cours et les transformer en liste d'objets 'Course'.
     * La méthode filtre les cours par la session spécifiée en argument.
     * Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     * @param arg la session pour laquelle on veut récupérer la liste des cours
     * @throws Exception si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux
     */
    public void handleLoadCourses(String arg) {

        File fichierCours = new File("cours.txt");

        try {
            Scanner scan = new Scanner(fichierCours);

            ArrayList<Course> listCourse = new ArrayList<Course>;

            String code_du_cours = scan.nextLine();
            String nom_du_cours = scan.nextLine().substring(1);
            String session = scan.nextLine().substring(2); 

            while (scan.hasNextLine()) {
                Course donneeCours = new Course(nom_du_cours, code_du_cours, session);
                listCourse.add(donneeCours);
            }

            switch(arg) {
                case "Hiver":
                    FileOutputStream fileOs = new FileOutputStream("coursHivers.dat");
                    ObjectOutputStream os = new ObjectOutputStream(fileOs);
                    os.writeObject();
                    os.close();

                case "Automne" :
                    //TODO

            }

        } catch (Exception e) {
            // si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux
            // FileNotFoundException
            // IOException
        }
    }

    /**
     * Traite une demande d'inscription en enregistrant le formulaire d'inscription du client dans un fichier.
     * @throws IOException si une erreur survient lors de la lecture ou de l'écriture du formulaire d'inscription
     * @throws ClassNotFoundException si la classe RegistrationForm n'est pas trouvée lors de la serialisation
     */
    public void handleRegistration() throws Exception {
        RegistrationForm registrationForm;
        File registrationFile = new File("registration_form.txt");
        try {
            while ((registrationForm = (RegistrationForm) this.objectInputStream.readObject()) != null) {
                try (FileOutputStream fos = new FileOutputStream(registrationFile);
                     ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    oos.writeObject(registrationForm);
                    System.out.println("Votre inscription est enregistrée.");
                    oos.close();
                }
            }

        } catch (EOFException e) {
            System.out.println("Fin de l'inscription.");

        } catch (IOException | ClassNotFoundException e) {
            throw new Exception("Une erreur est survenue lors de votre inscription. Veuillez réessayer.", e);
        } finally {
            this.objectInputStream.close();
        }
    }
}


