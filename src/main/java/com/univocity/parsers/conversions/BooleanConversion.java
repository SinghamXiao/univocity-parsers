/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.conversions;

import java.util.*;

import com.univocity.parsers.common.*;

/**
 * Converts Strings to Booleans and vice versa
 * 
 * <p> This class supports multiple representations of boolean values. For example, you can define conversions from  different Strings such as "Yes, Y, 1" to true, and 
 * "No, N, 0" to false.
 * 
 * <p> The reverse conversion from a Boolean to String (in {@link #revert(Boolean)} will return the first String provided in this class constructor
 * <p> Using the previous example, a call to <code>revert(true)</code> will produce "Yes" and a call <code>revert(false)</code> will produce "No". 
 * 
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class BooleanConversion extends ObjectConversion<Boolean> {

	private String defaultForTrue;
	private String defaultForFalse;

	private Set<String> falseValues = new HashSet<String>();
	private Set<String> trueValues = new HashSet<String>();

	/**
	 * Creates conversions from String to Boolean.
	 * This default constructor assumes the output of a conversion should be null when input is null
	 * <p>The list of Strings that identify "true" the list of Strings that identify "false" are mandatory. 
	 * @param valuesForTrue Strings that identify the boolean value <i>true</i>. The first element will be returned when executing <code>revert(true)</code>
	 * @param valuesForFalse Strings that identify the boolean value <i>false</i>. The first element will be returned when executing <code>#revert(false)</code>
	 */
	public BooleanConversion(String[] valuesForTrue, String[] valuesForFalse) {
		this(null, null, valuesForTrue, valuesForFalse);
	}

	/**
	 * Creates a Conversion from String to Boolean with default values to return when the input is null.
	 * <p>The list of Strings that identify "true" the list of Strings that identify "false" are mandatory.
	 * @param valueIfStringIsNull default Boolean value to be returned when the input String is null. Used when {@link #execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a Boolean input is null. Used when {@link #revert(Boolean)} is invoked.
	 * @param valuesForTrue Strings that identify the boolean value <i>true</i>. The first element will be returned when executing  <code>revert(true)</code>
	 * @param valuesForFalse Strings that identify the boolean value <i>false</i>. The first element will be returned when executing <code>#revert(false)</code>
	 */
	public BooleanConversion(Boolean valueIfStringIsNull, String valueIfObjectIsNull, String[] valuesForTrue, String[] valuesForFalse) {
		super(valueIfStringIsNull, valueIfObjectIsNull);
		ArgumentUtils.notEmpty("Values for true", valuesForTrue);
		ArgumentUtils.notEmpty("Values for false", valuesForFalse);

		ArgumentUtils.fill(falseValues, valuesForFalse);
		ArgumentUtils.fill(trueValues, valuesForTrue);

		for (String falseValue : falseValues) {
			if (trueValues.contains(falseValue)) {
				throw new IllegalArgumentException("Ambiguous string representation for both false and true values: '" + falseValue + "'");
			}
		}

		defaultForTrue = valuesForTrue[0];
		defaultForFalse = valuesForFalse[0];
	}

	/**
	 * Converts a Boolean back to a String
	 * <p> The return value depends on the list of values for true/false provided in the constructor of this class. 
	 * @param input the Boolean to be converted to a String
	 * @return a String representation for this boolean value, or the value of {@link #getValueIfObjectIsNull()} if the Boolean input is null.
	 */
	@Override
	public String revert(Boolean input) {
		if (input != null) {
			if (Boolean.FALSE.equals(input)) {
				return defaultForFalse;
			}
			if (Boolean.TRUE.equals(input)) {
				return defaultForTrue;
			}
		}
		return getValueIfObjectIsNull();
	}

	/**
	 * Converts a String to a Boolean
	 * @param input a String to be converted into a Boolean value.
	 * @return true if the input String is part of {@link #trueValues}, false if the input String is part of {@link #falseValues}, or {@link #getValueIfStringIsNull()} if the input String is null.
	 */
	@Override
	protected Boolean fromString(String input) {
		if (input != null) {
			if (falseValues.contains(input)) {
				return Boolean.FALSE;
			}
			if (trueValues.contains(input)) {
				return Boolean.TRUE;
			}
			throw new IllegalArgumentException("Unable to convert '" + input + "' to Boolean. Allowed Strings are: " + trueValues + " for true; and " + falseValues + " for false.");
		}
		return super.getValueIfStringIsNull();
	}

}