package com.workoutstudioapp.xadessignercr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.transforms.Transforms;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import xades4j.production.DataObjectReference;
import xades4j.production.SignedDataObjects;
import xades4j.production.XadesEpesSigningProfile;
import xades4j.production.XadesSignatureResult;
import xades4j.production.XadesSigner;
import xades4j.properties.AllDataObjsCommitmentTypeProperty;
import xades4j.properties.DataObjectDesc;
import xades4j.properties.DataObjectTransform;
import xades4j.properties.IdentifierType;
import xades4j.properties.ObjectIdentifier;
import xades4j.properties.SignaturePolicyBase;
import xades4j.properties.SignaturePolicyIdentifierProperty;
import xades4j.providers.BasicSignatureOptionsProvider;
import xades4j.providers.KeyingDataProvider;
import xades4j.providers.SignaturePolicyInfoProvider;
import xades4j.providers.SigningCertChainException;
import xades4j.providers.impl.FileSystemKeyStoreKeyingDataProvider;
import xades4j.verification.UnexpectedJCAException;

public class Signer {

	public static void main(String[] args) {
		Signer signer = new Signer();
		signer.sign("../test-data/cert.p12", "1234", "../test-data/factura.xml", "../test-data/out.xml");
	}
	public void sign(String keyPath, String password, String xmlInPath, String xmlOutPath) {
		try {
			File keyFile = new File(keyPath);
			if (!keyFile.exists()) {
				System.err.println("key file is missing for path: " + keyPath);
			}
			XadesSigner signer = getSigner(password, keyPath);
			signWithoutIDEnveloped(xmlInPath, xmlOutPath, signer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private XadesSigner getSigner(String password, String pfxPath) throws Exception {
        KeyingDataProvider keyingProvider = getKeyingDataProvider(pfxPath, password);
        SignaturePolicyInfoProvider policyInfoProvider = new SignaturePolicyInfoProvider() {
        	public SignaturePolicyBase getSignaturePolicy() {
        		return new SignaturePolicyIdentifierProperty(
                        new ObjectIdentifier("https://tribunet.hacienda.go.cr/docs/esquemas/2017/v4.2/Resolucion%20Comprobantes%20Electronicos%20%20DGT-R-48-2016.pdf", IdentifierType.URI),
                        "".getBytes());
        	}
        };
        BasicSignatureOptionsProvider basicSignatureOptionsProvider = new BasicSignatureOptionsProvider() {
        	public boolean includePublicKey() {
        		return true;
        	}
        	public boolean includeSigningCertificate() {
        		return true;
        	}
        	public boolean signSigningCertificate() {
        		return true;
        	}
        };
        XadesEpesSigningProfile p = new XadesEpesSigningProfile(keyingProvider, policyInfoProvider);
        p.withBasicSignatureOptionsProvider(basicSignatureOptionsProvider);
        return p.newSigner();
    }
    private KeyingDataProvider getKeyingDataProvider(String pfxPath, String password) throws KeyStoreException, SigningCertChainException, UnexpectedJCAException {
        KeyingDataProvider keyingProvider = new FileSystemKeyStoreKeyingDataProvider("pkcs12", pfxPath, new FirstCertificateSelector(), new DirectPasswordProvider(password), new DirectPasswordProvider(password), true);
        if (keyingProvider.getSigningCertificateChain().isEmpty()) {
            throw new IllegalArgumentException("Cannot initialize keystore with path " + pfxPath);
        }
        return keyingProvider;
    }
    private void signWithoutIDEnveloped(String inputPath, String outputPath, XadesSigner signer) throws Exception {
        // Copy source doc into target document
        Document sourceDoc = XmlHelper.getDocument(inputPath);
        sourceDoc.setDocumentURI(null);

        writeXMLToFile(sourceDoc, outputPath);

        sourceDoc = XmlHelper.getDocument(outputPath);

        Element signatureParent = (Element) sourceDoc.getDocumentElement();
        Element elementToSign = sourceDoc.getDocumentElement();
        String refUri;
        if (elementToSign.hasAttribute("Id")) {
            refUri = '#' + elementToSign.getAttribute("Id");
        } else {
            if (elementToSign.getParentNode().getNodeType() != Node.DOCUMENT_NODE) {
                throw new IllegalArgumentException("Element without Id must be the document root");
            }
            refUri = "";
        }

        DataObjectDesc dataObjRef = new DataObjectReference(refUri).withTransform(new DataObjectTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE));
        XadesSignatureResult result = signer.sign(new SignedDataObjects(dataObjRef).withCommitmentType(AllDataObjsCommitmentTypeProperty.proofOfOrigin()), signatureParent);


        writeXMLToFile(sourceDoc, outputPath);
    }
    private void writeXMLToFile(Document doc, String outputPath) throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException, FileNotFoundException {
        // Write the output to a file
        Source source = new DOMSource(doc);

        // Prepare the output file
        File outFile = new File(outputPath);
        outFile.getParentFile().mkdirs();
        outFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(outFile);

        StreamResult result = new StreamResult(fos);

        // Write the DOM document to the file
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(source, result);

        fos.close();
    }
}
