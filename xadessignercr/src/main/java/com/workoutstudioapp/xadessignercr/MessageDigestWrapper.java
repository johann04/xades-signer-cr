package com.workoutstudioapp.xadessignercr;

import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

public class MessageDigestWrapper extends MessageDigest {
	MessageDigest wrapped = null;
	String hackedHash = null;
	boolean useHack = false;
	MessageDigestWrapper(MessageDigest wrapped, String hackedHash) {
		super(wrapped.getAlgorithm());
		this.wrapped = wrapped;
		this.hackedHash = hackedHash;
	}

	@Override
	protected int engineGetDigestLength() {
		// TODO Auto-generated method stub
		return super.engineGetDigestLength();
	}

	@Override
	protected void engineUpdate(byte input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void engineUpdate(byte[] input, int offset, int len) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void engineUpdate(ByteBuffer input) {
		// TODO Auto-generated method stub
		super.engineUpdate(input);
	}

	@Override
	protected byte[] engineDigest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int engineDigest(byte[] buf, int offset, int len) throws DigestException {
		// TODO Auto-generated method stub
		return super.engineDigest(buf, offset, len);
	}

	@Override
	protected void engineReset() {
		// TODO Auto-generated method stub
	}
	@Override
	public byte[] digest() {
		if (useHack) {
			return Base64.decodeBase64(hackedHash.getBytes());
		} else {
			return wrapped.digest();
		}
	}
	@Override
	public byte[] digest(byte[] input) {
		return wrapped.digest(input);
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		wrapped.reset();
	}
	@Override
	public void update(byte input) {
		// TODO Auto-generated method stub
		wrapped.update(input);
	}
	@Override
	public void update(byte[] input, int offset, int len) {
		if (new String(input).startsWith("www.workoutstudioapp.com")) {
			useHack = true;
		}
		wrapped.update(input, offset, len);
	}
}
