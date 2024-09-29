/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.smartcard;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;

import sun.security.pkcs11.wrapper.PKCS11Exception;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.common.util.StringUtil;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.LoginException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartCardException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartOp;

/**
 *
 * @author hakan
 */
@SuppressWarnings("restriction")
public class Manager extends ManagerBase {
    private static final Object lockObject = new Object();
    private static Manager mSCManager;

    /**
     * @throws SmartCardException
     */
    private  Manager() throws SmartCardException {
        super();
        LOGGER.info("Manager Class created");
        LOGGER.debug("Manager Class created");
        
        LoggerContext context = LoggerContext.getContext(false);
        Configuration config = context.getConfiguration();
        
        for (org.apache.logging.log4j.core.Appender appender : config.getAppenders().values()) {
            if (appender instanceof FileAppender) {
                FileAppender fileAppender = (FileAppender) appender;
                String fileName = fileAppender.getFileName();
                System.out.println("Log file location: " + fileName);
            }
        }
    }

    /**
     * Singleton is used for this class. If many card placed, it wants to user to select one of cards.
     * If there is a influential change in the smart card environment, it  repeat the selection process.
     * The influential change can be:
     * If there is a new smart card connected to system.
     * The cached card is removed from system.
     * These situations are checked in getInstance() function. So for your non-squantial SmartCard Operation,
     * call getInstance() function to check any change in the system.
     * <p>
     * In order to reset these selections, call reset function.
     *
     * @return SmartCardManager instance
     * @throws SmartCardException
     */
    public static Manager getInstance() throws SmartCardException {
        System.out.println("selam");
        LOGGER.debug("Manager Class created");
        synchronized (lockObject) {
            if (mSCManager == null) {
                mSCManager = new Manager();
                return mSCManager;
            } else {
                //Check is there any change
                try {
                    
                    //If there is a new card in the system, user will select a smartcard.
                    //Create new SmartCard.
                    if (mSCManager.getSlotCount() < SmartOp.getCardTerminals().length) {
                        LOGGER.debug("New card pluged in to system");
                        mSCManager = null;
                        return getInstance();
                    }

                    //If used card is removed, select new card.
                    String availableSerial;
                    try {
                        availableSerial = StringUtil.toString(mSCManager.getBasicSmartCard().getSerial());
                    } catch (SmartCardException ex) {
                        LOGGER.debug("Card removed");
                        mSCManager = null;
                        return getInstance();
                    }
                    if (!mSCManager.getSelectedSerialNumber().equals(availableSerial)) {
                        LOGGER.debug("Serial number changed. New card is placed to system");
                        mSCManager = null;
                        return getInstance();
                    }

                    return mSCManager;
                } catch (SmartCardException e) {
                    mSCManager = null;
                    throw e;
                }
            }
        }
    }

    public static void reset() throws SmartCardException {
        synchronized (lockObject) {
            mSCManager = null;
        }
    }

    /**
     * BaseSigner interface for the requested certificate. Do not forget to logout after your cyrpto
     * operation finished
     *
     * @param aCardPIN
     * @param aCert
     * @return
     * @throws SmartCardException
     * @throws LoginException
     */
    public synchronized BaseSigner getSigner(String aCardPIN, ECertificate aCert) throws SmartCardException, LoginException {
        return getSignerBase(aCardPIN, aCert.asX509Certificate());
    }

    /**
     * BaseSigner interface for the requested certificate. Do not forget to logout after your cyrpto
     * operation finished
     *
     * @param aCardPIN
     * @param aCert
     * @param aSigningAlg
     * @param aParams
     * @return
     * @throws SmartCardException
     * @throws LoginException
     */
    public synchronized BaseSigner getSigner(String aCardPIN, ECertificate aCert, String aSigningAlg, AlgorithmParameterSpec aParams) throws SmartCardException, LoginException {
        return getSignerBase(aCardPIN, aCert.asX509Certificate(), aSigningAlg, aParams);
    }


    /**
     * Returns for the signature certificate. If there are more than one certificates in the card in requested
     * attributes, it wants user to select the certificate. It caches the selected certificate, to reset cache,
     * call reset function.
     *
     * @param checkIsQualified       Only selects the qualified certificates if it is true.
     * @param checkBeingNonQualified Only selects the non-qualified certificates if it is true.
     *                               if the two parameters are false, it selects all certificates.
     *                               if the two parameters are true, it throws ESYAException. A certificate can not be qualified and non qualified at
     *                               the same time.
     * @return certificate
     * @throws SmartCardException
     * @throws ESYAException
     */
    public synchronized ECertificate getSignatureCertificate(boolean checkIsQualified, boolean checkBeingNonQualified) throws ESYAException{

        byte[] encodedX509Cert = null;
        try {
            encodedX509Cert = getSignatureCertificateBase(checkIsQualified, checkBeingNonQualified).getEncoded();
        } catch (CertificateEncodingException e) {
            throw new ESYAException("Error in encoding X509 Certificate");
        }

        return new ECertificate(encodedX509Cert);
    }
    
    public synchronized List<ECertificate> getSignatureCertificate(String terminal) throws ESYAException, IOException, PKCS11Exception{
        List<ECertificate> eCertificates = new ArrayList<>();
        List<X509Certificate> certificates;
        try {
            certificates = getSignatureCertificateByTerminalBase(true, false, terminal);
            
            for(X509Certificate certificate:certificates){
                eCertificates.add(new ECertificate(certificate.getEncoded())); 
            }
        } catch (CertificateEncodingException e) {
            throw new ESYAException("Error in encoding X509 Certificate");
        }
        
        return eCertificates;
    }

    public synchronized ECertificate getSignatureCertificate(boolean isQualified) throws ESYAException {
        return getSignatureCertificate(isQualified, !isQualified);
    }

    /**
     * Returns for the encryption certificate. If there are more than one certificates in the card in requested
     * attributes, it wants user to select the certificate. It caches the selected certificate, to reset cache,
     * call reset function.
     *
     * @param checkIsQualified
     * @param checkBeingNonQualified
     * @return
     * @throws SmartCardException
     * @throws ESYAException
     */
    public synchronized ECertificate getEncryptionCertificate(boolean checkIsQualified, boolean checkBeingNonQualified) throws ESYAException {
        byte[] encodedX509Cert = null;
        try {
           encodedX509Cert = getEncryptionCertificateBase(checkIsQualified,checkBeingNonQualified).getEncoded();
        } catch (CertificateEncodingException e) {
            throw new ESYAException("Error in encoding X509 Certificate");
        }
        return new ECertificate(encodedX509Cert);
    }
}
