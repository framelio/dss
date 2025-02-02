/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.pdf;

import java.io.IOException;
import java.util.Date;

import eu.europa.esig.dss.model.DSSException;

public class PdfSigDict {

	private PdfDict dictionary;
	
	private String sigFieldName;

	public PdfSigDict(PdfDict dictionary) {
		this.dictionary = dictionary;
	}

	public PdfSigDict(PdfDict dictionary, String signatureName) {
		this(dictionary);
		this.sigFieldName = signatureName;
	}
	
	public String getSigFieldName() {
		return sigFieldName;
	}

	public String getSignerName() {
		return dictionary.getStringValue("Name");
	}

	public String getContactInfo() {
		return dictionary.getStringValue("ContactInfo");
	}

	public String getReason() {
		return dictionary.getStringValue("Reason");
	}

	public String getLocation() {
		return dictionary.getStringValue("Location");
	}

	public Date getSigningDate() {
		return dictionary.getDateValue("M");
	}

	public String getFilter() {
		return dictionary.getNameValue("Filter");
	}

	public String getSubFilter() {
		return dictionary.getNameValue("SubFilter");
	}

	public byte[] getContents() {
		try {
			return dictionary.getBinariesValue("Contents");
		} catch (IOException e) {
			throw new DSSException("Unable to retrieve the signature content", e);
		}
	}

	public int[] getByteRange() {
		PdfArray byteRangeArray = dictionary.getAsArray("ByteRange");
		if (byteRangeArray == null) {
			throw new DSSException("Unable to retrieve the ByteRange");
		}
		
		int arraySize = byteRangeArray.size();
		int[] result = new int[arraySize];
		for (int i = 0; i < arraySize; i++) {
			try {
				result[i] = byteRangeArray.getInt(i);
			} catch (IOException e) {
				throw new DSSException("Unable to parse integer from the ByteRange", e);
			}
		}
		return result;
	}

}
