package be.guntherdw.bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class customTitle {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Invalid argument");
        } else {
            Parser parser = new Parser(new File(args[0]));
            parser.parse();
        }
    }
}
