import java.io.*;
import java.net.*;
import java.security.*;

public class SignSend {
    public static void main(String args[]) {
        try {
            Socket sock = new Socket(args[0], 6001); // connect
            OutputStream o = sock.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(o);
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(".keystore"), null);
            Signature s = Signature.getInstance("DSA");
            char[] password = args[2].toCharArray();
            s.initSign((PrivateKey)ks.getKey(args[1], password));
            byte[] message = readFile(args[3]);
            s.update(message);
            oos.writeObject(message); // send message
            oos.writeObject(s.sign()); // send signature
            oos.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static byte[] readFile(String file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        int i = 0, n, len = (int)(new File(file)).length();
        byte[] buf = new byte[len];
        do { if ( (n = in.read(buf, i, len - i)) != -1 ) i += n;
        } while ( i < len && n != -1 );
        in.close();
        return buf;
    }
}