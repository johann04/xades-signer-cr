package com.workoutstudioapp.xadessignercr;

import java.util.HashMap;
import java.util.Map;

import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;

import xades4j.UnsupportedAlgorithmException;
import xades4j.algorithms.Algorithm;
import xades4j.algorithms.CanonicalXMLWithoutComments;
import xades4j.algorithms.GenericAlgorithm;
import xades4j.providers.AlgorithmsProviderEx;

/**
 * This class was created to overwrite the default "getDigestAlgorithmForDataObjsReferences" so it return SHA-1 instead of SHA-256.
 * @author johann04
 */
public class MyAlgorithmsProviderEx implements AlgorithmsProviderEx {
	private static final Map<String, Algorithm> signatureAlgsMaps;
	private int hackCounter = 0;

    static
    {
        signatureAlgsMaps = new HashMap<String, Algorithm>(2);
        signatureAlgsMaps.put("DSA", new GenericAlgorithm(XMLSignature.ALGO_ID_SIGNATURE_DSA));
        signatureAlgsMaps.put("RSA", new GenericAlgorithm(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256));
    }

    public Algorithm getSignatureAlgorithm(String keyAlgorithmName) throws UnsupportedAlgorithmException
    {
        Algorithm sigAlg = signatureAlgsMaps.get(keyAlgorithmName);
        if (null == sigAlg)
        {
            throw new UnsupportedAlgorithmException("Signature algorithm not supported by the provider", keyAlgorithmName);
        }

        return sigAlg;
    }

    public Algorithm getCanonicalizationAlgorithmForSignature()
    {
        return new CanonicalXMLWithoutComments();
    }

    public Algorithm getCanonicalizationAlgorithmForTimeStampProperties()
    {
        return new CanonicalXMLWithoutComments();
    }

    public String getDigestAlgorithmForDataObjsReferences()
    {
        return MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256;
    }

    public String getDigestAlgorithmForReferenceProperties()
    {
        if (hackCounter == 1) {
        	return MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1;
        } else {
        	hackCounter++;
        	return MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256;
        }
    }

    public String getDigestAlgorithmForTimeStampProperties()
    {
        return MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1;
    }
}
