import converter.MainKt;
import org.hyperskill.hstest.v6.stage.BaseStageTest;


abstract public class ConverterTest<T> extends BaseStageTest<T> {
    public ConverterTest() throws Exception {
        super(MainKt.class);
    }
}
