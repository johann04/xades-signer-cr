package com.workoutstudioapp.xadessignercr;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import xades4j.production.Enveloped;
import xades4j.production.XadesEpesSigningProfile;
import xades4j.production.XadesSigner;
import xades4j.production.XadesSigningProfile;
import xades4j.properties.ObjectIdentifier;
import xades4j.properties.SignaturePolicyBase;
import xades4j.properties.SignaturePolicyIdentifierProperty;
import xades4j.providers.KeyingDataProvider;
import xades4j.providers.SignaturePolicyInfoProvider;
import xades4j.providers.impl.FileSystemKeyStoreKeyingDataProvider;

public class Signer {
	public void sign(String keyPath, String password, String xmlInPath, String xmlOutPath) {
		KeyingDataProvider kp;
		try {

			SignaturePolicyInfoProvider policyInfoProvider = new SignaturePolicyInfoProvider() {
				public SignaturePolicyBase getSignaturePolicy() {
					return new SignaturePolicyIdentifierProperty(new ObjectIdentifier(
					    "https://tribunet.hacienda.go.cr/docs/esquemas/2016/v4.1/Resolucion_Comprobantes_Electronicos_DGT-R-48-2016.pdf"),
					    new ByteArrayInputStream("Politica de Factura Digital".getBytes()));
				}
			};

			kp = new FileSystemKeyStoreKeyingDataProvider("pkcs12", keyPath, new FirstCertificateSelector(),
			    new DirectPasswordProvider(password), new DirectPasswordProvider(password), false);

			// SignaturePolicyInfoProvider spi = new
			XadesSigningProfile p = new XadesEpesSigningProfile(kp, policyInfoProvider);
			// p.withBasicSignatureOptionsProvider(new SignatureOptionsProvider());

			// open file
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = null;
			builder = factory.newDocumentBuilder();
			Document doc1 = builder.parse(new File(xmlInPath));
			Element elemToSign = doc1.getDocumentElement();

			XadesSigner signer = p.newSigner();

			new Enveloped(signer).sign(elemToSign);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(xmlOutPath);
			Source input = new DOMSource(doc1);

			transformer.transform(input, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
