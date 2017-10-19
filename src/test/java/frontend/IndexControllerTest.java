package frontend;

import org.junit.Test;
import static org.junit.Assert.*;

public class IndexControllerTest {

    @Test
    public void shouldReturnIndex() throws Exception {
        IndexController controllerToTest = new IndexController();
        assertEquals(controllerToTest.index(), "index");
    }
}