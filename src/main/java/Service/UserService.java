package Service;
import java.util.Optional;
import DAO.AccountDAO;
import Model.Account;



public class UserService {

    // fileds
    private final AccountDAO accountDAO;

    // constractor with parameter 

    public UserService(AccountDAO accountDAO){

        this.accountDAO = accountDAO;
    }
    /* Attempt to log in a user by username and password
     * @param username the username of the account
     * @param password the password of the account
     * @return an optional containing the account if found, otherwise optional.empty
     * the code below and this documentation is by HENOK
     */

    public Optional<Account> login(String username, String password){
        return accountDAO.findByUsernameAndPassword(username, password);
    }
    /* Find an account by the username
     * @param username the username of the account
     * @return an optional containg the account if found, otherwise empty 
     * the code below and this documentation is by HENOK
     */

    public Optional<Account> findByUsername(String username){
        return accountDAO.findByUsername(username);
    }
    /* Find to find user by account_id ranter than username
     * 
     * i created this to avoid the conflect of username string and account_id int datatype
     */
    public Optional<Account> findById(int accountId){
        return accountDAO.findById(accountId);
    }

    /* Registers a new account 
     * @param username for new account
     * @param password for a new account
     * @return return newly created account
     * @throws IllegalArgumentException if the usernameis blank, password is less than 4 characters, or username already exisx
     * the code below and this documentation is by HENOK
     */

    public Account register(String username, String password){

        if (username.isBlank() || password.length() < 4 || accountDAO.findByUsername(username).isPresent()) {

            throw new IllegalArgumentException("Invalid data for registration: username is blank, password is too short or username is already exist ");
            
        }
        Account newAccount = new Account();
        newAccount.setUsername(username);
        newAccount.setPassword(password);
        return accountDAO.save(newAccount);
    }

}



    

