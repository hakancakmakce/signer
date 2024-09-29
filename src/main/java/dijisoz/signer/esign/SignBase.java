/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.util.LicenseUtil;
import tr.gov.tubitak.uekae.esya.api.crypto.alg.DigestAlg;
import tr.gov.tubitak.uekae.esya.api.infra.tsclient.TSSettings;

/**
 *
 * @author hakan
 */
public class SignBase {
    protected static Logger logger = LoggerFactory.getLogger(SignBase.class);

    // bundle root directory of project
    private static final String ROOT_DIR;

    // gets only qualified certificates in smart card
    private static final boolean IS_QUALIFIED = true;

    private static TSSettings tsSettings;

    static {

        URL root = SignBase.class.getResource("/");
        String classPath = root.getPath();
        File binDir = new File(classPath);
        ROOT_DIR = binDir.getParentFile().getParent();
        System.out.println(ROOT_DIR);
        
        try {
            LicenseUtil.setLicenseXml(new FileInputStream(ROOT_DIR + "\\lisans\\lisans.xml"));
            /*
            * LicenseUtil.setLicenseXml(new FileInputStream(
            * "c:digisoz/source/signer/src/main/resources/licences/apiLicence.xml"));
            *
            * Date expirationDate = LicenseUtil.getExpirationDate();
            * SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            * System.out.println("License expiration date :" +
            * dateFormat.format(expirationDate));
            * System.out.println("MA3 API version: " + VersionUtil.getAPIVersion());
            */
            
            // To set class path
            
            /*
            * // To sign with pfx file
            * String PFX_FILE = ROOT_DIR + "/sertifika deposu/test1@test.com_745418.pfx";
            * String PFX_PASS = "745418";
            * PfxSignTest pfxSigner = new PfxSignTest(SignatureAlg.RSA_SHA256, PFX_FILE,
            * PFX_PASS.toCharArray());
            * certificate = pfxSigner.getSignersCertificate();
            */
        } catch (FileNotFoundException | ESYAException ex) {
            java.util.logging.Logger.getLogger(SignBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the bundle root directory of project
     *
     * @return the root dir
     */
    public static String getRootDir() {
        return ROOT_DIR;
    }

    /**
     * Gets the pin of the smart card
     *
     * @return the pin
     * @throws tr.gov.tubitak.uekae.esya.api.common.ESYAException
     */
    protected static String getPin() throws ESYAException {
        throw new ESYAException("Set the pin of the smart card!");
        // return PIN_SMARTCARD;
    }

    /**
     * The parameter to choose the qualified certificates in smart card
     *
     * @return the
     */
    protected static boolean isQualified() {
        return IS_QUALIFIED;
    }

    protected static  TSSettings getTSSettings() {
        if(tsSettings == null)
            tsSettings = new TSSettings("http://tzd.kamusm.gov.tr", 12427, "CaH73aRu", DigestAlg.SHA256);
        
        return tsSettings;
    }
}
