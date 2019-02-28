package de.dailab.apppets.plib.crypt;

import de.dailab.apppets.plib.random.SecureSecureRandom;

/**
 * Created by arik on 06.07.2017.
 */

public class PLibSimpleStringAnonymizer extends PLibAbstractStringPseudoAndAnonymizer{

	private char[] buf;

	/**
	 * Initiates a random object for generating random strings.
	 *
	 * @param type indicated whether to generate a string containing digits only, digits and letters only or
	 * furthermore symbols
	 */
	public PLibSimpleStringAnonymizer(int type){

		super(type);
		random = SecureSecureRandom.get();
	}

	/**
	 * Initiates a random object for generating random strings.
	 *
	 * @param valuesToUse a string containing all values allowed to be used
	 */

	public PLibSimpleStringAnonymizer(String valuesToUse){

		super(valuesToUse);
		random = SecureSecureRandom.get();
	}

	/**
	 * Returns a randomized string of the original length.
	 *
	 * @param originalString the original string
	 *
	 * @return randomized string
	 */
	public String nextString(String originalString){

		if(originalString==null){
			return nextString(0);
		}

		return nextString(originalString.length());
	}

	/**
	 * Get a randomized string.
	 *
	 * @param length the length, have to be at least 0
	 *
	 * @return randomized string
	 */
	public String nextString(int length){

		if(length < 0){
			throw new IllegalArgumentException("length < 0: " + length);
		}
		buf = new char[length];
		for(int idx = 0; idx < buf.length; ++idx){
			buf[idx] = getSymbolsToUse()[random.nextInt(getSymbolsToUse().length)];
		}
		return new String(buf);
	}
}
