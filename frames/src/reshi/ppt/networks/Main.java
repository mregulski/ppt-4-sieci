package reshi.ppt.networks;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(FileSystems.getDefault().getPath(args[1]));
        }
        catch (IOException ioe){
            System.out.println("Cannot open file: " + args[1]);
            System.exit(1);
        }
        StringBuilder data = new StringBuilder("");
        lines.forEach(data::append);

        EthernetStuffer stuffer = new EthernetStuffer(8);
        String output = null;
        switch (args[0]) {
            case "send":
                output = stuffer.encode(data.toString());

                break;
            case "receive":
                output = stuffer.decode(data.toString());
                break;
            default:
                System.out.println("unknown operation");
                System.exit(1);
        }

        System.out.println(output);
    }

}
