import com.google.gson.Gson;

public class GsonTest {
    public static void main(String[] args) {
        Gson gson = new Gson();
        System.out.println(gson.toJson("Hello Gson!"));
    }
}