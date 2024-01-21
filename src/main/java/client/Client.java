package client;

import java.io.*;
import java.net.Socket;

import java.util.Scanner;
import java.util.InputMismatchException;

public class Client {
    ObjectOutputStream sessionStream;
    private String session;

    public Client() {
        this.sessionStream = null;
        this.session = null;
    }

    /**
     * charger est la méthode qui affiche les cours pour la session (chiffre) entrée en argument.
     * @throws IOException si une erreur d'entrée/sortie du flux se produit
     */
    public void charger(String session, Socket port) throws IOException {
        this.session = session;
        this.sessionStream = new ObjectOutputStream(port.getOutputStream());

        switch (session) {
            case "1":
                this.session = "automne";
                break;
            case "2":
                this.session = "hiver";
                break;
            case "3":
                this.session = "ete";
                break;
            default:
                throw new IllegalArgumentException("Invalid session: " + session);
        }

    }

    /**
     * Cette méthode permet aux clients de s'inscrire à un/des cours offerts à l'UDEM. L'utilisateur peut d'abord choisir la session
     * à laquelle ils souhaitent consulter la liste des cours, puis sélectionner le cours qu'ils souhaitent prendre durant la session.
     * Ils ont ensuite soit le choix de consulter les cours d'une autre session, soit de finaliser l'inscription, étape dans laquelle
     * ils sont demandés d'entrer leurs informations personnelles.
     * @param args les données entrées en argument
     */
    public void inscription(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            Socket client = new Socket("127.0.0.1",1337);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String request = in.readLine();

            if (request.equals("inscription")) {
                System.out.println("*** Bienvenue au portail d'inscription de l'UDEM ***");
                System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:\n" +
                        " 1.Automne\n 2. Hiver\n 3. Ete");
                int uneSession = scanner.nextInt();
                System.out.println("Choix: " + uneSession + "Les cours offerts pendant la session d'" + session + "sont:");

                charger(String.valueOf(uneSession), client);

                System.out.println("Choix: \n" +
                        "1. Consulter les cours offerts pour une autre session\n" +
                        "2. Inscription à un autre cours");
                int selection = scanner.nextInt();


                if (selection == 1){
                    System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:\n" +
                            " 1.Automne\n 2. Hiver\n 3. Ete");
                    int otherSession = scanner.nextInt();
                    System.out.println("Choix: " + otherSession + "Les cours offerts pendant la session d'" + session + "sont:");
                    charger(String.valueOf(otherSession), client);
                }

                if (selection == 2){
                    System.out.println("Choix: 2\nVeuillez saisir votre prénom");
                    String prenom = scanner.nextLine();
                    System.out.println("Veuillez saisir votre nom");
                    String nom = scanner.nextLine();
                    System.out.println("Veuillez saisir votre email");
                    String email = scanner.nextLine();
                    System.out.println("Veuillez saisir votre matricule");
                    int matricule = scanner.nextInt();
                    System.out.println("Veuillez saisir le code du cours");
                    String codeCours = scanner.nextLine();

                    System.out.println("Félicitations! Inscription réussie de" + prenom + "au cours" + codeCours);

                    try {
                        if (prenom.isEmpty() || nom.isEmpty()) {
                            throw new Exception("Le prénom et le nom ne doivent pas être vides");
                        }
                        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                            throw new Exception("Le format de l'email est invalide.");
                        }

                        if (!codeCours.matches("[IFT]+-[0-9]")) {
                            throw new Exception("Le code du cours doit être écrit dans le bon format et " +
                                    "figurer parmi la liste des cours. Exemple: 'IFT-1025'");
                        }
                    } catch (Exception e) {
                        System.out.println("Erreur: " + e.getMessage());
                        return;
                    }
                }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InputMismatchException e) {
            System.out.println("La matricule doit être composée de 8 chiffres");
        }
    }
}
