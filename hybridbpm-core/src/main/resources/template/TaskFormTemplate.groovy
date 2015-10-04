import com.vaadin.ui.*;
import com.vaadin.ui.declarative.*;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.fieldgroup.FieldGroup;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.BigDecimal;
import java.util.Date;
import com.hybridbpm.ui.component.*;
import com.hybridbpm.ui.component.bpm.*;

@DesignRoot
public class $moduleName extends TaskForm {
    
    private static final Logger logger = Logger.getLogger("$moduleName");
    
    /* generated datasources start */
    /* generated datasources end */
    
    /* generated components start */
    /* generated components end */
    
    /* generated files start */
    /* generated files end */
    
    public $moduleName() {
        Design.read(this); 
    /* generated bindings start */
    /* generated bindings end */
    }
    
    @Override
    public void commit() {
        try {
    /* generated commits start */
    /* generated commits end */
        } catch (FieldGroup.CommitException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
