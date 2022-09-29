import org.junit.jupiter.api.Test;

public class Test001 {

    @Test
    public void Test1(){
        String name="duhrvddsv.jpg";
        String suffix=name.substring(name.lastIndexOf("."));
        System.out.println(suffix);
    }
}
