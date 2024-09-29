/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.xades;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import dijisoz.signer.esign.SignBase;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.PolicyReader;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.ESYARuntimeException;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.Context;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.ValidationResult;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.XMLSignature;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.XMLSignatureException;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.config.Config;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.document.FileDocument;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.model.xades.UnsignedSignatureProperties;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.resolver.OfflineResolver;

/**
 *
 * @author hakan
 */
public class XadesBase extends SignBase {
    private static ValidationPolicy validationPolicy;
    private static final String configFile;     // config file path
    private static final String policyFile;       // certificate validation policy file path
    private static final String policyFileCrl; 
    private static final OfflineResolver offlineResolver;
    private final String ENVELOPE_XML =  // sample XML document used for enveloped signature
            "<envelope>\n" +
                    "  <data id=\"data1\">\n" +
                    "    <item>Item 1</item>\n" +
                    "    <item>Item 2</item>\n" +
                    "    <item>Item 3</item>\n" +
                    "  </data>\n" +
                    "</envelope>\n";

    static {
        configFile = getRootDir() + "/configs/xmlsignature-config.xml";
        policyFile = getRootDir() + "/configs/policy.xml";
        policyFileCrl = getRootDir() + "/configs/certval-policy-crl.xml";
        offlineResolver = new OfflineResolver();
        offlineResolver.register("urn:oid:2.16.792.1.61.0.1.5070.3.1.1", getRootDir() + "/config/profiller/Elektronik_Imza_Kullanim_Profilleri_Rehberi.pdf", "text/plain");
        offlineResolver.register("urn:oid:2.16.792.1.61.0.1.5070.3.2.1", getRootDir() + "/config/profiller/Elektronik_Imza_Kullanim_Profilleri_Rehberi.pdf", "text/plain");
        offlineResolver.register("urn:oid:2.16.792.1.61.0.1.5070.3.3.1", getRootDir() + "/config/profiller/Elektronik_Imza_Kullanim_Profilleri_Rehberi.pdf", "text/plain");
    }

    public XadesBase() {

    }
    
     /**
     * Creates context for signature creation and validation
     *
     * @param workingDirectory
     * @return created context
     */
    protected Context createContext(String workingDirectory) {
        Context context = null;
        
        try {
            context = new Context(workingDirectory);
            //String path =  getClass().getResource(configFile).getPath();
            context.setConfig(new Config(configFile));
        } catch (XMLSignatureException ex) {
            System.out.println(ex);
        }
        
        return context;
    }

    
    protected synchronized ValidationPolicy getPolicy() throws ESYAException, IOException {

        if (validationPolicy == null) {
            try {
                validationPolicy = PolicyReader.readValidationPolicy(new FileInputStream(policyFile));
            } catch (FileNotFoundException e) {
                throw new ESYARuntimeException("Policy file could not be found", e);
            }
        }
        
        return validationPolicy;
    }
    
    /**
     * Creates sample envelope XML that will contain signature inside
     *
     * @return envelope in Document format
     * @throws ESYAException
     */
    protected Document newEnvelope() throws ESYAException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();

            return db.parse(new ByteArrayInputStream(ENVELOPE_XML.getBytes()));
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            // we shouldn't be here if ENVELOPE_XML is valid
            System.out.println(ex);
        }
        
        throw new ESYAException("Cant construct envelope xml ");
    }

    /**
     * Reads an XML document into DOM document format
     *
     * @param uri      XML file to be read
     * @param aContext signature context
     * @return DOM document format of read XML document
     * @throws Exception
     */
    protected Document parseDoc(String uri, Context aContext) throws Exception {

        // generate document builders for parsing
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        // open the document
        File f = new File(uri);

        // parse into DOM format
        org.w3c.dom.Document document = db.parse(f);
        aContext.setDocument(document);

        return document;
    }

    /**
     * Gets the signature by searching for tag in an enveloped signature
     *
     * @param aDocument XML document to be looked for
     * @param aContext  signature context
     * @return XML signature in the XML document
     * @throws Exception
     */
    protected XMLSignature readSignature(Document aDocument, Context aContext) throws Exception {

        // get the signature in enveloped signature format
        Element signatureElement = ((Element) aDocument.getDocumentElement().getElementsByTagName("ds:Signature").item(0));

        // return the XML signature created with signature element
        return new XMLSignature(signatureElement, aContext);
    }

    /**
     * Gets the signature by searching for tag in a parallel signature
     *
     * @param aDocument XML document to be looked for
     * @param aContext  signature context
     * @param item      order of signature to be read in parallel structure
     * @return XML signature in the XML document
     * @throws Exception
     */
    protected XMLSignature readSignature(Document aDocument, Context aContext, int item) throws Exception {

        // get the first signature element searching for the tag in the XML document
        Element signatureElement = ((Element) ((Element) aDocument.getDocumentElement().getElementsByTagName("signatures").item(0)).getElementsByTagName("ds:Signature").item(item));

        // return the XML signature using signature element
        return new XMLSignature(signatureElement, aContext);
    }

    /**
     * Gets policy file crl
     *
     * @return the policy file crl
     */
    protected String getPolicyFileCrl() {
        return policyFileCrl;
    }

    /**
     * Gets policy file
     *
     * @return the policy file
     */
    protected String getPolicyFile() {
        return policyFile;
    }

    protected OfflineResolver getOfflineResolver() {
        return offlineResolver;
    }    
    
    public ValidationResult baseValidate(XadesValidationInfo validationInfo){
        try {
            Context context = createContext(validationInfo.getWorkingDirectory());

        /* optional - specifying policy from code
        // generate policy to be used in certificate validation
        ValidationPolicy policy = PolicyReader.readValidationPolicy(POLICY_FILE);

        CertValidationPolicies policies = new CertValidationPolicies();
        // null means default
        policies.register(null,policy);

        context.getConfig().getValidationConfig().setCertValidationPolicies(policies);
        */

        // add external resolver to resolve policies
        context.addExternalResolver(offlineResolver);

        XMLSignature signature = XMLSignature.parse(new FileDocument(new File(validationInfo.getFilePath())), context);

        // no parameters, use the certificate in key info
        ValidationResult result = signature.verify();
        System.out.println(result.toXml());
        
            
        signatureVerify(validationInfo.getFilePath(), signature);
        } catch (XMLSignatureException e) {
        }
        System.out.println("XADES Validation");
        return null;
    }

    private void signatureVerify(String fileName, XMLSignature signature) throws XMLSignatureException {

        UnsignedSignatureProperties usp = signature.getQualifyingProperties().getUnsignedSignatureProperties();
        if (usp != null) {
            List<XMLSignature> counterSignatures = usp.getAllCounterSignatures();
            for (XMLSignature counterSignature : counterSignatures) {
                ValidationResult counterResult = signature.verify();

                System.out.println(counterResult.toXml());
                System.out.println(" verify counter signature" + fileName + " : " + counterSignature.getId() +"-"+ counterResult.getType());
            }
        }
    }
}
