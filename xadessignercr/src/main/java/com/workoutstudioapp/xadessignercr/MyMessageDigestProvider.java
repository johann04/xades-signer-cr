package com.workoutstudioapp.xadessignercr;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.HashMap;

import org.apache.xml.security.algorithms.MessageDigestAlgorithm;

import xades4j.UnsupportedAlgorithmException;
import xades4j.providers.MessageDigestEngineProvider;

public class MyMessageDigestProvider implements MessageDigestEngineProvider
{
    private static final HashMap<String, String> algorithmMapper;

    static
    {
        algorithmMapper = new HashMap<String, String>(4);
        algorithmMapper.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1, "SHA-1");
        algorithmMapper.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256, "SHA-256");
        algorithmMapper.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384, "SHA-384");
        algorithmMapper.put(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA512, "SHA-512");
    }

    private String hackedHash = null;
    public MyMessageDigestProvider(String hackedHash) {
    	this.hackedHash = hackedHash;
    }

    public MessageDigest getEngine(String digestAlgorithmURI) throws UnsupportedAlgorithmException
    {
        String digestAlgorithmName = algorithmMapper.get(digestAlgorithmURI);
        if (null == digestAlgorithmName)
            throw new UnsupportedAlgorithmException("Digest algorithm not supported by the provider", digestAlgorithmURI);
        try
        {
        	MessageDigest messageDigest = null;
        	if (digestAlgorithmName.equals("SHA-1")) {
        		messageDigest = MessageDigest.getInstance(digestAlgorithmName);
        		messageDigest = new MessageDigestWrapper(messageDigest, this.hackedHash);
        	} else {
        		messageDigest = MessageDigest.getInstance(digestAlgorithmName);
        	}
            return messageDigest;
        }
        catch (NoSuchAlgorithmException nsae)
        {
            throw new UnsupportedAlgorithmException(nsae.getMessage(), digestAlgorithmURI, nsae);
        }
    }
}

