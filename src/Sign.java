import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Security;
import java.util.Iterator;

class Sign {
    static void sign(String filePath, String secretKeyPath, String password, String newFileName) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        FileInputStream fin = new FileInputStream(secretKeyPath);
        InputStream in = PGPUtil.getDecoderStream(fin);
        PGPSecretKeyRingCollection skrc;
        skrc = new PGPSecretKeyRingCollection(
                in, new JcaKeyFingerprintCalculator());
        Iterator<PGPSecretKeyRing> kri = skrc.getKeyRings();
        PGPSecretKey key = null;
        while (key == null && kri.hasNext()) {
            PGPSecretKeyRing keyRing = kri.next();
            Iterator<PGPSecretKey> ki = keyRing.getSecretKeys();
            while (key == null && ki.hasNext()) {
                PGPSecretKey k = ki.next();
                if (k.isSigningKey()) { key = k; }
            }
        }
        if ( key == null )
            throw new IllegalArgumentException("Can't find key");
        PGPPrivateKey pgpPrivKey = key.extractPrivateKey(
                new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(
                        password.toCharArray()));
        PGPSignatureGenerator sGen = new PGPSignatureGenerator(
                new JcaPGPContentSignerBuilder(key.getPublicKey().getAlgorithm(),
                        PGPUtil.SHA1).setProvider("BC"));
        sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);
        PGPCompressedDataGenerator cGen =
                new PGPCompressedDataGenerator(PGPCompressedData.ZLIB);
        FileOutputStream out = new FileOutputStream(newFileName + ".bpg");
        BCPGOutputStream bOut = new BCPGOutputStream(cGen.open(out));
        FileInputStream fIn = new FileInputStream(filePath);
        int ch;
        while ((ch = fIn.read()) >= 0) { sGen.update((byte)ch); }
        sGen.generate().encode(bOut);
        cGen.close();
        out.close();
        fIn.close();
    }
}