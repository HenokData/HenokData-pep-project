import Controller.SocialMediaController;
import Service.UserService;
import Service.MessageService; 
import DAO.AccountDAO;
import DAO.MessageDAO; 

import io.javalin.Javalin;

/**
 * This class is provided with a main method to allow you to manually run and test your application. This class will not
 * affect your program in any way and you may write whatever code you like here.
 */
public class Main {
    public static void main(String[] args) {

        // here i make an object 
        AccountDAO accountDAO = new AccountDAO();
        MessageDAO messageDAO = new MessageDAO();
       
        UserService userService = new UserService(accountDAO);
        MessageService messageService = new MessageService(messageDAO);

        // i added (userService) in SocialMediaController 
        SocialMediaController controller = new SocialMediaController(userService,messageService);
        Javalin app = controller.startAPI();
        app.start(8080);
    }
}

