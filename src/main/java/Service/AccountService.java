package Service;

import Model.Account;
import DAO.AccountDAO;

import java.util.List;

/**
 * The purpose of a Service class is to contain "business logic" that sits between the web layer (controller) and
 * persistence layer (DAO). That means that the Service class performs tasks that aren't done through the web or
 * SQL: programming tasks like checking that the input is valid, conducting additional security checks, or saving the
 * actions undertaken by the API to a logging file.
 *
 * It's perfectly normal to have Service methods that only contain a single line that calls a DAO method. An
 * application that follows best practices will often have unnecessary code, but this makes the code more
 * readable and maintainable in the long run!
 */
public class AccountService {
    private AccountDAO accountDAO;

    /**
     * no-args constructor for creating a new AccountService with a new AccountDAO.
     * There is no need to change this constructor.
     */
    public AccountService(){
        accountDAO = new AccountDAO();
    }
    
    /**
     * Constructor for a AccountService when a AccountDAO is provided.
     * This is used for when a mock AccountDAO that exhibits mock behavior is used in the test cases.
     * This would allow the testing of AccountService independently of AccountDAO.
     * There is no need to modify this constructor.
     * @param accountDAO
     */
    public AccountService(AccountDAO accountDAO){
        this.accountDAO = accountDAO;
    }

    public Account registerUser(Account account) {

        // Check for the username is not blank, the password is at least 4 characters long, and an Account with that username does not already exist.
        if( account.getUsername().isBlank() || account.getPassword().length() < 4 || accountDAO.getAccountById(account) != null ) {
            return null;
        }
        
        // If all above conditions are met, the response body should contain a JSON of the Account
        return accountDAO.insertAccount(account);
    }


    public Account loginUser(Account account) {

        // Check for the username is not blank, the password is at least 4 characters long, and an Account with that username already exist.
        if( account.getUsername().isBlank() || account.getPassword().length() < 4 || accountDAO.getAccountById(account) == null ) {
            return null;
        }

        return accountDAO.getAccountById(account);
    }

}
