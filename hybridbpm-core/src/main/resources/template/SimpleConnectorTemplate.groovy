import com.hybridbpm.core.connector.BpmConnector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class $moduleName extends BpmConnector {
    
    private static final Logger LOGGER = Logger.getLogger("$moduleName");
    
    /* generated in parameters start */
    /* generated in parameters end */
    
    /* generated out parameters start */
    /* generated out parameters end */
    
    @Override
    public void execute() {
        try {
            // implement connector here
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
