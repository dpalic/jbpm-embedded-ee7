package com.xenovation.jbpmjee.embedded.ejb;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Abstract class for the stateless EJBs.
 * 
 * @author Darko Palic
 */
public abstract class AbstractStatelessEJB {

	/**
	 * generate a random message.
	 * 
	 * @return a random message
	 */
	public String getRandomMessage() {
		SecureRandom random = new SecureRandom();
		BigInteger lValue = new BigInteger(130, random);
		return lValue.toString(32);
	}

}
