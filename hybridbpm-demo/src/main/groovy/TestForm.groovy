import com.vaadin.ui.*;
import com.vaadin.ui.declarative.*;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.ObjectProperty;
import java.math.BigDecimal;
import java.util.Date;

@DesignRoot
public class TestForm extends VerticalLayout implements Button.ClickListener {
    
    /* generated bindings start */
    /* generated bindings end */
    
    private Label testLabel;
    private TextField testTextField;
    private Button testButton;
    
    public TestForm() {
        Design.read(this); // DO NOT CHANGE THIS LINE !!!
         testButton.addClickListener(this);
    }
    
    @Override
    public void buttonClick(Button.ClickEvent event) {
        try {
            if (event.getButton().equals(testButton)) {
                Notification.show("HELLO WORLD", Notification.Type.HUMANIZED_MESSAGE); 
            }
        } catch (Exception ex) {
            Notification.show(ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }
}
